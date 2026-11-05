package com.odontoconnect.conversation.controller;

import com.odontoconnect.conversation.dto.ConversationResponse;
import com.odontoconnect.conversation.dto.CreateConversationRequest;
import com.odontoconnect.conversation.service.ConversationService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversations", description = "Gerenciamento de conversas")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    // Recebe uma nova conversa iniciada pelo aluno.
    @PostMapping
    @Operation(summary = "Iniciar uma nova conversa")
    public ResponseEntity<ConversationResponse> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {

        ConversationResponse response =
                conversationService.createConversation(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Lista as conversas disponíveis para a equipe.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'SUPERVISOR', 'ATTENDANT')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar conversas")
    public ResponseEntity<List<ConversationResponse>> findAllConversations() {

        return ResponseEntity.ok(conversationService.findAllConversations());
    }

    // Consulta uma conversa específica.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'SUPERVISOR', 'ATTENDANT')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar conversa por ID")
    public ResponseEntity<ConversationResponse> findConversationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                conversationService.findConversationById(id)
        );
    }
}