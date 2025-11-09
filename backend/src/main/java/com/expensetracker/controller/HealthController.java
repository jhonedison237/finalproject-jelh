package com.expensetracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check endpoint
 * Useful for monitoring and deployment verification
 */
@RestController
@RequestMapping("/api/v1/health")
@Slf4j
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Returns API health status")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "ExpenseTracker API",
                "version", "1.0.0"
        ));
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping", description = "Simple ping endpoint")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}

