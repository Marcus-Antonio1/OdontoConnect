
package com.odontoconnect.user.service;

import com.odontoconnect.user.dto.CreateUserRequest;
import com.odontoconnect.user.entity.Role;
import com.odontoconnect.user.entity.User;
import com.odontoconnect.user.repository.RoleRepository;
import com.odontoconnect.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odontoconnect.common.exception.UserAlreadyExistsException;

import java.util.Set;
import java.util.stream.Collectors;
import com.odontoconnect.user.dto.UserResponse;
import java.util.List;
import com.odontoconnect.common.exception.UserNotFoundException;

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

    @Transactional
    public User createUser(CreateUserRequest request) {

        String email = request.email().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException(
                    "Já existe um usuário cadastrado com esse e-mail."
            );
        }

        Set<Role> roles = request.roles().stream()
                .map(roleName -> roleRepository.findByName(
                        roleName.trim().toUpperCase()
                ).orElseThrow(() -> new IllegalArgumentException(
                        "Perfil não encontrado: " + roleName
                )))
                .collect(Collectors.toSet());

        String passwordHash =
                passwordEncoder.encode(request.password());

        User user = new User(
                request.name().trim(),
                email,
                passwordHash,
                request.registrationNumber()
        );

        roles.forEach(user::addRole);

        return userRepository.save(user);
    }
    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRegistrationNumber(),
                        user.isActive(),
                        user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet()),
                        user.getCreatedAt()
                ))
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
}
