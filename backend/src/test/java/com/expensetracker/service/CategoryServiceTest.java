package com.expensetracker.service;

import com.expensetracker.dto.response.CategoryDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setDescription("Food expenses");
        testCategory.setColor("#FF5733");
        testCategory.setIcon("🍔");
        testCategory.setIsDefault(false);
        testCategory.setUser(testUser);
        testCategory.setActive(true);
    }

    @Test
    @DisplayName("Should get all user categories")
    void getUserCategories_Success() {
        // Given
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Transport");
        category2.setActive(true);

        List<Category> categories = Arrays.asList(testCategory, category2);
        when(categoryRepository.findByUserIdAndActiveTrue(1L)).thenReturn(categories);

        // When
        List<CategoryDTO> result = categoryService.getUserCategories(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Food");
        assertThat(result.get(1).getName()).isEqualTo("Transport");
        verify(categoryRepository, times(1)).findByUserIdAndActiveTrue(1L);
    }

    @Test
    @DisplayName("Should get category by id")
    void getCategoryById_Success() {
        // Given
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testCategory));

        // When
        CategoryDTO result = categoryService.getCategoryById(1L, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Food");
        assertThat(result.getColor()).isEqualTo("#FF5733");
        assertThat(result.getIcon()).isEqualTo("🍔");
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void getCategoryById_NotFound_ThrowsException() {
        // Given
        when(categoryRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryById(999L, testUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");

        verify(categoryRepository, times(1)).findByIdAndUserId(999L, 1L);
    }

    @Test
    @DisplayName("Should validate category for user")
    void isValidCategoryForUser_Valid_ReturnsTrue() {
        // Given
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testCategory));

        // When
        boolean result = categoryService.isValidCategoryForUser(1L, 1L);

        // Then
        assertThat(result).isTrue();
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("Should return false for invalid category")
    void isValidCategoryForUser_Invalid_ReturnsFalse() {
        // Given
        when(categoryRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When
        boolean result = categoryService.isValidCategoryForUser(999L, 1L);

        // Then
        assertThat(result).isFalse();
        verify(categoryRepository, times(1)).findByIdAndUserId(999L, 1L);
    }
}

