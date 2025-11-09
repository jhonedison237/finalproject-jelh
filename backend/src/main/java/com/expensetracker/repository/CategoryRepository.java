package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all categories for a user
     * @param userId the user's ID
     * @return list of categories
     */
    List<Category> findByUserId(Long userId);

    /**
     * Find all active categories for a user
     * @param userId the user's ID
     * @return list of active categories
     */
    List<Category> findByUserIdAndActiveTrue(Long userId);

    /**
     * Find category by ID and user ID
     * @param id the category ID
     * @param userId the user's ID
     * @return Optional containing the category if found
     */
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    /**
     * Find category by name and user ID
     * @param name the category name
     * @param userId the user's ID
     * @return Optional containing the category if found
     */
    Optional<Category> findByNameAndUserId(String name, Long userId);

    /**
     * Check if category exists for user
     * @param name the category name
     * @param userId the user's ID
     * @return true if category exists
     */
    boolean existsByNameAndUserId(String name, Long userId);

    /**
     * Find all default categories
     * @return list of default categories
     */
    List<Category> findByIsDefaultTrue();

    /**
     * Count active categories for a user
     * @param userId the user's ID
     * @return count of active categories
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.user.id = :userId AND c.active = true")
    long countActiveByUserId(@Param("userId") Long userId);
}

