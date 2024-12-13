package com.vision.middleware.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a response containing a presigned URL and the file name.
 */
@Data
@AllArgsConstructor
public class PresignedUrlResponse {
    private String url;
    private String fileName;
}
