package com.odontoconnect.auth.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            Base64.getEncoder().encodeToString(
                    "odontoconnect-test-secret-key-32".getBytes(StandardCharsets.UTF_8)
            );

    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3600000);

        userDetails = User.withUsername("usuario@teste.com")
                .password("senha")
                .roles("ATTENDANT")
                .build();
    }

    @Test
    void deveGerarTokenJwt() {

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void deveExtrairEmailDoToken() {

        String token = jwtService.generateToken(userDetails);

        String email = jwtService.extractUsername(token);

        assertEquals("usuario@teste.com", email);
    }

    @Test
    void deveConsiderarTokenValidoQuandoPertenceAoUsuario() {

        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertTrue(valid);
    }

    @Test
    void deveConsiderarTokenInvalidoQuandoPertenceAOutroUsuario() {

        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = User.withUsername("outro@teste.com")
                .password("senha")
                .roles("ATTENDANT")
                .build();

        boolean valid = jwtService.isTokenValid(token, anotherUser);

        assertFalse(valid);
    }

    @Test
    void deveRejeitarTokenExpirado() {

        JwtService expiredJwtService = new JwtService(SECRET, -1000);

        String token = expiredJwtService.generateToken(userDetails);

        assertThrows(
                JwtException.class,
                () -> jwtService.isTokenValid(token, userDetails)
        );
    }

    @Test
    void deveRejeitarTokenComAssinaturaInvalida() {

        String token = jwtService.generateToken(userDetails);

        String anotherSecret = Base64.getEncoder().encodeToString(
                "another-test-secret-key-32-bytes".getBytes(StandardCharsets.UTF_8)
        );

        JwtService anotherJwtService = new JwtService(anotherSecret, 3600000);

        assertThrows(
                JwtException.class,
                () -> anotherJwtService.extractUsername(token)
        );
    }
}