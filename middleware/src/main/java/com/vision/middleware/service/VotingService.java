package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.baseEntities.VotableEntity;
import com.vision.middleware.domain.enums.VotableType;
import com.vision.middleware.domain.interfaces.Votable;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.exceptions.DuplicateVoteException;
import com.vision.middleware.repo.PostRepository;
import com.vision.middleware.repo.ReplyRepository;
import com.vision.middleware.repo.UserVoteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VotingService {

    @Autowired
    private UserVoteRepository userVoteRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Transactional
    public void voteOnVotable(ApplicationUser user, VotableEntity votable, UserVote.VoteType voteType) {
        VotableType votableType = getVotableType(votable);
        Optional<UserVote> optionalVote = userVoteRepository.findByUserAndVotableAndVotableType(user, votable, votableType);
        optionalVote.ifPresentOrElse(
                vote -> updateVote(vote, voteType, votable),
                () -> createVote(user, votable, votableType, voteType)
        );
    }

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

    public Optional<UserVote.VoteType> getUserVoteOnVotable(ApplicationUser user, VotableEntity votable) {
        VotableType type = getVotableType(votable);
        Optional<UserVote> optionalVote = userVoteRepository.findByUserAndVotableAndVotableType(user, votable, type);
        return optionalVote.map(UserVote::getVoteType);
    }

    private VotableType getVotableType(VotableEntity votable) {
        if (votable instanceof Post) {
            return VotableType.POST;
        } else if (votable instanceof Reply) {
            return VotableType.REPLY;
        } else {
            throw new IllegalArgumentException("Unsupported votable type");
        }
    }

    private void updateVote(UserVote vote, UserVote.VoteType voteType, VotableEntity votable) {
        if (vote.getVoteType() != voteType) {
            updateVotableCounts(votable, vote.getVoteType(), voteType);
            vote.setVoteType(voteType);
            userVoteRepository.save(vote);
        } else {
            throw new DuplicateVoteException("User vote for votable already exists.");
        }
    }

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

    private void updateVotableCounts(VotableEntity votable, UserVote.VoteType oldVoteType, UserVote.VoteType newVoteType) {
        if (votable instanceof Post post) {
            updateCounts(post, oldVoteType, newVoteType);
            postRepository.save(post);
        } else if (votable instanceof Reply reply) {
            updateCounts(reply, oldVoteType, newVoteType);
            replyRepository.save(reply);
        }
    }

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