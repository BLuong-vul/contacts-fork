package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.enums.VotableType;
import com.vision.middleware.domain.interfaces.Votable;
import com.vision.middleware.domain.relations.UserVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserVote} entities.
 */
public interface UserVoteRepository extends JpaRepository<UserVote, Long> {

    /**
     * Finds a {@link UserVote} by the specified user, votable, and votable type.
     *
     * @param user        the user who voted
     * @param votable     the votable item
     * @param votableType the type of the votable item
     * @return an {@link Optional} containing the found {@link UserVote}, or an empty {@link Optional} if no vote is found
     */
    Optional<UserVote> findByUserAndVotableAndVotableType(ApplicationUser user, Votable votable, VotableType votableType);

    /**
     * Deletes a {@link UserVote} by the specified votable ID and user ID.
     *
     * @param votableId the ID of the votable item
     * @param userId    the ID of the user who voted
     */
    void deleteByVotableIdAndUserId(long votableId, long userId);
}
