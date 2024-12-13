package com.vision.middleware.domain;

import com.vision.middleware.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents a notification in the system.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notifications")
@Builder
public class Notification {
    /**
     * The unique identifier for the notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private long id;

    /**
     * The user associated with the notification.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser associatedUser;

    /**
     * The type of the notification.
     */
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    /**
     * The body of the notification.
     */
    @NonNull
    @Column(length = 255)
    private String notificationBody;

    /**
     * The timestamp when the notification was created.
     */
    @NonNull
    @Column(name = "time_created")
    private Instant timeCreated;

    /**
     * Indicates whether the notification has been acknowledged.
     */
    private boolean acknowledged;
}
