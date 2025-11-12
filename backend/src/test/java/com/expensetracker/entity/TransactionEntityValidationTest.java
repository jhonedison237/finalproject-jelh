package com.expensetracker.entity;

import com.expensetracker.entity.enums.PaymentMethod;
import com.expensetracker.entity.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for Transaction entity validation logic
 * Tests @PrePersist and @PreUpdate validation methods
 */
@DisplayName("Transaction Entity Validation Tests")
class TransactionEntityValidationTest {

    @Test
    @DisplayName("Should validate income amount is positive in @PrePersist")
    void validateAmountSign_Income_MustBePositive() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("-100.00")); // Negative amount
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setDescription("Test");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setPaymentMethod(PaymentMethod.TRANSFER);

        // When & Then
        assertThatThrownBy(() -> {
            // Simulate @PrePersist call
            transaction.validateAmountSign();
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Income amount must be positive");
    }

    @Test
    @DisplayName("Should validate expense amount is negative in @PrePersist")
    void validateAmountSign_Expense_MustBeNegative() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100.00")); // Positive amount
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setDescription("Test");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setPaymentMethod(PaymentMethod.CASH);

        // When & Then
        assertThatThrownBy(() -> {
            // Simulate @PrePersist call
            transaction.validateAmountSign();
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expense amount must be negative");
    }

    @Test
    @DisplayName("Should accept positive amount for income")
    void validateAmountSign_Income_PositiveAmount_Success() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setDescription("Salary");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setPaymentMethod(PaymentMethod.TRANSFER);

        // When & Then
        assertThatCode(() -> transaction.validateAmountSign())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept negative amount for expense")
    void validateAmountSign_Expense_NegativeAmount_Success() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("-50.00"));
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setDescription("Lunch");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setPaymentMethod(PaymentMethod.CARD);

        // When & Then
        assertThatCode(() -> transaction.validateAmountSign())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should reject zero amount for income")
    void validateAmountSign_Income_ZeroAmount_ThrowsException() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setTransactionType(TransactionType.INCOME);

        // When & Then
        assertThatThrownBy(() -> transaction.validateAmountSign())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Income amount must be positive");
    }

    @Test
    @DisplayName("Should reject zero amount for expense")
    void validateAmountSign_Expense_ZeroAmount_ThrowsException() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setTransactionType(TransactionType.EXPENSE);

        // When & Then
        assertThatThrownBy(() -> transaction.validateAmountSign())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expense amount must be negative");
    }

    @Test
    @DisplayName("getAbsoluteAmount should return positive value for negative amount")
    void getAbsoluteAmount_NegativeAmount_ReturnsPositive() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("-50.00"));

        // When
        BigDecimal absoluteAmount = transaction.getAbsoluteAmount();

        // Then
        assertThat(absoluteAmount).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("getAbsoluteAmount should return same value for positive amount")
    void getAbsoluteAmount_PositiveAmount_ReturnsSame() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100.00"));

        // When
        BigDecimal absoluteAmount = transaction.getAbsoluteAmount();

        // Then
        assertThat(absoluteAmount).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("isIncome should return true for INCOME transaction")
    void isIncome_IncomeTransaction_ReturnsTrue() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.INCOME);

        // When & Then
        assertThat(transaction.isIncome()).isTrue();
        assertThat(transaction.isExpense()).isFalse();
    }

    @Test
    @DisplayName("isExpense should return true for EXPENSE transaction")
    void isExpense_ExpenseTransaction_ReturnsTrue() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.EXPENSE);

        // When & Then
        assertThat(transaction.isExpense()).isTrue();
        assertThat(transaction.isIncome()).isFalse();
    }

    @Test
    @DisplayName("toString should not cause stack overflow or expose sensitive data")
    void toString_SafeImplementation() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("-50.00"));
        transaction.setDescription("Test transaction");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setPaymentMethod(PaymentMethod.CASH);
        transaction.setActive(true);

        // When
        String result = transaction.toString();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("Transaction");
        assertThat(result).contains("id=1");
        assertThat(result).contains("amount=-50.00");
        assertThat(result).contains("EXPENSE");
        assertThat(result).doesNotContain("User@"); // Should not have circular reference
    }

    @Test
    @DisplayName("Builder should create valid transaction")
    void builder_CreatesValidTransaction() {
        // Given & When
        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(new BigDecimal("-75.50"))
                .description("Dinner")
                .transactionDate(LocalDate.now())
                .transactionType(TransactionType.EXPENSE)
                .paymentMethod(PaymentMethod.CARD)
                .notes("Italian restaurant")
                .active(true)
                .build();

        // Then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getAmount()).isEqualByComparingTo(new BigDecimal("-75.50"));
        assertThat(transaction.getDescription()).isEqualTo("Dinner");
        assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(transaction.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(transaction.getNotes()).isEqualTo("Italian restaurant");
        assertThat(transaction.getActive()).isTrue();
    }

    @Test
    @DisplayName("Default active should be true")
    void defaultActive_ShouldBeTrue() {
        // Given & When
        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal("-10.00"))
                .build();

        // Then
        assertThat(transaction.getActive()).isTrue();
    }
}

