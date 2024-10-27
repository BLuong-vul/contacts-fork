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
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final UserService userService;

    @MessageMapping("/notifications/acknowledge")
    public void acknowledgeNotification(long notificationId) {
        notificationService.acknowledgeNotification(notificationId);
    }

    @MessageMapping("/notifications/getUnread")
    public void getUnreadNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("inside getUnread");

        log.info("Authentication: {}", auth);

        if (auth != null) {
            ApplicationUser user = userService.loadUserById((Long) auth.getPrincipal());
            notificationService.sendUnreadNotifications(user);
            log.info("unread user notifications sent to {}", user.getId());
        }
    }

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
