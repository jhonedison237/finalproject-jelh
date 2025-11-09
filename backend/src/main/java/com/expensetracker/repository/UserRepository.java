package com.expensetracker.repository;

import com.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     * @param email the user's email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     * @param username the user's username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if user exists by email
     * @param email the user's email
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists by username
     * @param username the user's username
     * @return true if user exists
     */
    boolean existsByUsername(String username);

    /**
     * Find active user by email
     * @param email the user's email
     * @return Optional containing the user if found and active
     */
    Optional<User> findByEmailAndActiveTrue(String email);
}

