package com.odontoconnect.conversation.dto;

import com.odontoconnect.conversation.entity.Channel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Dados necessários para iniciar uma nova conversa.
public record CreateConversationRequest(

        @NotBlank(message = "O nome do aluno é obrigatório")
        @Size(max = 150)
        String studentName,

        @Email(message = "Informe um e-mail válido")
        @Size(max = 180)
        String studentEmail,

        @NotNull(message = "O canal da conversa é obrigatório")
        Channel channel

) {
}