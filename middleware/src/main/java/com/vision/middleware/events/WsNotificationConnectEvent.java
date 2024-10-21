package com.vision.middleware.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WsNotificationConnectEvent extends ApplicationEvent {
    private final long userId;

    public WsNotificationConnectEvent(Object source, long userId) {
        super(source);
        this.userId = userId;
    }
}
