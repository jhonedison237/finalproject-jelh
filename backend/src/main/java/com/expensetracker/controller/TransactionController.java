package com.expensetracker.controller;

import com.expensetracker.dto.request.TransactionCreateDTO;
import com.expensetracker.dto.request.TransactionUpdateDTO;
import com.expensetracker.dto.response.PageResponseDTO;
import com.expensetracker.dto.response.TransactionResponseDTO;
import com.expensetracker.dto.response.TransactionSummaryDTO;
import com.expensetracker.entity.User;
import com.expensetracker.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Transaction operations
 * Main controller for ET-001 ticket
 * 
 * Base path: /api/v1/transactions
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Transaction management endpoints")
public class TransactionController {

    private final TransactionService transactionService;

    // TODO: Replace with @AuthenticationPrincipal after security implementation
    private User getCurrentUser() {
        // Temporary: Return demo user (ID=1)
        // This will be replaced with proper JWT authentication
        User demoUser = new User();
        demoUser.setId(1L);
        demoUser.setEmail("demo@expensetracker.com");
        demoUser.setUsername("demo");
        return demoUser;
    }

    @PostMapping
    @Operation(summary = "Create new transaction", description = "Creates a new income or expense transaction")
    @ApiResponse(responseCode = "201", description = "Transaction created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionCreateDTO dto) {
        
        log.info("Creating new transaction: type={}, amount={}", 
                dto.getTransactionType(), dto.getAmount());
        
        User currentUser = getCurrentUser();
        TransactionResponseDTO response = transactionService.createTransaction(dto, currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transaction", description = "Updates an existing transaction")
    @ApiResponse(responseCode = "200", description = "Transaction updated successfully")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateDTO dto) {
        
        log.info("Updating transaction: id={}", id);
        
        User currentUser = getCurrentUser();
        TransactionResponseDTO response = transactionService.updateTransaction(id, dto, currentUser);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Soft deletes a transaction")
    @ApiResponse(responseCode = "204", description = "Transaction deleted successfully")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("Deleting transaction: id={}", id);
        
        User currentUser = getCurrentUser();
        transactionService.deleteTransaction(id, currentUser);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves a specific transaction")
    @ApiResponse(responseCode = "200", description = "Transaction found")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        log.info("Getting transaction: id={}", id);
        
        User currentUser = getCurrentUser();
        TransactionResponseDTO response = transactionService.getTransactionById(id, currentUser);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieves paginated list of user's transactions")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    public ResponseEntity<PageResponseDTO<TransactionSummaryDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-indexed)") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "transactionDate") @Parameter(description = "Sort field") String sortBy,
            @RequestParam(defaultValue = "DESC") @Parameter(description = "Sort direction") String sortDir) {
        
        log.info("Getting all transactions: page={}, size={}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        User currentUser = getCurrentUser();
        
        Page<TransactionSummaryDTO> transactions = transactionService.getUserTransactions(currentUser, pageable);
        
        return ResponseEntity.ok(mapToPageResponse(transactions));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get transactions by date range", 
               description = "Retrieves transactions within a specific date range")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    public ResponseEntity<PageResponseDTO<TransactionSummaryDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            @Parameter(description = "Start date (YYYY-MM-DD)") LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            @Parameter(description = "End date (YYYY-MM-DD)") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting transactions by date range: {} to {}", startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        User currentUser = getCurrentUser();
        
        Page<TransactionSummaryDTO> transactions = transactionService.getTransactionsByDateRange(
                currentUser, startDate, endDate, pageable);
        
        return ResponseEntity.ok(mapToPageResponse(transactions));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get transactions by category", 
               description = "Retrieves transactions for a specific category")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<PageResponseDTO<TransactionSummaryDTO>> getTransactionsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting transactions by category: categoryId={}", categoryId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        User currentUser = getCurrentUser();
        
        Page<TransactionSummaryDTO> transactions = transactionService.getTransactionsByCategory(
                currentUser, categoryId, pageable);
        
        return ResponseEntity.ok(mapToPageResponse(transactions));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent transactions", description = "Retrieves most recent transactions")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    public ResponseEntity<List<TransactionSummaryDTO>> getRecentTransactions(
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of transactions") int limit) {
        
        log.info("Getting recent transactions: limit={}", limit);
        
        User currentUser = getCurrentUser();
        List<TransactionSummaryDTO> transactions = transactionService.getRecentTransactions(currentUser, limit);
        
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/summary/totals")
    @Operation(summary = "Get income and expense totals", 
               description = "Calculates total income and expenses for a date range")
    @ApiResponse(responseCode = "200", description = "Totals calculated successfully")
    public ResponseEntity<Map<String, BigDecimal>> getTotals(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Calculating totals for date range: {} to {}", startDate, endDate);
        
        User currentUser = getCurrentUser();
        BigDecimal totalIncome = transactionService.calculateTotalIncome(currentUser, startDate, endDate);
        BigDecimal totalExpenses = transactionService.calculateTotalExpenses(currentUser, startDate, endDate);
        BigDecimal balance = totalIncome.subtract(totalExpenses); // totalExpenses is already absolute
        
        return ResponseEntity.ok(Map.of(
                "totalIncome", totalIncome,
                "totalExpenses", totalExpenses, // Already positive from repository
                "balance", balance
        ));
    }

    @GetMapping("/summary/by-category")
    @Operation(summary = "Get expenses by category", 
               description = "Retrieves expense breakdown by category")
    @ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
    public ResponseEntity<Map<String, BigDecimal>> getExpensesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting expenses by category for date range: {} to {}", startDate, endDate);
        
        User currentUser = getCurrentUser();
        Map<String, BigDecimal> expensesByCategory = transactionService.getExpensesByCategory(
                currentUser, startDate, endDate);
        
        return ResponseEntity.ok(expensesByCategory);
    }

    // ========== Private Helper Methods ==========

    /**
     * Maps Spring Data Page to custom PageResponseDTO
     */
    private <T> PageResponseDTO<T> mapToPageResponse(Page<T> page) {
        return PageResponseDTO.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}

