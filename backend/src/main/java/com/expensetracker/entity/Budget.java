package com.expensetracker.entity;

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
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Budget entity representing monthly spending limits per category
 * Maps to 'budgets' table in the database
 */
@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "category_id", "month", "year"})
}, indexes = {
    @Index(name = "idx_budgets_user_id", columnList = "user_id"),
    @Index(name = "idx_budgets_user_period", columnList = "user_id, year, month")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull(message = "Limit amount is required")
    @Positive(message = "Limit amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Limit amount must have at most 10 integer digits and 2 decimal places")
    @Column(name = "limit_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal limitAmount;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @Column(nullable = false)
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    @Column(nullable = false)
    private Integer year;

    @NotNull
    @PositiveOrZero(message = "Spent amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Spent amount must have at most 10 integer digits and 2 decimal places")
    @Column(name = "spent_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(name = "alert_enabled", nullable = false)
    @Builder.Default
    private Boolean alertEnabled = true;

    @NotNull
    @DecimalMin(value = "0.0", message = "Alert threshold must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Alert threshold must be between 0 and 100")
    @Digits(integer = 3, fraction = 2, message = "Alert threshold must have at most 3 integer digits and 2 decimal places")
    @Column(name = "alert_threshold", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal alertThreshold = new BigDecimal("80.00");

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
     * Calculates the percentage of budget used
     * @return percentage as BigDecimal (0-100+)
     */
    public BigDecimal getPercentageUsed() {
        if (limitAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return spentAmount
                .divide(limitAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the remaining amount in the budget
     * @return remaining amount as BigDecimal
     */
    public BigDecimal getRemainingAmount() {
        return limitAmount.subtract(spentAmount);
    }

    /**
     * Checks if the budget has been exceeded
     * @return true if spent amount exceeds limit amount
     */
    public boolean isExceeded() {
        return spentAmount.compareTo(limitAmount) > 0;
    }

    /**
     * Checks if the budget has reached the alert threshold
     * @return true if percentage used is greater than or equal to alert threshold
     */
    public boolean hasReachedAlertThreshold() {
        if (!alertEnabled) {
            return false;
        }
        return getPercentageUsed().compareTo(alertThreshold) >= 0;
    }

    /**
     * Updates the spent amount by adding the transaction amount
     * @param amount the transaction amount to add (should be positive)
     */
    public void addSpentAmount(BigDecimal amount) {
        this.spentAmount = this.spentAmount.add(amount.abs());
    }

    /**
     * Updates the spent amount by subtracting the transaction amount
     * @param amount the transaction amount to subtract (should be positive)
     */
    public void subtractSpentAmount(BigDecimal amount) {
        this.spentAmount = this.spentAmount.subtract(amount.abs());
        if (this.spentAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.spentAmount = BigDecimal.ZERO;
        }
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", limitAmount=" + limitAmount +
                ", month=" + month +
                ", year=" + year +
                ", spentAmount=" + spentAmount +
                ", percentageUsed=" + getPercentageUsed() + "%" +
                ", active=" + active +
                '}';
    }
}

