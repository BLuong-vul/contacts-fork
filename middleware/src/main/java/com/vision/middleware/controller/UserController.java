package com.vision.middleware.controller;

import com.vision.middleware.domain.relations.UserFollows;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.service.FollowerService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin("*") // todo: change this later
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final FollowerService followerService;

    @Autowired
    private final JwtUtil jwtUtil;

    @GetMapping("/")
    public String helloUserController() {
        return "User access level";
    }

    @GetMapping("/info")
    public UserDetails getUserInfo(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        return userService.loadUserById(id);
    }

    @GetMapping("/id/{username}")
    public Long getIdByUsername(@PathVariable String username){
        return userService.loadUserByUsername(username).getId();
    }

    @GetMapping("/following/list")
    public List<UserDTO> getFollowing(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);

        List<UserFollows> userFollowsList = followerService.getByFollowingUser(id);

        return userFollowsList.stream().map(UserFollows::getFollowee).map(
                user -> UserDTO.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .build()
        ).toList();
    }

    @GetMapping("/followers/list")
    public List<UserDTO> getFollowers(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);

        List<UserFollows> userFollowersRelation = followerService.getByFolloweeUser(id);

        return userFollowersRelation.stream().map(UserFollows::getFollower).map(
                user -> UserDTO.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .build()
        ).toList();
    }

    @PostMapping("/follow/{followeeId}")
    public String followUser(@RequestHeader("Authorization") String token, @PathVariable long followeeId) {
        long followerId = jwtUtil.checkJwtAuthAndGetUserId(token);
        followerService.followUser(followerId, followeeId);

        return String.format("User %s followed", followeeId);
    }

    @PostMapping("/unfollow/{followeeId}")
    public String unfollowUser(@RequestHeader("Authorization") String token, @PathVariable long followeeId) {
        long followerId = jwtUtil.checkJwtAuthAndGetUserId(token);
        followerService.unfollowUser(followerId, followeeId);

        return String.format("User %s unfollowed", followeeId);
    }
}
