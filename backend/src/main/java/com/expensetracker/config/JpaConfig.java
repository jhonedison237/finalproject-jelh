package com.expensetracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration
 * Enables JPA auditing for automatic timestamp management
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // JPA Auditing configuration
    // This enables @CreatedDate and @LastModifiedDate annotations in entities
    // Note: Ensure this is the ONLY place where @EnableJpaAuditing is declared
}

