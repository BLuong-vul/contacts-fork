package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.relations.UserFollows;
import com.vision.middleware.repo.UserFollowsRepository;
import com.vision.middleware.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing user follow relationships.
 * Provides methods for following, unfollowing, and retrieving follow relationships.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Service
public class FollowerService {

    /**
     * Repository for user follow relationships.
     */
    @Autowired
    private UserFollowsRepository followsRepository;

    /**
     * Service for user-related operations.
     */
    @Autowired
    private UserService userService;

    /**
     * Establishes a follow relationship between two users.
     *
     * @param followerId the ID of the user initiating the follow
     * @param followeeId the ID of the user being followed
     * @throws NullPointerException if either user ID does not correspond to an existing user
     */
    @Transactional
    public void followUser(long followerId, long followeeId) {
        ApplicationUser follower = userService.loadUserById(followerId);
        ApplicationUser followee = userService.loadUserById(followeeId);

        // does relation exist already?
        if (followsRepository.findByFollowerAndFollowee(follower, followee).isPresent()) {
            return; // exists, no need to add this again.
        }

        // create entity that states relationship
        /*Design Pattern: Builder*/
        UserFollows follow = UserFollows.builder()
                .follower(follower)
                .followee(followee)
                .build();
        /*Design Pattern: Builder*/
        // persist the relationship
        followsRepository.save(follow);
    }

    /**
     * Dissolves a follow relationship between two users.
     *
     * @param followerId the ID of the user initiating the unfollow
     * @param followeeId the ID of the user being unfollowed
     */
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        ApplicationUser follower = userService.loadUserById(followerId);
        ApplicationUser followee = userService.loadUserById(followeeId);

        followsRepository.deleteByFollowerAndFollowee(follower, followee);
    }

    /**
     * Retrieves a list of follow relationships where the specified user is the follower.
     *
     * @param userId the ID of the user to retrieve follow relationships for
     * @return a list of UserFollows entities representing the user's follow relationships
     */
    public List<UserFollows> getByFollowingUser(long userId) {
        ApplicationUser user = userService.loadUserById(userId);
        return followsRepository.findByFollower(user);
    }

    /**
     * Retrieves a list of follow relationships where the specified user is the followee.
     *
     * @param userId the ID of the user to retrieve follow relationships for
     * @return a list of UserFollows entities representing the user's follow relationships
     */
    public List<UserFollows> getByFolloweeUser(long userId) {
        ApplicationUser user = userService.loadUserById(userId);
        return followsRepository.findByFollowee(user);
    }
}
