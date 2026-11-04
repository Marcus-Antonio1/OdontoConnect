package com.odontoconnect.user.repository;

import com.odontoconnect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repositório responsável pelas operações de persistência da entidade User.
// JpaRepository já fornece métodos prontos para consultar, salvar e excluir registros.
public interface UserRepository extends JpaRepository<User, Long> {

    // Busca um usuário pelo e-mail, ignorando diferenças entre maiúsculas e minúsculas.
    // Optional representa que o usuário pode ou não ser encontrado.
    Optional<User> findByEmailIgnoreCase(String email);

    // Verifica se já existe um usuário cadastrado com o e-mail informado.
    // Retorna true se existir e false caso contrário.
    boolean existsByEmailIgnoreCase(String email);

    // Verifica se existe algum usuário associado ao perfil informado.
    // A propriedade Name é acessada por meio do relacionamento User -> Role.
    boolean existsByRoles_Name(String roleName);
}
