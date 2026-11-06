package com.odontoconnect.user.controller;

import com.odontoconnect.auth.service.JwtService;
import com.odontoconnect.common.exception.GlobalExceptionHandler;
import com.odontoconnect.config.SecurityConfig;
import com.odontoconnect.user.service.CustomUserDetailsService;
import com.odontoconnect.user.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDeveConseguirListarUsuarios() throws Exception {

        when(userService.findAllUsers()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        verify(userService).findAllUsers();
    }

    @Test
    @WithMockUser(roles = "ATTENDANT")
    void atendenteNaoDeveConseguirListarUsuarios() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDeveConseguirConsultarUsuarioPorId() throws Exception {

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());

        verify(userService).findUserById(1L);
    }

    @Test
    void usuarioNaoAutenticadoNaoDeveConseguirListarUsuarios() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(userService);
    }
}