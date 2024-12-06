package com.vision.middleware.controller;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.dto.LoginDTO;
import com.vision.middleware.dto.LoginResponseDTO;
import com.vision.middleware.dto.RegistrationDTO;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin("*") // todo: update this later
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO body) {
        try {
            ApplicationUser user = authenticationService.registerUser(body);
            UserDTO dto = UserDTO.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .address(user.getAddress())
                    .country(user.getCountry())
                    .state(user.getState())
                    .phoneNumber(user.getPhoneNumber())
                    .build();

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account creation failed.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginDTO body) {
        try {
            LoginResponseDTO user = authenticationService.loginUser(body.getUsername(), body.getPassword());
            return ResponseEntity.ok(user);
        } catch (AuthenticationException e) {
            log.error("Failed to login user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        // This is only reached if the token is valid
        // because in SecurityConfig this endpoint only accepts USER or ADMIN
        return ResponseEntity.ok("Token is valid");
    }
}
