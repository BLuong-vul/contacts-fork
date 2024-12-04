package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Message;
import com.vision.middleware.dto.MessageDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.MessageRepository;
import com.vision.middleware.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MessagingService {

    @Autowired
    private final MessageRepository messageRepository;

    @Autowired
    private final UserService userService;


    public void sendMessage(long senderId, MessageDTO messageDTO) {
        /*Design Pattern: Builder*/
        Message message = Message.builder()
                .messageBody(messageDTO.getBody())
                .sendingUserId(senderId)
                .receivingUserId(messageDTO.getRecipientId())
                .dateSent(new Date())
                .build();
        /*Design Pattern: Builder*/
        messageRepository.save(message);
    }

    public List<Message> getChatBetween(long user1Id, long user2Id) {
        ApplicationUser user1 = userService.loadUserById(user1Id);
        ApplicationUser user2 = userService.loadUserById(user2Id);

        // lists retrieved from repository are immutable, need new list to join them.
        List<Message> chat = messageRepository.findBySendingUserIdAndReceivingUserId(user1, user2);
        List<Message> u2SentTou1 = messageRepository.findBySendingUserIdAndReceivingUserId(user2, user1);
        List<Message> retList = new ArrayList<>(chat.size() + u2SentTou1.size());

        retList.addAll(chat);
        retList.addAll(u2SentTou1);
        retList.sort(Comparator.comparing(Message::getDateSent));

        return retList;
    }
}
