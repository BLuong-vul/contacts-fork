package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Role;
import com.vision.middleware.dto.LoginDTO;
import com.vision.middleware.dto.LoginResponseDTO;
import com.vision.middleware.dto.RegistrationDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.RoleRepository;
import com.vision.middleware.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.Set;

/**
 * Service class responsible for handling user authentication-related operations,
 * including user registration and login. Each method call is treated as a single transaction.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class AuthenticationService {

    /**
     * Repository for managing user data.
     */
    private final UserRepository userRepository;

    /**
     * Repository for managing user roles.
     */
    private final RoleRepository roleRepository;

    /**
     * Service for encoding and decoding passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Manager for handling authentication processes.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Service for generating and managing security tokens.
     */
    private final TokenService tokenService;

    /**
     * Registers a new user with the provided details.
     *
     * @param user Registration data for the new user.
     * @return The newly created user entity.
     * @throws ConstraintViolationException if the registration data violates any constraints.
     */
    public ApplicationUser registerUser(RegistrationDTO user) throws ConstraintViolationException {
        final String encodedPassword = passwordEncoder.encode(user.getPassword());

        final Role userRole;
        try {
            userRole = roleRepository.findByAuthority("USER").orElseThrow(
                    () -> new RoleNotFoundException("USER role not found (should not happen)")
            );
        } catch (RoleNotFoundException e) {
            // this shouldn't happen: USER role should be created at program start.
            throw new RuntimeException(e);
        }

        final Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        final ApplicationUser newUser = ApplicationUser.builder()
                .username(user.getUsername())
                .password(encodedPassword)
                .authorities(authorities)
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZipCode())
                .country(user.getCountry())
                .fullName(user.getFullName())
                .followerCount(0)
                .build();

        return userRepository.save(newUser);
    }

    /**
     * Authenticates a user based on the provided credentials and returns a login response
     * containing user details and a generated JWT token.
     *
     * @param username Username of the user to authenticate.
     * @param password Password of the user to authenticate.
     * @return Login response containing user details and a JWT token.
     * @throws AuthenticationException if the authentication fails.
     */
    public LoginResponseDTO loginUser(String username, String password) throws AuthenticationException {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        ApplicationUser user =  userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        long userId = user.getId();
        String token = tokenService.generateJwt(auth, userId);

        return LoginResponseDTO.builder()
                .username(user.getUsername())
                .userId(user.getId())
                .roles(user.getAuthorities())
                .jwt(token)
                .build();
    }
}
