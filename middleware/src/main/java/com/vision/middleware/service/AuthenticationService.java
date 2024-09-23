package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Role;
import com.vision.middleware.dto.LoginResponseDTO;
import com.vision.middleware.repo.RoleRepository;
import com.vision.middleware.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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

@AllArgsConstructor
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class) // <- each method call is treated as a single transaction.
public class AuthenticationService {

    private final UserRepository userRepository; // users should be allowed to search for themselves
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    // todo: you do not want to be sending over a password ike this
    public ApplicationUser registerUser(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        Role userRole;
        try {
            userRole = roleRepository.findByAuthority("USER").orElseThrow(
                    () -> new RoleNotFoundException("USER role not found (should not happen)")
            );
        } catch (RoleNotFoundException e) {
            // this shouldn't happen: USER role should be created at program start.
            throw new RuntimeException(e);
        }

        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        ApplicationUser newUser = ApplicationUser.builder()
                .username(username)
                .password(encodedPassword)
                .authorities(authorities)
                .build();

        return userRepository.save(newUser);
    }

    public LoginResponseDTO loginUser(String username, String password) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(auth);

            ApplicationUser user =  userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("user not found"));

            return LoginResponseDTO.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(user.getAuthorities())
                    .jwt(token)
                    .build();

        } catch (AuthenticationException e) {
            // todo: throw custom exception here, make sure response is a 401
            return new LoginResponseDTO();
        }

    }
}
