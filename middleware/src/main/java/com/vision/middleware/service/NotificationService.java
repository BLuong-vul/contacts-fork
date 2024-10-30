package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Notification;
import com.vision.middleware.dto.NotificationDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private final NotificationRepository notificationRepository;

    public void sendNotification(Notification notification) {

        // save to repository to persist. Flush is a blocking method which is desired here.
        // Notifications should be saved to the repository before we send a message, as the user will
        // send their own message to acknowledge it through a websocket. This must be done before that.
        notificationRepository.saveAndFlush(notification);

        long userId = notification.getAssociatedUser().getId();
        /*Design Pattern: Builder*/
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .notificationId(notification.getId())
                .notificationBody(notification.getNotificationBody())
                .timeCreated(notification.getTimeCreated())
                .build();
        /*Design Pattern: Builder*/
        
        // send notification to user.
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/notifications", notificationDTO);
    }

    public void acknowledgeNotification(long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new IdNotFoundException("Notification id not found")
        );

        notification.setAcknowledged(true);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(ApplicationUser user) {
        return notificationRepository.findByAssociatedUserAndAcknowledgedFalse(user);
    }
}
