package com.itoffice.ticketsystem.service;

import com.itoffice.ticketsystem.dto.request.CreateTicketRequest;
import com.itoffice.ticketsystem.dto.request.UpdateTicketRequest;
import com.itoffice.ticketsystem.dto.response.TicketResponse;
import com.itoffice.ticketsystem.exception.ResourceNotFoundException;
import com.itoffice.ticketsystem.exception.UnauthorizedException;
import com.itoffice.ticketsystem.model.Ticket;
import com.itoffice.ticketsystem.model.User;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.model.enums.TicketStatus;
import com.itoffice.ticketsystem.repository.CommentRepository;
import com.itoffice.ticketsystem.repository.TicketRepository;
import com.itoffice.ticketsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public Page<TicketResponse> getAllTickets(String username, Pageable pageable) {
        User currentUser = getUserByUsername(username);

        // Regular users only see their own tickets
        if (currentUser.getRole() == Role.ROLE_USER) {
            return ticketRepository.findByCreatedBy(currentUser, pageable)
                    .map(ticket -> mapToTicketResponse(ticket));
        }

        // Technicians, Managers and Admins see all tickets
        return ticketRepository.findAll(pageable)
                .map(ticket -> mapToTicketResponse(ticket));
    }

    public TicketResponse getTicketById(UUID id, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Ticket", "id", id));

        User currentUser = getUserByUsername(username);

        // Regular users can only see their own tickets
        if (currentUser.getRole() == Role.ROLE_USER &&
            !ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException(
                "You don't have permission to view this ticket");
        }

        return mapToTicketResponse(ticket);
    }

    public TicketResponse createTicket(CreateTicketRequest request,
                                        String username) {
        User currentUser = getUserByUsername(username);

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .createdBy(currentUser)
                .build();

        return mapToTicketResponse(ticketRepository.save(ticket));
    }

    public TicketResponse updateTicket(UUID id, UpdateTicketRequest request,
                                        String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Ticket", "id", id));

        User currentUser = getUserByUsername(username);

        // Only creator, manager, or admin can update
        boolean isCreator = ticket.getCreatedBy()
            .getId().equals(currentUser.getId());
        boolean isManagerOrAdmin = currentUser.getRole() == Role.ROLE_MANAGER
            || currentUser.getRole() == Role.ROLE_ADMIN;
        boolean isTechnician = currentUser.getRole() == Role.ROLE_TECHNICIAN;

        if (!isCreator && !isManagerOrAdmin && !isTechnician) {
            throw new UnauthorizedException(
                "You don't have permission to update this ticket");
        }

        // Update fields if provided
        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }

        // Only technicians, managers and admins can update status
        if (request.getStatus() != null &&
            (isTechnician || isManagerOrAdmin)) {
            ticket.setStatus(request.getStatus());

            // Set resolvedAt when ticket is resolved
            if (request.getStatus() == TicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }

        // Only managers and admins can assign tickets
        if (request.getAssignedToId() != null && isManagerOrAdmin) {
            User assignee = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", request.getAssignedToId()));
            ticket.setAssignedTo(assignee);
        }

        return mapToTicketResponse(ticketRepository.save(ticket));
    }

    public void deleteTicket(UUID id, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Ticket", "id", id));

        User currentUser = getUserByUsername(username);

        boolean isCreator = ticket.getCreatedBy()
            .getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isCreator && !isAdmin) {
            throw new UnauthorizedException(
                "You don't have permission to delete this ticket");
        }

        ticketRepository.delete(ticket);
    }

    public Page<TicketResponse> searchTickets(String searchTerm,
                                               Pageable pageable) {
        return ticketRepository.searchTickets(searchTerm, pageable)
                .map(ticket -> mapToTicketResponse(ticket));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User", "username", username));
    }

    public TicketResponse mapToTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdBy(userService.mapToUserResponse(ticket.getCreatedBy()))
                .assignedTo(ticket.getAssignedTo() != null ?
                    userService.mapToUserResponse(ticket.getAssignedTo()) : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .commentCount(commentRepository.countByTicket(ticket))
                .build();
    }
}
