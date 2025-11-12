package com.expensetracker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for all tests
 * Provides common test configuration
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseTest {
    // Common test configuration and utilities can go here
}

