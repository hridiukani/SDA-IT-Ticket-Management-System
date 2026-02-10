package com.itoffice.ticketsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class for IT Office Ticket Management System
 *
 * This application provides a comprehensive ticket management system with:
 * - User authentication and authorization (JWT)
 * - Role-based access control (Admin, Agent, User)
 * - Ticket lifecycle management
 * - Department and category organization
 * - Email notifications
 * - File attachments
 * - SLA tracking
 * - Audit logging
 *
 * @author IT Office
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class TicketSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketSystemApplication.class, args);
    }

}
