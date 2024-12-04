package com.vision.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.vision.middleware.exceptions.InvalidFileException;
import com.vision.middleware.exceptions.MediaNotFoundException;
import com.vision.middleware.exceptions.MediaUploadException;
import com.vision.middleware.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MediaServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        // Set bucket name via reflection for testing
        ReflectionTestUtils.setField(mediaService, "bucketName", "test-bucket");
    }

    private MultipartFile createMockMultipartFile(String contentType, long size, String filename) throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getSize()).thenReturn(size);
        when(mockFile.getOriginalFilename()).thenReturn(filename);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        return mockFile;
    }

    @Test
    void uploadMedia_ValidFile_ShouldUploadSuccessfully() throws IOException {
        // Arrange
        MultipartFile mockFile = createMockMultipartFile("image/png", 5L * 1024 * 1024, "test-image.png");
        when(s3Client.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class))).thenReturn(null);

        // Act
        String fileName = mediaService.uploadMedia(mockFile);

        // Assert
        assertThat(fileName).isNotNull();
        assertThat(fileName).endsWith(".png");
        verify(s3Client).putObject(
                eq("test-bucket"),
                argThat(name -> name.matches("[0-9a-f-]+\\.png")),
                any(InputStream.class),
                any(ObjectMetadata.class)
        );
    }

    @Test
    void uploadMedia_EmptyFile_ShouldThrowInvalidFileException() throws IOException {
        // Arrange
        MultipartFile mockFile = createMockMultipartFile("image/png", 5L * 1024 * 1024, "test-image.png");
        when(mockFile.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> mediaService.uploadMedia(mockFile))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("File is empty");
    }

    @Test
    void uploadMedia_InvalidContentType_ShouldThrowInvalidFileException() throws IOException {
        // Arrange
        MultipartFile mockFile = createMockMultipartFile("application/pdf", 5L * 1024 * 1024, "test-file.pdf");

        // Act & Assert
        assertThatThrownBy(() -> mediaService.uploadMedia(mockFile))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("Invalid file type. Allowed types: PNG, JPEG, GIF, MP4");
    }

    @Test
    void uploadMedia_FileTooLarge_ShouldThrowInvalidFileException() throws IOException {
        // Arrange
        MultipartFile mockFile = createMockMultipartFile("image/png", 15L * 1024 * 1024, "large-image.png");

        // Act & Assert
        assertThatThrownBy(() -> mediaService.uploadMedia(mockFile))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("File size exceeds 10MB size limit");
    }

    @Test
    void uploadMedia_IOExceptionDuringUpload_ShouldThrowMediaUploadException() throws IOException {
        // Arrange
        MultipartFile mockFile = createMockMultipartFile("image/png", 5L * 1024 * 1024, "test-image.png");
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        // Act & Assert
        assertThatThrownBy(() -> mediaService.uploadMedia(mockFile))
                .isInstanceOf(MediaUploadException.class)
                .hasMessageContaining("Failed to upload media file");
    }

    @Test
    void getMedia_ExistingFile_ShouldReturnS3Object() {
        // Arrange
        S3Object mockS3Object = mock(S3Object.class);
        when(s3Client.getObject("test-bucket", "test-file.png")).thenReturn(mockS3Object);

        // Act
        S3Object result = mediaService.getMedia("test-file.png");

        // Assert
        assertThat(result).isEqualTo(mockS3Object);
        verify(s3Client).getObject("test-bucket", "test-file.png");
    }

    @Test
    void getMedia_NonExistingFile_ShouldThrowMediaNotFoundException() {
        // Arrange
        when(s3Client.getObject("test-bucket", "non-existing.png"))
                .thenThrow(new AmazonS3Exception("File not found"));

        // Act & Assert
        assertThatThrownBy(() -> mediaService.getMedia("non-existing.png"))
                .isInstanceOf(MediaNotFoundException.class)
                .hasMessageContaining("Media file not found: non-existing.png");
    }
}