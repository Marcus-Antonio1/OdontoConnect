package com.odontoconnect.user.controller;

import com.odontoconnect.user.dto.CreateUserRequest;
import com.odontoconnect.user.dto.UserResponse;
import com.odontoconnect.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.odontoconnect.user.dto.UpdateUserRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;

// Indica que esta classe recebe requisições HTTP e retorna respostas no formato REST.
@RestController

// Define o caminho base de todos os endpoints deste controller.
@RequestMapping("/api/users")

// Personaliza a documentação deste grupo de endpoints no Swagger.
@Tag(name = "Usuários", description = "Gerenciamento de usuários")

// Informa ao Swagger que os endpoints deste controller utilizam autenticação JWT.
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // Recebe o serviço responsável pelas regras de negócio dos usuários.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint responsável por cadastrar um novo usuário.
    // Endpoint responsável por cadastrar um novo usuário.
    // Endpoint responsável por cadastrar um novo usuário.
    @PostMapping
    @Operation(
            summary = "Cadastrar usuário",
            description = "Cria um novo usuário e associa seus perfis."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou perfil inexistente"),
            @ApiResponse(responseCode = "409", description = "Já existe um usuário com esse e-mail")
    })
// Restringe o cadastro de usuários a quem possui o perfil ADMIN.
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint responsável por listar todos os usuários cadastrados.
    @GetMapping
    @Operation(
            summary = "Listar usuários",
            description = "Retorna todos os usuários cadastrados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuários listados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            )
    })
    // Somente administradores podem consultar a lista completa.
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAllUsers() {

        return ResponseEntity.ok(userService.findAllUsers());
    }

    // Endpoint responsável por consultar um usuário específico pelo ID.
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados de um usuário específico."
    )
    // Restringe a consulta individual ao perfil ADMIN.
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> findUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(userService.findUserById(id));
    }

    // Atualiza os dados cadastrais de um usuário.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateUser(id, request);

        return ResponseEntity.ok(response);
    }

    // Desativa a conta de um usuário.
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar usuário")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {

        UserResponse response = userService.deactivateUser(id);

        return ResponseEntity.ok(response);
    }

    // Reativa a conta de um usuário.
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reativar usuário")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {

        UserResponse response = userService.activateUser(id);

        return ResponseEntity.ok(response);
    }
}
