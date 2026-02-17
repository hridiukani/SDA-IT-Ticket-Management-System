package com.itoffice.ticketsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itoffice.ticketsystem.dto.request.LoginRequest;
import com.itoffice.ticketsystem.dto.request.RegisterRequest;
import com.itoffice.ticketsystem.model.User;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/auth/register - Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@test.com")
                .password("Test@1234")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.user.username", is("testuser")))
                .andExpect(jsonPath("$.user.email", is("test@test.com")))
                .andExpect(jsonPath("$.user.role", is("ROLE_USER")));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should fail with duplicate username")
    void shouldFailWithDuplicateUsername() throws Exception {
        // Create existing user
        userRepository.save(User.builder()
                .username("testuser")
                .email("existing@test.com")
                .password(passwordEncoder.encode("Test@1234"))
                .role(Role.ROLE_USER)
                .enabled(true)
                .build());

        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("new@test.com")
                .password("Test@1234")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                    containsString("Username already taken")));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should fail with invalid email")
    void shouldFailWithInvalidEmail() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("not-an-email")
                .password("Test@1234")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email",
                    notNullValue()));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should fail with short password")
    void shouldFailWithShortPassword() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@test.com")
                .password("short")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.password",
                    notNullValue()));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        // Create user first
        userRepository.save(User.builder()
                .username("testuser")
                .email("test@test.com")
                .password(passwordEncoder.encode("Test@1234"))
                .role(Role.ROLE_USER)
                .enabled(true)
                .build());

        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("Test@1234")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.username", is("testuser")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should fail with wrong password")
    void shouldFailWithWrongPassword() throws Exception {
        userRepository.save(User.builder()
                .username("testuser")
                .email("test@test.com")
                .password(passwordEncoder.encode("Test@1234"))
                .role(Role.ROLE_USER)
                .enabled(true)
                .build());

        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("WrongPassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
