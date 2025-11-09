package com.expensetracker.service.impl;

import com.expensetracker.dto.request.TransactionCreateDTO;
import com.expensetracker.dto.request.TransactionUpdateDTO;
import com.expensetracker.dto.response.TransactionResponseDTO;
import com.expensetracker.dto.response.TransactionSummaryDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.entity.enums.TransactionType;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.BusinessValidationException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of TransactionService
 * Main service implementation for ET-001 ticket
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionCreateDTO dto, User user) {
        log.debug("Creating transaction for user: {}", user.getId());

        // Validate category belongs to user
        Category category = categoryRepository.findByIdAndUserId(dto.getCategoryId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

        // Validate amount sign matches transaction type
        validateAmountAndType(dto.getAmount(), dto.getTransactionType());

        // Create transaction entity
        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(adjustAmountSign(dto.getAmount(), dto.getTransactionType()))
                .description(dto.getDescription())
                .transactionDate(dto.getTransactionDate())
                .transactionType(dto.getTransactionType())
                .paymentMethod(dto.getPaymentMethod())
                .notes(dto.getNotes())
                .active(true)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created successfully with ID: {}", savedTransaction.getId());

        return mapToResponseDTO(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO dto, User user) {
        log.debug("Updating transaction {} for user: {}", id, user.getId());

        // Find transaction and verify ownership
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        // Update category if provided
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUserId(dto.getCategoryId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
            transaction.setCategory(category);
        }

        // Update amount and type
        if (dto.getAmount() != null && dto.getTransactionType() != null) {
            validateAmountAndType(dto.getAmount(), dto.getTransactionType());
            transaction.setAmount(adjustAmountSign(dto.getAmount(), dto.getTransactionType()));
            transaction.setTransactionType(dto.getTransactionType());
        } else if (dto.getAmount() != null) {
            validateAmountAndType(dto.getAmount(), transaction.getTransactionType());
            transaction.setAmount(adjustAmountSign(dto.getAmount(), transaction.getTransactionType()));
        } else if (dto.getTransactionType() != null) {
            transaction.setTransactionType(dto.getTransactionType());
            transaction.setAmount(adjustAmountSign(transaction.getAmount().abs(), dto.getTransactionType()));
        }

        // Update other fields if provided
        if (dto.getDescription() != null) {
            transaction.setDescription(dto.getDescription());
        }
        if (dto.getTransactionDate() != null) {
            transaction.setTransactionDate(dto.getTransactionDate());
        }
        if (dto.getPaymentMethod() != null) {
            transaction.setPaymentMethod(dto.getPaymentMethod());
        }
        if (dto.getNotes() != null) {
            transaction.setNotes(dto.getNotes());
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transaction {} updated successfully", id);

        return mapToResponseDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id, User user) {
        log.debug("Deleting transaction {} for user: {}", id, user.getId());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        // Soft delete
        transaction.setActive(false);
        transactionRepository.save(transaction);

        log.info("Transaction {} deleted successfully (soft delete)", id);
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id, User user) {
        log.debug("Getting transaction {} for user: {}", id, user.getId());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        return mapToResponseDTO(transaction);
    }

    @Override
    public Page<TransactionSummaryDTO> getUserTransactions(User user, Pageable pageable) {
        log.debug("Getting transactions for user: {}", user.getId());

        Page<Transaction> transactions = transactionRepository.findByUserIdAndActiveTrue(
                user.getId(), 
                pageable
        );

        return transactions.map(this::mapToSummaryDTO);
    }

    @Override
    public Page<TransactionSummaryDTO> getTransactionsByDateRange(
            User user, 
            LocalDate startDate, 
            LocalDate endDate, 
            Pageable pageable) {
        
        log.debug("Getting transactions for user {} between {} and {}", 
                user.getId(), startDate, endDate);

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }

        Page<Transaction> transactions = transactionRepository
                .findByUserIdAndActiveTrueAndTransactionDateBetween(
                        user.getId(), 
                        startDate, 
                        endDate, 
                        pageable
                );

        return transactions.map(this::mapToSummaryDTO);
    }

    @Override
    public Page<TransactionSummaryDTO> getTransactionsByCategory(
            User user, 
            Long categoryId, 
            Pageable pageable) {
        
        log.debug("Getting transactions for user {} in category {}", user.getId(), categoryId);

        // Verify category belongs to user
        categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Page<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(
                user.getId(), 
                categoryId, 
                pageable
        );

        return transactions.map(this::mapToSummaryDTO);
    }

    @Override
    public List<TransactionSummaryDTO> getRecentTransactions(User user, int limit) {
        log.debug("Getting {} recent transactions for user: {}", limit, user.getId());

        Pageable pageable = PageRequest.of(0, limit, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionRepository.findRecentTransactions(
                user.getId(), 
                pageable
        );

        return transactions.getContent().stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateTotalIncome(User user, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating total income for user {} between {} and {}", 
                user.getId(), startDate, endDate);

        return transactionRepository.calculateTotalIncomeByDateRange(
                user.getId(), 
                startDate, 
                endDate
        );
    }

    @Override
    public BigDecimal calculateTotalExpenses(User user, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating total expenses for user {} between {} and {}", 
                user.getId(), startDate, endDate);

        return transactionRepository.calculateTotalExpensesByDateRange(
                user.getId(), 
                startDate, 
                endDate
        );
    }

    @Override
    public Map<String, BigDecimal> getExpensesByCategory(
            User user, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        log.debug("Getting expenses by category for user {} between {} and {}", 
                user.getId(), startDate, endDate);

        List<Object[]> results = transactionRepository.getExpensesByCategoryGrouped(
                user.getId(), 
                startDate, 
                endDate
        );

        Map<String, BigDecimal> expensesByCategory = new LinkedHashMap<>();
        for (Object[] result : results) {
            String categoryName = (String) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            expensesByCategory.put(categoryName, amount);
        }

        return expensesByCategory;
    }

    @Override
    public long getTransactionCount(User user) {
        return transactionRepository.countByUserIdAndActiveTrue(user.getId());
    }

    // ========== Private Helper Methods ==========

    /**
     * Validates that amount and transaction type are consistent
     */
    private void validateAmountAndType(BigDecimal amount, TransactionType type) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
    }

    /**
     * Adjusts amount sign based on transaction type
     * Income: positive, Expense: negative
     */
    private BigDecimal adjustAmountSign(BigDecimal amount, TransactionType type) {
        BigDecimal absoluteAmount = amount.abs();
        return type == TransactionType.INCOME ? absoluteAmount : absoluteAmount.negate();
    }

    /**
     * Maps Transaction entity to TransactionResponseDTO
     */
    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .absoluteAmount(transaction.getAbsoluteAmount())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .transactionType(transaction.getTransactionType())
                .paymentMethod(transaction.getPaymentMethod())
                .notes(transaction.getNotes())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .categoryColor(transaction.getCategory().getColor())
                .categoryIcon(transaction.getCategory().getIcon())
                .userId(transaction.getUser().getId())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .active(transaction.getActive())
                .build();
    }

    /**
     * Maps Transaction entity to TransactionSummaryDTO
     */
    private TransactionSummaryDTO mapToSummaryDTO(Transaction transaction) {
        return TransactionSummaryDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .absoluteAmount(transaction.getAbsoluteAmount())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .transactionType(transaction.getTransactionType())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .categoryColor(transaction.getCategory().getColor())
                .build();
    }
}

