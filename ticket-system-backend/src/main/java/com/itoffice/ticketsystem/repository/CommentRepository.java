package com.itoffice.ticketsystem.repository;

import com.itoffice.ticketsystem.model.Comment;
import com.itoffice.ticketsystem.model.Ticket;
import com.itoffice.ticketsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTicket(Ticket ticket);
    List<Comment> findByTicketOrderByCreatedAtDesc(Ticket ticket);
    List<Comment> findByUser(User user);
    long countByTicket(Ticket ticket);
}
