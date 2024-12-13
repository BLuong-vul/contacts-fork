package com.vision.middleware.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for WebSocket message broker.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;

    /**
     * Configures the client inbound channel to add authentication interceptors.
     *
     * @param registration the {@link ChannelRegistration} object for configuring the client inbound channel
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }

    /**
     * Configures the message broker to enable simple broker and set destination prefixes.
     *
     * @param config the {@link MessageBrokerRegistry} object for configuring the message broker
     */
    @Override
    public void configureMessageBroker (MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registers STOMP endpoints and sets allowed origins.
     *
     * @param registry the {@link StompEndpointRegistry} object for registering STOMP endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] allowedOrigins = {"https://contacts-5min.onrender.com/", "*"};

        // sock js
        registry.addEndpoint("/ws", "/ws/notifications")
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS(); // todo: clean up later when done with testing

        // normal ws
        registry.addEndpoint("/ws", "/ws/notifications")
                .setAllowedOriginPatterns(allowedOrigins);
    }
}
