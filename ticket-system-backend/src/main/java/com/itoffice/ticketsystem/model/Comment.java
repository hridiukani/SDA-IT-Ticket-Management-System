package com.itoffice.ticketsystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Comment entity representing a comment on a ticket
 *
 * Comments allow users and technicians to communicate about ticket resolution.
 * Each comment is associated with a ticket and a user who created it.
 */
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comment_ticket", columnList = "ticket_id"),
        @Index(name = "idx_comment_user", columnList = "user_id"),
        @Index(name = "idx_comment_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Relationship: Ticket this comment belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @JsonBackReference(value = "ticket-comments")
    private Ticket ticket;

    // Relationship: User who created this comment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-comments")
    private User user;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "is_internal", nullable = false)
    @Builder.Default
    private boolean internal = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Check if this is an internal comment (visible only to staff)
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * Check if comment is created by the ticket owner
     */
    public boolean isFromTicketOwner() {
        return ticket != null && user != null &&
               ticket.getCreatedBy() != null &&
               user.getId().equals(ticket.getCreatedBy().getId());
    }

    /**
     * Check if comment is created by assigned technician
     */
    public boolean isFromAssignedTechnician() {
        return ticket != null && user != null &&
               ticket.getAssignedTo() != null &&
               user.getId().equals(ticket.getAssignedTo().getId());
    }

    /**
     * Get comment preview (first 100 characters)
     */
    public String getPreview() {
        if (content == null) {
            return "";
        }
        return content.length() > 100 ? content.substring(0, 97) + "..." : content;
    }
}
