package com.expensetracker.service;

import com.expensetracker.dto.request.TransactionCreateDTO;
import com.expensetracker.dto.request.TransactionUpdateDTO;
import com.expensetracker.dto.response.TransactionResponseDTO;
import com.expensetracker.dto.response.TransactionSummaryDTO;
import com.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for Transaction operations
 * Main service for ET-001 ticket
 */
public interface TransactionService {

    /**
     * Create a new transaction
     * @param dto transaction data
     * @param user authenticated user
     * @return created transaction
     */
    TransactionResponseDTO createTransaction(TransactionCreateDTO dto, User user);

    /**
     * Update an existing transaction
     * @param id transaction ID
     * @param dto update data
     * @param user authenticated user
     * @return updated transaction
     */
    TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO dto, User user);

    /**
     * Delete a transaction (soft delete)
     * @param id transaction ID
     * @param user authenticated user
     */
    void deleteTransaction(Long id, User user);

    /**
     * Get transaction by ID
     * @param id transaction ID
     * @param user authenticated user
     * @return transaction details
     */
    TransactionResponseDTO getTransactionById(Long id, User user);

    /**
     * Get all transactions for a user (paginated)
     * @param user authenticated user
     * @param pageable pagination information
     * @return page of transactions
     */
    Page<TransactionSummaryDTO> getUserTransactions(User user, Pageable pageable);

    /**
     * Get transactions by date range
     * @param user authenticated user
     * @param startDate start date
     * @param endDate end date
     * @param pageable pagination information
     * @return page of transactions
     */
    Page<TransactionSummaryDTO> getTransactionsByDateRange(
            User user, 
            LocalDate startDate, 
            LocalDate endDate, 
            Pageable pageable
    );

    /**
     * Get transactions by category
     * @param user authenticated user
     * @param categoryId category ID
     * @param pageable pagination information
     * @return page of transactions
     */
    Page<TransactionSummaryDTO> getTransactionsByCategory(
            User user, 
            Long categoryId, 
            Pageable pageable
    );

    /**
     * Get recent transactions
     * @param user authenticated user
     * @param limit number of transactions to retrieve
     * @return list of recent transactions
     */
    List<TransactionSummaryDTO> getRecentTransactions(User user, int limit);

    /**
     * Calculate total income for a date range
     * @param user authenticated user
     * @param startDate start date
     * @param endDate end date
     * @return total income
     */
    BigDecimal calculateTotalIncome(User user, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate total expenses for a date range
     * @param user authenticated user
     * @param startDate start date
     * @param endDate end date
     * @return total expenses (positive value)
     */
    BigDecimal calculateTotalExpenses(User user, LocalDate startDate, LocalDate endDate);

    /**
     * Get expense summary by category
     * @param user authenticated user
     * @param startDate start date
     * @param endDate end date
     * @return map of category name to total amount
     */
    Map<String, BigDecimal> getExpensesByCategory(User user, LocalDate startDate, LocalDate endDate);

    /**
     * Get transaction count for user
     * @param user authenticated user
     * @return total count
     */
    long getTransactionCount(User user);
}

