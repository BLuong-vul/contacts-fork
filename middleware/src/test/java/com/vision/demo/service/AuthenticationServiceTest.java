package com.vision.demo.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Role;
import com.vision.middleware.dto.LoginResponseDTO;
import com.vision.middleware.dto.RegistrationDTO;
import com.vision.middleware.repo.RoleRepository;
import com.vision.middleware.repo.UserRepository;
import com.vision.middleware.service.AuthenticationService;
import com.vision.middleware.service.TokenService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    private RegistrationDTO registrationDTO;
    private Role userRole;

    @BeforeEach
    public void setUp() {
        registrationDTO = RegistrationDTO.builder()
                .username("testUser")
                .password("testPassword")
                .fullName("a")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .address("123 Test St")
                .city("Test City")
                .state("Test State")
                .zipCode("12345")
                .country("Test Country")
                .build();

        userRole = new Role();
        userRole.setAuthority("USER");
    }

    @Test
    public void testRegisterUserSuccess() throws ConstraintViolationException {
        ApplicationUser savedUser = ApplicationUser.builder()
                .id(1L)
                .username("testUser")
                .password("encodedPassword")
                .fullName("a")
                .authorities(new HashSet<>(Set.of(userRole)))
                .email("test@example.com")
                .phoneNumber("1234567890")
                .address("123 Test St")
                .city("Test City")
                .state("Test State")
                .zipCode("12345")
                .country("Test Country")
                .followerCount(0)
                .build();

        when(roleRepository.findByAuthority("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(ApplicationUser.class))).thenReturn(savedUser);

        ApplicationUser registeredUser = authenticationService.registerUser(registrationDTO);

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getUsername()).isEqualTo("testUser");
        assertThat(registeredUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(registeredUser.getAuthorities().size()).isEqualTo(1);
        assertTrue(registeredUser.getAuthorities().contains(userRole));
    }

    @Test
    public void testRegisterUserRoleNotFound() {
        when(roleRepository.findByAuthority("USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.registerUser(registrationDTO))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testLoginUserSuccess() throws AuthenticationException {
        String username = "testUser";
        String password = "testPassword";

        ApplicationUser user = ApplicationUser.builder()
                .id(1L)
                .username(username)
                .password("encodedPassword")
                .fullName("a")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .address("123 Test St")
                .city("Test City")
                .state("Test State")
                .zipCode("12345")
                .country("Test Country")
                .followerCount(0)
                .authorities(new HashSet<>(Set.of(userRole)))
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(tokenService.generateJwt(any(Authentication.class), eq(1L))).thenReturn("testToken");

        LoginResponseDTO loginResponseDTO = authenticationService.loginUser(username, password);

        assertThat(loginResponseDTO).isNotNull();
        assertThat(loginResponseDTO.getUsername()).isEqualTo(username);
        assertThat(loginResponseDTO.getUserId()).isEqualTo(1L);
        assertThat(loginResponseDTO.getRoles().size()).isEqualTo(1);
        assertTrue(loginResponseDTO.getRoles().contains(userRole));
        assertThat(loginResponseDTO.getJwt()).isEqualTo("testToken");
    }

    @Test
    public void testLoginUserNotFound() {
        String username = "testUser";
        String password = "testPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.loginUser(username, password))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    public void testLoginUserBadCredentials() {
        String username = "testUser";
        String password = "wrongPassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authenticationService.loginUser(username, password))
                .isInstanceOf(BadCredentialsException.class);
    }
}