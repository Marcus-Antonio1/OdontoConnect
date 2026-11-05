package com.odontoconnect.conversation.dto;

import com.odontoconnect.conversation.entity.Channel;
import com.odontoconnect.conversation.entity.ConversationStatus;

import java.time.OffsetDateTime;

// Dados retornados pela API ao consultar uma conversa.
public record ConversationResponse(
        Long id,
        String studentName,
        String studentEmail,
        Channel channel,
        ConversationStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}