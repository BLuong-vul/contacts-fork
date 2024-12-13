package com.vision.middleware.config;

import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * A WebSocket channel interceptor that handles authentication for incoming WebSocket connections.
 * It extracts a JWT token from the connection headers, validates it, and sets the user authentication context.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private final JwtUtil jwtUtil;

    /**
     * Intercepts messages before they are sent to the channel.
     * Specifically, it handles the authentication for WebSocket CONNECT commands.
     *
     * @param message the message to be sent
     * @param channel the message channel
     * @return the message to be sent, which may be a modified version of the original
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assert accessor != null;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                // Create mutable copy of headers
                StompHeaderAccessor mutableAccessor = StompHeaderAccessor.wrap(message);

                long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
                Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
                mutableAccessor.setUser(auth);

                log.info("WS Authentication set for user: {}", userId);

                // Return new message with mutable headers
                return MessageBuilder.createMessage(message.getPayload(), mutableAccessor.getMessageHeaders());
            }
        }

        // behavior as of now is to allow non-authenticated connections.
        // these will not share the same context as user specific authenticated connections.

        return message;
    }
}
