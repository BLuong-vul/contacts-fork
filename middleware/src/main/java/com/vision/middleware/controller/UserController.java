package com.vision.middleware.controller;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.relations.UserFollows;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.service.FollowerService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> helloUserController() {
        return ResponseEntity.ok("User access level");
    }

    // Right now there is not much point to using this over /public-info
    // but the idea is that this can return more private info about the logged in user
    // while public-info should not do that
    @GetMapping("/info")
    public ResponseEntity<UserDTO> getUserInfo(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        ApplicationUser userDetails = userService.loadUserById(id);
        return ResponseEntity.ok(buildUserInfoDTO(userDetails));
    }

    @GetMapping("/public-info")
    public ResponseEntity<UserDTO> getPublicInfoByUsername(@RequestParam("username") String username) {
        ApplicationUser userDetails = userService.loadUserByUsername(username);
        return ResponseEntity.ok(buildUserInfoDTO(userDetails));
    }

    @GetMapping("/id/{username}")
    public ResponseEntity<Long> getIdByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.loadUserByUsername(username).getId());
    }

    @GetMapping("/following/list")
    public ResponseEntity<List<UserDTO>> getFollowing(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        List<UserFollows> userFollowsList = followerService.getByFollowingUser(id);

        return ResponseEntity.ok(
                userFollowsList.stream().map(UserFollows::getFollowee)
                        .map(this::buildSimpleUserDTO).toList()
        );
    }

    @GetMapping("/followers/list")
    public ResponseEntity<List<UserDTO>> getFollowers(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);

        List<UserFollows> userFollowersRelation = followerService.getByFolloweeUser(id);

        return ResponseEntity.ok(
                userFollowersRelation.stream().map(UserFollows::getFollower)
                        .map(this::buildSimpleUserDTO).toList()
        );
    }

    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<String> followUser(@RequestHeader("Authorization") String token, @PathVariable long followeeId) {
        long followerId = jwtUtil.checkJwtAuthAndGetUserId(token);
        followerService.followUser(followerId, followeeId);

        return ResponseEntity.ok(String.format("User %s followed", followeeId));
    }

    @PostMapping("/unfollow/{followeeId}")
    public ResponseEntity<String> unfollowUser(@RequestHeader("Authorization") String token, @PathVariable long followeeId) {
        long followerId = jwtUtil.checkJwtAuthAndGetUserId(token);
        followerService.unfollowUser(followerId, followeeId);

        return ResponseEntity.ok(String.format("User %s unfollowed", followeeId));
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
                userService.searchUsers(query).stream()
                        .map(this::buildSimpleUserDTO).toList()
        );
    }

    // Profile customization updates
    // todo: potential improvement - make sure that image exists in s3 before approving the change
    @PostMapping("/account/profile-picture-file-name")
    public void updateProfilePictureFileName(@RequestHeader("Authorization") String token,
                                                    @RequestParam(value = "profile-picture-file-name") String profilePictureFileName) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateProfilePictureById(id, profilePictureFileName);
    }

    @PostMapping("/account/banner-picture-file-name")
    public void updateBannerPictureFileName(@RequestHeader("Authorization") String token,
                                                    @RequestParam(value = "banner-picture-file-name") String bannerPictureFileName) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateBannerPictureById(id, bannerPictureFileName);
    }

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

    // helper functions
    private UserDTO buildUserInfoDTO(ApplicationUser userDetails) {
        // design pattern: builder
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
                .profilePictureFileName(userDetails.getProfilePictureFileName())
                .bannerPictureFileName(userDetails.getBannerPictureFileName())
                .build();
    }

    private UserDTO buildSimpleUserDTO(ApplicationUser user) {
        return UserDTO.builder()
                /*Design Pattern: Builder*/
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .profilePictureFileName(user.getProfilePictureFileName())
                .build();
    }
}
