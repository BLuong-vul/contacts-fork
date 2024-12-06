package com.vision.testing.controller;

import com.vision.middleware.controller.UserController;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.relations.UserFollows;
import com.vision.middleware.service.FollowerService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private FollowerService followerService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testHelloUserController() throws Exception {
        mockMvc.perform(get("/user/"))
                .andExpect(status().isOk())
                .andExpect(content().string("User access level"));
    }

    @Test
    public void testGetUserInfo() throws Exception {
        String token = "validToken";
        long userId = 1L;
        ApplicationUser user = ApplicationUser.builder()
                .id(userId)
                .username("testUser")
                .password("password")
                .fullName("Test User")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .build();

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);
        when(userService.loadUserById(userId)).thenReturn(user);
        when(userService.getFollowerCount(user)).thenReturn(5);
        when(userService.getFollowingCount(user)).thenReturn(10);

        mockMvc.perform(get("/user/info")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.followerCount").value(5))
                .andExpect(jsonPath("$.followingCount").value(10));
    }

    @Test
    public void testGetPublicInfoByUsername() throws Exception {
        String username = "testUser";
        ApplicationUser user = ApplicationUser.builder()
                .id(1L)
                .username(username)
                .password("password")
                .fullName("Test User")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .build();

        when(userService.loadUserByUsername(username)).thenReturn(user);
        when(userService.getFollowerCount(user)).thenReturn(5);
        when(userService.getFollowingCount(user)).thenReturn(10);

        mockMvc.perform(get("/user/public-info")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.followerCount").value(5))
                .andExpect(jsonPath("$.followingCount").value(10));
    }

    @Test
    public void testGetIdByUsername() throws Exception {
        String username = "testUser";
        ApplicationUser user = ApplicationUser.builder()
                .id(1L)
                .username(username)
                .password("password")
                .fullName("Test User")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .build();

        when(userService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/user/id/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void testGetFollowing() throws Exception {
        String token = "validToken";
        long userId = 1L;
        ApplicationUser user0 = ApplicationUser.builder()
                .id(userId)
                .username("user0")
                .password("password")
                .fullName("User Zero")
                .email("user0@example.com")
                .phoneNumber("1234567890")
                .build();
        ApplicationUser user1 = ApplicationUser.builder()
                .id(2L)
                .username("user1")
                .password("password")
                .fullName("User One")
                .email("user1@example.com")
                .phoneNumber("1234567890")
                .build();
        ApplicationUser user2 = ApplicationUser.builder()
                .id(3L)
                .username("user2")
                .password("password")
                .fullName("User Two")
                .email("user2@example.com")
                .phoneNumber("1234567890")
                .build();

        List<UserFollows> userFollowsList = Arrays.asList(
                UserFollows.builder()
                        .id(1L)
                        .follower(user0)
                        .followee(user1)
                        .build(),
                UserFollows.builder()
                        .id(2L)
                        .follower(user0)
                        .followee(user2)
                        .build()
        );

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);
        when(followerService.getByFollowingUser(userId)).thenReturn(userFollowsList);

        mockMvc.perform(get("/user/following/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2L))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].userId").value(3L))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    public void testGetFollowers() throws Exception {
        String token = "validToken";
        long userId = 1L;
        ApplicationUser user0 = ApplicationUser.builder()
                .id(userId)
                .username("user0")
                .password("password")
                .fullName("User Zero")
                .email("user0@example.com")
                .phoneNumber("1234567890")
                .build();
        ApplicationUser user1 = ApplicationUser.builder()
                .id(2L)
                .username("user1")
                .password("password")
                .fullName("User One")
                .email("user1@example.com")
                .phoneNumber("1234567890")
                .build();
        ApplicationUser user2 = ApplicationUser.builder()
                .id(3L)
                .username("user2")
                .password("password")
                .fullName("User Two")
                .email("user2@example.com")
                .phoneNumber("1234567890")
                .build();

        List<UserFollows> userFollowersRelation = Arrays.asList(
                UserFollows.builder()
                        .id(1L)
                        .follower(user1)
                        .followee(user0)
                        .build(),
                UserFollows.builder()
                        .id(2L)
                        .follower(user2)
                        .followee(user0)
                        .build()
        );

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);
        when(followerService.getByFolloweeUser(userId)).thenReturn(userFollowersRelation);

        mockMvc.perform(get("/user/followers/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2L))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].userId").value(3L))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    public void testFollowUser() throws Exception {
        String token = "validToken";
        long followerId = 1L;
        long followeeId = 2L;

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(followerId);

        mockMvc.perform(post("/user/follow/{followeeId}", followeeId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("User 2 followed"));
    }

    @Test
    public void testUnfollowUser() throws Exception {
        String token = "validToken";
        long followerId = 1L;
        long followeeId = 2L;

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(followerId);

        mockMvc.perform(post("/user/unfollow/{followeeId}", followeeId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("User 2 unfollowed"));
    }

    @Test
    public void testSearchUsers_ValidQuery() throws Exception {
        String query = "test";
        ApplicationUser user1 = ApplicationUser.builder()
                .id(1L)
                .username("testUser1")
                .password("password")
                .fullName("Test User One")
                .email("test1@example.com")
                .phoneNumber("1234567890")
                .build();
        ApplicationUser user2 = ApplicationUser.builder()
                .id(2L)
                .username("testUser2")
                .password("password")
                .fullName("Test User Two")
                .email("test2@example.com")
                .phoneNumber("1234567890")
                .build();

        List<ApplicationUser> users = Arrays.asList(user1, user2);

        when(userService.searchUsers(query)).thenReturn(users);

        mockMvc.perform(get("/user/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].username").value("testUser1"))
                .andExpect(jsonPath("$[1].userId").value(2L))
                .andExpect(jsonPath("$[1].username").value("testUser2"));
    }

    @Test
    public void testSearchUsers_InvalidQuery() throws Exception {
        String query = "";

        mockMvc.perform(get("/user/search")
                        .param("query", query))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateDisplayNameById() throws Exception {
        String token = "validToken";
        long userId = 1L;
        String displayName = "newDisplayName";

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);

        mockMvc.perform(post("/user/account/displayName")
                        .header("Authorization", token)
                        .param("displayName", displayName))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateBioById() throws Exception {
        String token = "validToken";
        long userId = 1L;
        String bio = "newBio";

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);

        mockMvc.perform(post("/user/account/bio")
                        .header("Authorization", token)
                        .param("bio", bio))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateOccupationById() throws Exception {
        String token = "validToken";
        long userId = 1L;
        String occupation = "newOccupation";

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);

        mockMvc.perform(post("/user/account/occupation")
                        .header("Authorization", token)
                        .param("occupation", occupation))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateLocationById() throws Exception {
        String token = "validToken";
        long userId = 1L;
        String location = "newLocation";

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);

        mockMvc.perform(post("/user/account/location")
                        .header("Authorization", token)
                        .param("location", location))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateBirthdateById() throws Exception {
        String token = "validToken";
        long userId = 1L;
        Date birthdate = new Date();

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);

        mockMvc.perform(post("/user/account/birthdate")
                        .header("Authorization", token)
                        .param("birthdate", birthdate.toString()))
                .andExpect(status().isOk());
    }
}