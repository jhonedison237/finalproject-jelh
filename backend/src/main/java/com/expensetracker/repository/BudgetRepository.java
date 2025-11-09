package com.expensetracker.repository;

import com.expensetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Budget entity
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Find budget by ID and user ID
     * @param id the budget ID
     * @param userId the user's ID
     * @return Optional containing the budget if found
     */
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    /**
     * Find all budgets for a user
     * @param userId the user's ID
     * @return list of budgets
     */
    List<Budget> findByUserId(Long userId);

    /**
     * Find active budgets for a user
     * @param userId the user's ID
     * @return list of active budgets
     */
    List<Budget> findByUserIdAndActiveTrue(Long userId);

    /**
     * Find budgets for a user in a specific month and year
     * @param userId the user's ID
     * @param month the month
     * @param year the year
     * @return list of budgets
     */
    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    /**
     * Find budget by user, category, month and year
     * @param userId the user's ID
     * @param categoryId the category ID
     * @param month the month
     * @param year the year
     * @return Optional containing the budget if found
     */
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId, 
            Long categoryId, 
            Integer month, 
            Integer year
    );

    /**
     * Check if budget exists for user, category, month and year
     * @param userId the user's ID
     * @param categoryId the category ID
     * @param month the month
     * @param year the year
     * @return true if budget exists
     */
    boolean existsByUserIdAndCategoryIdAndMonthAndYear(
            Long userId, 
            Long categoryId, 
            Integer month, 
            Integer year
    );

    /**
     * Find budgets that have reached alert threshold
     * @param userId the user's ID
     * @return list of budgets
     */
    @Query("SELECT b FROM Budget b " +
           "WHERE b.user.id = :userId " +
           "AND b.active = true " +
           "AND b.alertEnabled = true " +
           "AND (b.spentAmount / b.limitAmount * 100) >= b.alertThreshold")
    List<Budget> findBudgetsReachedAlertThreshold(@Param("userId") Long userId);
}

