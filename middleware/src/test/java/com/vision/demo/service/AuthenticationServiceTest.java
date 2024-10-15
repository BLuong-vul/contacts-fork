package com.vision.demo.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Role;
import com.vision.middleware.dto.LoginResponseDTO;
import com.vision.middleware.dto.RegistrationDTO;
import com.vision.middleware.repo.RoleRepository;
import com.vision.middleware.repo.UserRepository;
import com.vision.middleware.service.AuthenticationService;
import com.vision.middleware.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    // define classes that should be mocked
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

    // using good ol' constructor injection, we build an instance of AuthenticationService
    // containing our mocked dependencies.
    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        // actions to be run before each test
    }

    @Test
    public void testRegisterUser() throws Exception {
        // Setup
        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .build();

        Role role = new Role();
        role.setAuthority("USER");

        ApplicationUser savedUser = ApplicationUser.builder()
                .username("testuser")
                .password("encodedpassword")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .build();

        // configure mocked behaviors
        when(roleRepository.findByAuthority("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(ApplicationUser.class))).thenReturn(savedUser);

        // Execute
        ApplicationUser result = authenticationService.registerUser(registrationDTO);

        // Verify
        assertEquals(savedUser.getUsername(), result.getUsername());
        assertEquals(savedUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(ApplicationUser.class));
    }

    @Test
    public void testLoginUser() throws Exception {
        // Setup
        String username = "testuser";
        String password = "password";
        ApplicationUser user = ApplicationUser.builder()
                .username(username)
                .password(password)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tokenService.generateJwt(any(Authentication.class), anyLong())).thenReturn("dummyToken");

        // Execute
        LoginResponseDTO response = authenticationService.loginUser(username, password);

        // Verify
        assertEquals(username, response.getUsername());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateJwt(any(Authentication.class), anyLong());
    }

}
