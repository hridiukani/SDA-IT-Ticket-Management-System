package com.itoffice.ticketsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Configuration
 *
 * Enables JPA Auditing to automatically populate @CreatedDate and @LastModifiedDate
 * fields in entities that use @EntityListeners(AuditingEntityListener.class)
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    /**
     * Provides the current auditor (authenticated user) for JPA auditing
     *
     * This allows automatic population of @CreatedBy and @LastModifiedBy fields
     * with the currently authenticated user's username
     *
     * @return AuditorAware instance
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system");
            }

            String username = authentication.getName();
            return Optional.of(username != null ? username : "anonymous");
        };
    }
}
