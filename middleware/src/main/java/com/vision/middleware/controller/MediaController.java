package com.vision.middleware.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.vision.middleware.exceptions.InvalidFileException;
import com.vision.middleware.exceptions.MediaNotFoundException;
import com.vision.middleware.exceptions.MediaUploadException;
import com.vision.middleware.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing media files.
 * Provides endpoints for uploading and retrieving media files stored in Amazon S3.
 */
@RestController
@RequestMapping("/media")
@CrossOrigin("*")
public class MediaController {

    /**
     * Service layer dependency for media operations.
     */
    @Autowired
    private MediaService mediaService;

    /**
     * Uploads a media file to Amazon S3. Files are granted a new unique name when uploaded.
     *
     * @param file the media file to be uploaded (max 10MB, allowed types: PNG, JPEG, GIF, MP4)
     * @return the uploaded file name with HTTP 200 (OK) status if successful
     * @return HTTP 400 (Bad Request) if the file is invalid (empty, wrong type, or exceeds size limit)
     * @return HTTP 500 (Internal Server Error) if the upload fails
     */
    // todo: improve this: refer to other class for constraints on file upload
    @PostMapping("/upload")
    public ResponseEntity<String> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = mediaService.uploadMedia(file);
            return ResponseEntity.ok(fileName);
        } catch (InvalidFileException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (MediaUploadException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Uploads a media file to Amazon S3.
     * <b>File Upload Constraints:</b> Refer to {@link MediaService#uploadMedia(MultipartFile)} for allowed file types, size limits, and other upload constraints.
     * <br/>
     * <i>Note:</i> Uploaded files are assigned a new unique name.
     *
     * @param file the media file to be uploaded
     * @return the newly generated file name with HTTP 200 (OK) status if successful
     * @return HTTP 400 (Bad Request) if the file is invalid
     * @return HTTP 500 (Internal Server Error) if the upload fails
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<InputStreamResource> getMedia(@PathVariable String fileName) {
        try {
            S3Object s3Object = mediaService.getMedia(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()));
            headers.setContentLength(s3Object.getObjectMetadata().getContentLength());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(s3Object.getObjectContent()));
        } catch (MediaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
