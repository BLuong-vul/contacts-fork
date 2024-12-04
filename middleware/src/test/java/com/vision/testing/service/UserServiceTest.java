package com.vision.testing.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.exceptions.IdNotFoundException;
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

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
}