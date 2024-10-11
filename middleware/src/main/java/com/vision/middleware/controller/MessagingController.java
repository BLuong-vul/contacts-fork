package com.vision.middleware.controller;

import com.vision.middleware.domain.Message;
import com.vision.middleware.dto.MessageDTO;
import com.vision.middleware.service.MessagingService;
import com.vision.middleware.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
//    @MessageMapping("/sendMessage")
//    public void sendMessage(Message message) {
//        System.out.println("MESSAGE RECEIVED: " + message.getMessageBody());
//        String conversationId = message.getSendingUserId() < message.getReceivingUserId()
//            ? message.getSendingUserId() + "-" + message.getReceivingUserId()
//            : message.getReceivingUserId() + "-" + message.getSendingUserId();
//        System.out.println("SENDING TO DESTINATION: " + "/user/conversations/" + conversationId);
//        // assuming we are sending a message to User B via "/topic/messages"
//        messagingTemplate.convertAndSend(
//            "/user/conversations/" + conversationId,
//            message
//        );
//    }

    // Old code, reuse later

     @PostMapping("/sendMessage")
     public ResponseEntity<String> sendMessage(
             @RequestHeader("Authorization") String token,
             @RequestBody MessageDTO messageDTO
     ) {
         long userId = jwtUtil.checkJwtAuthAndGetUserId(token);
         messageDTO.setSenderId(userId); // since we are forwarding the DTO elsewhere, it must be correct.

         // save message to repository
         messagingService.sendMessage(userId, messageDTO);

         // send websocket notification to appropriate topic
         messagingTemplate.convertAndSend("/topic/notification/%d".formatted(userId), "New Message"); //todo: figure out something more appropriate

         // send message to conversation topic between the two users
         String conversationTopic = "/topic/conversation/%s".formatted(getConversationId(userId, messageDTO.getRecipientId()));
         messagingTemplate.convertAndSend(conversationTopic, messageDTO); // this is where we forward the DTO

         // respond to user
         return ResponseEntity.ok("Message sent");
     }

    private static String getConversationId(long userId1, long userId2) {
        return userId1 < userId2 ? "%d-%d".formatted(userId1, userId2) : "%d-%d".formatted(userId2, userId1);
    }

    @GetMapping("/getChat")
     public ResponseEntity<List<MessageDTO>> getConversation(
             @RequestHeader("Authorization") String token,
             @RequestParam(value = "otherUId") long otherUId
     ) {
         List<Message> chat = messagingService.getChatBetween(jwtUtil.checkJwtAuthAndGetUserId(token), otherUId);

         List<MessageDTO> chatDTO = chat.stream().map(
                 message -> MessageDTO.builder()
                         .body(message.getMessageBody())
                         .senderId(message.getSendingUser().getUserId())
                         .recipientId(message.getReceivingUser().getUserId())
                         .dateSent(message.getDateSent())
                         .build()
         ).toList();

         return ResponseEntity.ok(chatDTO);
     }
}
