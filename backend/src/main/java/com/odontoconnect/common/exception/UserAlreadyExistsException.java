package com.odontoconnect.common.exception;

/**
 * Exceção lançada quando ocorre uma tentativa de cadastrar
 * um usuário com e-mail já existente.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}