package com.vision.middleware.controller;

import com.vision.middleware.domain.Message;
import com.vision.middleware.dto.MessageDTO;
import com.vision.middleware.service.MessagingService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@CrossOrigin("*") // todo: change this later
public class MessagingController {

    @Autowired
    private final MessagingService messagingService;
    @Autowired
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private final JwtUtil jwtUtil;


    // endpoint to receive messages from User A
    @MessageMapping("/sendMessage")
    public void sendMessage(MessageDTO message) {
        System.out.println("MESSAGE RECEIVED: " + message.getBody());

        messagingService.sendMessage(message.getRecipientId(), message);

        String conversationId = message.getSenderId() < message.getRecipientId()
            ? message.getSenderId() + "-" + message.getRecipientId()
            : message.getRecipientId() + "-" + message.getSenderId();
        System.out.println("SENDING TO DESTINATION: " + "/topic/conversations/" + conversationId);

        // assuming we are sending a message to User B via "/topic/messages"
        messagingTemplate.convertAndSend(
            "/topic/conversations/" + conversationId,
            message
        );
    }
    

    private static String getConversationId(long userId1, long userId2) {
        return userId1 < userId2 ? "%d-%d".formatted(userId1, userId2) : "%d-%d".formatted(userId2, userId1);
    }

    // @GetMapping("/getChat")
    //  public ResponseEntity<List<MessageDTO>> getConversation(
    //          @RequestHeader("Authorization") String token,
    //          @RequestParam(value = "otherUId") long otherUId
    //  ) {
    //      List<Message> chat = messagingService.getChatBetween(jwtUtil.checkJwtAuthAndGetUserId(token), otherUId);

    //      List<MessageDTO> chatDTO = chat.stream().map(
    //              message -> MessageDTO.builder()
    //                      .body(message.getMessageBody())
    //                      .senderId(message.getSendingUser().getId())
    //                      .recipientId(message.getReceivingUser().getId())
    //                      .dateSent(message.getDateSent())
    //                      .build()
    //      ).toList();

    //      return ResponseEntity.ok(chatDTO);
    //  }
}
