package com.vision.testing.config;

import com.vision.middleware.Application;
import com.vision.middleware.config.AuthChannelInterceptor;
import com.vision.middleware.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthChannelInterceptorTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthChannelInterceptor interceptor;

    @Mock
    private MessageChannel mockChannel;

    @BeforeEach
    void setUp() {
        // Clear the security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void testPreSend_WhenValidAuthorizationHeader_ShouldSetAuthentication() {
        // Arrange
        long expectedUserId = 123L;
        String validToken = "Bearer valid.token.here";

        // Create a mock STOMP message with an authorization header
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.addNativeHeader("Authorization", validToken);
        Message<?> message = org.springframework.messaging.support.MessageBuilder.createMessage("", accessor.getMessageHeaders());

        // Mock the JWT utility to return the expected user ID
        when(jwtUtil.checkJwtAuthAndGetUserId(validToken)).thenReturn(expectedUserId);

        // Act
        Message<?> result = interceptor.preSend(message, mock(MessageChannel.class));

        // Assert
        assertNotNull(result);

        // Verify that the JWT utility was called with the correct token
        verify(jwtUtil).checkJwtAuthAndGetUserId(validToken);

        // Check that authentication was set in the security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(expectedUserId, auth.getPrincipal());

        // Verify that the accessor's user was set
        StompHeaderAccessor resultAccessor = MessageHeaderAccessor.getAccessor(result, StompHeaderAccessor.class);
        assertNotNull(resultAccessor);
        assertEquals(auth, resultAccessor.getUser());
    }

    @Test
    void testPreSend_WhenNoAuthorizationHeader_ShouldReturnMessageUnchanged() {
        // Arrange
        // Create a CONNECT STOMP message without Authorization header
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        Message<?> message = MessageBuilder.createMessage("", accessor.getMessageHeaders());

        // Act
        Message<?> result = interceptor.preSend(message, mockChannel);

        // Assert
        assertNotNull(result);

        // Verify no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testPreSend_WhenNonConnectCommand_ShouldReturnMessageUnchanged() {
        // Arrange
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        Message<?> message = MessageBuilder.createMessage("", accessor.getMessageHeaders());

        // Act
        Message<?> result = interceptor.preSend(message, mockChannel);

        // Assert
        assertNotNull(result);

        // Verify no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testPreSend_WhenInvalidTokenPrefix_ShouldNotSetAuthentication() {
        // Arrange
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "InvalidToken");
        Message<?> message = MessageBuilder.createMessage("", accessor.getMessageHeaders());

        // Act
        Message<?> result = interceptor.preSend(message, mockChannel);

        // Assert
        assertNotNull(result);

        // Verify no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}