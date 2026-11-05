package com.odontoconnect.conversation.service;

import com.odontoconnect.conversation.dto.ConversationResponse;
import com.odontoconnect.conversation.dto.CreateConversationRequest;
import com.odontoconnect.conversation.entity.Conversation;
import com.odontoconnect.conversation.exception.ConversationNotFoundException;
import com.odontoconnect.conversation.repository.ConversationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    // Converte a entidade para o DTO de resposta.
    private ConversationResponse toConversationResponse(Conversation conversation) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getStudentName(),
                conversation.getStudentEmail(),
                conversation.getChannel(),
                conversation.getStatus(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    // Cria uma nova conversa.
    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {

        Conversation conversation = new Conversation(
                request.studentName().trim(),
                request.studentEmail() == null || request.studentEmail().isBlank()
                        ? null
                        : request.studentEmail().trim(),
                request.channel()
        );

        Conversation savedConversation = conversationRepository.save(conversation);

        return toConversationResponse(savedConversation);
    }

    // Lista todas as conversas, da mais recente para a mais antiga.
    @Transactional(readOnly = true)
    public List<ConversationResponse> findAllConversations() {

        return conversationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toConversationResponse)
                .toList();
    }

    // Busca uma conversa pelo identificador.
    @Transactional(readOnly = true)
    public ConversationResponse findConversationById(Long id) {

        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException(
                        "Conversa não encontrada com ID: " + id
                ));

        return toConversationResponse(conversation);
    }
}