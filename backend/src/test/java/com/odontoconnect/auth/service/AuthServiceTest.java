package com.odontoconnect.auth.service;

import com.odontoconnect.auth.dto.LoginRequest;
import com.odontoconnect.auth.dto.LoginResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    private final long expiration = 3600000;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                authenticationManager,
                jwtService,
                expiration
        );
    }

    @Test
    void deveRealizarLoginERetornarToken() {

        LoginRequest request = new LoginRequest(
                "usuario@teste.com",
                "senha123"
        );

        UserDetails userDetails = User.withUsername("usuario@teste.com")
                .password("senha123")
                .roles("ATTENDANT")
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtService.generateToken(userDetails))
                .thenReturn("token-de-teste");

        LoginResponse response = authService.login(request);

        assertEquals("token-de-teste", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(expiration, response.expiresIn());

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void devePropagarExcecaoQuandoCredenciaisForemInvalidas() {

        LoginRequest request = new LoginRequest(
                "usuario@teste.com",
                "senha-errada"
        );

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any());
    }
}