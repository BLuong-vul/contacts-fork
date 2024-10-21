package com.vision.middleware.controller;

import com.vision.middleware.domain.Notification;
import com.vision.middleware.dto.NotificationDTO;
import com.vision.middleware.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    @MessageMapping("/notifications/acknowledge")
    public void acknowledgeNotification(long notificationId) {
        notificationService.acknowledgeNotification(notificationId);
    }
}
