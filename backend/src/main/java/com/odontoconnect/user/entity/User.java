package com.odontoconnect.user.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

// Indica que esta classe é uma entidade gerenciada pelo JPA.
@Entity

// Define o nome da tabela correspondente no banco de dados.
@Table(name = "users")
public class User {

    // Identificador único do usuário, gerado automaticamente pelo banco.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome obrigatório, limitado a 150 caracteres.
    @Column(nullable = false, length = 150)
    private String name;

    // E-mail obrigatório e único, utilizado também no processo de login.
    @Column(nullable = false, unique = true, length = 180)
    private String email;

    // Armazena o hash da senha, nunca a senha em texto puro.
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // Número de matrícula ou registro do usuário. Pode ser nulo.
    @Column(name = "registration_number", unique = true, length = 50)
    private String registrationNumber;

    // Define se a conta está habilitada para acessar o sistema.
    @Column(nullable = false)
    private boolean active = true;

    // Data de criação. Não pode ser alterada após a inserção.
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Data da última atualização do registro.
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Um usuário pode possuir vários perfis de acesso.
    // LAZY evita carregar os perfis automaticamente em toda consulta de usuário.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Construtor protegido exigido pelo JPA para instanciar a entidade.
    protected User() {
    }

    // Construtor utilizado pela aplicação para criar novos usuários.
    public User(String name, String email, String passwordHash,
                String registrationNumber) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationNumber = registrationNumber;
    }

    // Executado automaticamente pelo JPA antes de inserir o registro.
    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    // Executado automaticamente pelo JPA antes de atualizar o registro.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Adiciona um perfil de acesso ao usuário.
    public void addRole(Role role) {
        roles.add(role);
    }

    // Remove um perfil de acesso do usuário.
    public void removeRole(Role role) {
        roles.remove(role);
    }

    // Getters utilizados para consultar os dados da entidade.
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
