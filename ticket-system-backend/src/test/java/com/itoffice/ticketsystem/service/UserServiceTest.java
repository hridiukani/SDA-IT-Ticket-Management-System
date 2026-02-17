package com.itoffice.ticketsystem.service;

import com.itoffice.ticketsystem.dto.request.RegisterRequest;
import com.itoffice.ticketsystem.dto.response.UserResponse;
import com.itoffice.ticketsystem.exception.BadRequestException;
import com.itoffice.ticketsystem.exception.ResourceNotFoundException;
import com.itoffice.ticketsystem.model.User;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@test.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        // Arrange
        User secondUser = User.builder()
                .id(UUID.randomUUID())
                .username("seconduser")
                .email("second@test.com")
                .role(Role.ROLE_TECHNICIAN)
                .enabled(true)
                .build();
        when(userRepository.findAll())
            .thenReturn(Arrays.asList(mockUser, secondUser));

        // Act
        List<UserResponse> users = userService.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        assertEquals("seconduser", users.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user by ID")
    void shouldReturnUserById() {
        // Arrange
        when(userRepository.findById(userId))
            .thenReturn(Optional.of(mockUser));

        // Act
        UserResponse response = userService.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(Role.ROLE_USER, response.getRole());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId))
            .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.getUserById(nonExistentId)
        );
        assertTrue(exception.getMessage().contains("User"));
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("new@test.com")
                .password("Test@1234")
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponse response = userService.createUser(request, Role.ROLE_USER);

        // Assert
        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("Test@1234");
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("new@test.com")
                .password("Test@1234")
                .build();
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> userService.createUser(request, Role.ROLE_USER)
        );
        assertTrue(exception.getMessage().contains("Username already taken"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("test@test.com")
                .password("Test@1234")
                .build();
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class,
            () -> userService.createUser(request, Role.ROLE_USER));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update user role successfully")
    void shouldUpdateUserRoleSuccessfully() {
        // Arrange
        when(userRepository.findById(userId))
            .thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponse response = userService.updateUserRole(userId, Role.ROLE_ADMIN);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> userService.deleteUser(nonExistentId));
        verify(userRepository, never()).deleteById(any());
    }
}
