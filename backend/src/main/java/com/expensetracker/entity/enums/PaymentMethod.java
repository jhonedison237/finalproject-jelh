package com.expensetracker.entity.enums;

/**
 * Enum representing the payment method used in a transaction
 */
public enum PaymentMethod {
    /**
     * Cash payment
     */
    CASH,
    
    /**
     * Credit or debit card payment
     */
    CARD,
    
    /**
     * Bank transfer or wire transfer
     */
    TRANSFER,
    
    /**
     * Other payment methods (e.g., digital wallets, checks)
     */
    OTHER
}

