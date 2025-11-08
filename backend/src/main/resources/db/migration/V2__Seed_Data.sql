-- ============================================================================
-- ExpenseTracker Database - Seed Data Migration
-- Version: 2.0
-- Description: Inserts initial test data for development and demonstration
-- ============================================================================

-- ============================================================================
-- SEED DATA: Users
-- ============================================================================

-- Demo user with password: Demo1234!
-- BCrypt hash generated with cost factor 10
INSERT INTO users (email, username, password_hash, first_name, last_name, currency, active) 
VALUES (
    'demo@expensetracker.com',
    'demo_user',
    '$2a$10$rB8L5vQzN.7xXqJ5K2fVm.YvXqZqDqKzJ3LqGZhQzX4vF5qF9vQ1G', -- Demo1234!
    'Usuario',
    'Demo',
    'USD',
    true
) ON CONFLICT (email) DO NOTHING;

-- Additional test user
INSERT INTO users (email, username, password_hash, first_name, last_name, currency, active) 
VALUES (
    'test@expensetracker.com',
    'test_user',
    '$2a$10$rB8L5vQzN.7xXqJ5K2fVm.YvXqZqDqKzJ3LqGZhQzX4vF5qF9vQ1G', -- Demo1234!
    'Test',
    'User',
    'COP',
    true
) ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- SEED DATA: Categories (Default System Categories)
-- ============================================================================

-- Get demo user ID for FK relationships
DO $$
DECLARE
    v_demo_user_id BIGINT;
BEGIN
    SELECT id INTO v_demo_user_id FROM users WHERE email = 'demo@expensetracker.com';

    -- Income Categories
    INSERT INTO categories (user_id, name, description, color, icon, is_default, active) VALUES
    (v_demo_user_id, 'Salario', 'Ingresos por salario mensual', '#4CAF50', 'attach_money', true, true),
    (v_demo_user_id, 'Freelance', 'Ingresos por trabajos independientes', '#8BC34A', 'work', true, true),
    (v_demo_user_id, 'Inversiones', 'Rendimientos de inversiones', '#66BB6A', 'trending_up', true, true);

    -- Expense Categories
    INSERT INTO categories (user_id, name, description, color, icon, is_default, active) VALUES
    (v_demo_user_id, 'Alimentación', 'Supermercado, restaurantes, comida', '#FF6384', 'restaurant', true, true),
    (v_demo_user_id, 'Transporte', 'Gasolina, transporte público, taxi', '#36A2EB', 'directions_car', true, true),
    (v_demo_user_id, 'Entretenimiento', 'Cine, streaming, salidas', '#FFCE56', 'movie', true, true),
    (v_demo_user_id, 'Salud', 'Medicamentos, consultas médicas', '#4BC0C0', 'local_hospital', true, true),
    (v_demo_user_id, 'Educación', 'Cursos, libros, capacitación', '#9966FF', 'school', true, true),
    (v_demo_user_id, 'Vivienda', 'Arriendo, servicios públicos, mantenimiento', '#FF9F40', 'home', true, true),
    (v_demo_user_id, 'Servicios', 'Internet, teléfono, suscripciones', '#FF6384', 'build', true, true),
    (v_demo_user_id, 'Ropa', 'Vestuario y accesorios', '#C9CBCF', 'checkroom', true, true),
    (v_demo_user_id, 'Deporte', 'Gimnasio, actividades deportivas', '#4DD0E1', 'fitness_center', true, true),
    (v_demo_user_id, 'Mascotas', 'Comida y cuidado de mascotas', '#FFB74D', 'pets', true, true),
    (v_demo_user_id, 'Regalos', 'Regalos y donaciones', '#F06292', 'card_giftcard', true, true),
    (v_demo_user_id, 'Otros Gastos', 'Gastos misceláneos', '#B0BEC5', 'more_horiz', true, true);

END $$;

-- ============================================================================
-- SEED DATA: Transactions (Sample transactions for demo)
-- ============================================================================

DO $$
DECLARE
    v_demo_user_id BIGINT;
    v_cat_salario BIGINT;
    v_cat_alimentacion BIGINT;
    v_cat_transporte BIGINT;
    v_cat_entretenimiento BIGINT;
    v_cat_salud BIGINT;
    v_cat_vivienda BIGINT;
    v_cat_servicios BIGINT;
BEGIN
    -- Get user and category IDs
    SELECT id INTO v_demo_user_id FROM users WHERE email = 'demo@expensetracker.com';
    SELECT id INTO v_cat_salario FROM categories WHERE user_id = v_demo_user_id AND name = 'Salario';
    SELECT id INTO v_cat_alimentacion FROM categories WHERE user_id = v_demo_user_id AND name = 'Alimentación';
    SELECT id INTO v_cat_transporte FROM categories WHERE user_id = v_demo_user_id AND name = 'Transporte';
    SELECT id INTO v_cat_entretenimiento FROM categories WHERE user_id = v_demo_user_id AND name = 'Entretenimiento';
    SELECT id INTO v_cat_salud FROM categories WHERE user_id = v_demo_user_id AND name = 'Salud';
    SELECT id INTO v_cat_vivienda FROM categories WHERE user_id = v_demo_user_id AND name = 'Vivienda';
    SELECT id INTO v_cat_servicios FROM categories WHERE user_id = v_demo_user_id AND name = 'Servicios';

    -- Income transactions (current month)
    INSERT INTO transactions (user_id, category_id, amount, description, transaction_date, transaction_type, payment_method, notes) VALUES
    (v_demo_user_id, v_cat_salario, 3500.00, 'Salario mensual de octubre', CURRENT_DATE - INTERVAL '5 days', 'INCOME', 'TRANSFER', 'Pago de nómina'),
    (v_demo_user_id, v_cat_salario, 3500.00, 'Salario mensual de septiembre', CURRENT_DATE - INTERVAL '35 days', 'INCOME', 'TRANSFER', 'Pago de nómina');

    -- Expense transactions (current month)
    INSERT INTO transactions (user_id, category_id, amount, description, transaction_date, transaction_type, payment_method, notes) VALUES
    (v_demo_user_id, v_cat_vivienda, -800.00, 'Arriendo mensual', CURRENT_DATE - INTERVAL '3 days', 'EXPENSE', 'TRANSFER', 'Pago de arriendo'),
    (v_demo_user_id, v_cat_alimentacion, -85.50, 'Supermercado semanal', CURRENT_DATE - INTERVAL '2 days', 'EXPENSE', 'CARD', 'Compras del mes'),
    (v_demo_user_id, v_cat_alimentacion, -32.00, 'Almuerzo en restaurante', CURRENT_DATE - INTERVAL '1 day', 'EXPENSE', 'CARD', 'Almuerzo con colegas'),
    (v_demo_user_id, v_cat_transporte, -50.00, 'Gasolina', CURRENT_DATE - INTERVAL '4 days', 'EXPENSE', 'CARD', 'Tanque lleno'),
    (v_demo_user_id, v_cat_transporte, -25.00, 'Uber', CURRENT_DATE - INTERVAL '6 days', 'EXPENSE', 'CARD', 'Transporte al aeropuerto'),
    (v_demo_user_id, v_cat_entretenimiento, -45.00, 'Cine y palomitas', CURRENT_DATE - INTERVAL '7 days', 'EXPENSE', 'CASH', 'Salida fin de semana'),
    (v_demo_user_id, v_cat_entretenimiento, -12.99, 'Suscripción Netflix', CURRENT_DATE - INTERVAL '10 days', 'EXPENSE', 'CARD', 'Pago mensual streaming'),
    (v_demo_user_id, v_cat_salud, -60.00, 'Consulta médica', CURRENT_DATE - INTERVAL '8 days', 'EXPENSE', 'CARD', 'Cita general'),
    (v_demo_user_id, v_cat_servicios, -55.00, 'Internet y teléfono', CURRENT_DATE - INTERVAL '5 days', 'EXPENSE', 'TRANSFER', 'Factura mensual'),
    (v_demo_user_id, v_cat_alimentacion, -120.00, 'Supermercado grande', CURRENT_DATE - INTERVAL '12 days', 'EXPENSE', 'CARD', 'Compras mensuales');

    -- Previous month transactions for comparison
    INSERT INTO transactions (user_id, category_id, amount, description, transaction_date, transaction_type, payment_method) VALUES
    (v_demo_user_id, v_cat_alimentacion, -95.00, 'Supermercado', CURRENT_DATE - INTERVAL '40 days', 'EXPENSE', 'CARD'),
    (v_demo_user_id, v_cat_transporte, -60.00, 'Gasolina', CURRENT_DATE - INTERVAL '38 days', 'EXPENSE', 'CARD'),
    (v_demo_user_id, v_cat_vivienda, -800.00, 'Arriendo', CURRENT_DATE - INTERVAL '33 days', 'EXPENSE', 'TRANSFER'),
    (v_demo_user_id, v_cat_entretenimiento, -80.00, 'Salida nocturna', CURRENT_DATE - INTERVAL '42 days', 'EXPENSE', 'CASH');

END $$;

-- ============================================================================
-- SEED DATA: Budgets (Sample budgets for current month)
-- ============================================================================

DO $$
DECLARE
    v_demo_user_id BIGINT;
    v_current_month INT;
    v_current_year INT;
    v_cat_alimentacion BIGINT;
    v_cat_transporte BIGINT;
    v_cat_entretenimiento BIGINT;
    v_cat_vivienda BIGINT;
    v_total_spent DECIMAL(12,2);
BEGIN
    -- Get current month and year
    SELECT EXTRACT(MONTH FROM CURRENT_DATE) INTO v_current_month;
    SELECT EXTRACT(YEAR FROM CURRENT_DATE) INTO v_current_year;
    
    -- Get user and category IDs
    SELECT id INTO v_demo_user_id FROM users WHERE email = 'demo@expensetracker.com';
    SELECT id INTO v_cat_alimentacion FROM categories WHERE user_id = v_demo_user_id AND name = 'Alimentación';
    SELECT id INTO v_cat_transporte FROM categories WHERE user_id = v_demo_user_id AND name = 'Transporte';
    SELECT id INTO v_cat_entretenimiento FROM categories WHERE user_id = v_demo_user_id AND name = 'Entretenimiento';
    SELECT id INTO v_cat_vivienda FROM categories WHERE user_id = v_demo_user_id AND name = 'Vivienda';

    -- Budget for Alimentación
    SELECT COALESCE(SUM(ABS(amount)), 0) INTO v_total_spent
    FROM transactions 
    WHERE user_id = v_demo_user_id 
      AND category_id = v_cat_alimentacion
      AND transaction_type = 'EXPENSE'
      AND EXTRACT(MONTH FROM transaction_date) = v_current_month
      AND EXTRACT(YEAR FROM transaction_date) = v_current_year;

    INSERT INTO budgets (user_id, category_id, limit_amount, month, year, spent_amount, alert_enabled, alert_threshold) 
    VALUES (v_demo_user_id, v_cat_alimentacion, 400.00, v_current_month, v_current_year, v_total_spent, true, 80.00)
    ON CONFLICT (user_id, category_id, month, year) DO NOTHING;

    -- Budget for Transporte
    SELECT COALESCE(SUM(ABS(amount)), 0) INTO v_total_spent
    FROM transactions 
    WHERE user_id = v_demo_user_id 
      AND category_id = v_cat_transporte
      AND transaction_type = 'EXPENSE'
      AND EXTRACT(MONTH FROM transaction_date) = v_current_month
      AND EXTRACT(YEAR FROM transaction_date) = v_current_year;

    INSERT INTO budgets (user_id, category_id, limit_amount, month, year, spent_amount, alert_enabled, alert_threshold) 
    VALUES (v_demo_user_id, v_cat_transporte, 200.00, v_current_month, v_current_year, v_total_spent, true, 85.00)
    ON CONFLICT (user_id, category_id, month, year) DO NOTHING;

    -- Budget for Entretenimiento
    SELECT COALESCE(SUM(ABS(amount)), 0) INTO v_total_spent
    FROM transactions 
    WHERE user_id = v_demo_user_id 
      AND category_id = v_cat_entretenimiento
      AND transaction_type = 'EXPENSE'
      AND EXTRACT(MONTH FROM transaction_date) = v_current_month
      AND EXTRACT(YEAR FROM transaction_date) = v_current_year;

    INSERT INTO budgets (user_id, category_id, limit_amount, month, year, spent_amount, alert_enabled, alert_threshold) 
    VALUES (v_demo_user_id, v_cat_entretenimiento, 150.00, v_current_month, v_current_year, v_total_spent, true, 75.00)
    ON CONFLICT (user_id, category_id, month, year) DO NOTHING;

    -- Budget for Vivienda
    SELECT COALESCE(SUM(ABS(amount)), 0) INTO v_total_spent
    FROM transactions 
    WHERE user_id = v_demo_user_id 
      AND category_id = v_cat_vivienda
      AND transaction_type = 'EXPENSE'
      AND EXTRACT(MONTH FROM transaction_date) = v_current_month
      AND EXTRACT(YEAR FROM transaction_date) = v_current_year;

    INSERT INTO budgets (user_id, category_id, limit_amount, month, year, spent_amount, alert_enabled, alert_threshold) 
    VALUES (v_demo_user_id, v_cat_vivienda, 850.00, v_current_month, v_current_year, v_total_spent, true, 90.00)
    ON CONFLICT (user_id, category_id, month, year) DO NOTHING;

END $$;

-- ============================================================================
-- VERIFICATION QUERIES (Commented - uncomment to verify data)
-- ============================================================================

-- SELECT 'Users created:' as info, COUNT(*) as count FROM users;
-- SELECT 'Categories created:' as info, COUNT(*) as count FROM categories;
-- SELECT 'Transactions created:' as info, COUNT(*) as count FROM transactions;
-- SELECT 'Budgets created:' as info, COUNT(*) as count FROM budgets;

-- ============================================================================
-- END OF MIGRATION V2
-- ============================================================================

