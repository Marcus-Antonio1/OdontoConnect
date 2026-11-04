package com.odontoconnect.user.entity;

import jakarta.persistence.*;

// Representa uma entidade persistida pelo JPA.
@Entity

// Define a tabela roles no banco de dados.
@Table(name = "roles")
public class Role {

    // Identificador único do perfil, gerado pelo banco.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome do perfil. Não pode ser nulo nem duplicado.
    @Column(nullable = false, unique = true, length = 40)
    private String name;

    // Descrição opcional para explicar a finalidade do perfil.
    @Column(length = 255)
    private String description;

    // Construtor exigido pelo JPA para instanciar a entidade.
    protected Role() {
    }

    // Retorna o identificador do perfil.
    public Long getId() {
        return id;
    }

    // Retorna o nome do perfil.
    public String getName() {
        return name;
    }

    // Retorna a descrição do perfil.
    public String getDescription() {
        return description;
    }
}
