package com.odontoconnect.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

/**
 * Centraliza o tratamento de exceções da API,
 * retornando respostas de erro padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata tentativas de cadastro com e-mail já existente.
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(
            UserAlreadyExistsException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // Trata consultas de usuários que não existem.
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(
            UserNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // Trata erros de validação dos DTOs recebidos pela API.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos.");

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    // Trata argumentos inválidos enviados durante a execução.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // Trata falhas de autenticação, sem expor detalhes internos.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(
            AuthenticationException exception) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos."
        );
    }

    // Monta a resposta padronizada utilizada pelos demais handlers.
    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status, String message) {

        ApiError error = new ApiError(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(error);
    }
}