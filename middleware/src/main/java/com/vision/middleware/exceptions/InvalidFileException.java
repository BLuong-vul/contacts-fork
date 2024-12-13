package com.vision.middleware.exceptions;

/**
 * Exception thrown when a file is invalid, either due to its format, contents, or other file-related issues.
 */
public class InvalidFileException extends RuntimeException {

    /**
     * Constructs an <code>InvalidFileException</code> with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public InvalidFileException(String message) {
        super(message);
    }
}
