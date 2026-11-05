
package com.odontoconnect.user.service;

import com.odontoconnect.common.exception.UserAlreadyExistsException;
import com.odontoconnect.common.exception.UserNotFoundException;
import com.odontoconnect.user.dto.CreateUserRequest;
import com.odontoconnect.user.dto.UpdateUserRequest;
import com.odontoconnect.user.dto.UpdateUserRolesRequest;
import com.odontoconnect.user.entity.Role;
import com.odontoconnect.user.entity.User;
import com.odontoconnect.user.repository.RoleRepository;
import com.odontoconnect.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // Criação de usuário

    @Test
    void shouldCreateUserSuccessfully() {
        Role role = mock(Role.class);
        when(role.getName()).thenReturn("ATTENDANT");

        when(userRepository.existsByEmailIgnoreCase("marcus@email.com"))
                .thenReturn(false);
        when(roleRepository.findByName("ATTENDANT"))
                .thenReturn(Optional.of(role));
        when(passwordEncoder.encode("SenhaTeste123"))
                .thenReturn("senha-criptografada");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateUserRequest request = new CreateUserRequest(
                " Marcus ",
                " marcus@email.com ",
                "SenhaTeste123",
                "12345",
                Set.of(" attendant ")
        );

        var response = userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("Marcus", savedUser.getName());
        assertEquals("marcus@email.com", savedUser.getEmail());
        assertEquals("12345", savedUser.getRegistrationNumber());
        assertEquals("senha-criptografada", savedUser.getPasswordHash());
        assertTrue(savedUser.getRoles().contains(role));

        assertEquals("Marcus", response.name());
        assertEquals("marcus@email.com", response.email());
        assertTrue(response.active());

        verify(passwordEncoder).encode("SenhaTeste123");
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        when(userRepository.existsByEmailIgnoreCase("marcus@email.com"))
                .thenReturn(true);

        CreateUserRequest request = new CreateUserRequest(
                "Marcus",
                "marcus@email.com",
                "SenhaTeste123",
                "12345",
                Set.of("ATTENDANT")
        );

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(roleRepository, passwordEncoder);
    }

    @Test
    void shouldThrowExceptionWhenRoleDoesNotExistDuringCreation() {
        when(userRepository.existsByEmailIgnoreCase("marcus@email.com"))
                .thenReturn(false);
        when(roleRepository.findByName("INVALID"))
                .thenReturn(Optional.empty());

        CreateUserRequest request = new CreateUserRequest(
                "Marcus",
                "marcus@email.com",
                "SenhaTeste123",
                null,
                Set.of("INVALID")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    // Consultas

    @Test
    void shouldReturnAllUsers() {
        User first = new User("Marcus", "marcus@email.com", "hash", "123");
        User second = new User("Ana", "ana@email.com", "hash", "456");

        when(userRepository.findAll()).thenReturn(List.of(first, second));

        var response = userService.findAllUsers();

        assertEquals(2, response.size());
        assertEquals("Marcus", response.get(0).name());
        assertEquals("Ana", response.get(1).name());

        verify(userRepository).findAll();
    }

    @Test
    void shouldFindUserById() {
        User user = mockUser(1L, "Marcus", "marcus@email.com",
                "123", true, Set.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var response = userService.findUserById(1L);

        assertEquals(1L, response.id());
        assertEquals("Marcus", response.name());
        assertEquals("marcus@email.com", response.email());

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(999L)
        );
    }

    // Atualização de dados pessoais

    @Test
    void shouldUpdateUserSuccessfully() {
        User user = new User(
                "Marcus",
                "antigo@email.com",
                "hash",
                "123"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("novo@email.com"))
                .thenReturn(Optional.empty());
        when(userRepository.findByRegistrationNumber("456"))
                .thenReturn(Optional.empty());

        UpdateUserRequest request = new UpdateUserRequest(
                " Marcus Atualizado ",
                " novo@email.com ",
                " 456 "
        );

        var response = userService.updateUser(1L, request);

        assertEquals("Marcus Atualizado", response.name());
        assertEquals("novo@email.com", response.email());
        assertEquals("456", response.registrationNumber());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingEmail() {
        User user = new User(
                "Marcus", "antigo@email.com", "hash", "123"
        );

        User anotherUser = mock(User.class);

        when(anotherUser.getId()).thenReturn(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("ocupado@email.com"))
                .thenReturn(Optional.of(anotherUser));

        UpdateUserRequest request = new UpdateUserRequest(
                "Marcus", "ocupado@email.com", "123"
        );

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.updateUser(1L, request)
        );

        assertEquals("antigo@email.com", user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingRegistrationNumber() {
        User user = new User(
                "Marcus", "antigo@email.com", "hash", "123"
        );

        User anotherUser = mock(User.class);

        when(anotherUser.getId()).thenReturn(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("novo@email.com"))
                .thenReturn(Optional.empty());
        when(userRepository.findByRegistrationNumber("456"))
                .thenReturn(Optional.of(anotherUser));

        UpdateUserRequest request = new UpdateUserRequest(
                "Marcus", "novo@email.com", "456"
        );

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.updateUser(1L, request)
        );

        assertEquals("antigo@email.com", user.getEmail());
        assertEquals("123", user.getRegistrationNumber());
    }

    // Ativação e desativação

    @Test
    void shouldDeactivateUser() {
        User user = new User("Marcus", "marcus@email.com", "hash", "123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var response = userService.deactivateUser(1L);

        assertFalse(response.active());
    }

    @Test
    void shouldActivateUser() {
        User user = new User("Marcus", "marcus@email.com", "hash", "123");
        user.deactivate();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var response = userService.activateUser(1L);

        assertTrue(response.active());
    }

    // Gerenciamento de permissões

    @Test
    void shouldUpdateUserRolesSuccessfully() {
        Role role = mock(Role.class);
        when(role.getName()).thenReturn("ADMIN");

        User user = new User("Marcus", "marcus@email.com", "hash", "123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ADMIN"))
                .thenReturn(Optional.of(role));

        UpdateUserRolesRequest request =
                new UpdateUserRolesRequest(Set.of(" admin "));

        var response = userService.updateUserRoles(1L, request);

        assertTrue(response.roles().contains("ADMIN"));
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void shouldThrowExceptionWhenRoleDoesNotExistDuringUpdate() {
        User user = new User(
                "Marcus", "marcus@email.com", "hash", "123"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("INVALID"))
                .thenReturn(Optional.empty());

        UpdateUserRolesRequest request =
                new UpdateUserRolesRequest(Set.of("INVALID"));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserRoles(1L, request)
        );

        assertTrue(user.getRoles().isEmpty());
    }

    // Usuário inexistente em operações de escrita

    @Test
    void shouldThrowExceptionWhenDeactivatingNonexistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deactivateUser(999L)
        );
    }

    @Test
    void shouldThrowExceptionWhenActivatingNonexistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.activateUser(999L)
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRolesOfNonexistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UpdateUserRolesRequest request =
                new UpdateUserRolesRequest(Set.of("ADMIN"));

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserRoles(999L, request)
        );

        verifyNoInteractions(roleRepository);
    }
    // Cria usuários simulados para os testes sem acessar o banco.
    private User mockUser(
            Long id,
            String name,
            String email,
            String registrationNumber,
            boolean active,
            Set<Role> roles
    ) {
        User user = mock(User.class);

        when(user.getId()).thenReturn(id);
        when(user.getName()).thenReturn(name);
        when(user.getEmail()).thenReturn(email);
        when(user.getRegistrationNumber()).thenReturn(registrationNumber);
        when(user.isActive()).thenReturn(active);
        when(user.getRoles()).thenReturn(roles);
        when(user.getCreatedAt()).thenReturn(OffsetDateTime.now());

        return user;
    }

}
