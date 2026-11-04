package com.odontoconnect.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO utilizado para receber os dados necessários para criar um usuário.
 * As anotações validam os campos antes de chegarem ao serviço.
 */
public record CreateUserRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 180)
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 100,
                message = "A senha deve ter entre 8 e 100 caracteres")
        String password,

        @Size(max = 50)
        String registrationNumber,

        @NotEmpty(message = "Informe pelo menos um perfil")
        Set<@NotBlank(message = "O nome do perfil não pode ser vazio") String> roles

) {
}