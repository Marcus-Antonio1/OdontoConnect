package com.odontoconnect.user.repository;

import com.odontoconnect.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repositório responsável pelas operações de persistência da entidade Role.
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Busca um perfil pelo nome.
    // Retorna um Optional vazio caso o perfil não exista.
    Optional<Role> findByName(String name);
}
