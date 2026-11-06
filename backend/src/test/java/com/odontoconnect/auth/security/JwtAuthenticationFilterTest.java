package com.odontoconnect.auth.security;

import com.odontoconnect.auth.service.JwtService;
import com.odontoconnect.user.service.CustomUserDetailsService;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.mock.web.MockFilterChain;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService
        );

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        userDetails = User.withUsername("usuario@teste.com")
                .password("senha")
                .roles("ATTENDANT")
                .build();

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveContinuarQuandoTokenNaoForInformado()
            throws ServletException, IOException {

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void deveContinuarQuandoCabecalhoNaoContiverBearer()
            throws ServletException, IOException {

        request.addHeader("Authorization", "Basic abc123");

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void deveAutenticarUsuarioQuandoTokenForValido()
            throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer token-valido");

        when(jwtService.extractUsername("token-valido"))
                .thenReturn("usuario@teste.com");

        when(userDetailsService.loadUserByUsername("usuario@teste.com"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid("token-valido", userDetails))
                .thenReturn(true);

        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals("usuario@teste.com", authentication.getName());

        verify(userDetailsService).loadUserByUsername("usuario@teste.com");
    }

    @Test
    void naoDeveAutenticarQuandoTokenForInvalido()
            throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer token-invalido");

        when(jwtService.extractUsername("token-invalido"))
                .thenReturn("usuario@teste.com");

        when(userDetailsService.loadUserByUsername("usuario@teste.com"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid("token-invalido", userDetails))
                .thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void naoDeveAutenticarUsuarioInativo()
            throws ServletException, IOException {

        UserDetails inactiveUser = mock(UserDetails.class);

        when(inactiveUser.isEnabled()).thenReturn(false);

        request.addHeader("Authorization", "Bearer token-valido");

        when(jwtService.extractUsername("token-valido"))
                .thenReturn("usuario@teste.com");

        when(userDetailsService.loadUserByUsername("usuario@teste.com"))
                .thenReturn(inactiveUser);

        when(jwtService.isTokenValid("token-valido", inactiveUser))
                .thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).isTokenValid("token-valido", inactiveUser);
        verify(inactiveUser).isEnabled();
    }

    @Test
    void naoDeveAutenticarQuandoTokenLancarExcecaoJwt()
            throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer token-invalido");

        when(jwtService.extractUsername("token-invalido"))
                .thenThrow(new JwtException("Token inválido"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(userDetailsService);
    }

    @Test
    void naoDeveSubstituirAutenticacaoExistente()
            throws ServletException, IOException {

        Authentication existingAuthentication =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        request.addHeader("Authorization", "Bearer token-valido");

        when(jwtService.extractUsername("token-valido"))
                .thenReturn("usuario@teste.com");

        filter.doFilter(request, response, filterChain);

        assertSame(
                existingAuthentication,
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(userDetailsService);
    }
}