package com.expensetracker.service;

import com.expensetracker.entity.User;

import java.util.Optional;

/**
 * Service interface for User operations
 * Basic operations needed for authentication and transaction management
 */
public interface UserService {

    /**
     * Find user by ID
     * @param id user ID
     * @return user if found
     */
    Optional<User> findById(Long id);

    /**
     * Find user by email
     * @param email user email
     * @return user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     * @param username username
     * @return user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if email already exists
     * @param email email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}

