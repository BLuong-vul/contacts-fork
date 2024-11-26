package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.enums.VotableType;
import com.vision.middleware.domain.interfaces.Votable;
import com.vision.middleware.domain.relations.UserVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {
    Optional<UserVote> findByUserAndVotableAndVotableType(ApplicationUser user, Votable votable, VotableType votableType);
    void deleteByVotableIdAndUserId(long votableId, long userId);
}