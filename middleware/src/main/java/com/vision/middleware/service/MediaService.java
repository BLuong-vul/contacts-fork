package com.vision.middleware.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.vision.middleware.exceptions.InvalidFileException;
import com.vision.middleware.exceptions.MediaNotFoundException;
import com.vision.middleware.exceptions.MediaUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service class responsible for handling media file operations, including uploading and retrieving files from Amazon S3.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MediaService {

    /**
     * Name of the Amazon S3 bucket used for storing media files.
     */
    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * Autowired Amazon S3 client for interacting with S3 services.
     */
    @Autowired
    private final AmazonS3 s3Client;

    /**
     * Maximum allowed file size in bytes (10 MB).
     */
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    /**
     * List of allowed content types for uploaded media files.
     */
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/png",
            "image/jpeg",
            "image/gif",
            "video/mp4"
    );

    /**
     * Uploads a media file to Amazon S3.
     *
     * @param file the media file to be uploaded (MultipartFile)
     * @return the generated filename of the uploaded file in S3
     * @throws MediaUploadException if the upload process fails
     */
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

    /**
     * Retrieves a media file from Amazon S3 by its filename.
     *
     * @param fileName the name of the file to retrieve from S3
     * @return the retrieved S3Object
     * @throws MediaNotFoundException if the file is not found in S3
     */
    public S3Object getMedia(String fileName) {
        try {
            return s3Client.getObject(bucketName, fileName);
        } catch (AmazonS3Exception e) {
            log.error("Error retrieving file from S3", e);
            throw new MediaNotFoundException("Media file not found: " + fileName);
        }
    }

    /**
     * Validates the provided media file based on size, content type, and emptiness.
     *
     * @param file the media file to be validated (MultipartFile)
     * @throws InvalidFileException if the file is invalid (empty, wrong type, or exceeds size limit)
     */
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

    /**
     * Generates a unique filename for the given media file by appending the original file extension.
     *
     * @param file the media file for which to generate a filename (MultipartFile)
     * @return the generated filename (UUID + original file extension)
     */
    private String generateFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
}
