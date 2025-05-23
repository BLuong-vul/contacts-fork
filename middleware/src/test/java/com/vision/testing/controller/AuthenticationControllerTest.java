package com.vision.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.middleware.Application;
import com.vision.middleware.config.SecurityConfig;
import com.vision.middleware.controller.AuthenticationController;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.dto.LoginDTO;
import com.vision.middleware.dto.LoginResponseDTO;
import com.vision.middleware.dto.RegistrationDTO;
import com.vision.middleware.service.AuthenticationService;
import com.vision.middleware.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration
public class AuthenticationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;
    
    @Autowired
    private JwtUtil jwtUtil;

    private static RegistrationDTO validUserDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();

        validUserDTO = RegistrationDTO.builder()
                .username("testUser")
                .password("testPassword")
                .email("test@example.com")
                .city("somewhere")
                .state("ca")
                .country("nowhere")
                .zipCode("11111")
                .phoneNumber("1234567890")
                .fullName("test User")
                .address("place at place 111")
                .build();
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        // Arrange
        RegistrationDTO registrationDTO = validUserDTO;

        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        when(authenticationService.registerUser(any(RegistrationDTO.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testRegisterUser_Failure() throws Exception {
        // Arrange
        RegistrationDTO registrationDTO = validUserDTO;

        when(authenticationService.registerUser(any(RegistrationDTO.class))).thenThrow(new RuntimeException("Account creation failed."));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Account creation failed."));
    }

    @Test
    public void testRegisterUser_Failure_DuplicateUsername() throws Exception {
        // Arrange
        RegistrationDTO registrationDTO = validUserDTO;

        when(authenticationService.registerUser(any(RegistrationDTO.class))).thenThrow(new RuntimeException("Duplicate Key (username)."));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Username already exists\n"));
    }

    @Test
    public void testRegisterUser_Failure_DuplicateEmail() throws Exception {
        // Arrange
        RegistrationDTO registrationDTO = validUserDTO;

        when(authenticationService.registerUser(any(RegistrationDTO.class))).thenThrow(new RuntimeException("Duplicate Key (email)."));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Email already registered\n"));
    }

    @Test
    public void testRegisterUser_Failure_DuplicatePhoneNumber() throws Exception {
        // Arrange
        RegistrationDTO registrationDTO = validUserDTO;

        when(authenticationService.registerUser(any(RegistrationDTO.class))).thenThrow(new RuntimeException("Duplicate Key (phone_number)."));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Phone number already in use\n"));
    }

    @Test
    public void testRegisterUser_DTOBindFailure() throws Exception {
        // Create an invalid DTO with missing/invalid fields
        String invalidRegistrationJson = "{" +
                "\"username\": \"\"," +
                "\"password\": \"short\"," +
                "\"email\": \"invalid-email\"," +
                "\"phoneNumber\": \"123\"" +
                "}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRegistrationJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assert responseBody.contains("Username is required");
                    assert responseBody.contains("Password must be at least 8 characters");
                    assert responseBody.contains("Invalid email format");
                    assert responseBody.contains("Invalid phone number");
                });
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("testUser", "testPassword");

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setUsername("dummyResult");

        when(authenticationService.loginUser("testUser", "testPassword")).thenReturn(loginResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("testUser", "testPassword");

        when(authenticationService.loginUser("testUser", "testPassword")).thenThrow(new AuthenticationException("Login failed.") {});

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string(emptyOrNullString()));
    }

    @Test
    public void testValidTokenWithUserRole() throws Exception {
        // Setup authentication with USER role
        setupAuthentication("USER");

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/validate")
                        .header("Authorization", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Token is valid"));
    }

    // Utility method to set up authentication with a specific role
    private void setupAuthentication(String role) {
        List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", "password", authorities);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }
}