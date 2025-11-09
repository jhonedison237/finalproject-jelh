package com.expensetracker.repository;

import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Transaction entity
 * Main repository for the ET-001 ticket
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ========== Basic Queries ==========

    /**
     * Find transaction by ID and user ID
     * @param id the transaction ID
     * @param userId the user's ID
     * @return Optional containing the transaction if found
     */
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    /**
     * Find all transactions for a user (paginated)
     * @param userId the user's ID
     * @param pageable pagination information
     * @return page of transactions
     */
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    /**
     * Find all active transactions for a user (paginated)
     * @param userId the user's ID
     * @param pageable pagination information
     * @return page of active transactions
     */
    Page<Transaction> findByUserIdAndActiveTrue(Long userId, Pageable pageable);

    // ========== Date Range Queries ==========

    /**
     * Find transactions by user and date range
     * @param userId the user's ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @param pageable pagination information
     * @return page of transactions
     */
    Page<Transaction> findByUserIdAndTransactionDateBetween(
            Long userId, 
            LocalDate startDate, 
            LocalDate endDate, 
            Pageable pageable
    );

    /**
     * Find active transactions by user and date range
     * @param userId the user's ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @param pageable pagination information
     * @return page of active transactions
     */
    Page<Transaction> findByUserIdAndActiveTrueAndTransactionDateBetween(
            Long userId, 
            LocalDate startDate, 
            LocalDate endDate, 
            Pageable pageable
    );

    // ========== Category Queries ==========

    /**
     * Find transactions by user and category
     * @param userId the user's ID
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return page of transactions
     */
    Page<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    /**
     * Find transactions by user, category and date range
     * @param userId the user's ID
     * @param categoryId the category ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndCategoryIdAndTransactionDateBetween(
            Long userId, 
            Long categoryId, 
            LocalDate startDate, 
            LocalDate endDate
    );

    // ========== Type Queries ==========

    /**
     * Find transactions by user and type
     * @param userId the user's ID
     * @param transactionType the transaction type
     * @param pageable pagination information
     * @return page of transactions
     */
    Page<Transaction> findByUserIdAndTransactionType(
            Long userId, 
            TransactionType transactionType, 
            Pageable pageable
    );

    // ========== Aggregation Queries ==========

    /**
     * Calculate total income for a user in a date range
     * @param userId the user's ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return total income amount
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.transactionType = 'INCOME' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.active = true")
    BigDecimal calculateTotalIncomeByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Calculate total expenses for a user in a date range (returns positive value)
     * @param userId the user's ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return total expenses amount (absolute value)
     */
    @Query("SELECT COALESCE(ABS(SUM(t.amount)), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.active = true")
    BigDecimal calculateTotalExpensesByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Calculate total expenses by category in a date range
     * @param userId the user's ID
     * @param categoryId the category ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return total expenses amount (absolute value)
     */
    @Query("SELECT COALESCE(ABS(SUM(t.amount)), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.category.id = :categoryId " +
           "AND t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.active = true")
    BigDecimal calculateExpensesByCategoryAndDateRange(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Get expense summary by category for a user in a date range
     * Returns category name and total expenses
     * @param userId the user's ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return list of Object arrays [categoryName, totalAmount]
     */
    @Query("SELECT c.name, COALESCE(ABS(SUM(t.amount)), 0) " +
           "FROM Transaction t " +
           "JOIN t.category c " +
           "WHERE t.user.id = :userId " +
           "AND t.transactionType = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.active = true " +
           "GROUP BY c.id, c.name " +
           "ORDER BY SUM(t.amount) ASC")
    List<Object[]> getExpensesByCategoryGrouped(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ========== Count Queries ==========

    /**
     * Count transactions for a user
     * @param userId the user's ID
     * @return count of transactions
     */
    long countByUserId(Long userId);

    /**
     * Count active transactions for a user
     * @param userId the user's ID
     * @return count of active transactions
     */
    long countByUserIdAndActiveTrue(Long userId);

    /**
     * Count transactions by user and date range
     * @param userId the user's ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return count of transactions
     */
    long countByUserIdAndTransactionDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    // ========== Recent Transactions ==========

    /**
     * Find recent transactions for a user (limited)
     * @param userId the user's ID
     * @param pageable pagination information (use for limiting)
     * @return page of recent transactions
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.active = true " +
           "ORDER BY t.transactionDate DESC, t.createdAt DESC")
    Page<Transaction> findRecentTransactions(@Param("userId") Long userId, Pageable pageable);
}

