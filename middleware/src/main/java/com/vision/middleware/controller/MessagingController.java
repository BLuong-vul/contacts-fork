package com.vision.middleware.controller;

import com.vision.middleware.domain.Message;
import com.vision.middleware.dto.MessageDTO;
import com.vision.middleware.service.MessagingService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles REST and WebSocket requests related to messaging between users.
 * <p>
 * This controller provides endpoints for sending messages and is responsible for
 * routing messages to the intended conversation topics via WebSocket.
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@CrossOrigin("*") // todo: change this later
@Slf4j
public class MessagingController {

    /**
     * Service layer component responsible for messaging business logic.
     */
    @Autowired
    private final MessagingService messagingService;

    /**
     * Template for sending messages to connected clients via WebSocket.
     */
    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    // todo: is there a way here to get the chat history between two users?

    /**
     * Handles incoming messages from clients, persists them, and routes to the
     * intended conversation topic.
     * <p>
     * NOTE: Constraints on message content (e.g., length) are pending implementation.
     *
     * @param message The message to be sent, containing sender, recipient, and body.
     * @see MessageDTO
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(MessageDTO message) {
        // todo: add constraints on message (eg. length)

        log.info("MESSAGE RECEIVED: {}", message.getBody());

        messagingService.sendMessage(message.getRecipientId(), message);

        String conversationId = message.getSenderId() < message.getRecipientId()
            ? message.getSenderId() + "-" + message.getRecipientId()
            : message.getRecipientId() + "-" + message.getSenderId();
        log.info("SENDING TO DESTINATION: /topic/conversations/{}", conversationId);

        // assuming we are sending a message to User B via "/topic/messages"
        messagingTemplate.convertAndSend(
            "/topic/conversations/" + conversationId,
            message
        );
    }

    /**
     * Generates a unique conversation ID for two users, ensuring a consistent format.
     * <p>
     * The conversation ID is a string in the format "userId1-userId2", where the
     * smaller user ID comes first.
     *
     * @param userId1 The ID of the first user.
     * @param userId2 The ID of the second user.
     * @return A formatted string representing the conversation ID.
     */
    private static String getConversationId(long userId1, long userId2) {
        return userId1 < userId2 ? "%d-%d".formatted(userId1, userId2) : "%d-%d".formatted(userId2, userId1);
    }
}
