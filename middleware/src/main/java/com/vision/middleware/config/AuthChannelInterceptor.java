package com.vision.middleware.config;

import com.vision.middleware.exceptions.InvalidTokenException;
import com.vision.middleware.repo.UserRepository;
import com.vision.middleware.service.NotificationService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNullApi;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assert accessor != null;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                long userId = jwtUtil.checkJwtAuthAndGetUserId(token); // method handles invalid tokens
                Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
                accessor.setUser(auth);

                // if the connection is for notifications, then send all unread notifications.
                if ("/ws/notifications".equals(accessor.getDestination())) {
                    notificationService.sendUnreadNotifications(userService.loadUserById(userId));
                }
            }
        }

        // behavior as of now is to allow non-authenticated connections.
        // these will not share the same context as user specific authenticated connections.

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // Optionally handle post-send logic
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        // Optionally handle post-send completion logic
    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        // Optionally handle pre-receive logic
        return true;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        // Optionally handle post-receive logic
        return message;
    }
}
