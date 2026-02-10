package com.itoffice.ticketsystem.model.enums;

/**
 * Priority level of a ticket
 *
 * - LOW: Non-urgent issues, can be addressed in normal schedule
 * - MEDIUM: Standard priority, should be addressed within regular timeframe
 * - HIGH: Important issues that need prompt attention
 * - CRITICAL: Urgent issues requiring immediate attention
 */
public enum TicketPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
