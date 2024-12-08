package com.vision.testing.controller;

import com.vision.middleware.controller.NotificationController;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Notification;
import com.vision.middleware.domain.enums.NotificationType;
import com.vision.middleware.dto.NotificationDTO;
import com.vision.middleware.service.NotificationService;
import com.vision.middleware.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private Principal principal;

    @Mock
    private ApplicationUser mockUser;

    @BeforeEach
    void setUp() {
        // Reset SecurityContextHolder before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAcknowledgeNotification_WhenNotificationBelongsToUser() {
        // Arrange
        long notificationId = 1L;
        long userId = 100L;

        // Simulate principal with user ID
        when(principal.getName()).thenReturn(String.valueOf(userId));

        // Mock user lookup
        when(userService.loadUserById(userId)).thenReturn(mockUser);

        // Mock notification ownership check
        when(notificationService.doesNotificationBelongToUser(notificationId, mockUser))
                .thenReturn(true);

        // Act
        notificationController.acknowledgeNotification(principal, notificationId);

        // Assert
        verify(userService).loadUserById(userId);
        verify(notificationService).doesNotificationBelongToUser(notificationId, mockUser);
        verify(notificationService).acknowledgeNotification(notificationId);
    }

    @Test
    void testAcknowledgeNotification_WhenNotificationDoesNotBelongToUser() {
        // Arrange
        long notificationId = 1L;
        long userId = 100L;

        // Simulate principal with user ID
        when(principal.getName()).thenReturn(String.valueOf(userId));

        // Mock user lookup
        when(userService.loadUserById(userId)).thenReturn(mockUser);

        // Mock notification ownership check
        when(notificationService.doesNotificationBelongToUser(notificationId, mockUser))
                .thenReturn(false);

        // Act
        notificationController.acknowledgeNotification(principal, notificationId);

        // Assert
        verify(userService).loadUserById(userId);
        verify(notificationService).doesNotificationBelongToUser(notificationId, mockUser);
        verify(notificationService, never()).acknowledgeNotification(notificationId);
    }

    @Test
    void testGetUnreadNotifications_WhenPrincipalIsNotNull() {
        // Arrange
        long userId = 100L;

        // Simulate principal with user ID
        when(principal.getName()).thenReturn(String.valueOf(userId));

        // Mock user lookup
        when(userService.loadUserById(userId)).thenReturn(mockUser);

        // Act
        notificationController.getUnreadNotifications(principal);

        // Assert
        verify(userService).loadUserById(userId);
        verify(notificationService).sendUnreadNotifications(mockUser);
    }

    @Test
    void testGetUnreadNotifications_WhenPrincipalIsNull() {
        // Act
        notificationController.getUnreadNotifications(null);

        // Assert
        verify(userService, never()).loadUserById(anyLong());
        verify(notificationService, never()).sendUnreadNotifications(any());
    }

    @Test
    void testCreateCustomNotification() {
        // Arrange
        NotificationDTO inputDto = new NotificationDTO();
        inputDto.setNotificationType(NotificationType.FOLLOW);
        inputDto.setNotificationBody("Test Notification");
        inputDto.setToUserId(100L);

        // Mock user lookup
        when(userService.loadUserById(100L)).thenReturn(mockUser);

        // Act
        NotificationDTO result = notificationController.createCustomNotification(inputDto);

        // Assert
        verify(userService).loadUserById(100L);
        verify(notificationService).sendNotification(any(Notification.class));

        assertNotNull(result);
    }
}