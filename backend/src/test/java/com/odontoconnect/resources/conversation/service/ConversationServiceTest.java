package com.odontoconnect.resources.conversation.service;

import com.odontoconnect.conversation.dto.CreateConversationRequest;
import com.odontoconnect.conversation.entity.Channel;
import com.odontoconnect.conversation.entity.Conversation;
import com.odontoconnect.conversation.entity.ConversationStatus;
import com.odontoconnect.conversation.exception.ConversationNotFoundException;
import com.odontoconnect.conversation.repository.ConversationRepository;
import com.odontoconnect.conversation.service.ConversationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    void shouldCreateConversationSuccessfully() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest(
                "  Marcus Toledo  ",
                "  marcus@email.com  ",
                Channel.WEBSITE
        );

        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = conversationService.createConversation(request);

        // Assert
        assertEquals("Marcus Toledo", response.studentName());
        assertEquals("marcus@email.com", response.studentEmail());
        assertEquals(Channel.WEBSITE, response.channel());
        assertEquals(ConversationStatus.OPEN, response.status());

        verify(conversationRepository, times(1)).save(any(Conversation.class));
    }

    @Test
    void shouldReturnAllConversations() {
        // Arrange
        Conversation first = mock(Conversation.class);
        Conversation second = mock(Conversation.class);

        when(conversationRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(first, second));

        // Act
        var response = conversationService.findAllConversations();

        // Assert
        assertEquals(2, response.size());

        verify(conversationRepository, times(1))
                .findAllByOrderByCreatedAtDesc();
    }

    @Test
    void shouldFindConversationById() {
        // Arrange
        Long conversationId = 1L;
        Conversation conversation = mock(Conversation.class);

        when(conversation.getId()).thenReturn(conversationId);
        when(conversation.getStudentName()).thenReturn("Marcus");
        when(conversation.getStudentEmail()).thenReturn("marcus@email.com");
        when(conversation.getChannel()).thenReturn(Channel.WEBSITE);
        when(conversation.getStatus()).thenReturn(ConversationStatus.OPEN);

        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.of(conversation));

        // Act
        var response = conversationService.findConversationById(conversationId);

        // Assert
        assertEquals(conversationId, response.id());
        assertEquals("Marcus", response.studentName());
        assertEquals("marcus@email.com", response.studentEmail());
        assertEquals(Channel.WEBSITE, response.channel());
        assertEquals(ConversationStatus.OPEN, response.status());

        verify(conversationRepository, times(1)).findById(conversationId);
    }

    @Test
    void shouldThrowExceptionWhenConversationDoesNotExist() {
        // Arrange
        Long conversationId = 999L;

        when(conversationRepository.findById(conversationId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ConversationNotFoundException.class,
                () -> conversationService.findConversationById(conversationId)
        );

        verify(conversationRepository, times(1)).findById(conversationId);
    }
}