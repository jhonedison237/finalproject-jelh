package com.expensetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * UserSession entity for managing JWT tokens and active sessions
 * Maps to 'user_sessions' table in the database
 */
@Entity
@Table(name = "user_sessions", indexes = {
    @Index(name = "idx_user_sessions_token", columnList = "jwt_token", unique = true),
    @Index(name = "idx_user_sessions_user_active", columnList = "user_id, active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "JWT token is required")
    @Size(max = 500, message = "JWT token must not exceed 500 characters")
    @Column(name = "jwt_token", nullable = false, unique = true, length = 500)
    private String jwtToken;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Size(max = 45, message = "IP address must not exceed 45 characters")
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Size(max = 255, message = "User agent must not exceed 255 characters")
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Checks if the session has expired
     * @return true if the session has expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Checks if the session is valid (active and not expired)
     * @return true if the session is valid
     */
    public boolean isValid() {
        return active && !isExpired();
    }

    /**
     * Invalidates the session by setting active to false
     */
    public void invalidate() {
        this.active = false;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", expiresAt=" + expiresAt +
                ", ipAddress='" + ipAddress + '\'' +
                ", active=" + active +
                ", expired=" + isExpired() +
                '}';
    }
}

