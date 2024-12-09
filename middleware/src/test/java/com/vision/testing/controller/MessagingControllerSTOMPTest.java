package com.vision.testing.controller;

import com.vision.middleware.Application;
import com.vision.middleware.dto.MessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class MessagingControllerSTOMPTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void whenConnectedToSocket_shouldSendAndReceiveMessage() throws Exception {
        // Connect to websocket
        StompSession session = stompClient
                .connect(String.format("ws://localhost:%d/ws", port),
                        new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        // TODO: take note that there is no authentication required for sending / receiving messages. This is not desired behavior, but whvr

        // Track received messages
        BlockingQueue<MessageDTO> messages = new LinkedBlockingQueue<>();

        // Subscribe to conversation topic
        session.subscribe("/topic/conversations/1-2",
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessageDTO.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        messages.add((MessageDTO) payload);
                    }
                });

        // Send message
        MessageDTO message = new MessageDTO();
        message.setSenderId(1L);
        message.setRecipientId(2L);
        message.setBody("Test message");

        session.send("/app/sendMessage", message);

        // Verify message received
        MessageDTO received = messages.poll(1, TimeUnit.SECONDS);
        assertNotNull(received);
        assertEquals("Test message", received.getBody());
    }
}
