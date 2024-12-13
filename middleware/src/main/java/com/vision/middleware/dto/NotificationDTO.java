package com.vision.middleware.dto;

import com.vision.middleware.domain.enums.NotificationType;
import lombok.*;

import java.time.Instant;

/**
 * Data Transfer Object for handling Notification data.
 * This class is used to transfer notification relevant details
 * between the application layers.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationDTO {
    private long notificationId;
    private NotificationType notificationType;
    private String notificationBody;
    private Instant timeCreated;
    private long toUserId;
}
