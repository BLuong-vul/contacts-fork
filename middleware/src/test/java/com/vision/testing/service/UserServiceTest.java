package com.vision.testing.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.relations.UserFollows;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.UserFollowsRepository;
import com.vision.middleware.repo.UserRepository;
import com.vision.middleware.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserFollowsRepository userFollowsRepository;

    @InjectMocks
    private UserService userService;

    private ApplicationUser testUser;

    @BeforeEach
    public void setUp() {
        testUser = new ApplicationUser();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        ApplicationUser foundUser = userService.loadUserByUsername("testUser");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("nonExistentUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("user nonExistentUser not found");

        verify(userRepository, times(1)).findByUsername("nonExistentUser");
    }

    @Test
    public void testLoadUserById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ApplicationUser foundUser = userService.loadUserById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testLoadUserById_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserById(2L))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessage("id 2 not found");

        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    public void testGetAuthorities() {
        Collection<GrantedAuthority> authorities = userService.getAuthorities("ROLE_USER");

        assertThat(authorities).isNotNull();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }


    @Test
    public void testGetFollowerCount() {
        // Prepare test user
        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        user.setUsername("testUser");

        // Create mock list of UserFollows
        Set<UserFollows> followers = new HashSet<>(Arrays.asList(
                new UserFollows(),
                new UserFollows()
        ));
        user.setFollowers(followers);

        // Test the getFollowerCount method
        int followerCount = userService.getFollowerCount(user);

        assertThat(followerCount).isEqualTo(2);
    }

    @Test
    public void testGetFollowingCount() {
        // Prepare test user
        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        user.setUsername("testUser");

        // Create mock list of UserFollows
        Set<UserFollows> following = new HashSet<>(Arrays.asList(
                new UserFollows(),
                new UserFollows(),
                new UserFollows()
        ));
        user.setFollowing(following);

        // Test the getFollowingCount method
        int followingCount = userService.getFollowingCount(user);

        assertThat(followingCount).isEqualTo(3);
    }

    @Test
    public void testUpdateDisplayNameById() {
        Long userId = 1L;
        String newDisplayName = "New Display Name";

        // Call the method
        userService.updateDisplayNameById(userId, newDisplayName);

        // Verify that the repository method was called with correct parameters
        verify(userRepository, times(1)).updateDisplayNameById(userId, newDisplayName);
    }

    @Test
    public void testUpdateBioById() {
        Long userId = 1L;
        String newBio = "Updated personal bio";

        // Call the method
        userService.updateBioById(userId, newBio);

        // Verify that the repository method was called with correct parameters
        verify(userRepository, times(1)).updateBioById(userId, newBio);
    }

    @Test
    public void testUpdateOccupationById() {
        Long userId = 1L;
        String newOccupation = "Software Engineer";

        // Call the method
        userService.updateOccupationById(userId, newOccupation);

        // Verify that the repository method was called with correct parameters
        verify(userRepository, times(1)).updateOccupationById(userId, newOccupation);
    }

    @Test
    public void testUpdateLocationById() {
        Long userId = 1L;
        String newLocation = "San Francisco, CA";

        // Call the method
        userService.updateLocationById(userId, newLocation);

        // Verify that the repository method was called with correct parameters
        verify(userRepository, times(1)).updateLocationById(userId, newLocation);
    }

    @Test
    public void testUpdateBirthdateById() {
        long userId = 1L;
        Date newBirthdate = new Date();

        // Call the method
        userService.updateBirthdateById(userId, newBirthdate);

        // Verify that the repository method was called with correct parameters
        verify(userRepository, times(1)).updateBirthdateById(userId, newBirthdate);
    }

    @Test
    public void testSearchUsers() {
        // Prepare test data
        String searchQuery = "john";
        List<ApplicationUser> mockUsers = new ArrayList<>();
        ApplicationUser user1 = new ApplicationUser();
        user1.setUsername("johndoe");
        ApplicationUser user2 = new ApplicationUser();
        user2.setUsername("johnsmith");
        mockUsers.add(user1);
        mockUsers.add(user2);

        // Mock the repository method
        when(userRepository.findAllByUsernameContainingIgnoreCase(searchQuery))
                .thenReturn(mockUsers);

        // Call the method
        List<ApplicationUser> foundUsers = userService.searchUsers(searchQuery);

        // Verify results
        assertThat(foundUsers).isNotNull();
        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers.get(0).getUsername()).isEqualTo("johndoe");
        assertThat(foundUsers.get(1).getUsername()).isEqualTo("johnsmith");

        // Verify repository method was called
        verify(userRepository, times(1)).findAllByUsernameContainingIgnoreCase(searchQuery);
    }


    @Test
    public void testUpdateProfilePictureById() {
        long userId = 1L;
        String newProfilePictureFileName = "newProfilePicture.jpg";

        // Call the method
        userService.updateProfilePictureById(userId, newProfilePictureFileName);

        // Verify that the repository method was called with correct parameters
        verify(userRepository, times(1)).updateProfilePictureFileNameById(userId, newProfilePictureFileName);
    }

    @Test
    public void testUpdateBannerPictureById() {
        long userId = 1L;
        String newBannerPictureFileName = "newBannerPicture.jpg";

        // Call the method
        userService.updateBannerPictureById(userId, newBannerPictureFileName);

        // Verify that the repository method was called with correct parameters
        verify(userRepository, times(1)).updateBannerPictureFileNameById(userId, newBannerPictureFileName);
    }
}