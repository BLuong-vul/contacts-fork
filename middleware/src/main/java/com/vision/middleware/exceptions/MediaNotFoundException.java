package com.vision.middleware.exceptions;

/**
 * Custom runtime exception thrown when a requested media resource is not found.
 */
public class MediaNotFoundException extends RuntimeException {

    /**
     * Constructs a new MediaNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public MediaNotFoundException(String message) {
        super(message);
    }
}
