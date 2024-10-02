package com.vision.middleware.exceptions;

import org.springframework.security.core.AuthenticationException;

public class IdNotFoundException extends AuthenticationException {
    public IdNotFoundException(String message) {
        super(message);
    }
}
