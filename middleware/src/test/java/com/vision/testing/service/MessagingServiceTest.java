package com.vision.testing.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Message;
import com.vision.middleware.dto.MessageDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.MessageRepository;
import com.vision.middleware.service.MessagingService;
import com.vision.middleware.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessagingService messagingService;

    private ApplicationUser sender;
    private ApplicationUser recipient;
    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        sender = new ApplicationUser();
        sender.setId(1L);

        recipient = new ApplicationUser();
        recipient.setId(2L);

        messageDTO = new MessageDTO();
        messageDTO.setBody("Test message");
        messageDTO.setRecipientId(recipient.getId());
    }

    @Test
    void sendMessage_ShouldSaveMessageCorrectly() {
        // When
        messagingService.sendMessage(sender.getId(), messageDTO);

        // Then
        verify(messageRepository, times(1)).save(argThat(message ->
                message.getMessageBody().equals(messageDTO.getBody()) &&
                        message.getSendingUserId() == sender.getId() &&
                        message.getReceivingUserId() == recipient.getId() &&
                        message.getDateSent() != null
        ));
    }

    @Test
    void getChatBetween_ShouldReturnSortedMessages() {
        // Given
        when(userService.loadUserById(sender.getId())).thenReturn(sender);
        when(userService.loadUserById(recipient.getId())).thenReturn(recipient);

        Message message1 = Message.builder()
                .sendingUserId(sender.getId())
                .receivingUserId(recipient.getId())
                .messageBody("First message")
                .dateSent(new Date(System.currentTimeMillis() - 1000))
                .build();

        Message message2 = Message.builder()
                .sendingUserId(recipient.getId())
                .receivingUserId(sender.getId())
                .messageBody("Second message")
                .dateSent(new Date())
                .build();

        when(messageRepository.findBySendingUserIdAndReceivingUserId(sender, recipient))
                .thenReturn(Collections.singletonList(message1));
        when(messageRepository.findBySendingUserIdAndReceivingUserId(recipient, sender))
                .thenReturn(Collections.singletonList(message2));

        // When
        List<Message> chatHistory = messagingService.getChatBetween(sender.getId(), recipient.getId());

        // Then
        assertThat(chatHistory).hasSize(2);
        assertThat(chatHistory.get(0)).isEqualTo(message1);
        assertThat(chatHistory.get(1)).isEqualTo(message2);
    }

    @Test
    void getChatBetween_WhenNoMessages_ReturnsEmptyList() {
        // Given
        when(userService.loadUserById(sender.getId())).thenReturn(sender);
        when(userService.loadUserById(recipient.getId())).thenReturn(recipient);

        when(messageRepository.findBySendingUserIdAndReceivingUserId(sender, recipient))
                .thenReturn(List.of());
        when(messageRepository.findBySendingUserIdAndReceivingUserId(recipient, sender))
                .thenReturn(List.of());

        // When
        List<Message> chatHistory = messagingService.getChatBetween(sender.getId(), recipient.getId());

        // Then
        assertThat(chatHistory).isEmpty();
    }

    @Test
    void sendMessage_NullMessageDTO_ShouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> messagingService.sendMessage(sender.getId(), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getChatBetween_InvalidUserId_ShouldThrowException() {
        // Given
        when(userService.loadUserById(anyLong())).thenThrow(new IdNotFoundException("User not found"));

        // When/Then
        assertThatThrownBy(() -> messagingService.getChatBetween(sender.getId(), recipient.getId()))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}