-- ============================================================================
-- ExpenseTracker Database - Performance Optimization Indexes
-- Version: 3.0
-- Description: Additional strategic indexes for query performance optimization
-- ============================================================================

-- ============================================================================
-- ADDITIONAL INDEXES FOR TRANSACTIONS
-- Purpose: Optimize frequent query patterns
-- ============================================================================

-- Index for filtering transactions by user and category
-- Use case: "Show all transactions for user X in category Y"
CREATE INDEX IF NOT EXISTS idx_transactions_user_category 
ON transactions(user_id, category_id);

-- Index for filtering by transaction type
-- Use case: "Show all income/expense transactions for a user"
CREATE INDEX IF NOT EXISTS idx_transactions_user_type 
ON transactions(user_id, transaction_type);

-- Index for date range queries
-- Use case: "Show transactions between date X and date Y"
CREATE INDEX IF NOT EXISTS idx_transactions_date_range 
ON transactions(transaction_date) 
WHERE active = true;

-- Composite index for dashboard queries
-- Use case: "Show recent active transactions for user with category details"
CREATE INDEX IF NOT EXISTS idx_transactions_user_date_active 
ON transactions(user_id, transaction_date DESC, active)
WHERE active = true;

-- Index for payment method statistics
-- Use case: "How much was spent using each payment method?"
CREATE INDEX IF NOT EXISTS idx_transactions_user_payment 
ON transactions(user_id, payment_method)
WHERE transaction_type = 'EXPENSE';

-- ============================================================================
-- ADDITIONAL INDEXES FOR CATEGORIES
-- ============================================================================

-- Index for searching categories by name
-- Use case: "Find category by name prefix"
CREATE INDEX IF NOT EXISTS idx_categories_name_trgm 
ON categories(name);

-- Index for filtering default categories
-- Use case: "Show all system default categories"
CREATE INDEX IF NOT EXISTS idx_categories_default 
ON categories(is_default)
WHERE is_default = true;

-- ============================================================================
-- ADDITIONAL INDEXES FOR BUDGETS
-- ============================================================================

-- Index for finding budgets near their limit
-- Use case: "Show all budgets where spent_amount is close to limit_amount"
CREATE INDEX IF NOT EXISTS idx_budgets_alert 
ON budgets(user_id, alert_enabled)
WHERE alert_enabled = true AND active = true;

-- Index for budget period queries
-- Use case: "Show budgets for a specific year"
CREATE INDEX IF NOT EXISTS idx_budgets_year 
ON budgets(user_id, year DESC);

-- Composite index for active budgets by period
-- Use case: "Show all active budgets for current month/year"
CREATE INDEX IF NOT EXISTS idx_budgets_period_active 
ON budgets(year, month, active)
WHERE active = true;

-- ============================================================================
-- ADDITIONAL INDEXES FOR USER_SESSIONS
-- ============================================================================

-- Index for cleaning up expired sessions
-- Use case: "Delete all expired sessions"
CREATE INDEX IF NOT EXISTS idx_sessions_expired 
ON user_sessions(expires_at)
WHERE active = true;

-- Index for user session history
-- Use case: "Show all sessions for a user ordered by creation date"
CREATE INDEX IF NOT EXISTS idx_sessions_user_created 
ON user_sessions(user_id, created_at DESC);

-- ============================================================================
-- ADDITIONAL INDEXES FOR USERS
-- ============================================================================

-- Index for searching users by username
-- Use case: "Find user by username for profile lookup"
CREATE INDEX IF NOT EXISTS idx_users_username 
ON users(username)
WHERE active = true;

-- Index for user creation date (analytics)
-- Use case: "Show user registration trends over time"
CREATE INDEX IF NOT EXISTS idx_users_created_at 
ON users(created_at DESC)
WHERE active = true;

-- ============================================================================
-- STATISTICS AND MAINTENANCE
-- ============================================================================

-- Update table statistics for query planner optimization
ANALYZE users;
ANALYZE categories;
ANALYZE transactions;
ANALYZE budgets;
ANALYZE user_sessions;

-- ============================================================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON INDEX idx_transactions_user_category IS 'Optimizes filtering transactions by user and category';
COMMENT ON INDEX idx_transactions_user_date_active IS 'Optimizes dashboard queries for recent active transactions';
COMMENT ON INDEX idx_budgets_alert IS 'Optimizes finding budgets near their spending limit for alerts';
COMMENT ON INDEX idx_sessions_expired IS 'Optimizes cleanup of expired sessions';

-- ============================================================================
-- QUERY PERFORMANCE VERIFICATION (Commented - uncomment to test)
-- ============================================================================

/*
-- Test query 1: Recent transactions for a user
EXPLAIN ANALYZE
SELECT t.*, c.name as category_name 
FROM transactions t
JOIN categories c ON t.category_id = c.id
WHERE t.user_id = 1 
  AND t.active = true
ORDER BY t.transaction_date DESC
LIMIT 10;

-- Test query 2: Budget status for current month
EXPLAIN ANALYZE
SELECT b.*, c.name as category_name,
       (b.spent_amount / b.limit_amount * 100) as percentage_used
FROM budgets b
JOIN categories c ON b.category_id = c.id
WHERE b.user_id = 1 
  AND b.month = EXTRACT(MONTH FROM CURRENT_DATE)
  AND b.year = EXTRACT(YEAR FROM CURRENT_DATE)
  AND b.active = true;

-- Test query 3: Expense summary by category
EXPLAIN ANALYZE
SELECT c.name, 
       COUNT(t.id) as transaction_count,
       SUM(ABS(t.amount)) as total_amount
FROM transactions t
JOIN categories c ON t.category_id = c.id
WHERE t.user_id = 1
  AND t.transaction_type = 'EXPENSE'
  AND t.transaction_date >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY c.name
ORDER BY total_amount DESC;
*/

-- ============================================================================
-- INDEX SIZE AND USAGE MONITORING (Commented - uncomment to check)
-- ============================================================================

/*
-- Check index sizes
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC;

-- Check index usage statistics
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan as index_scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;
*/

-- ============================================================================
-- END OF MIGRATION V3
-- ============================================================================

