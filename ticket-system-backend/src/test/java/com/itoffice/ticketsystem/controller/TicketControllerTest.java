package com.itoffice.ticketsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itoffice.ticketsystem.dto.request.CreateTicketRequest;
import com.itoffice.ticketsystem.model.User;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.model.enums.TicketPriority;
import com.itoffice.ticketsystem.repository.TicketRepository;
import com.itoffice.ticketsystem.repository.UserRepository;
import com.itoffice.ticketsystem.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("TicketController Integration Tests")
class TicketControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    private String adminToken;
    private String userToken;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = userRepository.save(User.builder()
                .username("admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("Test@1234"))
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .build());

        regularUser = userRepository.save(User.builder()
                .username("user")
                .email("user@test.com")
                .password(passwordEncoder.encode("Test@1234"))
                .role(Role.ROLE_USER)
                .enabled(true)
                .build());

        adminToken = generateToken("admin", "ROLE_ADMIN");
        userToken = generateToken("user", "ROLE_USER");
    }

    private String generateToken(String username, String role) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username, "password",
                Collections.singletonList(
                    new SimpleGrantedAuthority(role)));
        return jwtUtil.generateToken(userDetails);
    }

    @Test
    @DisplayName("GET /api/tickets - Should return tickets for authenticated user")
    void shouldReturnTicketsForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/tickets")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @DisplayName("GET /api/tickets - Should return 403 for unauthenticated request")
    void shouldReturn403ForUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/tickets - Should create ticket successfully")
    void shouldCreateTicketSuccessfully() throws Exception {
        CreateTicketRequest request = CreateTicketRequest.builder()
                .title("Test Ticket")
                .description("Test Description")
                .priority(TicketPriority.HIGH)
                .build();

        mockMvc.perform(post("/api/tickets")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Test Ticket")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.priority", is("HIGH")));
    }

    @Test
    @DisplayName("POST /api/tickets - Should fail without title")
    void shouldFailWithoutTitle() throws Exception {
        CreateTicketRequest request = CreateTicketRequest.builder()
                .description("No title ticket")
                .priority(TicketPriority.LOW)
                .build();

        mockMvc.perform(post("/api/tickets")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title",
                    notNullValue()));
    }

    @Test
    @DisplayName("DELETE /api/tickets/{id} - Admin should delete any ticket")
    void adminShouldDeleteAnyTicket() throws Exception {
        // Create ticket as regular user first
        CreateTicketRequest createRequest = CreateTicketRequest.builder()
                .title("Ticket to delete")
                .description("Will be deleted")
                .priority(TicketPriority.LOW)
                .build();

        String createResponse = mockMvc.perform(post("/api/tickets")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ticketId = objectMapper.readTree(createResponse).get("id").asText();

        // Admin deletes it
        mockMvc.perform(delete("/api/tickets/" + ticketId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
