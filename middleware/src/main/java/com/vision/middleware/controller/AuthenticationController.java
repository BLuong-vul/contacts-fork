package com.vision.middleware.controller;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.dto.LoginDTO;
import com.vision.middleware.dto.LoginResponseDTO;
import com.vision.middleware.dto.RegistrationDTO;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin("*") // todo: update this later
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO body, BindingResult bindingResult) {
        // when binding input json -> RegistrationDTO, some errors can be thrown:
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(String.join("\n", errors));
        }

        // binding success, we can proceed.
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
            String errorMessage = constraintErrorToMessage(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    }

    private static String constraintErrorToMessage(Exception e) {
        // not the greatest way to handle this, an exceptionHandler would be better. But whatever.
        // todo: better type signature for this method
        String errorMessage = e.getMessage();
        StringBuilder errorBuilder = new StringBuilder();
        if (errorMessage.contains("username")) {
            errorBuilder.append("Username already exists\n");
        }
        if (errorMessage.contains("email")) {
            errorBuilder.append("Email already registered\n");
        }
        if (errorMessage.contains("phone_number")) {
            errorBuilder.append("Phone number already in use\n");
        }

        if (errorBuilder.isEmpty()) {
            // if there is no specific message to cast, then just return whatever message was provided.
            errorBuilder.append(e.getMessage());
        }

        return errorBuilder.toString();
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
