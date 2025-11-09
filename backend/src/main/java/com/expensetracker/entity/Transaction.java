package com.expensetracker.entity;

import com.expensetracker.entity.enums.PaymentMethod;
import com.expensetracker.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Transaction entity representing financial transactions (income and expenses)
 * Maps to 'transactions' table in the database
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transactions_user_id", columnList = "user_id"),
    @Index(name = "idx_transactions_user_date", columnList = "user_id, transaction_date"),
    @Index(name = "idx_transactions_category_id", columnList = "category_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull(message = "Amount is required")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String description;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Size(max = 255, message = "Notes must not exceed 255 characters")
    @Column(length = 255)
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Validates that the amount sign matches the transaction type
     * Income should be positive, Expense should be negative
     */
    @PrePersist
    @PreUpdate
    private void validateAmountSign() {
        if (transactionType == TransactionType.INCOME && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income amount must be positive");
        }
        if (transactionType == TransactionType.EXPENSE && amount.compareTo(BigDecimal.ZERO) >= 0) {
            throw new IllegalArgumentException("Expense amount must be negative");
        }
    }

    /**
     * Gets the absolute value of the amount (for display purposes)
     */
    public BigDecimal getAbsoluteAmount() {
        return amount.abs();
    }

    /**
     * Checks if this is an income transaction
     */
    public boolean isIncome() {
        return transactionType == TransactionType.INCOME;
    }

    /**
     * Checks if this is an expense transaction
     */
    public boolean isExpense() {
        return transactionType == TransactionType.EXPENSE;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                ", transactionType=" + transactionType +
                ", paymentMethod=" + paymentMethod +
                ", active=" + active +
                '}';
    }
}

