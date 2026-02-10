# Entity Design Documentation

## Overview

This document describes the JPA entity model for the IT Ticket Management System.

## Entity Relationship Diagram

```
┌─────────────────┐
│      User       │
├─────────────────┤
│ UUID id (PK)    │
│ username        │
│ email           │
│ password        │
│ role (enum)     │
│ enabled         │
│ firstName       │
│ lastName        │
│ phone           │
│ createdAt       │
│ updatedAt       │
└────────┬────────┘
         │
         │ 1:N (created)
         │
         ├──────────────────┐
         │                  │
         │ 1:N (assigned)   │ 1:N (comments)
         │                  │
    ┌────▼────────┐    ┌────▼────────┐
    │   Ticket    │    │   Comment   │
    ├─────────────┤    ├─────────────┤
    │ UUID id(PK) │    │ UUID id(PK) │
    │ title       │◄───┤ ticket_id   │
    │ description │ 1:N│ user_id     │
    │ status      │    │ content     │
    │ priority    │    │ internal    │
    │ created_by  │    │ createdAt   │
    │ assigned_to │    └─────────────┘
    │ createdAt   │
    │ updatedAt   │
    │ resolvedAt  │
    │ closedAt    │
    └─────────────┘
```

## Entities

### 1. User Entity

**Location:** `com.itoffice.ticketsystem.model.User`

**Purpose:** Represents system users with different roles

**Fields:**
- `id`: UUID - Primary key
- `username`: String (50) - Unique, not null
- `email`: String (100) - Unique, not null
- `password`: String - Hashed password, not null, @JsonIgnore
- `role`: Role enum - User role, not null
- `enabled`: boolean - Account status, default true
- `firstName`: String (50) - Optional
- `lastName`: String (50) - Optional
- `phone`: String (20) - Optional
- `createdAt`: LocalDateTime - Auto-populated
- `updatedAt`: LocalDateTime - Auto-updated

**Relationships:**
- `createdTickets`: One-to-Many with Ticket (as creator)
- `assignedTickets`: One-to-Many with Ticket (as assignee)
- `comments`: One-to-Many with Comment

**Indexes:**
- `idx_user_username` on username
- `idx_user_email` on email
- `idx_user_role` on role

**Helper Methods:**
- `getFullName()`: Returns formatted full name
- `isAdmin()`: Check if user is admin
- `isTechnician()`: Check if user is technician or higher
- `isManager()`: Check if user is manager or higher

---

### 2. Ticket Entity

**Location:** `com.itoffice.ticketsystem.model.Ticket`

**Purpose:** Represents an IT support ticket

**Fields:**
- `id`: UUID - Primary key
- `title`: String (200) - Not null
- `description`: Text (2000) - Optional
- `status`: TicketStatus enum - Default OPEN
- `priority`: TicketPriority enum - Default MEDIUM
- `createdBy`: User - Many-to-One, not null
- `assignedTo`: User - Many-to-One, nullable
- `createdAt`: LocalDateTime - Auto-populated
- `updatedAt`: LocalDateTime - Auto-updated
- `resolvedAt`: LocalDateTime - When resolved
- `closedAt`: LocalDateTime - When closed

**Relationships:**
- `createdBy`: Many-to-One with User
- `assignedTo`: Many-to-One with User (nullable)
- `comments`: One-to-Many with Comment (cascade delete)

**Indexes:**
- `idx_ticket_status` on status
- `idx_ticket_priority` on priority
- `idx_ticket_created_by` on created_by_id
- `idx_ticket_assigned_to` on assigned_to_id
- `idx_ticket_created_at` on created_at

**Helper Methods:**
- `getTicketNumber()`: Returns first 8 chars of UUID
- `isOpen()`, `isInProgress()`, `isResolved()`, `isClosed()`
- `isAssigned()`: Check if ticket has assignee
- `isHighPriority()`, `isCritical()`: Priority checks
- `addComment()`, `removeComment()`: Comment management

---

### 3. Comment Entity

**Location:** `com.itoffice.ticketsystem.model.Comment`

**Purpose:** Represents a comment on a ticket

**Fields:**
- `id`: UUID - Primary key
- `ticket`: Ticket - Many-to-One, not null
- `user`: User - Many-to-One, not null
- `content`: String (1000) - Not null
- `internal`: boolean - Internal note flag, default false
- `createdAt`: LocalDateTime - Auto-populated

**Relationships:**
- `ticket`: Many-to-One with Ticket
- `user`: Many-to-One with User

**Indexes:**
- `idx_comment_ticket` on ticket_id
- `idx_comment_user` on user_id
- `idx_comment_created_at` on created_at

**Helper Methods:**
- `isInternal()`: Check if internal comment
- `isFromTicketOwner()`: Check if from ticket creator
- `isFromAssignedTechnician()`: Check if from assignee
- `getPreview()`: Returns first 100 characters

---

## Enums

### 1. Role

**Location:** `com.itoffice.ticketsystem.model.enums.Role`

**Values:**
- `ROLE_USER`: Regular user (create/view own tickets)
- `ROLE_TECHNICIAN`: Technical staff (assigned tickets)
- `ROLE_MANAGER`: Manager (department oversight)
- `ROLE_ADMIN`: System administrator (full access)

---

### 2. TicketStatus

**Location:** `com.itoffice.ticketsystem.model.enums.TicketStatus`

**Values:**
- `OPEN`: Newly created, awaiting assignment
- `IN_PROGRESS`: Being worked on
- `RESOLVED`: Issue resolved, awaiting confirmation
- `CLOSED`: Completed and closed

**Status Flow:**
```
OPEN → IN_PROGRESS → RESOLVED → CLOSED
  ↓         ↓           ↓
  └─────────┴───────────┘
  (Can reopen to previous state)
```

---

### 3. TicketPriority

**Location:** `com.itoffice.ticketsystem.model.enums.TicketPriority`

**Values:**
- `LOW`: Non-urgent (SLA: 72 hours)
- `MEDIUM`: Standard (SLA: 24 hours)
- `HIGH`: Important (SLA: 4 hours)
- `CRITICAL`: Urgent (SLA: immediate)

---

## JSON Serialization Strategy

### @JsonManagedReference / @JsonBackReference

To prevent infinite recursion in JSON serialization:

**Managed References (include in JSON):**
- User → createdTickets
- User → assignedTickets
- User → comments
- Ticket → comments

**Back References (exclude from JSON):**
- Ticket → createdBy
- Ticket → assignedTo
- Comment → ticket
- Comment → user

### @JsonIgnore

- User.password: Never serialize passwords

---

## Auditing Configuration

**JpaConfig** (`com.itoffice.ticketsystem.config.JpaConfig`)

- Enables JPA Auditing with `@EnableJpaAuditing`
- Provides `AuditorAware<String>` bean
- Auto-populates `@CreatedDate` and `@LastModifiedDate`
- Uses authenticated username from SecurityContext
- Falls back to "system" for unauthenticated operations

---

## Database Indexes

All entities have appropriate indexes for:
- Foreign keys (relationships)
- Frequently queried fields (status, priority, role)
- Timestamp fields (for sorting/filtering)
- Unique constraints (username, email)

---

## Cascade Operations

### User Entity
- `CascadeType.ALL` on all relationships
- Deleting a user cascades to their tickets and comments

### Ticket Entity
- `CascadeType.ALL` on comments
- `orphanRemoval = true` on comments
- Deleting a ticket deletes all comments

### Comment Entity
- No cascade operations (leaf entity)

---

## Fetch Strategies

All relationships use `FetchType.LAZY` by default to avoid:
- N+1 query problems
- Loading unnecessary data
- Performance issues

Use `@EntityGraph` or `JOIN FETCH` in repositories when needed.

---

## Validation

Validation is handled at the DTO layer, not in entities.
Entities define database constraints only:
- `@Column(nullable = false)`
- `@Column(unique = true)`
- `@Column(length = X)`

---

## Best Practices Applied

1. ✅ UUID primary keys for distributed systems
2. ✅ Lombok for reduced boilerplate
3. ✅ Builder pattern for object creation
4. ✅ Auditing with @CreatedDate/@LastModifiedDate
5. ✅ Proper bidirectional relationship management
6. ✅ Lazy loading by default
7. ✅ Indexes on foreign keys and frequently queried fields
8. ✅ Cascade delete for dependent entities
9. ✅ JSON serialization control
10. ✅ Helper methods for common business logic

---

## Next Steps

1. Create Spring Data JPA Repositories
2. Create DTOs for API requests/responses
3. Create MapStruct mappers for entity-DTO conversion
4. Create Flyway migration scripts
5. Write unit tests for entity relationships
