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

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

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
