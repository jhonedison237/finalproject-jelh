package com.expensetracker.dto.response;

import com.expensetracker.entity.enums.PaymentMethod;
import com.expensetracker.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for transaction response with full details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {

    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private TransactionType transactionType;
    private PaymentMethod paymentMethod;
    private String notes;
    
    // Category information
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    
    // User information (minimal)
    private Long userId;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;
    
    // Computed fields
    private BigDecimal absoluteAmount;
}

