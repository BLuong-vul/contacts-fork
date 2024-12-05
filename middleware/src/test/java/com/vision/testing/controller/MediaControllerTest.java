package com.vision.testing.controller;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.vision.middleware.controller.MediaController;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController).build();
    }

    @Test
    void uploadMedia_ValidFile_ReturnsFileName() {
        // Arrange
        String expectedFileName = "sample-image.png";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sample-image.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5} // Minimal PNG byte content
        );

        when(mediaService.uploadMedia(any())).thenReturn(expectedFileName);

        // Act & Assert
        ResponseEntity<String> response = mediaController.uploadMedia(mockFile);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedFileName);
        verify(mediaService).uploadMedia(mockFile);
    }

    @Test
    void getMedia_ExistingFile_ReturnsCorrectResponse() throws IOException {
        // Arrange
        String fileName = "sample-image.png";
        S3Object mockS3Object = mock(S3Object.class);
        ObjectMetadata mockMetadata = new ObjectMetadata();
        mockMetadata.setContentType("image/png");
        mockMetadata.setContentLength(100L);

        byte[] fileContent = new byte[]{1, 2, 3, 4, 5}; // Minimal PNG byte content
        S3ObjectInputStream mockInputStream = new S3ObjectInputStream(
                new ByteArrayInputStream(fileContent),
                null
        );

        when(mockS3Object.getObjectMetadata()).thenReturn(mockMetadata);
        when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);

        when(mediaService.getMedia(fileName)).thenReturn(mockS3Object);

        // Act
        ResponseEntity<InputStreamResource> response = mediaController.getMedia(fileName);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        HttpHeaders headers = response.getHeaders();
        assertThat(headers.getContentType()).isEqualTo(MediaType.parseMediaType("image/png"));
        assertThat(headers.getContentLength()).isEqualTo(100L);

        verify(mediaService).getMedia(fileName);
    }

    @Test
    void uploadMedia_EndpointValidation_Success() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sample-image.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5} // Minimal PNG byte content
        );

        when(mediaService.uploadMedia(any())).thenReturn("sample-image.png");

        // Act & Assert
        mockMvc.perform(multipart("/media/upload")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string("sample-image.png"));
    }

    @Test
    void getMedia_EndpointValidation_Success() throws Exception {
        // Arrange
        String fileName = "sample-image.png";
        S3Object mockS3Object = mock(S3Object.class);
        ObjectMetadata mockMetadata = new ObjectMetadata();
        mockMetadata.setContentType("image/png");
        mockMetadata.setContentLength(100L);

        byte[] fileContent = new byte[]{1, 2, 3, 4, 5}; // Minimal PNG byte content
        S3ObjectInputStream mockInputStream = new S3ObjectInputStream(
                new ByteArrayInputStream(fileContent),
                null
        );

        when(mockS3Object.getObjectMetadata()).thenReturn(mockMetadata);
        when(mockS3Object.getObjectContent()).thenReturn(mockInputStream);

        when(mediaService.getMedia(fileName)).thenReturn(mockS3Object);

        // Act & Assert
        mockMvc.perform(get("/media/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(header().longValue("Content-Length", 100L));
    }

    @Test
    void uploadMedia_EmptyFile_ShouldHandleGracefully() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty-image.png",
                "image/png",
                new byte[0]
        );

        when(mediaService.uploadMedia(any())).thenThrow(new InvalidFileException("empty file"));

        // Act and assert
        assertThat(mediaController.uploadMedia(emptyFile).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getMedia_NonExistentFile_ShouldHandleGracefully() throws Exception {
        // Arrange
        String nonExistentFileName = "non-existent-image.png";
        when(mediaService.getMedia(nonExistentFileName)).thenThrow(new MediaNotFoundException("media not found"));

        // Act & Assert
        mockMvc.perform(get("/media/{fileName}", nonExistentFileName))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadMedia_UnsupportedFileType_BadRequest() {
        // Arrange
        MockMultipartFile unsupportedFile = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                new byte[]{1, 2, 3, 4, 5} // Some file content
        );

        when(mediaService.uploadMedia(any())).thenThrow(new InvalidFileException("Unsupported file type"));

        // Act & Assert
        ResponseEntity<String> response = mediaController.uploadMedia(unsupportedFile);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Unsupported file type");
    }

    @Test
    void uploadMedia_UnsupportedFileType_EndpointValidation() throws Exception {
        // Arrange
        MockMultipartFile unsupportedFile = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                new byte[]{1, 2, 3, 4, 5} // Some file content
        );

        when(mediaService.uploadMedia(any())).thenThrow(new InvalidFileException("Unsupported file type"));

        // Act & Assert
        mockMvc.perform(multipart("/media/upload").file(unsupportedFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unsupported file type"));
    }

    @Test
    void uploadMedia_S3UploadError() {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sample-image.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5} // Minimal PNG byte content
        );

        when(mediaService.uploadMedia(any())).thenThrow(new MediaUploadException("upload failed", new IOException()));

        // Act and assert
        assertThat(mediaController.uploadMedia(mockFile).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void uploadMedia_S3UploadError_EndpointValidation() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sample-image.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5} // Minimal PNG byte content
        );

        when(mediaService.uploadMedia(any())).thenThrow(new MediaUploadException("upload failed", new IOException()));

        // Act and assert
        mockMvc.perform(multipart("/media/upload").file(mockFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("upload failed"));
    }
}