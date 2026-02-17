package com.itoffice.ticketsystem.service;

import com.itoffice.ticketsystem.dto.request.LoginRequest;
import com.itoffice.ticketsystem.dto.request.RegisterRequest;
import com.itoffice.ticketsystem.dto.response.AuthResponse;
import com.itoffice.ticketsystem.dto.response.UserResponse;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@test.com")
                .password("Test@1234")
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("Test@1234")
                .build();

        mockUserDetails = new org.springframework.security.core.userdetails.User(
                "testuser",
                "encodedPassword",
                Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        UserResponse mockUserResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(userService.createUser(any(RegisterRequest.class), any(Role.class)))
                .thenReturn(mockUserResponse);
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class)))
                .thenReturn("mockJwtToken");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        assertEquals("testuser", response.getUser().getUsername());
        verify(userService, times(1))
            .createUser(any(RegisterRequest.class), eq(Role.ROLE_USER));
        verify(jwtUtil, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() {
        // Arrange
        UserResponse mockUserResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class)))
                .thenReturn("mockJwtToken");
        when(userService.getUserByUsername(anyString()))
                .thenReturn(mockUserResponse);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        assertEquals("Bearer", response.getType());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void shouldThrowExceptionForInvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class,
            () -> authService.login(loginRequest));
        verify(jwtUtil, never()).generateToken(any());
    }
}
