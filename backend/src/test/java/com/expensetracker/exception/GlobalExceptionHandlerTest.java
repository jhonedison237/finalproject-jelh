package com.expensetracker.exception;

import com.expensetracker.dto.request.TransactionCreateDTO;
import com.expensetracker.entity.Category;
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
 * Tests for GlobalExceptionHandler
 * Tests all exception handling scenarios
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

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
    @DisplayName("Should handle ResourceNotFoundException with 404")
    void handleResourceNotFoundException() throws Exception {
        // When & Then - Try to get non-existent transaction
        mockMvc.perform(get("/api/v1/transactions/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("Transaction")))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/transactions/999"));
    }

    @Test
    @DisplayName("Should handle BadRequestException with 400")
    void handleBadRequestException() throws Exception {
        // Given - Try to create transaction with invalid category
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with validation errors")
    void handleValidationException() throws Exception {
        // Given - DTO with missing required fields
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        // Missing: description, categoryId, transactionType, paymentMethod, transactionDate

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Invalid input data"))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should include validation errors for multiple fields")
    void handleValidationException_MultipleFields() throws Exception {
        // Given - DTO with multiple validation errors
        TransactionCreateDTO dto = new TransactionCreateDTO();
        // All required fields missing

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details", hasSize(greaterThanOrEqualTo(5))));
    }

    @Test
    @DisplayName("Should handle validation error for @Size constraint")
    void handleValidationException_SizeConstraint() throws Exception {
        // Given - Description too long
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setDescription("A".repeat(300)); // Exceeds max 255
        dto.setCategoryId(testCategory.getId());
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details[?(@=~/.*description.*255.*/)]]").exists());
    }

    @Test
    @DisplayName("Should handle validation error for future date")
    void handleValidationException_FutureDate() throws Exception {
        // Given - Transaction date in the future
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setDescription("Test");
        dto.setCategoryId(testCategory.getId());
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now().plusDays(1));

        // When & Then
        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").exists());
    }
}

