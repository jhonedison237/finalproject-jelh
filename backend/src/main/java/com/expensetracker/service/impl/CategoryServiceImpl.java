package com.expensetracker.service.impl;

import com.expensetracker.dto.response.CategoryDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CategoryService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getUserCategories(User user) {
        log.debug("Getting categories for user: {}", user.getId());

        List<Category> categories = categoryRepository.findByUserIdAndActiveTrue(user.getId());

        return categories.stream()
                .map(this::mapToCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id, User user) {
        log.debug("Getting category {} for user: {}", id, user.getId());

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return mapToCategoryDTO(category);
    }

    @Override
    public boolean isValidCategoryForUser(Long categoryId, Long userId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId).isPresent();
    }

    /**
     * Maps Category entity to CategoryDTO
     */
    private CategoryDTO mapToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .icon(category.getIcon())
                .isDefault(category.getIsDefault())
                .build();
    }
}

