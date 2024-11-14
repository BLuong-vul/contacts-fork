package com.vision.middleware.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.vision.middleware.exceptions.InvalidFileException;
import com.vision.middleware.exceptions.MediaNotFoundException;
import com.vision.middleware.exceptions.MediaUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MediaService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/png",
            "image/jpeg",
            "image/gif",
            "video/mp4"
    );

    public String uploadMedia(MultipartFile file) {
        validateFile(file);

        try {
            String fileName = generateFileName(file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);

            return fileName;

        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new MediaUploadException("Failed to upload media file", e);
        }
    }

    public S3Object getMedia(String fileName) {
        try {
            return s3Client.getObject(bucketName, fileName);
        } catch (AmazonS3Exception e) {
            log.error("Error retrieving file from S3", e);
            throw new MediaNotFoundException("Media file not found: " + fileName);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Invalid file type. Allowed types: PNG, JPEG, GIF, MP4");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds 10MB size limit");
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
}