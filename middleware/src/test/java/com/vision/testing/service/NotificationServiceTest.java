package com.vision.testing.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Notification;
import com.vision.middleware.domain.enums.NotificationType;
import com.vision.middleware.dto.NotificationDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.NotificationRepository;
import com.vision.middleware.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private ApplicationUser user;

    @BeforeEach
    public void setUp() {
        user = new ApplicationUser();
        user.setId(1L);

        notification = new Notification();
        notification.setId(1L);
        notification.setAssociatedUser(user);
        notification.setNotificationBody("Test Notification");
        notification.setNotificationType(NotificationType.MESSAGE);
    }

    @Test
    public void testSendNotification() {
        when(notificationRepository.saveAndFlush(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotification(notification);

        verify(notificationRepository, times(1)).saveAndFlush(notification);
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("1"), eq("/queue/notifications"), any(NotificationDTO.class));
    }

    @Test
    public void testDoesNotificationBelongToUser_Valid() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        boolean result = notificationService.doesNotificationBelongToUser(1L, user);

        assertThat(result).isTrue();
    }

    @Test
    public void testDoesNotificationBelongToUser_Invalid() {
        ApplicationUser otherUser = new ApplicationUser();
        otherUser.setId(2L);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        boolean result = notificationService.doesNotificationBelongToUser(1L, otherUser);

        assertThat(result).isFalse();
    }

    @Test
    public void testDoesNotificationBelongToUser_NotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.doesNotificationBelongToUser(1L, user))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessage("Notification ID 1 does not exist.");
    }

    @Test
    public void testAcknowledgeNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.acknowledgeNotification(1L);

        assertThat(notification.isAcknowledged()).isTrue();
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    public void testAcknowledgeNotification_NotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.acknowledgeNotification(1L))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessage("Notification id not found");
    }

    @Test
    public void testSendUnreadNotifications() {
        Notification unreadNotification = new Notification();
        unreadNotification.setId(2L);
        unreadNotification.setAssociatedUser(user);
        unreadNotification.setNotificationBody("Unread Notification");
        unreadNotification.setNotificationType(NotificationType.MESSAGE);
        unreadNotification.setAcknowledged(false);

        when(notificationRepository.findByAssociatedUserAndAcknowledgedFalse(user)).thenReturn(List.of(unreadNotification));

        notificationService.sendUnreadNotifications(user);

        verify(messagingTemplate, times(1)).convertAndSendToUser(eq("1"), eq("/queue/notifications"), any(NotificationDTO.class));
    }
}