package com.vision.middleware.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception thrown when an invalid token is encountered during the authentication process.
 */
public class InvalidTokenException extends AuthenticationException {

    /**
     * Constructs a new {@code InvalidTokenException} with the specified detail message.
     *
     * @param message the detailed message describing the invalid token error
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}
