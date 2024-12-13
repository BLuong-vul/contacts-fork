package com.vision.middleware.exceptions;

/**
 * Exception thrown when a voter attempts to cast a vote that has already been submitted.
 */
public class DuplicateVoteException extends RuntimeException {

    /**
     * Constructs a new DuplicateVoteException with the specified detail message.
     *
     * @param message a detailed message describing the cause of the exception
     */
    public DuplicateVoteException(String message) {
        super(message);
    }
}
