package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.relations.UserFollows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing UserFollows entities.
 * Provides custom query methods for retrieving and deleting user follow relationships.
 */
@Repository
public interface UserFollowsRepository extends JpaRepository<UserFollows, Long> {

    /**
     * Retrieves a list of UserFollows entities where the specified ApplicationUser is the follower.
     *
     * @param follower The ApplicationUser object representing the follower
     * @return A list of UserFollows entities where the provided user is the follower
     */
    List<UserFollows> findByFollower(ApplicationUser follower);

    /**
     * Retrieves a list of UserFollows entities where the specified ApplicationUser is the followee.
     *
     * @param followee The ApplicationUser object representing the followee
     * @return A list of UserFollows entities where the provided user is the followee
     */
    List<UserFollows> findByFollowee(ApplicationUser followee);

    /**
     * Retrieves a UserFollows entity by both the follower and followee ApplicationUser, if it exists.
     *
     * @param follower The ApplicationUser object representing the follower
     * @param followee The ApplicationUser object representing the followee
     * @return An Optional containing the UserFollows entity if found, empty otherwise
     */
    Optional<UserFollows> findByFollowerAndFollowee(ApplicationUser follower, ApplicationUser followee);

    /**
     * Deletes the UserFollows entity associated with the specified follower and followee ApplicationUsers, if it exists.
     *
     * @param follower The ApplicationUser object representing the follower
     * @param followee The ApplicationUser object representing the followee
     */
    void deleteByFollowerAndFollowee(ApplicationUser follower, ApplicationUser followee);
}
