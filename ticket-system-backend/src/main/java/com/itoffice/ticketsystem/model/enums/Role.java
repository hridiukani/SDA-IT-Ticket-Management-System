package com.itoffice.ticketsystem.model.enums;

/**
 * User roles in the ticket management system
 *
 * - ROLE_USER: Regular user who can create and view their own tickets
 * - ROLE_TECHNICIAN: Technical staff who can be assigned tickets and resolve them
 * - ROLE_MANAGER: Manager who can view and manage tickets within their department
 * - ROLE_ADMIN: System administrator with full access
 */
public enum Role {
    ROLE_USER,
    ROLE_TECHNICIAN,
    ROLE_MANAGER,
    ROLE_ADMIN
}
