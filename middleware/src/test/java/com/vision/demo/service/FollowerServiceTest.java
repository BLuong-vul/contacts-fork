package com.vision.demo.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Role;
import com.vision.middleware.domain.relations.UserFollows;
import com.vision.middleware.repo.UserFollowsRepository;
import com.vision.middleware.service.FollowerService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowerServiceTest {

    @InjectMocks
    private FollowerService followerService;

    @Mock
    private UserFollowsRepository followsRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private Role userRole;

    private ApplicationUser user1;
    private ApplicationUser user2;
    private ApplicationUser user3;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setAuthority("USER");

        // Create user with userId 1
        user1 = ApplicationUser.builder()
                .id(1L)
                .username("user1")
                .fullName("a")
                .password("encodedPassword1")
                .authorities(new HashSet<>(Set.of(userRole)))
                .email("user1@example.com")
                .phoneNumber("1234567890")
                .address("123 Test St")
                .city("Test City")
                .state("Test State")
                .zipCode("12345")
                .country("Test Country")
                .followerCount(0)
                .build();

        // Create user with userId 2
        user2 = ApplicationUser.builder()
                .id(2L)
                .username("user2")
                .fullName("a")
                .password("encodedPassword2")
                .authorities(new HashSet<>(Set.of(userRole)))
                .email("user2@example.com")
                .phoneNumber("1234567890")
                .address("123 Test St")
                .city("Test City")
                .state("Test State")
                .zipCode("12345")
                .country("Test Country")
                .followerCount(0)
                .build();

        // Create user with userId 3
        user3 = ApplicationUser.builder()
                .id(3L)
                .username("user3")
                .fullName("a")
                .password("encodedPassword3")
                .authorities(new HashSet<>(Set.of(userRole)))
                .email("user3@example.com")
                .phoneNumber("1234567890")
                .address("123 Test St")
                .city("Test City")
                .state("Test State")
                .zipCode("12345")
                .country("Test Country")
                .followerCount(0)
                .build();
    }

    @Test
    void testUser1FollowsUser2() {
        when(userService.loadUserById(1L)).thenReturn(user1);
        when(userService.loadUserById(2L)).thenReturn(user2);
        // user 1 does not follow user 2 to begin with.
        when(followsRepository.findByFollowerAndFollowee(user1, user2)).thenReturn(Optional.empty());

        followerService.followUser(1L, 2L);

        // relation should be saved
        verify(followsRepository, times(1)).save(any(UserFollows.class));
    }

    @Test
    void testUser1FollowsUser2ButAlreadyFollowsThem() {
        when(userService.loadUserById(1L)).thenReturn(user1);
        when(userService.loadUserById(2L)).thenReturn(user2);
        // relation exists:
        when(followsRepository.findByFollowerAndFollowee(user1, user2))
                .thenReturn(Optional.of(new UserFollows()));

        followerService.followUser(1L, 2L);

        // relation exists already, so followsRepository's save() method
        // should not ever be run.
        verify(followsRepository, never()).save(any(UserFollows.class));
    }

    @Test
    void testUser1FollowsUser2And3() {
        when(userService.loadUserById(1L)).thenReturn(user1);
        when(userService.loadUserById(2L)).thenReturn(user2);
        when(userService.loadUserById(3L)).thenReturn(user3);

        // relation dne
        when(followsRepository.findByFollowerAndFollowee(user1, user2))
                .thenReturn(Optional.empty());
        when(followsRepository.findByFollowerAndFollowee(user1, user3))
                .thenReturn(Optional.empty());

        followerService.followUser(1L, 2L);
        followerService.followUser(1L, 3L);

        verify(followsRepository, times(2)).save(any(UserFollows.class));
    }

    @Test
    void testUser1UnfollowsUser2() {
        when(userService.loadUserById(1L)).thenReturn(user1);
        when(userService.loadUserById(2L)).thenReturn(user2);

        followerService.unfollowUser(1L, 2L);

        verify(followsRepository, times(1)).deleteByFollowerAndFollowee(user1, user2);
    }

    @Test
    void testGetByFollowing() {
        when(userService.loadUserById(1L)).thenReturn(user1);
        when(followsRepository.findByFollower(user1)).thenReturn(Collections.singletonList(new UserFollows()));

        List<UserFollows> result = followerService.getByFollowingUser(1L);

        assertThat(result).isNotEmpty();
        verify(followsRepository, times(1)).findByFollower(user1);
    }

    @Test
    void testGetByFollowee() {
        when(userService.loadUserById(1L)).thenReturn(user1);
        when(followsRepository.findByFollowee(user1)).thenReturn(Collections.singletonList(new UserFollows()));

        List<UserFollows> result = followerService.getByFolloweeUser(1L);

        verify(followsRepository, times(1)).findByFollowee(user1);
    }
}
