package com.vision.middleware.domain;

import com.vision.middleware.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notifications")
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser associatedUser;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @NonNull
    @Column(length = 255)
    private String notificationBody;

    @NonNull
    @Column(name = "time_created")
    private Instant timeCreated;

    private boolean acknowledged;
}
