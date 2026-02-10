package com.itoffice.ticketsystem.model.enums;

/**
 * Status of a ticket in its lifecycle
 *
 * - OPEN: Ticket has been created and is awaiting assignment
 * - IN_PROGRESS: Ticket has been assigned and is being worked on
 * - RESOLVED: Issue has been resolved, awaiting user confirmation
 * - CLOSED: Ticket is completed and closed
 */
public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
