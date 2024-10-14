package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.relations.UserFollows;
import com.vision.middleware.repo.UserFollowsRepository;
import com.vision.middleware.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Service
public class FollowerService {

    @Autowired
    private UserFollowsRepository followsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public void followUser(long followerId, long followeeId) {
        ApplicationUser follower = userService.loadUserById(followerId);
        ApplicationUser followee = userService.loadUserById(followeeId);

        // create entity that states relationship
        UserFollows follow = UserFollows.builder()
                .follower(follower)
                .followee(followee)
                .build();

        // persist the relationship
        followsRepository.save(follow);
    }

    public void unfollowUser(long followerId, long followeeId) {
        ApplicationUser follower = userService.loadUserById(followerId);
        ApplicationUser followee = userService.loadUserById(followeeId);

        followsRepository.deleteByFollowerAndFollowee(follower, followee);
    }

    public List<UserFollows> getByFollowingUser(long userId) {
        ApplicationUser user = userService.loadUserById(userId);
        return followsRepository.findByFollower(user);
    }

    public List<UserFollows> getByFolloweeUser(long userId) {
        ApplicationUser user = userService.loadUserById(userId);
        return followsRepository.findByFollowee(user);
    }
}
