package com.odontoconnect.common.exception;

/**
 * Exceção lançada quando um usuário solicitado não é encontrado.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}