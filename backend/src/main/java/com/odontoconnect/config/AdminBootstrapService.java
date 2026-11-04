package com.odontoconnect.config;

import com.odontoconnect.user.entity.Role;
import com.odontoconnect.user.entity.User;
import com.odontoconnect.user.repository.RoleRepository;
import com.odontoconnect.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsável por criar o primeiro administrador da aplicação,
 * caso o bootstrap esteja habilitado e ainda não exista um ADMIN.
 */
@Component
public class AdminBootstrapService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap.admin.name:}")
    private String name;

    @Value("${app.bootstrap.admin.email:}")
    private String email;

    @Value("${app.bootstrap.admin.password:}")
    private String password;

    public AdminBootstrapService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Executa a criação inicial do administrador durante a inicialização da aplicação.
    @Override
    @Transactional
    public void run(String... args) {

        // Verifica se o bootstrap está habilitado nas configurações.
        if (!enabled) {
            return;
        }

        // Evita criar outro administrador caso já exista um ADMIN.
        boolean adminExists = userRepository.existsByRoles_Name("ADMIN");

        if (adminExists) {
            System.out.println("Bootstrap ignorado: já existe um ADMIN.");
            return;
        }

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            throw new IllegalStateException(
                    "Dados do primeiro administrador não foram configurados."
            );
        }

        if (password.length() < 8) {
            throw new IllegalStateException(
                    "A senha do administrador deve ter pelo menos 8 caracteres."
            );
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException(
                    "O e-mail informado já está cadastrado."
            );
        }

        // Busca o perfil ADMIN que será associado ao novo usuário.
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "Perfil ADMIN não encontrado."
                ));

        // Cria o administrador armazenando a senha em formato BCrypt.
        User admin = new User(
                name,
                email,
                passwordEncoder.encode(password),
                null
        );

        admin.addRole(adminRole);

        userRepository.save(admin);

        System.out.println("Primeiro administrador criado com sucesso.");
    }
}