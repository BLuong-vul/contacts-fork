package com.vision.middleware.controller;

import com.nimbusds.jose.proc.SecurityContext;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Notification;
import com.vision.middleware.dto.NotificationDTO;
import com.vision.middleware.service.NotificationService;
import com.vision.middleware.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

/**
 * Handles incoming notifications-related requests, including acknowledging notifications and retrieving unread notifications.
 *
 * This controller utilizes Spring's WebSocket messaging capabilities to communicate with clients.
 */
@RequiredArgsConstructor
@Controller
@Slf4j
public class NotificationController {

    /**
     * Service for managing notifications-related business logic.
     */
    @Autowired
    private final NotificationService notificationService;

    /**
     * Service for managing user-related operations.
     */
    @Autowired
    private final UserService userService;

    /**
     * Handles WebSocket message to acknowledge a notification.
     *
     * @param principal  the authenticated user principal, containing the user's ID as the name
     * @param notificationId  the ID of the notification to acknowledge
     *
     * @throws IdNotFoundException if the notification ID does not exist
     */
    @MessageMapping("/notifications/acknowledge")
    public void acknowledgeNotification(Principal principal, @Payload long notificationId) {
        ApplicationUser user = userService.loadUserById(Long.parseLong(principal.getName()));

        // does notification belong to user?
        if (notificationService.doesNotificationBelongToUser(notificationId, user)) {
            notificationService.acknowledgeNotification(notificationId);
        }
    }

    /**
     * Handles WebSocket message to retrieve and send all unread notifications for the authenticated user.
     *
     * @param principal  the authenticated user principal, containing the user's ID
     */
    @MessageMapping("/notifications/getUnread")
    public void getUnreadNotifications(Principal principal) {
        if (principal != null) {
            ApplicationUser user = userService.loadUserById(Long.parseLong(principal.getName()));
            notificationService.sendUnreadNotifications(user);
            log.info("Unread user notifications sent to {}", user.getId());
        }
    }

    /**
     * **TODO: TEMPORARY TESTING METHOD - TO BE REMOVED LATER**
     *
     * Creates a custom notification for testing purposes and sends it to the specified user.
     *
     * @param notification  the notification data to be sent
     * @return the sent notification data
     *
     * @deprecated This method is intended for testing only and will be removed in future releases.
     */
    @PostMapping("/notifications/testCreate")
    @ResponseBody
    public NotificationDTO createCustomNotification(@RequestBody NotificationDTO notification) {

        Notification realNotification = Notification.builder()
                .notificationType(notification.getNotificationType())
                .timeCreated(Instant.now())
                .notificationBody(notification.getNotificationBody())
                .acknowledged(false)
                .associatedUser(userService.loadUserById(notification.getToUserId()))
                .build();

        notificationService.sendNotification(realNotification);
        return notification;
    }
}
