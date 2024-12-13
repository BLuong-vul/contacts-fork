package com.vision.middleware.exceptions;

/**
 * Custom runtime exception class for handling media upload related errors.
 */
public class MediaUploadException extends RuntimeException {

    /**
     * Constructs a new {@code MediaUploadException} with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public MediaUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
