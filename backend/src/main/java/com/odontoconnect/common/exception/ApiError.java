package com.odontoconnect.common.exception;

import java.time.OffsetDateTime;

/**
 * Representa a estrutura padronizada das respostas de erro da API.
 */
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message
) {
}