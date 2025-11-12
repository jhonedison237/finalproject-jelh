package com.expensetracker.controller;

import com.expensetracker.dto.request.TransactionCreateDTO;
import com.expensetracker.dto.request.TransactionUpdateDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.entity.enums.PaymentMethod;
import com.expensetracker.entity.enums.TransactionType;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TransactionController
 * Tests the full stack from HTTP request to database
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("TransactionController Integration Tests")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Clean up
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user with ID=1 (matches getCurrentUser() in controller)
        // Using native SQL to force ID=1
        entityManager.createNativeQuery(
                "INSERT INTO users (id, email, username, password_hash, first_name, last_name, currency, active, created_at, updated_at) " +
                        "VALUES (1, 'demo@expensetracker.com', 'demo', 'hashedpassword', 'Demo', 'User', 'USD', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();
        
        testUser = userRepository.findById(1L).orElseThrow();

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
    @DisplayName("POST /api/v1/transactions - Create income transaction successfully")
    void createTransaction_Income_Success() throws Exception {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setDescription("Salary payment");
        dto.setCategoryId(testCategory.getId());
        dto.setTransactionType(TransactionType.INCOME);
        dto.setPaymentMethod(PaymentMethod.TRANSFER);
        dto.setTransactionDate(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Salary payment"))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.transactionType").value("INCOME"))
                .andExpect(jsonPath("$.paymentMethod").value("TRANSFER"));
    }

    @Test
    @DisplayName("POST /api/v1/transactions - Create expense transaction successfully")
    void createTransaction_Expense_Success() throws Exception {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setDescription("Groceries");
        dto.setCategoryId(testCategory.getId());
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());
        dto.setNotes("Weekly shopping");

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Groceries"))
                .andExpect(jsonPath("$.amount").value(-50.00))
                .andExpect(jsonPath("$.transactionType").value("EXPENSE"))
                .andExpect(jsonPath("$.notes").value("Weekly shopping"));
    }

    @Test
    @DisplayName("POST /api/v1/transactions - Validation fails for missing fields")
    void createTransaction_ValidationFails_BadRequest() throws Exception {
        // Given - missing required fields
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        // Missing description, categoryId, transactionType, paymentMethod

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/transactions - Fails with invalid category")
    void createTransaction_InvalidCategory_NotFound() throws Exception {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setDescription("Test");
        dto.setCategoryId(999L); // Non-existent category
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/transactions - List all transactions with pagination")
    void getAllTransactions_Paginated_Success() throws Exception {
        // Given - create test transactions
        createTestTransaction("Lunch", new BigDecimal("-30.00"), TransactionType.EXPENSE);
        createTestTransaction("Salary", new BigDecimal("2000.00"), TransactionType.INCOME);

        // When & Then
        mockMvc.perform(get("/api/v1/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/transactions/{id} - Get transaction by id")
    void getTransactionById_Success() throws Exception {
        // Given
        Transaction transaction = createTestTransaction("Dinner", new BigDecimal("-45.00"), TransactionType.EXPENSE);

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/{id}", transaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction.getId()))
                .andExpect(jsonPath("$.description").value("Dinner"))
                .andExpect(jsonPath("$.amount").value(-45.00));
    }

    @Test
    @DisplayName("GET /api/v1/transactions/{id} - Transaction not found")
    void getTransactionById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/transactions/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/transactions/{id} - Update transaction successfully")
    void updateTransaction_Success() throws Exception {
        // Given
        Transaction transaction = createTestTransaction("Original", new BigDecimal("-20.00"), TransactionType.EXPENSE);

        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setDescription("Updated description");
        dto.setAmount(new BigDecimal("25.00"));

        // When & Then
        mockMvc.perform(put("/api/v1/transactions/{id}", transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @DisplayName("DELETE /api/v1/transactions/{id} - Soft delete transaction")
    void deleteTransaction_Success() throws Exception {
        // Given
        Transaction transaction = createTestTransaction("To delete", new BigDecimal("-10.00"), TransactionType.EXPENSE);

        // When & Then
        mockMvc.perform(delete("/api/v1/transactions/{id}", transaction.getId()))
                .andExpect(status().isNoContent());

        // Verify it's soft deleted
        Transaction deleted = transactionRepository.findById(transaction.getId()).orElseThrow();
        assert !deleted.getActive();
    }

    @Test
    @DisplayName("GET /api/v1/transactions/summary/totals - Calculate totals")
    void getTotals_Success() throws Exception {
        // Given
        createTestTransaction("Income 1", new BigDecimal("1000.00"), TransactionType.INCOME);
        createTestTransaction("Income 2", new BigDecimal("500.00"), TransactionType.INCOME);
        createTestTransaction("Expense 1", new BigDecimal("-200.00"), TransactionType.EXPENSE);
        createTestTransaction("Expense 2", new BigDecimal("-100.00"), TransactionType.EXPENSE);

        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/summary/totals")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(1500.00))
                .andExpect(jsonPath("$.totalExpenses").value(300.00))
                .andExpect(jsonPath("$.balance").value(1200.00));
    }

    @Test
    @DisplayName("GET /api/v1/transactions/date-range - Filter by date range")
    void getTransactionsByDateRange_Success() throws Exception {
        // Given
        createTestTransaction("Recent", new BigDecimal("-50.00"), TransactionType.EXPENSE);

        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/date-range")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    // Helper method
    private Transaction createTestTransaction(String description, BigDecimal amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setUser(testUser);
        transaction.setCategory(testCategory);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setTransactionType(type);
        transaction.setPaymentMethod(PaymentMethod.CARD);
        transaction.setActive(true);
        return transactionRepository.save(transaction);
    }
}
