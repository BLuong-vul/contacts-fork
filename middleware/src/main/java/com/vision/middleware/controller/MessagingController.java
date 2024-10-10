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

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody MessageDTO messageDTO
    ) {
        long userId = jwtUtil.checkJwtAuthAndGetUserId(token);

        // save message to repository
        messagingService.sendMessage(userId, messageDTO);

        // send websocket notification to appropriate topic
        messagingTemplate.convertAndSend("/topic/messages/" + userId, "New Message"); //todo: figure out something more appropriate

        // respond to user
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/getchat")
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
