package com.expensetracker.controller;

import com.expensetracker.dto.response.CategoryDTO;
import com.expensetracker.entity.User;
import com.expensetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category operations
 * 
 * Base path: /api/v1/categories
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    // TODO: Replace with @AuthenticationPrincipal after security implementation
    private User getCurrentUser() {
        User demoUser = new User();
        demoUser.setId(1L);
        demoUser.setEmail("demo@expensetracker.com");
        demoUser.setUsername("demo");
        return demoUser;
    }

    @GetMapping
    @Operation(summary = "Get all categories", 
               description = "Retrieves all active categories for the current user")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.info("Getting all categories for user");
        
        User currentUser = getCurrentUser();
        List<CategoryDTO> categories = categoryService.getUserCategories(currentUser);
        
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieves a specific category")
    @ApiResponse(responseCode = "200", description = "Category found")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.info("Getting category: id={}", id);
        
        User currentUser = getCurrentUser();
        CategoryDTO category = categoryService.getCategoryById(id, currentUser);
        
        return ResponseEntity.ok(category);
    }
}

