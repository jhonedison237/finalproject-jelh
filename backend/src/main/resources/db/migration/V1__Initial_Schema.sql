-- ============================================================================
-- ExpenseTracker Database Schema - Initial Migration
-- Version: 1.0
-- Description: Creates core tables for the expense tracking system
-- ============================================================================

-- ============================================================================
-- TABLE: users
-- Purpose: Stores user account information and authentication data
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Constraints
    CONSTRAINT check_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT check_username_length CHECK (LENGTH(username) >= 3)
);

-- Indexes for users table
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(active);

COMMENT ON TABLE users IS 'Stores user account information and authentication credentials';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.currency IS 'ISO 4217 currency code (USD, EUR, COP, etc.)';

-- ============================================================================
-- TABLE: categories
-- Purpose: Organizes transactions into meaningful categories
-- ============================================================================
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    color VARCHAR(7) NOT NULL DEFAULT '#007bff',
    icon VARCHAR(50) NOT NULL DEFAULT 'category',
    is_default BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign Keys
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT uq_categories_user_name UNIQUE (user_id, name),
    CONSTRAINT check_color_format CHECK (color ~* '^#[0-9A-Fa-f]{6}$')
);

-- Indexes for categories table
CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_categories_user_active ON categories(user_id, active);

COMMENT ON TABLE categories IS 'Classification system for transactions (food, transport, etc.)';
COMMENT ON COLUMN categories.is_default IS 'True if this is a system-provided default category';
COMMENT ON COLUMN categories.color IS 'Hex color code for UI visualization';

-- ============================================================================
-- TABLE: transactions
-- Purpose: Records individual income and expense transactions
-- ============================================================================

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    notes VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign Keys
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) ON DELETE RESTRICT,
    
    -- Constraints
    CONSTRAINT check_amount_not_zero CHECK (amount != 0),
    CONSTRAINT check_transaction_type CHECK (transaction_type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT check_payment_method CHECK (payment_method IN ('CASH', 'CARD', 'TRANSFER', 'OTHER')),
    CONSTRAINT check_transaction_date_not_future CHECK (transaction_date <= CURRENT_DATE),
    CONSTRAINT check_amount_sign CHECK (
        (transaction_type = 'INCOME' AND amount > 0) OR 
        (transaction_type = 'EXPENSE' AND amount < 0)
    )
);

-- Indexes for transactions table
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date DESC);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date DESC);

COMMENT ON TABLE transactions IS 'Individual income and expense records';
COMMENT ON COLUMN transactions.amount IS 'Positive for income, negative for expenses';
COMMENT ON COLUMN transactions.transaction_date IS 'Actual date of the transaction (not record date)';

-- ============================================================================
-- TABLE: budgets
-- Purpose: Defines spending limits per category and time period
-- ============================================================================
CREATE TABLE IF NOT EXISTS budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    limit_amount DECIMAL(12,2) NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    spent_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    alert_enabled BOOLEAN NOT NULL DEFAULT true,
    alert_threshold DECIMAL(5,2) NOT NULL DEFAULT 80.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign Keys
    CONSTRAINT fk_budgets_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT uq_budgets_user_category_period UNIQUE (user_id, category_id, month, year),
    CONSTRAINT check_limit_amount_positive CHECK (limit_amount > 0),
    CONSTRAINT check_month_valid CHECK (month BETWEEN 1 AND 12),
    CONSTRAINT check_year_valid CHECK (year >= 2000 AND year <= 2100),
    CONSTRAINT check_alert_threshold_valid CHECK (alert_threshold BETWEEN 0 AND 100),
    CONSTRAINT check_spent_amount_non_negative CHECK (spent_amount >= 0)
);

-- Indexes for budgets table
CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_user_period ON budgets(user_id, year, month);
CREATE INDEX idx_budgets_category_id ON budgets(category_id);

COMMENT ON TABLE budgets IS 'Spending limits per category and time period';
COMMENT ON COLUMN budgets.spent_amount IS 'Calculated amount spent in this budget period';
COMMENT ON COLUMN budgets.alert_threshold IS 'Percentage (0-100) at which to trigger alerts';

-- ============================================================================
-- TABLE: user_sessions
-- Purpose: Manages active user sessions and JWT tokens
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    jwt_token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    -- Foreign Keys
    CONSTRAINT fk_user_sessions_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT check_expires_at_future CHECK (expires_at > created_at)
);

-- Indexes for user_sessions table
CREATE UNIQUE INDEX idx_user_sessions_token ON user_sessions(jwt_token);
CREATE INDEX idx_user_sessions_user_active ON user_sessions(user_id, active);
CREATE INDEX idx_user_sessions_expires ON user_sessions(expires_at);

COMMENT ON TABLE user_sessions IS 'Active user sessions with JWT tokens for authentication';
COMMENT ON COLUMN user_sessions.jwt_token IS 'Unique JWT token for this session';
COMMENT ON COLUMN user_sessions.ip_address IS 'IP address from which session was created';

-- ============================================================================
-- TRIGGERS: Auto-update timestamps
-- ============================================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to all tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_transactions_updated_at BEFORE UPDATE ON transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_budgets_updated_at BEFORE UPDATE ON budgets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- END OF MIGRATION V1
-- ============================================================================

