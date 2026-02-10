package com.itoffice.ticketsystem.repository;

import com.itoffice.ticketsystem.model.Ticket;
import com.itoffice.ticketsystem.model.User;
import com.itoffice.ticketsystem.model.enums.TicketPriority;
import com.itoffice.ticketsystem.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    // Find by status
    List<Ticket> findByStatus(TicketStatus status);
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    // Find by priority
    List<Ticket> findByPriority(TicketPriority priority);
    Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);

    // Find by user (creator)
    List<Ticket> findByCreatedBy(User user);
    Page<Ticket> findByCreatedBy(User user, Pageable pageable);

    // Find by assigned user
    List<Ticket> findByAssignedTo(User user);
    Page<Ticket> findByAssignedTo(User user, Pageable pageable);

    // Find by status and priority
    Page<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority, Pageable pageable);

    // Custom query: Find tickets by status for a specific user (creator)
    @Query("SELECT t FROM Ticket t WHERE t.createdBy = :user AND t.status = :status")
    List<Ticket> findByCreatedByAndStatus(@Param("user") User user, @Param("status") TicketStatus status);

    // Custom query: Search tickets by title or description
    @Query("SELECT t FROM Ticket t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Ticket> searchTickets(@Param("searchTerm") String searchTerm, Pageable pageable);
}
