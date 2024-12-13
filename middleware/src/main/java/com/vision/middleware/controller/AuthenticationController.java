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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling authentication-related endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin("*") // todo: update this later
public class AuthenticationController {

    /**
     * Service layer dependency for authentication operations.
     */
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Registers a new user with the provided registration details.
     * RegistrationDTO must be valid, and the validation is checked at time of binding
     * when JSON is deserialized.
     *
     * @param body        Validated RegistrationDTO containing user registration details
     * @param bindingResult Binding result for validation errors
     * @return ResponseEntity with:
     *         - created UserDTO on successful registration (HTTP 200 OK)
     *         - error message on validation failure (HTTP 400 Bad Request)
     *         - error response on registration failure (HTTP 400 Bad Request)
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO body, BindingResult bindingResult) {
        // when binding input json -> RegistrationDTO as specified by @Valid, some errors can be thrown:
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


    /**
     * Converts a constraint violation exception to a human-readable error message.
     *
     * @param e the exception containing the error message
     * @return a string representing the error message, tailored for known constraint violations
     */
    private static String constraintErrorToMessage(Exception e) {
        // not the greatest way to handle this, an exceptionHandler would be better. But whatever.
        // another better way to do this would be to have the service class check for these things explicitly,
        // and then raise an error that contains a message specifying what went wrong.
        // This is something that
        String errorMessage = e.getMessage();
        StringBuilder errorBuilder = new StringBuilder();
        if (errorMessage.contains("Key (username)")) {
            errorBuilder.append("Username already exists\n");
        }
        if (errorMessage.contains("Key (email)")) {
            errorBuilder.append("Email already registered\n");
        }
        if (errorMessage.contains("Key (phone_number)")) {
            errorBuilder.append("Phone number already in use\n");
        }

        if (errorBuilder.isEmpty()) {
            // if there is no specific message we are looking for to convert, then just return whatever message was provided.
            errorBuilder.append(e.getMessage());
        }

        return errorBuilder.toString();
    }

    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param body LoginDTO containing the username and password
     * @return ResponseEntity with:
     *         - LoginResponseDTO on successful authentication (HTTP 200 OK)
     *         - null on authentication failure (HTTP 401 Unauthorized)
     */
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

    /**
     * Validates the provided authentication token.
     *
     * <p>Note: This endpoint is only accessible if the token is already validated by the SecurityConfig.</p>
     *
     * @param token the Authorization token to validate (included in the 'Authorization' header)
     * @return a simple "Token is valid" message (HTTP 200 OK) if the token is valid
     */
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        // This is only reached if the token is valid
        // because in SecurityConfig this endpoint only accepts USER or ADMIN
        return ResponseEntity.ok("Token is valid");
    }
}
