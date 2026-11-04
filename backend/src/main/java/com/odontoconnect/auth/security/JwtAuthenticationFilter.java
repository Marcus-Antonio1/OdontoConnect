package com.odontoconnect.auth.security;

import com.odontoconnect.auth.service.JwtService;
import com.odontoconnect.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepta as requisições HTTP para validar o JWT
 * e estabelecer a autenticação do usuário no Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Recupera o cabeçalho de autenticação enviado pelo cliente.
        final String authHeader = request.getHeader("Authorization");

        // Continua a requisição quando não existe um token Bearer.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove o prefixo "Bearer " para obter somente o JWT.
        final String jwt = authHeader.substring(7);

        try {
            // Extrai o e-mail armazenado no subject do token.
            String email = jwtService.extractUsername(jwt);

            // Evita autenticar novamente uma requisição já autenticada.
            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Busca os dados e as permissões atuais do usuário no banco.
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                // Valida o token e verifica se a conta está habilitada.
                if (jwtService.isTokenValid(jwt, userDetails)
                        && userDetails.isEnabled()) {

                    // Cria o objeto de autenticação utilizado pelo Spring Security.
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Adiciona informações da requisição à autenticação.
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Registra o usuário autenticado no contexto de segurança.
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (io.jsonwebtoken.JwtException |
                 IllegalArgumentException exception) {

            // Remove qualquer autenticação caso o token seja inválido.
            SecurityContextHolder.clearContext();
        }

        // Continua a execução da cadeia de filtros do Spring Security.
        filterChain.doFilter(request, response);
    }
}