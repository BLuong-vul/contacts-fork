package com.vision.middleware.exceptions;

public class MediaUploadException extends RuntimeException {
    public MediaUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}