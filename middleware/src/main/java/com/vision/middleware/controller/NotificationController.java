package com.vision.middleware.controller;

import com.vision.middleware.dto.NotificationDTO;
import com.vision.middleware.service.NotificationService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

//    @MessageMapping("/notifications")
//    @SendTo("/notifications")
//    public NotificationDTO sendNotification()
}
