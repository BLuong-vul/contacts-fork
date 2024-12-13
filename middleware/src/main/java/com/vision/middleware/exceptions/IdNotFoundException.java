package com.vision.middleware.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception to represent scenarios where an ID is not found during the authentication process.
 */
public class IdNotFoundException extends AuthenticationException {

    /**
     * Constructs a new {@code IdNotFoundException} with the specified detail message.
     *
     * @param message the detail message
     */
    public IdNotFoundException(String message) {
        super(message);
    }
}
