package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Notification;
import com.vision.middleware.dto.NotificationDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private final NotificationRepository notificationRepository;

    public void sendNotification(Notification notification) {

        log.info("In notification service: sending notification");

        // save to repository to persist. Flush is a blocking method which is desired here.
        // Notifications should be saved to the repository before we send a message, as the user will
        // send their own message to acknowledge it through a websocket. This must be done before that.
        notificationRepository.saveAndFlush(notification);

        long userId = notification.getAssociatedUser().getId();

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .notificationId(notification.getId())
                .notificationBody(notification.getNotificationBody())
                .notificationType(notification.getNotificationType())
                .toUserId(notification.getAssociatedUser().getId())
                .timeCreated(notification.getTimeCreated())
                .build();

        // send notification to user. sent to /user/queue/notifications
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/notifications", notificationDTO);
    }

    public boolean doesNotificationBelongToUser(long notificationId, ApplicationUser user) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new IdNotFoundException("Notification ID %d does not exist.".formatted(notificationId))
        );
        return notification.getAssociatedUser().getId() == user.getId();
    }

    public void acknowledgeNotification(long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new IdNotFoundException("Notification id not found")
        );

        notification.setAcknowledged(true);
        notificationRepository.save(notification);
    }

    public void sendUnreadNotifications(ApplicationUser user) {
        notificationRepository.findByAssociatedUserAndAcknowledgedFalse(user).stream().map(
                notification -> NotificationDTO.builder()
                        .notificationType(notification.getNotificationType())
                        .notificationBody(notification.getNotificationBody())
                        .notificationId(notification.getId())
                        .timeCreated(notification.getTimeCreated())
                        .build()
        ).forEach(
                dto -> messagingTemplate.convertAndSendToUser(String.valueOf(user.getId()), "/queue/notifications", dto)
        );
    }
}
