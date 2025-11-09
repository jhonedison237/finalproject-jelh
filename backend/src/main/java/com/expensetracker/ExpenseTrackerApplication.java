package com.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for ExpenseTracker
 * 
 * Personal expense tracking application that helps users manage
 * their finances by recording transactions, setting budgets, and
 * analyzing spending patterns.
 * 
 * @author ExpenseTracker Team
 * @version 1.0.0
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}

