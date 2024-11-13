package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.enums.VotableType;
import com.vision.middleware.domain.interfaces.Votable;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.exceptions.DuplicateVoteException;
import com.vision.middleware.repo.UserVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VotingService {

    @Autowired
    private UserVoteRepository userVoteRepository;

    @Transactional
    public void voteOnVotable(ApplicationUser user, Votable votable, UserVote.VoteType voteType) {
        VotableType votableType = getVotableType(votable);
        Optional<UserVote> optionalVote = userVoteRepository.findByUserAndVotableAndVotableType(user, votable, votableType);
        optionalVote.ifPresentOrElse(
                vote -> updateVote(vote, voteType),
                () -> createVote(user, votable, votableType, voteType)
        );
    }

    private VotableType getVotableType(Votable votable) {
        if (votable instanceof Post) {
            return VotableType.POST;
        } else if (votable instanceof Reply) {
            return VotableType.REPLY;
        } else {
            throw new IllegalArgumentException("Unsupported votable type");
        }
    }

    private void updateVote(UserVote vote, UserVote.VoteType voteType) {
        if (vote.getVoteType() != voteType) {
            vote.setVoteType(voteType);
            userVoteRepository.save(vote);
        } else {
            throw new DuplicateVoteException("User vote for votable already exists.");
        }
    }

    private void createVote(ApplicationUser user, Votable votable, VotableType votableType, UserVote.VoteType voteType) {
        UserVote vote = UserVote.builder()
                .user(user)
                .votable(votable)
                .votableType(votableType)
                .voteType(voteType)
                .build();
        userVoteRepository.save(vote);
    }
}