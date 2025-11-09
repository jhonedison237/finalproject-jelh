package com.expensetracker.service;

import com.expensetracker.dto.response.CategoryDTO;
import com.expensetracker.entity.User;

import java.util.List;

/**
 * Service interface for Category operations
 */
public interface CategoryService {

    /**
     * Get all active categories for a user (including default ones)
     * @param user authenticated user
     * @return list of categories
     */
    List<CategoryDTO> getUserCategories(User user);

    /**
     * Get category by ID
     * @param id category ID
     * @param user authenticated user
     * @return category details
     */
    CategoryDTO getCategoryById(Long id, User user);

    /**
     * Verify if category exists and belongs to user
     * @param categoryId category ID
     * @param userId user ID
     * @return true if valid, false otherwise
     */
    boolean isValidCategoryForUser(Long categoryId, Long userId);
}

