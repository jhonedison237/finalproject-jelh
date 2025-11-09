package com.expensetracker.repository;

import com.expensetracker.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserSession entity
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Find session by JWT token
     * @param jwtToken the JWT token
     * @return Optional containing the session if found
     */
    Optional<UserSession> findByJwtToken(String jwtToken);

    /**
     * Find active session by JWT token
     * @param jwtToken the JWT token
     * @return Optional containing the session if found and active
     */
    Optional<UserSession> findByJwtTokenAndActiveTrue(String jwtToken);

    /**
     * Find all active sessions for a user
     * @param userId the user's ID
     * @return list of active sessions
     */
    List<UserSession> findByUserIdAndActiveTrue(Long userId);

    /**
     * Find all sessions for a user
     * @param userId the user's ID
     * @return list of sessions
     */
    List<UserSession> findByUserId(Long userId);

    /**
     * Check if JWT token exists
     * @param jwtToken the JWT token
     * @return true if token exists
     */
    boolean existsByJwtToken(String jwtToken);

    /**
     * Delete expired sessions
     * @param now current timestamp
     */
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);

    /**
     * Invalidate all active sessions for a user
     * @param userId the user's ID
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.user.id = :userId AND s.active = true")
    void invalidateAllUserSessions(@Param("userId") Long userId);

    /**
     * Invalidate session by token
     * @param jwtToken the JWT token
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.jwtToken = :jwtToken")
    void invalidateSessionByToken(@Param("jwtToken") String jwtToken);
}

