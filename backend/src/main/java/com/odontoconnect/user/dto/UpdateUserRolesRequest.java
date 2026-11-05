package com.odontoconnect.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UpdateUserRolesRequest(

        @NotEmpty(message = "Informe pelo menos um perfil de acesso")
        Set<@NotBlank(message = "O perfil não pode estar vazio") String> roles

) {
}