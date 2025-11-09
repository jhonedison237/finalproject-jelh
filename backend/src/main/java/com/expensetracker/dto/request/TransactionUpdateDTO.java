package com.expensetracker.dto.request;

import com.expensetracker.entity.enums.PaymentMethod;
import com.expensetracker.entity.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for updating an existing transaction
 * All fields are optional to allow partial updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionUpdateDTO {

    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal amount;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    private TransactionType transactionType;

    private PaymentMethod paymentMethod;

    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDate transactionDate;

    @Size(max = 255, message = "Notes must not exceed 255 characters")
    private String notes;
}

