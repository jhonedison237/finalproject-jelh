package com.expensetracker.dto;

import com.expensetracker.dto.request.TransactionCreateDTO;
import com.expensetracker.dto.request.TransactionUpdateDTO;
import com.expensetracker.entity.enums.PaymentMethod;
import com.expensetracker.entity.enums.TransactionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validation tests for Transaction DTOs
 * Tests Bean Validation annotations
 */
@DisplayName("Transaction DTO Validation Tests")
class TransactionDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ===== TransactionCreateDTO Tests =====

    @Test
    @DisplayName("Valid TransactionCreateDTO should pass validation")
    void validTransactionCreateDTO() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("Valid transaction");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TransactionCreateDTO with null amount should fail")
    void transactionCreateDTO_NullAmount_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(null);
        dto.setDescription("Test");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Amount is required");
    }

    @Test
    @DisplayName("TransactionCreateDTO with blank description should fail")
    void transactionCreateDTO_BlankDescription_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("   ");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Description is required");
    }

    @Test
    @DisplayName("TransactionCreateDTO with description too long should fail")
    void transactionCreateDTO_DescriptionTooLong_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("A".repeat(300)); // Max is 255
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("255");
    }

    @Test
    @DisplayName("TransactionCreateDTO with null category should fail")
    void transactionCreateDTO_NullCategory_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("Test");
        dto.setCategoryId(null);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Category");
    }

    @Test
    @DisplayName("TransactionCreateDTO with null transaction type should fail")
    void transactionCreateDTO_NullTransactionType_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("Test");
        dto.setCategoryId(1L);
        dto.setTransactionType(null);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Transaction type");
    }

    @Test
    @DisplayName("TransactionCreateDTO with null payment method should fail")
    void transactionCreateDTO_NullPaymentMethod_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("Test");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(null);
        dto.setTransactionDate(LocalDate.now());

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Payment method");
    }

    @Test
    @DisplayName("TransactionCreateDTO with null transaction date should fail")
    void transactionCreateDTO_NullTransactionDate_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("Test");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(null);

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Transaction date");
    }

    @Test
    @DisplayName("TransactionCreateDTO with future date should fail")
    void transactionCreateDTO_FutureDate_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("Test");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now().plusDays(1));

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("future");
    }

    @Test
    @DisplayName("TransactionCreateDTO with notes too long should fail")
    void transactionCreateDTO_NotesTooLong_FailsValidation() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("Test");
        dto.setCategoryId(1L);
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setPaymentMethod(PaymentMethod.CASH);
        dto.setTransactionDate(LocalDate.now());
        dto.setNotes("A".repeat(300)); // Max is 255

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("255");
    }

    @Test
    @DisplayName("TransactionCreateDTO with all fields missing should have multiple violations")
    void transactionCreateDTO_AllFieldsMissing_MultipleViolations() {
        // Given
        TransactionCreateDTO dto = new TransactionCreateDTO();

        // When
        Set<ConstraintViolation<TransactionCreateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSizeGreaterThanOrEqualTo(6); // 6 required fields
    }

    // ===== TransactionUpdateDTO Tests =====

    @Test
    @DisplayName("Valid TransactionUpdateDTO should pass validation")
    void validTransactionUpdateDTO() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setDescription("Updated description");
        dto.setAmount(new BigDecimal("200.00"));

        // When
        Set<ConstraintViolation<TransactionUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TransactionUpdateDTO with only description should pass")
    void transactionUpdateDTO_OnlyDescription_PassesValidation() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setDescription("Updated");

        // When
        Set<ConstraintViolation<TransactionUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TransactionUpdateDTO with blank description should pass (optional field)")
    void transactionUpdateDTO_BlankDescription_PassesValidation() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setDescription("   "); // Blank is allowed for updates

        // When
        Set<ConstraintViolation<TransactionUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty(); // Description is optional in updates
    }

    @Test
    @DisplayName("TransactionUpdateDTO with description too long should fail")
    void transactionUpdateDTO_DescriptionTooLong_FailsValidation() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setDescription("A".repeat(300));

        // When
        Set<ConstraintViolation<TransactionUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("255");
    }

    @Test
    @DisplayName("TransactionUpdateDTO with invalid amount precision should fail")
    void transactionUpdateDTO_InvalidAmountPrecision_FailsValidation() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setAmount(new BigDecimal("100.12345")); // More than 2 decimal places

        // When
        Set<ConstraintViolation<TransactionUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("decimal");
    }

    @Test
    @DisplayName("TransactionUpdateDTO with future date should fail")
    void transactionUpdateDTO_FutureDate_FailsValidation() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setTransactionDate(LocalDate.now().plusDays(1));

        // When
        Set<ConstraintViolation<TransactionUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("future");
    }

    @Test
    @DisplayName("TransactionUpdateDTO with notes too long should fail")
    void transactionUpdateDTO_NotesTooLong_FailsValidation() {
        // Given
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setNotes("A".repeat(300));

        // When
        Set<ConstraintViolation<TransactionUpdateDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("255");
    }
}

