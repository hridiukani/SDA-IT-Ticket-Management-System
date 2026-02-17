package com.itoffice.ticketsystem.service;

import com.itoffice.ticketsystem.dto.request.CreateTicketRequest;
import com.itoffice.ticketsystem.dto.request.UpdateTicketRequest;
import com.itoffice.ticketsystem.dto.response.TicketResponse;
import com.itoffice.ticketsystem.exception.ResourceNotFoundException;
import com.itoffice.ticketsystem.exception.UnauthorizedException;
import com.itoffice.ticketsystem.model.Ticket;
import com.itoffice.ticketsystem.model.User;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.model.enums.TicketPriority;
import com.itoffice.ticketsystem.model.enums.TicketStatus;
import com.itoffice.ticketsystem.repository.CommentRepository;
import com.itoffice.ticketsystem.repository.TicketRepository;
import com.itoffice.ticketsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketService Tests")
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserService userService;

    @InjectMocks
    private TicketService ticketService;

    private User adminUser;
    private User regularUser;
    private User technicianUser;
    private Ticket mockTicket;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();

        adminUser = User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .build();

        regularUser = User.builder()
                .id(UUID.randomUUID())
                .username("user")
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();

        technicianUser = User.builder()
                .id(UUID.randomUUID())
                .username("technician")
                .role(Role.ROLE_TECHNICIAN)
                .enabled(true)
                .build();

        mockTicket = Ticket.builder()
                .id(ticketId)
                .title("Test Ticket")
                .description("Test Description")
                .status(TicketStatus.OPEN)
                .priority(TicketPriority.MEDIUM)
                .createdBy(regularUser)
                .build();
    }

    @Test
    @DisplayName("Admin should see all tickets")
    void adminShouldSeeAllTickets() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> ticketPage = new PageImpl<>(
            Arrays.asList(mockTicket));

        when(userRepository.findByUsername("admin"))
            .thenReturn(Optional.of(adminUser));
        when(ticketRepository.findAll(pageable))
            .thenReturn(ticketPage);
        when(userService.mapToUserResponse(any())).thenReturn(null);
        when(commentRepository.countByTicket(any())).thenReturn(0L);

        // Act
        Page<TicketResponse> result = ticketService
            .getAllTickets("admin", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(ticketRepository, times(1)).findAll(pageable);
        verify(ticketRepository, never()).findByCreatedBy(any(), any());
    }

    @Test
    @DisplayName("Regular user should only see own tickets")
    void regularUserShouldOnlySeeOwnTickets() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> ticketPage = new PageImpl<>(
            Arrays.asList(mockTicket));

        when(userRepository.findByUsername("user"))
            .thenReturn(Optional.of(regularUser));
        when(ticketRepository.findByCreatedBy(regularUser, pageable))
            .thenReturn(ticketPage);
        when(userService.mapToUserResponse(any())).thenReturn(null);
        when(commentRepository.countByTicket(any())).thenReturn(0L);

        // Act
        Page<TicketResponse> result = ticketService
            .getAllTickets("user", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(ticketRepository, never()).findAll(pageable);
        verify(ticketRepository, times(1))
            .findByCreatedBy(regularUser, pageable);
    }

    @Test
    @DisplayName("Should create ticket successfully")
    void shouldCreateTicketSuccessfully() {
        // Arrange
        CreateTicketRequest request = CreateTicketRequest.builder()
                .title("New Ticket")
                .description("New Description")
                .priority(TicketPriority.HIGH)
                .build();

        when(userRepository.findByUsername("user"))
            .thenReturn(Optional.of(regularUser));
        when(ticketRepository.save(any(Ticket.class)))
            .thenReturn(mockTicket);
        when(userService.mapToUserResponse(any())).thenReturn(null);
        when(commentRepository.countByTicket(any())).thenReturn(0L);

        // Act
        TicketResponse response = ticketService
            .createTicket(request, "user");

        // Assert
        assertNotNull(response);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should throw exception when regular user accesses others ticket")
    void shouldThrowExceptionWhenUserAccessesOthersTicket() {
        // Arrange
        User anotherUser = User.builder()
                .id(UUID.randomUUID())
                .username("another")
                .role(Role.ROLE_USER)
                .build();

        Ticket anotherTicket = Ticket.builder()
                .id(UUID.randomUUID())
                .title("Another Ticket")
                .createdBy(anotherUser)
                .build();

        when(userRepository.findByUsername("user"))
            .thenReturn(Optional.of(regularUser));
        when(ticketRepository.findById(anotherTicket.getId()))
            .thenReturn(Optional.of(anotherTicket));

        // Act & Assert
        assertThrows(UnauthorizedException.class,
            () -> ticketService.getTicketById(
                anotherTicket.getId(), "user"));
    }

    @Test
    @DisplayName("Should update ticket status to RESOLVED and set resolvedAt")
    void shouldSetResolvedAtWhenStatusIsResolved() {
        // Arrange
        UpdateTicketRequest request = UpdateTicketRequest.builder()
                .status(TicketStatus.RESOLVED)
                .build();

        when(userRepository.findByUsername("technician"))
            .thenReturn(Optional.of(technicianUser));
        when(ticketRepository.findById(ticketId))
            .thenReturn(Optional.of(mockTicket));
        when(ticketRepository.save(any(Ticket.class)))
            .thenReturn(mockTicket);
        when(userService.mapToUserResponse(any())).thenReturn(null);
        when(commentRepository.countByTicket(any())).thenReturn(0L);

        // Act
        ticketService.updateTicket(ticketId, request, "technician");

        // Assert
        assertNotNull(mockTicket.getResolvedAt());
        verify(ticketRepository, times(1)).save(mockTicket);
    }

    @Test
    @DisplayName("Should throw exception when ticket not found")
    void shouldThrowExceptionWhenTicketNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(ticketRepository.findById(nonExistentId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
            () -> ticketService.getTicketById(nonExistentId, "user"));
    }

    @Test
    @DisplayName("Should delete ticket when user is admin")
    void shouldDeleteTicketWhenUserIsAdmin() {
        // Arrange
        when(userRepository.findByUsername("admin"))
            .thenReturn(Optional.of(adminUser));
        when(ticketRepository.findById(ticketId))
            .thenReturn(Optional.of(mockTicket));

        // Act
        ticketService.deleteTicket(ticketId, "admin");

        // Assert
        verify(ticketRepository, times(1)).delete(mockTicket);
    }

    @Test
    @DisplayName("Should throw exception when non-admin tries to delete others ticket")
    void shouldThrowExceptionWhenNonAdminDeletesOthersTicket() {
        // Arrange
        User anotherUser = User.builder()
                .id(UUID.randomUUID())
                .username("another")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findByUsername("another"))
            .thenReturn(Optional.of(anotherUser));
        when(ticketRepository.findById(ticketId))
            .thenReturn(Optional.of(mockTicket));

        // Act & Assert
        assertThrows(UnauthorizedException.class,
            () -> ticketService.deleteTicket(ticketId, "another"));
        verify(ticketRepository, never()).delete(any());
    }
}
