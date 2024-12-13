package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.baseentities.VotableEntity;
import com.vision.middleware.domain.enums.VotableType;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.exceptions.DuplicateVoteException;
import com.vision.middleware.repo.PostRepository;
import com.vision.middleware.repo.ReplyRepository;
import com.vision.middleware.repo.UserVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service responsible for handling voting operations on votable entities (e.g., Posts, Replies).
 */
@Service
@RequiredArgsConstructor
public class VotingService {

    @Autowired
    private final UserVoteRepository userVoteRepository;

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final ReplyRepository replyRepository;

    /**
     * Casts a vote on a votable entity. If the user has already voted, updates the existing vote.
     *
     * @param user       the user casting the vote
     * @param votable    the entity being voted on (e.g., Post, Reply)
     * @param voteType   the type of vote (LIKE or DISLIKE)
     *
     * @throws DuplicateVoteException if the user's vote type is the same as the existing vote
     */
    @Transactional
    public void voteOnVotable(ApplicationUser user, VotableEntity votable, UserVote.VoteType voteType) {
        VotableType votableType = getVotableType(votable);
        Optional<UserVote> optionalVote = userVoteRepository.findByUserAndVotableAndVotableType(user, votable, votableType);
        optionalVote.ifPresentOrElse(
                vote -> updateVote(vote, voteType, votable),
                () -> createVote(user, votable, votableType, voteType)
        );
    }

    /**
     * Deletes a user's vote from a votable entity and updates the entity's vote counts.
     *
     * @param user     the user whose vote is being deleted
     * @param votable  the entity from which the vote is being deleted
     */
    @Transactional
    public void deleteVote(ApplicationUser user, VotableEntity votable){
        VotableType votableType = getVotableType(votable);
        Optional<UserVote> optionalVote = userVoteRepository.findByUserAndVotableAndVotableType(user, votable, votableType);
        optionalVote.ifPresent(
            vote -> {
                if (votable instanceof Post post){
                    if (vote.getVoteType() ==  UserVote.VoteType.LIKE){
                        post.setLikeCount(post.getLikeCount()-1);
                    } else if (vote.getVoteType() == UserVote.VoteType.DISLIKE){
                        post.setDislikeCount(post.getDislikeCount()-1);
                    }
                    postRepository.save(post);
                } else if (votable instanceof Reply reply) {
                    if (vote.getVoteType() ==  UserVote.VoteType.LIKE){
                        reply.setLikeCount(reply.getLikeCount()-1);
                    } else if (vote.getVoteType() == UserVote.VoteType.DISLIKE){
                        reply.setDislikeCount(reply.getDislikeCount()-1);
                    }
                    replyRepository.save(reply);
                }
            }
        );
        
        userVoteRepository.deleteByVotableIdAndUserId(votable.getId(), user.getId());
    }

    /**
     * Retrieves the vote type (LIKE or DISLIKE) of a user's vote on a votable entity, if it exists.
     *
     * @param user     the user whose vote is being retrieved
     * @param votable  the entity on which the user's vote is being retrieved
     * @return          an Optional containing the vote type, or an empty Optional if no vote exists
     */
    public Optional<UserVote.VoteType> getUserVoteOnVotable(ApplicationUser user, VotableEntity votable) {
        VotableType type = getVotableType(votable);
        Optional<UserVote> optionalVote = userVoteRepository.findByUserAndVotableAndVotableType(user, votable, type);
        return optionalVote.map(UserVote::getVoteType);
    }

    /**
     * Determines the votable type (POST or REPLY) of a given votable entity.
     *
     * @param votable  the entity whose type is being determined
     * @return          the votable type of the entity
     * @throws IllegalArgumentException if the entity is not a supported votable type
     */
    private VotableType getVotableType(VotableEntity votable) {
        if (votable instanceof Post) {
            return VotableType.POST;
        } else if (votable instanceof Reply) {
            return VotableType.REPLY;
        } else {
            throw new IllegalArgumentException("Unsupported votable type");
        }
    }

    /**
     * Updates an existing user vote with a new vote type and updates the votable entity's vote counts.
     *
     * @param vote      the existing user vote to be updated
     * @param voteType  the new vote type
     * @param votable   the entity whose vote counts are being updated
     *
     * @throws DuplicateVoteException if the new vote type is the same as the existing vote type
     */
    private void updateVote(UserVote vote, UserVote.VoteType voteType, VotableEntity votable) {
        if (vote.getVoteType() != voteType) {
            updateVotableCounts(votable, vote.getVoteType(), voteType);
            vote.setVoteType(voteType);
            userVoteRepository.save(vote);
        } else {
            throw new DuplicateVoteException("User vote for votable already exists.");
        }
    }

    /**
     * Creates a new user vote for a votable entity and updates the entity's vote counts.
     *
     * @param user       the user casting the vote
     * @param votable    the entity being voted on
     * @param votableType the type of votable entity
     * @param voteType   the type of vote (LIKE or DISLIKE)
     */
    private void createVote(ApplicationUser user, VotableEntity votable, VotableType votableType, UserVote.VoteType voteType) {
        updateVotableCounts(votable, null, voteType);
        UserVote vote = UserVote.builder()
                .user(user)
                .votable(votable)
                .votableType(votableType)
                .voteType(voteType)
                .build();
        userVoteRepository.save(vote);
    }

    /**
     * Updates the vote counts of a votable entity based on the old and new vote types.
     *
     * @param votable    the entity whose vote counts are being updated
     * @param oldVoteType the previous vote type (or null if creating a new vote)
     * @param newVoteType the new vote type
     */
    private void updateVotableCounts(VotableEntity votable, UserVote.VoteType oldVoteType, UserVote.VoteType newVoteType) {
        if (votable instanceof Post post) {
            updateCounts(post, oldVoteType, newVoteType);
            postRepository.save(post);
        } else if (votable instanceof Reply reply) {
            updateCounts(reply, oldVoteType, newVoteType);
            replyRepository.save(reply);
        }
    }

    /**
     * Updates the like and dislike counts of a votable entity based on the old and new vote types.
     *
     * @param votable    the entity whose counts are being updated
     * @param oldVoteType the previous vote type (or null if creating a new vote)
     * @param newVoteType the new vote type
     */
    private void updateCounts(VotableEntity votable, UserVote.VoteType oldVoteType, UserVote.VoteType newVoteType) {
        if (oldVoteType != null) {
            if (oldVoteType == UserVote.VoteType.LIKE) {
                votable.setLikeCount(votable.getLikeCount() - 1);
            } else if (oldVoteType == UserVote.VoteType.DISLIKE) {
                votable.setDislikeCount(votable.getDislikeCount() - 1);
            }
        }

        if (newVoteType == UserVote.VoteType.LIKE) {
            votable.setLikeCount(votable.getLikeCount() + 1);
        } else if (newVoteType == UserVote.VoteType.DISLIKE) {
            votable.setDislikeCount(votable.getDislikeCount() + 1);
        }
    }
}
