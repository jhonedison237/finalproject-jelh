package com.expensetracker.service;

import com.expensetracker.dto.request.TransactionCreateDTO;
import com.expensetracker.dto.request.TransactionUpdateDTO;
import com.expensetracker.dto.response.TransactionResponseDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.entity.enums.PaymentMethod;
import com.expensetracker.entity.enums.TransactionType;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionService
 * Uses Mockito to isolate service logic from dependencies
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private Category testCategory;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setUsername("testuser");

        // Setup test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setColor("#FF5733");
        testCategory.setIcon("🍔");
        testCategory.setUser(testUser);
        testCategory.setActive(true);

        // Setup test transaction
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setUser(testUser);
        testTransaction.setCategory(testCategory);
        testTransaction.setAmount(new BigDecimal("-50.00"));
        testTransaction.setDescription("Lunch");
        testTransaction.setTransactionDate(LocalDate.now());
        testTransaction.setTransactionType(TransactionType.EXPENSE);
        testTransaction.setPaymentMethod(PaymentMethod.CARD);
        testTransaction.setActive(true);
        testTransaction.setCreatedAt(LocalDateTime.now());
        testTransaction.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create income transaction successfully")
    void createTransaction_Income_Success() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setDescription("Salary");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.INCOME);
        dto.setPaymentMethod(PaymentMethod.TRANSFER);
        dto.setTransactionDate(LocalDate.now());

        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testCategory));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(2L);
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });

        // When
        TransactionResponseDTO result = transactionService.createTransaction(dto, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Salary");
        assertThat(result.getTransactionType()).isEqualTo(TransactionType.INCOME);
        assertThat(result.getAmount()).isPositive(); // Income should be positive
        
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create expense transaction with negative amount")
    void createTransaction_Expense_NegativeAmount() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setDescription("Groceries");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testCategory));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(3L);
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });

        // When
        TransactionResponseDTO result = transactionService.createTransaction(dto, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isNegative(); // Expense should be negative
        
        verify(transactionRepository, times(1)).save(argThat(transaction ->
                transaction.getAmount().compareTo(BigDecimal.ZERO) < 0
        ));
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void createTransaction_CategoryNotFound_ThrowsException() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setDescription("Test");
        dto.setCategoryId(999L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        when(categoryRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(dto, testUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when amount is zero")
    void createTransaction_ZeroAmount_ThrowsException() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(BigDecimal.ZERO);
        dto.setDescription("Test");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testCategory));

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(dto, testUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("greater than zero");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void updateTransaction_Success() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setDescription("Updated Lunch");
        dto.setAmount(new BigDecimal("60.00"));

        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        TransactionResponseDTO result = transactionService.updateTransaction(1L, dto, testUser);

        // Then
        assertThat(result).isNotNull();
        verify(transactionRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent transaction")
    void updateTransaction_NotFound_ThrowsException() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setDescription("Updated");

        when(transactionRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> transactionService.updateTransaction(999L, dto, testUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft delete transaction")
    void deleteTransaction_SoftDelete_Success() {
        // Given
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        transactionService.deleteTransaction(1L, testUser);

        // Then
        verify(transactionRepository, times(1)).save(argThat(transaction ->
                !transaction.getActive()
        ));
    }

    @Test
    @DisplayName("Should get transaction by id successfully")
    void getTransactionById_Success() {
        // Given
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testTransaction));

        // When
        TransactionResponseDTO result = transactionService.getTransactionById(1L, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Lunch");
        verify(transactionRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("Should get user transactions with pagination")
    void getUserTransactions_Paginated_Success() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> page = new PageImpl<>(transactions);
        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findByUserIdAndActiveTrue(1L, pageable)).thenReturn(page);

        // When
        var result = transactionService.getUserTransactions(testUser, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(transactionRepository, times(1)).findByUserIdAndActiveTrue(1L, pageable);
    }

    @Test
    @DisplayName("Should get transactions by date range")
    void getTransactionsByDateRange_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> page = new PageImpl<>(transactions);
        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findByUserIdAndActiveTrueAndTransactionDateBetween(
                1L, startDate, endDate, pageable)).thenReturn(page);

        // When
        var result = transactionService.getTransactionsByDateRange(testUser, startDate, endDate, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void getTransactionsByDateRange_InvalidDateRange_ThrowsException() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactionsByDateRange(
                testUser, startDate, endDate, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Start date");
    }

    @Test
    @DisplayName("Should calculate total income")
    void calculateTotalIncome_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        BigDecimal expectedTotal = new BigDecimal("5000.00");

        when(transactionRepository.calculateTotalIncomeByDateRange(1L, startDate, endDate))
                .thenReturn(expectedTotal);

        // When
        BigDecimal result = transactionService.calculateTotalIncome(testUser, startDate, endDate);

        // Then
        assertThat(result).isEqualTo(expectedTotal);
        verify(transactionRepository, times(1)).calculateTotalIncomeByDateRange(1L, startDate, endDate);
    }

    @Test
    @DisplayName("Should calculate total expenses")
    void calculateTotalExpenses_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        BigDecimal expectedTotal = new BigDecimal("3000.00");

        when(transactionRepository.calculateTotalExpensesByDateRange(1L, startDate, endDate))
                .thenReturn(expectedTotal);

        // When
        BigDecimal result = transactionService.calculateTotalExpenses(testUser, startDate, endDate);

        // Then
        assertThat(result).isEqualTo(expectedTotal);
        verify(transactionRepository, times(1)).calculateTotalExpensesByDateRange(1L, startDate, endDate);
    }

    @Test
    @DisplayName("Should get transaction count")
    void getTransactionCount_Success() {
        // Given
        when(transactionRepository.countByUserIdAndActiveTrue(1L)).thenReturn(10L);

        // When
        long count = transactionService.getTransactionCount(testUser);

        // Then
        assertThat(count).isEqualTo(10L);
        verify(transactionRepository, times(1)).countByUserIdAndActiveTrue(1L);
    }
}

