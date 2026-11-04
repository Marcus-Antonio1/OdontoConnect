package com.odontoconnect.user.service;

import com.odontoconnect.common.exception.UserAlreadyExistsException;
import com.odontoconnect.common.exception.UserNotFoundException;
import com.odontoconnect.user.dto.CreateUserRequest;
import com.odontoconnect.user.dto.UserResponse;
import com.odontoconnect.user.entity.Role;
import com.odontoconnect.user.entity.User;
import com.odontoconnect.user.repository.RoleRepository;
import com.odontoconnect.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.odontoconnect.user.dto.UpdateUserRequest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRegistrationNumber(),
                user.isActive(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                user.getCreatedAt()
        );
    }

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Cria um usuário, associa seus perfis e retorna os dados públicos cadastrados.
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        String email = request.email().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException("Já existe um usuário com esse e-mail");
        }

        Set<Role> roles = request.roles().stream()
                .map(roleName -> roleRepository.findByName(roleName.trim().toUpperCase())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Perfil não encontrado: " + roleName)))
                .collect(Collectors.toSet());

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.name().trim(),
                email,
                passwordHash,
                request.registrationNumber()
        );

        roles.forEach(user::addRole);

        User savedUser = userRepository.save(user);

        return toUserResponse(savedUser);
    }


    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {

        // Busca todos os usuários e transforma cada entidade em um DTO.
        // O resultado é uma lista contendo apenas os dados que podem ser expostos.
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }


    /* findAll() busca todos os usuários cadastrados.
        stream() permite percorrer a lista.
        map() transforma cada entidade User em um UserResponse.
        toList() devolve uma lista de respostas. */

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuário não encontrado."
                ));

        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        // Busca o usuário que será atualizado.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuário não encontrado com ID: " + id
                ));

        String email = request.email().trim();

        // Verifica se o e-mail já pertence a outro usuário.
        userRepository.findByEmailIgnoreCase(email)
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistsException(
                            "Já existe um usuário cadastrado com este e-mail."
                    );
                });

        String registrationNumber = request.registrationNumber() == null
                ? null
                : request.registrationNumber().trim();

        // Verifica se a matrícula já pertence a outro usuário.
        if (registrationNumber != null && !registrationNumber.isBlank()) {
            userRepository.findByRegistrationNumber(registrationNumber)
                    .filter(existingUser -> !existingUser.getId().equals(id))
                    .ifPresent(existingUser -> {
                        throw new UserAlreadyExistsException(
                                "Já existe um usuário cadastrado com esta matrícula."
                        );
                    });
        } else {
            registrationNumber = null;
        }

        // Atualiza os dados cadastrais da entidade.
        user.updatePersonalData(
                request.name().trim(),
                email,
                registrationNumber
        );

        return toUserResponse(user);
    }
}
