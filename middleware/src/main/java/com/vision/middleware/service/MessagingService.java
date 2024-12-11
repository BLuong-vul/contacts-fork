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

/**
 * Service class responsible for handling messaging-related operations.
 * Provides methods for sending messages and retrieving chat histories between two users.
 */
@Service
@RequiredArgsConstructor
public class MessagingService {

    /**
     * Repository for message data access.
     */
    @Autowired
    private final MessageRepository messageRepository;

    /**
     * Service for user-related operations, used for user ID to user object resolution.
     */
    @Autowired
    private final UserService userService;


    /**
     * Sends a message from one user to another.
     *
     * @param senderId  the ID of the user sending the message
     * @param messageDTO contains the message body and recipient's ID
     * @throws no explicit exceptions, but may throw underlying repository exceptions
     */
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

    /**
     * Retrieves the chat history between two users, including messages in both directions.
     *
     * @param user1Id the ID of the first user
     * @param user2Id the ID of the second user
     * @return a sorted list of messages exchanged between the two users, newest first
     * @throws IdNotFoundException if either user ID is not found (via {@link UserService#loadUserById})
     */
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
