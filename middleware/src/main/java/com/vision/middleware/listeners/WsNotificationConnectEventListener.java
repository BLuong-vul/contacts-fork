package com.vision.middleware.listeners;

import com.vision.middleware.events.WsNotificationConnectEvent;
import com.vision.middleware.service.NotificationService;
import com.vision.middleware.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WsNotificationConnectEventListener {
    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final UserService userService;

    @EventListener
    public void handleWsNotificationConnectEvent(WsNotificationConnectEvent event) {
        // send unread notifications to the specified user
        notificationService.sendUnreadNotifications(userService.loadUserById(event.getUserId()));
    }
}
