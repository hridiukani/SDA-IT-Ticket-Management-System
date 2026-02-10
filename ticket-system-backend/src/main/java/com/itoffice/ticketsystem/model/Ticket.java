package com.itoffice.ticketsystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.itoffice.ticketsystem.model.enums.TicketPriority;
import com.itoffice.ticketsystem.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Ticket entity representing an IT support ticket
 *
 * Tickets are created by users and can be assigned to technicians for resolution.
 * Each ticket has a status, priority, and can have multiple comments.
 */
@Entity
@Table(name = "tickets", indexes = {
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_priority", columnList = "priority"),
        @Index(name = "idx_ticket_created_by", columnList = "created_by_id"),
        @Index(name = "idx_ticket_assigned_to", columnList = "assigned_to_id"),
        @Index(name = "idx_ticket_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private TicketPriority priority = TicketPriority.MEDIUM;

    // Relationship: User who created this ticket
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @JsonBackReference(value = "user-created-tickets")
    private User createdBy;

    // Relationship: User assigned to resolve this ticket
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    @JsonBackReference(value = "user-assigned-tickets")
    private User assignedTo;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    // Relationship: Comments on this ticket
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference(value = "ticket-comments")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    /**
     * Get the ticket number for display (first 8 characters of UUID)
     */
    public String getTicketNumber() {
        return id != null ? id.toString().substring(0, 8).toUpperCase() : null;
    }

    /**
     * Check if ticket is open
     */
    public boolean isOpen() {
        return status == TicketStatus.OPEN;
    }

    /**
     * Check if ticket is in progress
     */
    public boolean isInProgress() {
        return status == TicketStatus.IN_PROGRESS;
    }

    /**
     * Check if ticket is resolved
     */
    public boolean isResolved() {
        return status == TicketStatus.RESOLVED;
    }

    /**
     * Check if ticket is closed
     */
    public boolean isClosed() {
        return status == TicketStatus.CLOSED;
    }

    /**
     * Check if ticket is assigned
     */
    public boolean isAssigned() {
        return assignedTo != null;
    }

    /**
     * Check if ticket is high priority or critical
     */
    public boolean isHighPriority() {
        return priority == TicketPriority.HIGH || priority == TicketPriority.CRITICAL;
    }

    /**
     * Check if ticket is critical
     */
    public boolean isCritical() {
        return priority == TicketPriority.CRITICAL;
    }

    /**
     * Add a comment to this ticket
     */
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setTicket(this);
    }

    /**
     * Remove a comment from this ticket
     */
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setTicket(null);
    }
}
