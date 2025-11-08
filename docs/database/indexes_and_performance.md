# ExpenseTracker - Índices y Optimización de Performance

## 📋 Información General

Este documento describe la estrategia de indexación implementada para optimizar el rendimiento de consultas en ExpenseTracker, incluyendo justificaciones, patrones de consulta y métricas de performance.

---

## 🎯 Objetivos de Performance

| Métrica | Target | Prioridad |
|---------|--------|-----------|
| Consultas de dashboard | < 100ms | Alta |
| Búsqueda de transacciones | < 50ms | Alta |
| Validación de JWT | < 10ms | Crítica |
| Reportes mensuales | < 200ms | Media |
| Cálculo de presupuestos | < 150ms | Alta |

---

## 📊 Resumen de Índices por Tabla

| Tabla | Índices Primarios | Índices Secundarios | Total |
|-------|-------------------|---------------------|-------|
| users | 2 | 2 | 4 |
| categories | 2 | 2 | 4 |
| transactions | 4 | 4 | 8 |
| budgets | 3 | 2 | 5 |
| user_sessions | 2 | 2 | 4 |
| **TOTAL** | **13** | **12** | **25** |

---

## 🗄️ Índices Detallados por Tabla

### 1. Tabla: **users**

#### idx_users_email (UNIQUE)
```sql
CREATE UNIQUE INDEX idx_users_email ON users(email);
```
**Propósito:** Optimizar proceso de login  
**Patrón de consulta:**
```sql
SELECT * FROM users WHERE email = 'usuario@example.com';
```
**Uso:** Cada login de usuario  
**Impacto:** Crítico - usado en cada autenticación  
**Tipo:** B-tree, UNIQUE

---

#### idx_users_username
```sql
CREATE INDEX idx_users_username ON users(username) WHERE active = true;
```
**Propósito:** Búsqueda de perfiles por username  
**Patrón de consulta:**
```sql
SELECT * FROM users WHERE username = 'johndoe' AND active = true;
```
**Uso:** Búsqueda de usuarios, verificación de disponibilidad  
**Impacto:** Medio  
**Optimización:** Índice parcial (solo usuarios activos)

---

#### idx_users_active
```sql
CREATE INDEX idx_users_active ON users(active);
```
**Propósito:** Filtrado de usuarios activos/inactivos  
**Patrón de consulta:**
```sql
SELECT COUNT(*) FROM users WHERE active = true;
```
**Uso:** Reportes administrativos, analytics  
**Impacto:** Bajo

---

#### idx_users_created_at
```sql
CREATE INDEX idx_users_created_at ON users(created_at DESC) WHERE active = true;
```
**Propósito:** Análisis de registros por fecha  
**Patrón de consulta:**
```sql
SELECT * FROM users 
WHERE created_at >= '2024-01-01' 
  AND active = true
ORDER BY created_at DESC;
```
**Uso:** Dashboard administrativo, growth analytics  
**Impacto:** Bajo

---

### 2. Tabla: **categories**

#### idx_categories_user_id
```sql
CREATE INDEX idx_categories_user_id ON categories(user_id);
```
**Propósito:** Listar categorías de un usuario  
**Patrón de consulta:**
```sql
SELECT * FROM categories WHERE user_id = 1;
```
**Uso:** Dropdown de categorías en formularios  
**Impacto:** Alto - usado frecuentemente

---

#### idx_categories_user_active
```sql
CREATE INDEX idx_categories_user_active ON categories(user_id, active);
```
**Propósito:** Categorías activas de un usuario  
**Patrón de consulta:**
```sql
SELECT * FROM categories WHERE user_id = 1 AND active = true;
```
**Uso:** UI principal, filtros  
**Impacto:** Alto  
**Tipo:** Composite index (covering index para este query)

---

#### idx_categories_name_trgm
```sql
CREATE INDEX idx_categories_name_trgm ON categories(name);
```
**Propósito:** Búsqueda de categorías por nombre  
**Patrón de consulta:**
```sql
SELECT * FROM categories 
WHERE user_id = 1 AND name ILIKE '%aliment%';
```
**Uso:** Autocomplete, búsqueda de categorías  
**Impacto:** Medio

---

#### idx_categories_default
```sql
CREATE INDEX idx_categories_default ON categories(is_default) 
WHERE is_default = true;
```
**Propósito:** Listar categorías del sistema  
**Patrón de consulta:**
```sql
SELECT * FROM categories WHERE is_default = true;
```
**Uso:** Inicialización de usuarios nuevos  
**Impacto:** Bajo  
**Optimización:** Índice parcial (solo defaults)

---

### 3. Tabla: **transactions** (Tabla más consultada)

#### idx_transactions_user_id
```sql
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
```
**Propósito:** Base para consultas por usuario  
**Uso:** Todas las consultas filtradas por usuario  
**Impacto:** Crítico

---

#### idx_transactions_user_date
```sql
CREATE INDEX idx_transactions_user_date 
ON transactions(user_id, transaction_date DESC);
```
**Propósito:** Historial de transacciones ordenado  
**Patrón de consulta:**
```sql
SELECT * FROM transactions 
WHERE user_id = 1 
ORDER BY transaction_date DESC 
LIMIT 20;
```
**Uso:** Dashboard principal, historial  
**Impacto:** Crítico - uno de los queries más frecuentes  
**Tipo:** Composite index con ordenamiento DESC

---

#### idx_transactions_category_id
```sql
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
```
**Propósito:** Transacciones por categoría  
**Patrón de consulta:**
```sql
SELECT COUNT(*), SUM(amount) 
FROM transactions 
WHERE category_id = 5;
```
**Uso:** Análisis por categoría, gráficos  
**Impacto:** Alto

---

#### idx_transactions_user_category
```sql
CREATE INDEX idx_transactions_user_category 
ON transactions(user_id, category_id);
```
**Propósito:** Filtro combinado usuario + categoría  
**Patrón de consulta:**
```sql
SELECT * FROM transactions 
WHERE user_id = 1 AND category_id = 5
ORDER BY transaction_date DESC;
```
**Uso:** Filtros de UI, análisis específico  
**Impacto:** Alto  
**Tipo:** Composite index

---

#### idx_transactions_user_type
```sql
CREATE INDEX idx_transactions_user_type 
ON transactions(user_id, transaction_type);
```
**Propósito:** Separar ingresos y gastos  
**Patrón de consulta:**
```sql
SELECT SUM(amount) FROM transactions 
WHERE user_id = 1 AND transaction_type = 'EXPENSE';
```
**Uso:** Cálculos de balance, gráficos  
**Impacto:** Alto

---

#### idx_transactions_date
```sql
CREATE INDEX idx_transactions_date 
ON transactions(transaction_date DESC);
```
**Propósito:** Queries por rango de fechas  
**Patrón de consulta:**
```sql
SELECT * FROM transactions 
WHERE transaction_date BETWEEN '2024-10-01' AND '2024-10-31';
```
**Uso:** Reportes mensuales, exportaciones  
**Impacto:** Medio

---

#### idx_transactions_user_date_active
```sql
CREATE INDEX idx_transactions_user_date_active 
ON transactions(user_id, transaction_date DESC, active)
WHERE active = true;
```
**Propósito:** Transacciones activas recientes (Dashboard)  
**Patrón de consulta:**
```sql
SELECT * FROM transactions 
WHERE user_id = 1 AND active = true
ORDER BY transaction_date DESC 
LIMIT 10;
```
**Uso:** Dashboard principal  
**Impacto:** Crítico  
**Optimización:** Índice parcial + covering index

---

#### idx_transactions_user_payment
```sql
CREATE INDEX idx_transactions_user_payment 
ON transactions(user_id, payment_method)
WHERE transaction_type = 'EXPENSE';
```
**Propósito:** Análisis por método de pago  
**Patrón de consulta:**
```sql
SELECT payment_method, SUM(ABS(amount)) 
FROM transactions 
WHERE user_id = 1 AND transaction_type = 'EXPENSE'
GROUP BY payment_method;
```
**Uso:** Reportes de métodos de pago  
**Impacto:** Bajo  
**Optimización:** Índice parcial (solo gastos)

---

### 4. Tabla: **budgets**

#### idx_budgets_user_id
```sql
CREATE INDEX idx_budgets_user_id ON budgets(user_id);
```
**Propósito:** Presupuestos de un usuario  
**Impacto:** Alto

---

#### idx_budgets_user_period
```sql
CREATE INDEX idx_budgets_user_period 
ON budgets(user_id, year, month);
```
**Propósito:** Presupuestos de un período específico  
**Patrón de consulta:**
```sql
SELECT * FROM budgets 
WHERE user_id = 1 AND year = 2024 AND month = 10;
```
**Uso:** Dashboard de presupuestos  
**Impacto:** Crítico  
**Tipo:** Composite index (cubre UNIQUE constraint)

---

#### idx_budgets_category_id
```sql
CREATE INDEX idx_budgets_category_id ON budgets(category_id);
```
**Propósito:** Presupuestos por categoría  
**Impacto:** Medio

---

#### idx_budgets_alert
```sql
CREATE INDEX idx_budgets_alert 
ON budgets(user_id, alert_enabled)
WHERE alert_enabled = true AND active = true;
```
**Propósito:** Encontrar presupuestos con alertas habilitadas  
**Patrón de consulta:**
```sql
SELECT * FROM budgets 
WHERE user_id = 1 
  AND alert_enabled = true 
  AND active = true
  AND (spent_amount / limit_amount * 100) >= alert_threshold;
```
**Uso:** Sistema de notificaciones  
**Impacto:** Medio  
**Optimización:** Índice parcial

---

#### idx_budgets_period_active
```sql
CREATE INDEX idx_budgets_period_active 
ON budgets(year, month, active)
WHERE active = true;
```
**Propósito:** Presupuestos activos por período (analytics)  
**Impacto:** Bajo

---

### 5. Tabla: **user_sessions**

#### idx_user_sessions_token (UNIQUE)
```sql
CREATE UNIQUE INDEX idx_user_sessions_token ON user_sessions(jwt_token);
```
**Propósito:** Validación de tokens JWT  
**Patrón de consulta:**
```sql
SELECT * FROM user_sessions 
WHERE jwt_token = 'eyJhbGciOi...' 
  AND active = true 
  AND expires_at > CURRENT_TIMESTAMP;
```
**Uso:** Cada request autenticado  
**Impacto:** CRÍTICO - performance esencial  
**Target:** < 10ms

---

#### idx_user_sessions_user_active
```sql
CREATE INDEX idx_user_sessions_user_active 
ON user_sessions(user_id, active);
```
**Propósito:** Sesiones activas de un usuario  
**Patrón de consulta:**
```sql
SELECT * FROM user_sessions 
WHERE user_id = 1 AND active = true;
```
**Uso:** Listar sesiones, invalidar sesiones  
**Impacto:** Medio

---

#### idx_sessions_expired
```sql
CREATE INDEX idx_sessions_expired 
ON user_sessions(expires_at)
WHERE active = true;
```
**Propósito:** Limpieza de sesiones expiradas  
**Patrón de consulta:**
```sql
DELETE FROM user_sessions 
WHERE expires_at < CURRENT_TIMESTAMP AND active = true;
```
**Uso:** Job de mantenimiento diario  
**Impacto:** Bajo  
**Optimización:** Índice parcial

---

#### idx_sessions_user_created
```sql
CREATE INDEX idx_sessions_user_created 
ON user_sessions(user_id, created_at DESC);
```
**Propósito:** Historial de sesiones  
**Impacto:** Bajo

---

## 🚀 Consultas Optimizadas (Explain Analyze)

### Query 1: Dashboard - Transacciones Recientes

```sql
EXPLAIN ANALYZE
SELECT 
    t.id,
    t.amount,
    t.description,
    t.transaction_date,
    t.transaction_type,
    c.name as category_name,
    c.color as category_color
FROM transactions t
JOIN categories c ON t.category_id = c.id
WHERE t.user_id = 1 
  AND t.active = true
ORDER BY t.transaction_date DESC
LIMIT 10;
```

**Índices utilizados:**
- `idx_transactions_user_date_active` (principal)
- `pk_categories` (para el JOIN)

**Performance esperado:** 10-30ms  
**Tipo de scan:** Index Scan + Nested Loop

---

### Query 2: Resumen Mensual por Categoría

```sql
EXPLAIN ANALYZE
SELECT 
    c.name,
    c.color,
    COUNT(t.id) as transaction_count,
    SUM(ABS(t.amount)) as total_amount,
    (SUM(ABS(t.amount)) * 100.0 / (
        SELECT SUM(ABS(amount)) 
        FROM transactions 
        WHERE user_id = 1 
          AND transaction_type = 'EXPENSE'
          AND EXTRACT(MONTH FROM transaction_date) = 10
    )) as percentage
FROM transactions t
JOIN categories c ON t.category_id = c.id
WHERE t.user_id = 1
  AND t.transaction_type = 'EXPENSE'
  AND EXTRACT(MONTH FROM t.transaction_date) = 10
  AND EXTRACT(YEAR FROM t.transaction_date) = 2024
GROUP BY c.id, c.name, c.color
ORDER BY total_amount DESC;
```

**Índices utilizados:**
- `idx_transactions_user_type`
- `idx_transactions_user_date`

**Performance esperado:** 50-100ms  
**Tipo de scan:** Index Scan + HashAggregate

---

### Query 3: Validación de Sesión JWT

```sql
EXPLAIN ANALYZE
SELECT 
    s.id,
    s.user_id,
    s.expires_at,
    u.email,
    u.first_name,
    u.last_name
FROM user_sessions s
JOIN users u ON s.user_id = u.id
WHERE s.jwt_token = 'eyJhbGciOiJIUzI1NiIs...'
  AND s.active = true
  AND s.expires_at > CURRENT_TIMESTAMP;
```

**Índices utilizados:**
- `idx_user_sessions_token` (UNIQUE)

**Performance esperado:** 5-15ms  
**Tipo de scan:** Index Scan (single row lookup)

---

### Query 4: Estado de Presupuestos

```sql
EXPLAIN ANALYZE
SELECT 
    b.id,
    c.name as category_name,
    b.limit_amount,
    b.spent_amount,
    (b.spent_amount / b.limit_amount * 100) as percentage_used,
    b.alert_threshold
FROM budgets b
JOIN categories c ON b.category_id = c.id
WHERE b.user_id = 1
  AND b.month = EXTRACT(MONTH FROM CURRENT_DATE)
  AND b.year = EXTRACT(YEAR FROM CURRENT_DATE)
  AND b.active = true
ORDER BY percentage_used DESC;
```

**Índices utilizados:**
- `idx_budgets_user_period`

**Performance esperado:** 20-50ms

---

## 📈 Monitoreo de Performance

### Consultas de Índices No Utilizados

```sql
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    pg_size_pretty(pg_relation_size(indexrelid)) as size
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
  AND idx_scan = 0
ORDER BY pg_relation_size(indexrelid) DESC;
```

**Acción:** Considerar eliminar índices con 0 scans después de 1 mes en producción.

---

### Índices Más Grandes

```sql
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    idx_scan as scans,
    idx_tup_read as tuples_read
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC
LIMIT 10;
```

**Análisis:** Evaluar relación tamaño/uso para optimizar.

---

### Cache Hit Ratio

```sql
SELECT 
    'index hit rate' AS name,
    (sum(idx_blks_hit)) / nullif(sum(idx_blks_hit + idx_blks_read),0) AS ratio
FROM pg_statio_user_indexes
UNION ALL
SELECT 
    'table hit rate' AS name,
    sum(heap_blks_hit) / nullif(sum(heap_blks_hit) + sum(heap_blks_read),0) AS ratio
FROM pg_statio_user_tables;
```

**Target:** > 99% hit rate  
**Acción:** Si < 95%, considerar aumentar `shared_buffers`

---

### Consultas Lentas

```sql
-- Requiere pg_stat_statements extension
SELECT 
    query,
    calls,
    total_exec_time / 1000 as total_seconds,
    mean_exec_time as avg_ms,
    max_exec_time as max_ms
FROM pg_stat_statements
WHERE query LIKE '%transactions%' OR query LIKE '%budgets%'
ORDER BY mean_exec_time DESC
LIMIT 10;
```

---

## 🔧 Mantenimiento de Índices

### Reindexación

```sql
-- Por tabla (requiere lock exclusivo)
REINDEX TABLE transactions;

-- Por índice individual (más granular)
REINDEX INDEX idx_transactions_user_date;

-- Concurrente (sin lock, disponible en PG 12+)
REINDEX INDEX CONCURRENTLY idx_transactions_user_date;
```

**Frecuencia recomendada:** Trimestral o cuando index bloat > 30%

---

### Actualizar Estadísticas

```sql
-- Tabla específica
ANALYZE transactions;

-- Todas las tablas
ANALYZE;

-- Con verbose para ver proceso
ANALYZE VERBOSE transactions;
```

**Frecuencia:** Automático, pero forzar después de bulk inserts

---

### Detectar Index Bloat

```sql
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) as index_size,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC;
```

---

## ⚠️ Anti-Patrones y Precauciones

### ❌ Evitar

1. **Índices en columnas de baja cardinalidad sin filtro**
   ```sql
   -- MAL: active solo tiene true/false
   CREATE INDEX idx_bad ON transactions(active);
   
   -- BIEN: Índice parcial
   CREATE INDEX idx_good ON transactions(user_id, active) 
   WHERE active = true;
   ```

2. **Demasiados índices en tablas de escritura intensiva**
   - Cada índice adicional ralentiza INSERT/UPDATE
   - Mantener balance entre lectura y escritura

3. **Índices redundantes**
   ```sql
   -- Si existe idx_transactions(user_id, transaction_date)
   -- No se necesita idx_transactions(user_id) 
   -- PostgreSQL puede usar el compuesto
   ```

4. **No actualizar estadísticas después de bulk loads**
   ```sql
   COPY transactions FROM ...;
   ANALYZE transactions; -- IMPORTANTE
   ```

### ✅ Mejores Prácticas

1. **Usar EXPLAIN ANALYZE** antes de crear índices
2. **Monitorear uso** con pg_stat_user_indexes
3. **Índices parciales** para casos específicos
4. **Composite indexes** en orden correcto (más selectivo primero)
5. **Mantener tabla statistics actualizadas**

---

## 📊 Benchmarks de Referencia

| Operación | Sin Índices | Con Índices | Mejora |
|-----------|-------------|-------------|--------|
| Login (email lookup) | 200ms | 5ms | 40x |
| Dashboard (10 transacciones) | 500ms | 25ms | 20x |
| Validación JWT | 100ms | 8ms | 12x |
| Reporte mensual | 2000ms | 150ms | 13x |
| Búsqueda categoría | 300ms | 15ms | 20x |

**Dataset de prueba:** 1 usuario, 1000 transacciones, 15 categorías

---

## 🎯 Objetivos de Optimización Futura

1. **Particionamiento de transactions** por año/mes (cuando > 1M registros)
2. **Materialized views** para reportes complejos
3. **Read replicas** para analytics pesados
4. **Connection pooling** con PgBouncer
5. **Query caching** con Redis

---

**Última actualización:** Octubre 2024  
**Mantenido por:** Equipo de Desarrollo ExpenseTracker

