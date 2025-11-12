package com.expensetracker.controller;

import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CategoryController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("CategoryController Integration Tests")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user with ID=1 (matches getCurrentUser() in controller)
        // Using native SQL to force ID=1
        entityManager.createNativeQuery(
                "INSERT INTO users (id, email, username, password_hash, first_name, last_name, currency, active, created_at, updated_at) " +
                        "VALUES (1, 'demo@expensetracker.com', 'demo', 'hashedpassword', 'Demo', 'User', 'USD', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .executeUpdate();
        
        testUser = userRepository.findById(1L).orElseThrow();

        // Create test categories
        createTestCategory("Food", "🍔", "#FF5733");
        createTestCategory("Transport", "🚗", "#3498DB");
        createTestCategory("Entertainment", "🎬", "#9B59B6");
    }

    @Test
    @DisplayName("GET /api/v1/categories - Get all categories for user")
    void getAllCategories_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[1].name").value("Transport"))
                .andExpect(jsonPath("$[2].name").value("Entertainment"));
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} - Get category by id")
    void getCategoryById_Success() throws Exception {
        // Given
        Category category = categoryRepository.findByUserIdAndActiveTrue(testUser.getId()).get(0);

        // When & Then
        mockMvc.perform(get("/api/v1/categories/{id}", category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.icon").value("🍔"))
                .andExpect(jsonPath("$.color").value("#FF5733"));
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} - Category not found")
    void getCategoryById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/categories/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/categories - Empty list when no categories")
    void getAllCategories_EmptyList() throws Exception {
        // Given - delete all categories
        categoryRepository.deleteAll();

        // When & Then
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Helper method
    private Category createTestCategory(String name, String icon, String color) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(name + " expenses");
        category.setIcon(icon);
        category.setColor(color);
        category.setIsDefault(false);
        category.setUser(testUser);
        category.setActive(true);
        return categoryRepository.save(category);
    }
}
