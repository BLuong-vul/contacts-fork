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

/**
 * RESTful controller for handling user-related operations.
 *
 * This controller provides endpoints for managing user information,
 * followers, and profile customization. All endpoints require
 * authentication, except for the publicly accessible user info
 * endpoint (/public-info).
 */
@RestController
@RequestMapping("/user")
@CrossOrigin("*") // todo: change this later
@RequiredArgsConstructor
public class UserController {

    /**
     * Service for interacting with user data.
     */
    @Autowired
    private final UserService userService;

    /**
     * Service for managing user follower relationships.
     */
    @Autowired
    private final FollowerService followerService;

    /**
     * Utility for handling JSON Web Token (JWT) operations.
     */
    @Autowired
    private final JwtUtil jwtUtil;

    /**
     * Test endpoint for verifying user access level.
     *
     * @return a success message indicating user access level
     */
    @GetMapping("/")
    public ResponseEntity<String> helloUserController() {
        return ResponseEntity.ok("User access level");
    }

    // Right now there is not much point to using this over /public-info
    // but the idea is that this can return more private info about the logged in user
    // while public-info should not do that

    /**
     * Retrieves detailed information about the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @return a {@link UserDTO} containing the user's detailed information
     */
    @GetMapping("/info")
    public ResponseEntity<UserDTO> getUserInfo(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        ApplicationUser userDetails = userService.loadUserById(id);
        return ResponseEntity.ok(buildUserInfoDTO(userDetails));
    }

    /**
     * Retrieves publicly accessible information about a user by username.
     *
     * @param username the target user's username
     * @return a {@link UserDTO} containing the user's public information
     */
    @GetMapping("/public-info")
    public ResponseEntity<UserDTO> getPublicInfoByUsername(@RequestParam("username") String username) {
        ApplicationUser userDetails = userService.loadUserByUsername(username);
        return ResponseEntity.ok(buildUserInfoDTO(userDetails));
    }

    /**
     * Retrieves the user ID associated with the given username.
     *
     * @param username the target user's username
     * @return the user's ID
     */
    @GetMapping("/id/{username}")
    public ResponseEntity<Long> getIdByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.loadUserByUsername(username).getId());
    }

    /**
     * Retrieves a list of users followed by the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @return a list of {@link UserDTO} objects representing the followed users
     */
    @GetMapping("/following/list")
    public ResponseEntity<List<UserDTO>> getFollowing(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        List<UserFollows> userFollowsList = followerService.getByFollowingUser(id);

        return ResponseEntity.ok(
                userFollowsList.stream().map(UserFollows::getFollowee)
                        .map(this::buildSimpleUserDTO).toList()
        );
    }

    /**
     * Retrieves a list of users who are following the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @return a list of {@link UserDTO} objects representing the followers
     */
    @GetMapping("/followers/list")
    public ResponseEntity<List<UserDTO>> getFollowers(@RequestHeader("Authorization") String token) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);

        List<UserFollows> userFollowersRelation = followerService.getByFolloweeUser(id);

        return ResponseEntity.ok(
                userFollowersRelation.stream().map(UserFollows::getFollower)
                        .map(this::buildSimpleUserDTO).toList()
        );
    }

    /**
     * Allows the currently logged-in user to follow another user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param followeeId the ID of the user to be followed
     * @return a success message
     */
    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<String> followUser(@RequestHeader("Authorization") String token, @PathVariable long followeeId) {
        long followerId = jwtUtil.checkJwtAuthAndGetUserId(token);
        followerService.followUser(followerId, followeeId);

        return ResponseEntity.ok(String.format("User %s followed", followeeId));
    }

    /**
     * Allows the currently logged-in user to unfollow another user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param followeeId the ID of the user to be unfollowed
     * @return a success message
     */
    @PostMapping("/unfollow/{followeeId}")
    public ResponseEntity<String> unfollowUser(@RequestHeader("Authorization") String token, @PathVariable long followeeId) {
        long followerId = jwtUtil.checkJwtAuthAndGetUserId(token);
        followerService.unfollowUser(followerId, followeeId);

        return ResponseEntity.ok(String.format("User %s unfollowed", followeeId));
    }

    /**
     * Searches for users by username (case-insensitive).
     *
     * @param query the search query (username or part of username)
     * @return a list of {@link UserDTO} objects representing the matching users
     */
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
    /**
     * Updates the profile picture file name for the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param profilePictureFileName the new profile picture file name
     */
    @PostMapping("/account/profile-picture-file-name")
    public void updateProfilePictureFileName(@RequestHeader("Authorization") String token,
                                                    @RequestParam(value = "profile-picture-file-name") String profilePictureFileName) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateProfilePictureById(id, profilePictureFileName);
    }

    /**
     * Updates the banner picture file name for the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param bannerPictureFileName the new banner picture file name
     */
    @PostMapping("/account/banner-picture-file-name")
    public void updateBannerPictureFileName(@RequestHeader("Authorization") String token,
                                                    @RequestParam(value = "banner-picture-file-name") String bannerPictureFileName) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateBannerPictureById(id, bannerPictureFileName);
    }

    /**
     * Updates the display name for the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param displayName the new display name (default: empty string if not provided)
     */
    @PostMapping("/account/displayName")
    public void updateDisplayNameById(@RequestHeader("Authorization") String token,
                                      @RequestParam(value = "displayName", defaultValue = "") String displayName) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateDisplayNameById(id, displayName);
    }

    /**
     * Updates the bio for the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param bio the new bio (default: empty string if not provided)
     */
    @PostMapping("/account/bio")
    public void updateBioById(@RequestHeader("Authorization") String token, @RequestParam(value = "bio", defaultValue = "") String bio) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateBioById(id, bio);
    }

    /**
     * Updates the occupation for the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param occupation the new occupation (default: empty string if not provided)
     */
    @PostMapping("/account/occupation")
    public void updateOccupationById(@RequestHeader("Authorization") String token,
                                     @RequestParam(value = "occupation", defaultValue = "") String occupation) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateOccupationById(id, occupation);
    }

    /**
     * Updates the location for the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param location the new location (default: empty string if not provided)
     */
    @PostMapping("/account/location")
    public void updateLocationById(@RequestHeader("Authorization") String token,
                                   @RequestParam(value = "location", defaultValue = "") String location) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateLocationById(id, location);
    }

    /**
     * Updates the birthdate for the currently logged-in user.
     *
     * @param token the authentication token (included in the 'Authorization' header)
     * @param birthdate the new birthdate (default: empty if not provided)
     * @note The birthdate is expected to be in a format that can be parsed by the {@link java.util.Date} class.
     */
    @PostMapping("/account/birthdate")
    public void updateBirthdateById(@RequestHeader("Authorization") String token,
                                    @RequestParam(value = "birthdate", defaultValue = "") Date birthdate) {
        long id = jwtUtil.checkJwtAuthAndGetUserId(token);
        userService.updateBirthdateById(id, birthdate);
    }

    // helper functions
    /**
     * Builds a detailed {@link UserDTO} object from the given {@link ApplicationUser} entity.
     *
     * This method creates a new {@link UserDTO} instance, populated with the user's detailed information.
     *
     * @param userDetails the {@link ApplicationUser} entity to extract information from
     * @return a {@link UserDTO} containing the user's detailed information
     * @see UserDTO
     * @see ApplicationUser
     */
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

    /**
     * Builds a simplified {@link UserDTO} object from the given {@link ApplicationUser} entity.
     *
     * This method creates a new {@link UserDTO} instance, populated with the user's basic information.
     *
     * @param user the {@link ApplicationUser} entity to extract information from
     * @return a simplified {@link UserDTO} containing the user's basic information
     * @see UserDTO
     * @see ApplicationUser
     */
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
