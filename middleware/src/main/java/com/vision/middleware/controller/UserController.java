package com.vision.middleware.controller;

import com.amazonaws.Response;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.relations.UserFollows;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.service.FollowerService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Date;

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

    // Right now there is not much point to using this over /public-info
    // but the idea is that this can return more private info about the logged in user
    // while public-info should not do that
    @GetMapping("/info")
    public UserDTO getUserInfo(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        ApplicationUser userDetails = userService.loadUserById(id);
        /*Design Pattern: Builder*/
        return UserDTO.builder()
            .userId(userDetails.getId())
            .username(userDetails.getUsername())
            .followerCount(userService.getFollowerCount(userDetails))
            .followingCount(userService.getFollowingCount(userDetails))
            .displayName(userDetails.getDisplayName())
            .bio(userDetails.getBio())
            .occupation(userDetails.getOccupation())
            .location(userDetails.getLocation())
            .birthdate(userDetails.getBirthdate())
            .joinDate(userDetails.getJoinDate())
            .build();
        /*Design Pattern: Builder*/
    }

    @GetMapping("/public-info")
    public UserDTO getPublicInfoByUsername(@RequestParam("username") String username) {
        ApplicationUser userDetails = userService.loadUserByUsername(username);
        /*Design Pattern: Builder*/
        return UserDTO.builder()
            .userId(userDetails.getId())
            .username(userDetails.getUsername())
            .followerCount(userService.getFollowerCount(userDetails))
            .followingCount(userService.getFollowingCount(userDetails))
            .displayName(userDetails.getDisplayName())
            .bio(userDetails.getBio())
            .occupation(userDetails.getOccupation())
            .location(userDetails.getLocation())
            .birthdate(userDetails.getBirthdate())
            .joinDate(userDetails.getJoinDate())
            .build();
        /*Design Pattern: Builder*/
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
                /*Design Pattern: Builder*/
                user -> UserDTO.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .build()
                /*Design Pattern: Builder*/
        ).toList();
    }

    @GetMapping("/followers/list")
    public List<UserDTO> getFollowers(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);

        List<UserFollows> userFollowersRelation = followerService.getByFolloweeUser(id);

        return userFollowersRelation.stream().map(UserFollows::getFollower).map(
                /*Design Pattern: Builder*/
                user -> UserDTO.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .build()
                /*Design Pattern: Builder*/
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

    // todo: add endpoint in security config
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
        // query valid?
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // perform search.
        return ResponseEntity.ok(
                userService.searchUsers(query).stream().map(
                        user -> UserDTO.builder()
                                .username(user.getUsername())
                                .userId(user.getId())
                                .displayName(user.getDisplayName())
                                .build()
                ).toList()
        );
    }

    // Profile customization updates
    @PostMapping("/account/displayName")
    public void updateDisplayNameById(@RequestHeader("Authorization") String token, 
                                       @RequestParam(value = "displayName", defaultValue = "") String displayName) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateDisplayNameById(id, displayName);
    }

    @PostMapping("/account/bio")
    public void updateBioById(@RequestHeader("Authorization") String token, @RequestParam(value = "bio", defaultValue = "") String bio) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateBioById(id, bio);
        return;
    }

    @PostMapping("/account/occupation")
    public void updateOccupationById(@RequestHeader("Authorization") String token, 
                                      @RequestParam(value = "occupation", defaultValue = "") String occupation) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateOccupationById(id, occupation);
    }

    @PostMapping("/account/location")
    public void updateLocationById(@RequestHeader("Authorization") String token, 
                                    @RequestParam(value = "location", defaultValue = "") String location) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateLocationById(id, location);
    }

    @PostMapping("/account/birthdate")
    public void updateBirthdateById(@RequestHeader("Authorization") String token, 
                                     @RequestParam(value = "birthdate", defaultValue = "") Date birthdate) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateBirthdateById(id, birthdate);
    }
}
