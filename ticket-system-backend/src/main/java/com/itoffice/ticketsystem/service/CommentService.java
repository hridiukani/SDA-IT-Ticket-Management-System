package com.itoffice.ticketsystem.service;

import com.itoffice.ticketsystem.dto.request.CreateCommentRequest;
import com.itoffice.ticketsystem.dto.response.CommentResponse;
import com.itoffice.ticketsystem.exception.ResourceNotFoundException;
import com.itoffice.ticketsystem.exception.UnauthorizedException;
import com.itoffice.ticketsystem.model.Comment;
import com.itoffice.ticketsystem.model.Ticket;
import com.itoffice.ticketsystem.model.User;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.repository.CommentRepository;
import com.itoffice.ticketsystem.repository.TicketRepository;
import com.itoffice.ticketsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<CommentResponse> getCommentsByTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Ticket", "id", ticketId));

        return commentRepository
            .findByTicketOrderByCreatedAtDesc(ticket)
            .stream()
            .map(this::mapToCommentResponse)
            .collect(Collectors.toList());
    }

    public CommentResponse addComment(UUID ticketId,
                                       CreateCommentRequest request,
                                       String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Ticket", "id", ticketId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User", "username", username));

        Comment comment = Comment.builder()
                .ticket(ticket)
                .user(user)
                .content(request.getContent())
                .build();

        return mapToCommentResponse(commentRepository.save(comment));
    }

    public void deleteComment(UUID commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Comment", "id", commentId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User", "username", username));

        boolean isOwner = comment.getUser()
            .getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(
                "You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(userService.mapToUserResponse(comment.getUser()))
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
