package com.expensetracker.dto.response;

import com.expensetracker.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for transaction summary (minimal information for lists)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSummaryDTO {

    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private TransactionType transactionType;
    
    // Category information
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    
    // Computed fields
    private BigDecimal absoluteAmount;
}

