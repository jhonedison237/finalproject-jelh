package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.entity.enums.PaymentMethod;
import com.expensetracker.entity.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Repository tests for TransactionRepository
 * Uses @DataJpaTest for fast repository layer testing
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TransactionRepository Tests")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Clean up
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setActive(true);
        testUser = userRepository.save(testUser);

        // Create test category
        testCategory = new Category();
        testCategory.setName("Food");
        testCategory.setDescription("Food expenses");
        testCategory.setColor("#FF5733");
        testCategory.setIcon("🍔");
        testCategory.setIsDefault(false);
        testCategory.setUser(testUser);
        testCategory.setActive(true);
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    @DisplayName("Should find active transactions by user id")
    void findByUserIdAndActiveTrue_Success() {
        // Given
        createTransaction("Lunch", new BigDecimal("-30.00"), TransactionType.EXPENSE, true);
        createTransaction("Salary", new BigDecimal("2000.00"), TransactionType.INCOME, true);
        createTransaction("Deleted", new BigDecimal("-10.00"), TransactionType.EXPENSE, false);

        // When
        Page<Transaction> result = transactionRepository.findByUserIdAndActiveTrue(
                testUser.getId(), PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(Transaction::getActive);
    }

    @Test
    @DisplayName("Should find transactions by user and id")
    void findByIdAndUserId_Success() {
        // Given
        Transaction transaction = createTransaction("Test", new BigDecimal("-50.00"), TransactionType.EXPENSE, true);

        // When
        var result = transactionRepository.findByIdAndUserId(transaction.getId(), testUser.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Test");
    }

    @Test
    @DisplayName("Should find transactions by date range")
    void findByUserIdAndTransactionDateBetween_Success() {
        // Given
        LocalDate today = LocalDate.now();
        createTransaction("Today", new BigDecimal("-30.00"), TransactionType.EXPENSE, today, true);
        createTransaction("Yesterday", new BigDecimal("-20.00"), TransactionType.EXPENSE, today.minusDays(1), true);
        createTransaction("Old", new BigDecimal("-10.00"), TransactionType.EXPENSE, today.minusDays(10), true);

        // When
        LocalDate startDate = today.minusDays(2);
        LocalDate endDate = today;
        Page<Transaction> result = transactionRepository.findByUserIdAndActiveTrueAndTransactionDateBetween(
                testUser.getId(), startDate, endDate, PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(t ->
                !t.getTransactionDate().isBefore(startDate) && !t.getTransactionDate().isAfter(endDate));
    }

    @Test
    @DisplayName("Should calculate total income by date range")
    void calculateTotalIncomeByDateRange_Success() {
        // Given
        LocalDate today = LocalDate.now();
        createTransaction("Income1", new BigDecimal("1000.00"), TransactionType.INCOME, today, true);
        createTransaction("Income2", new BigDecimal("500.00"), TransactionType.INCOME, today, true);
        createTransaction("Expense", new BigDecimal("-200.00"), TransactionType.EXPENSE, today, true);

        // When
        BigDecimal total = transactionRepository.calculateTotalIncomeByDateRange(
                testUser.getId(), today.minusDays(1), today);

        // Then
        assertThat(total).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("Should calculate total expenses by date range")
    void calculateTotalExpensesByDateRange_Success() {
        // Given
        LocalDate today = LocalDate.now();
        createTransaction("Expense1", new BigDecimal("-100.00"), TransactionType.EXPENSE, today, true);
        createTransaction("Expense2", new BigDecimal("-200.00"), TransactionType.EXPENSE, today, true);
        createTransaction("Income", new BigDecimal("1000.00"), TransactionType.INCOME, today, true);

        // When
        BigDecimal total = transactionRepository.calculateTotalExpensesByDateRange(
                testUser.getId(), today.minusDays(1), today);

        // Then
        assertThat(total).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Should get expenses grouped by category")
    void getExpensesByCategoryGrouped_Success() {
        // Given
        Category category2 = new Category();
        category2.setName("Transport");
        category2.setColor("#3498DB");
        category2.setIcon("🚗");
        category2.setUser(testUser);
        category2.setActive(true);
        category2 = categoryRepository.save(category2);

        LocalDate today = LocalDate.now();
        createTransaction("Food1", new BigDecimal("-100.00"), TransactionType.EXPENSE, today, true);
        createTransaction("Food2", new BigDecimal("-50.00"), TransactionType.EXPENSE, today, true);
        
        Transaction transport = new Transaction();
        transport.setUser(testUser);
        transport.setCategory(category2);
        transport.setAmount(new BigDecimal("-30.00"));
        transport.setDescription("Transport");
        transport.setTransactionDate(today);
        transport.setTransactionType(TransactionType.EXPENSE);
        transport.setPaymentMethod(PaymentMethod.CARD);
        transport.setActive(true);
        transactionRepository.save(transport);

        // When
        List<Object[]> result = transactionRepository.getExpensesByCategoryGrouped(
                testUser.getId(), today.minusDays(1), today);

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should count active transactions by user")
    void countByUserIdAndActiveTrue_Success() {
        // Given
        createTransaction("Trans1", new BigDecimal("-30.00"), TransactionType.EXPENSE, true);
        createTransaction("Trans2", new BigDecimal("-20.00"), TransactionType.EXPENSE, true);
        createTransaction("Deleted", new BigDecimal("-10.00"), TransactionType.EXPENSE, false);

        // When
        long count = transactionRepository.countByUserIdAndActiveTrue(testUser.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find recent transactions with limit")
    void findRecentTransactions_Success() {
        // Given
        createTransaction("Recent1", new BigDecimal("-30.00"), TransactionType.EXPENSE, true);
        createTransaction("Recent2", new BigDecimal("-20.00"), TransactionType.EXPENSE, true);
        createTransaction("Recent3", new BigDecimal("-10.00"), TransactionType.EXPENSE, true);

        // When
        Page<Transaction> result = transactionRepository.findByUserIdAndActiveTrue(
                testUser.getId(), PageRequest.of(0, 2));

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    // Helper methods
    private Transaction createTransaction(String description, BigDecimal amount, 
                                         TransactionType type, boolean active) {
        return createTransaction(description, amount, type, LocalDate.now(), active);
    }

    private Transaction createTransaction(String description, BigDecimal amount, 
                                         TransactionType type, LocalDate date, boolean active) {
        Transaction transaction = new Transaction();
        transaction.setUser(testUser);
        transaction.setCategory(testCategory);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionDate(date);
        transaction.setTransactionType(type);
        transaction.setPaymentMethod(PaymentMethod.CARD);
        transaction.setActive(active);
        return transactionRepository.save(transaction);
    }
}

