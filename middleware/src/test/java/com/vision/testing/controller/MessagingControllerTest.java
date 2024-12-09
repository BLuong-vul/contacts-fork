package com.vision.testing.controller;

import com.vision.middleware.controller.MessagingController;
import com.vision.middleware.dto.MessageDTO;
import com.vision.middleware.service.MessagingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessagingControllerTest {

    @Mock
    private MessagingService messagingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessagingController messagingController;

    @Test
    void sendMessage_ShouldProcessMessageCorrectly() {
        // Given
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSenderId(1L);
        messageDTO.setRecipientId(2L);
        messageDTO.setBody("Test message");

        String conversationId = "1-2";

        // When
        messagingController.sendMessage(messageDTO);

        // Then
        verify(messagingService).sendMessage(eq(2L), eq(messageDTO));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/conversations/" + conversationId),
                eq(messageDTO)
        );
    }

    @Test
    void sendMessage_WithReversedIds_ShouldMaintainConsistentConversationId() {
        // Given
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSenderId(2L);
        messageDTO.setRecipientId(1L);
        messageDTO.setBody("Test message");

        String conversationId = "1-2";

        // When
        messagingController.sendMessage(messageDTO);

        // Then
        verify(messagingService).sendMessage(eq(1L), eq(messageDTO));
        verify(messagingTemplate).convertAndSend(
                eq("/topic/conversations/" + conversationId),
                eq(messageDTO)
        );
    }

    @Test
    void getConversationId_PrivateMethodTest_ShouldReturnConsistentId() throws Exception {
        // Given
        long userId1 = 2L;
        long userId2 = 1L;
        String expectedConversationId = "1-2";

        // When & Then - Using Reflection to invoke the private method
        Class<?> clazz = MessagingController.class;
        Method privateMethod = clazz.getDeclaredMethod("getConversationId", long.class, long.class);
        privateMethod.setAccessible(true); // Make the private method accessible

        Object[] params = {userId1, userId2}; // Parameters to pass to the method
        String actualConversationId = (String) privateMethod.invoke(messagingController, params);

        assertEquals(expectedConversationId, actualConversationId);

        // Test the reverse case as well for consistency
        params = new Object[]{userId2, userId1};
        actualConversationId = (String) privateMethod.invoke(messagingController, params);
        assertEquals(expectedConversationId, actualConversationId);
    }

}
