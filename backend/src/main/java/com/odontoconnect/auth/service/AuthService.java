package com.odontoconnect.auth.service;

import com.odontoconnect.auth.dto.LoginRequest;
import com.odontoconnect.auth.dto.LoginResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final long expiration;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            @Value("${jwt.expiration}") long expiration) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.expiration = expiration;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String token = jwtService.generateToken(
                (org.springframework.security.core.userdetails.UserDetails)
                        authentication.getPrincipal()
        );

        return new LoginResponse(
                token,
                "Bearer",
                expiration
        );
    }
}