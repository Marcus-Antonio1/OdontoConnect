package com.odontoconnect.user.dto;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * DTO utilizado para retornar os dados públicos de um usuário.
 * Não expõe informações sensíveis, como o hash da senha.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        String registrationNumber,
        boolean active,
        Set<String> roles,
        OffsetDateTime createdAt
) {
}