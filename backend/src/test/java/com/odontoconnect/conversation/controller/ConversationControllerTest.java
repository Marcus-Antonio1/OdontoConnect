package com.odontoconnect.conversation.controller;

import com.odontoconnect.auth.service.JwtService;
import com.odontoconnect.common.exception.GlobalExceptionHandler;
import com.odontoconnect.config.SecurityConfig;
import com.odontoconnect.conversation.dto.CreateConversationRequest;
import com.odontoconnect.conversation.service.ConversationService;
import com.odontoconnect.user.service.CustomUserDetailsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationService conversationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void devePermitirCriacaoPublicaDeConversa() throws Exception {

        when(conversationService.createConversation(any(CreateConversationRequest.class)))
                .thenReturn(null);

        mockMvc.perform(post("/api/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "studentName": "Aluno Teste",
                                    "studentEmail": "aluno@teste.com",
                                    "channel": "WEBSITE"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(conversationService).createConversation(any(CreateConversationRequest.class));
    }

    @Test
    void deveRejeitarCriacaoDeConversaComDadosInvalidos() throws Exception {

        mockMvc.perform(post("/api/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "studentName": "",
                                    "studentEmail": "email-invalido",
                                    "channel": null
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(conversationService);
    }

    @Test
    @WithMockUser(roles = "ATTENDANT")
    void atendenteDeveConseguirListarConversas() throws Exception {

        when(conversationService.findAllConversations())
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().isOk());

        verify(conversationService).findAllConversations();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void administradorDeveConseguirListarConversas() throws Exception {

        when(conversationService.findAllConversations())
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().isOk());

        verify(conversationService).findAllConversations();
    }

    @Test
    @WithMockUser(roles = "PROFESSOR")
    void professorNaoDeveConseguirListarConversas() throws Exception {

        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(conversationService);
    }

    @Test
    void usuarioNaoAutenticadoNaoDeveConseguirListarConversas() throws Exception {

        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(conversationService);
    }
}