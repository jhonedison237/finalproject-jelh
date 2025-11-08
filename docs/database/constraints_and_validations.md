# ExpenseTracker - Constraints y Validaciones

## 📋 Información General

Este documento detalla todas las restricciones (constraints) y reglas de validación implementadas a nivel de base de datos para garantizar la integridad y consistencia de los datos en el sistema ExpenseTracker.

---

## 🔐 Filosofía de Validación

Las validaciones están distribuidas en dos capas:

1. **Nivel de Base de Datos (este documento):** Validaciones críticas que NUNCA pueden ser violadas
2. **Nivel de Aplicación:** Validaciones de negocio y UX (feedback más rico al usuario)

**Principio:** La base de datos es la última línea de defensa contra datos incorrectos.

---

## 🗂️ Constraints por Tabla

### 1. Tabla: **users**

#### Primary Key
```sql
PRIMARY KEY (id)
```

#### Unique Constraints
```sql
UNIQUE (email)
UNIQUE (username)
```
- Garantiza que no existan emails o usernames duplicados en el sistema

#### Not Null Constraints
- `email`, `username`, `password_hash`, `first_name`, `last_name` deben estar presentes

#### Check Constraints

**Email válido:**
```sql
CONSTRAINT check_email_format 
CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
```
- Valida formato básico de email usando regex
- Permite subdominios y dominios de 2+ caracteres

**Username mínimo:**
```sql
CONSTRAINT check_username_length 
CHECK (LENGTH(username) >= 3)
```
- Username debe tener al menos 3 caracteres
- Evita usernames demasiado cortos

#### Valores por Defecto
```sql
currency = 'USD'
created_at = CURRENT_TIMESTAMP
updated_at = CURRENT_TIMESTAMP
active = true
```

---

### 2. Tabla: **categories**

#### Primary Key
```sql
PRIMARY KEY (id)
```

#### Foreign Keys
```sql
CONSTRAINT fk_categories_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
```
- Elimina todas las categorías cuando se elimina el usuario propietario

#### Unique Constraints
```sql
CONSTRAINT uq_categories_user_name 
UNIQUE (user_id, name)
```
- Un usuario no puede tener dos categorías con el mismo nombre
- Diferentes usuarios SÍ pueden usar el mismo nombre

#### Check Constraints

**Formato de color hexadecimal:**
```sql
CONSTRAINT check_color_format 
CHECK (color ~* '^#[0-9A-Fa-f]{6}$')
```
- Valida formato hexadecimal estándar (#RRGGBB)
- Ejemplos válidos: `#FF6384`, `#007bff`, `#4BC0C0`
- Ejemplos inválidos: `#FFF`, `red`, `#GGGGGG`

#### Valores por Defecto
```sql
color = '#007bff'
icon = 'category'
is_default = false
active = true
```

---

### 3. Tabla: **transactions**

#### Primary Key
```sql
PRIMARY KEY (id)
```

#### Foreign Keys
```sql
CONSTRAINT fk_transactions_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT

CONSTRAINT fk_transactions_category 
FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
```
- **RESTRICT:** Impide eliminar usuarios o categorías si tienen transacciones asociadas
- Protege datos históricos importantes

#### Check Constraints

**Monto diferente de cero:**
```sql
CONSTRAINT check_amount_not_zero 
CHECK (amount != 0)
```
- Transacciones de $0 no tienen sentido
- Fuerza a registrar solo movimientos reales

**Fecha no futura:**
```sql
CONSTRAINT check_transaction_date_not_future 
CHECK (transaction_date <= CURRENT_DATE)
```
- No se pueden registrar transacciones futuras
- Usa CURRENT_DATE (no timestamp) para comparación de días

**Consistencia entre monto y tipo:**
```sql
CONSTRAINT check_amount_sign 
CHECK (
    (transaction_type = 'INCOME' AND amount > 0) OR 
    (transaction_type = 'EXPENSE' AND amount < 0)
)
```
- INCOME debe tener monto positivo
- EXPENSE debe tener monto negativo
- Previene inconsistencias de datos

#### Enumeraciones (ENUM)

**transaction_type:**
```sql
CREATE TYPE transaction_type_enum AS ENUM ('INCOME', 'EXPENSE');
```
- Solo dos valores permitidos
- Tipado fuerte a nivel de base de datos

**payment_method:**
```sql
CREATE TYPE payment_method_enum AS ENUM ('CASH', 'CARD', 'TRANSFER', 'OTHER');
```
- Métodos de pago estandarizados
- Facilita agrupación y análisis

#### Valores por Defecto
```sql
active = true
created_at = CURRENT_TIMESTAMP
updated_at = CURRENT_TIMESTAMP
```

---

### 4. Tabla: **budgets**

#### Primary Key
```sql
PRIMARY KEY (id)
```

#### Foreign Keys
```sql
CONSTRAINT fk_budgets_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE

CONSTRAINT fk_budgets_category 
FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
```
- CASCADE: Al eliminar usuario o categoría, se eliminan los presupuestos asociados
- Los presupuestos dependen completamente de estas entidades

#### Unique Constraints
```sql
CONSTRAINT uq_budgets_user_category_period 
UNIQUE (user_id, category_id, month, year)
```
- Un usuario solo puede tener UN presupuesto por categoría por mes
- Previene duplicación de límites de gasto

#### Check Constraints

**Monto límite positivo:**
```sql
CONSTRAINT check_limit_amount_positive 
CHECK (limit_amount > 0)
```
- Presupuesto debe ser mayor a cero
- No tiene sentido un límite de $0 o negativo

**Mes válido:**
```sql
CONSTRAINT check_month_valid 
CHECK (month BETWEEN 1 AND 12)
```
- Solo meses válidos del calendario
- Previene errores de lógica

**Año válido:**
```sql
CONSTRAINT check_year_valid 
CHECK (year >= 2000 AND year <= 2100)
```
- Rango razonable de años
- Protege contra errores de entrada

**Umbral de alerta válido:**
```sql
CONSTRAINT check_alert_threshold_valid 
CHECK (alert_threshold BETWEEN 0 AND 100)
```
- Porcentaje entre 0% y 100%
- Evita valores absurdos

**Monto gastado no negativo:**
```sql
CONSTRAINT check_spent_amount_non_negative 
CHECK (spent_amount >= 0)
```
- El gasto acumulado no puede ser negativo
- Protege integridad del cálculo

#### Valores por Defecto
```sql
spent_amount = 0
alert_enabled = true
alert_threshold = 80.00
active = true
```

---

### 5. Tabla: **user_sessions**

#### Primary Key
```sql
PRIMARY KEY (id)
```

#### Foreign Keys
```sql
CONSTRAINT fk_user_sessions_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
```
- Elimina sesiones cuando se elimina el usuario
- Mantiene limpieza de datos

#### Unique Constraints
```sql
UNIQUE (jwt_token)
```
- Cada token JWT debe ser único en el sistema
- Previene colisiones de tokens

#### Check Constraints

**Fecha de expiración futura:**
```sql
CONSTRAINT check_expires_at_future 
CHECK (expires_at > created_at)
```
- La expiración debe ser posterior a la creación
- Previene sesiones con tiempos inválidos

#### Valores por Defecto
```sql
active = true
created_at = CURRENT_TIMESTAMP
```

---

## 🔄 Políticas de Eliminación (ON DELETE)

### CASCADE (Eliminación en Cascada)

Usado cuando los datos dependientes NO tienen valor sin el padre:

```
users → categories (CASCADE)
users → budgets (CASCADE)
users → user_sessions (CASCADE)
categories → budgets (CASCADE)
```

**Ejemplo:** Si eliminas un usuario, sus categorías personalizadas también se eliminan.

### RESTRICT (Restricción)

Usado cuando los datos dependientes deben ser preservados:

```
users → transactions (RESTRICT)
categories → transactions (RESTRICT)
```

**Ejemplo:** No puedes eliminar un usuario que tenga transacciones registradas. Primero debes archivarlas o transferirlas.

---

## ✅ Ejemplos de Validación

### ✅ Casos Válidos

```sql
-- Usuario válido
INSERT INTO users (email, username, password_hash, first_name, last_name) 
VALUES ('juan@example.com', 'juanperez', '$2a$10$...', 'Juan', 'Pérez');

-- Transacción de gasto válida
INSERT INTO transactions (user_id, category_id, amount, description, 
                          transaction_date, transaction_type, payment_method) 
VALUES (1, 5, -25.50, 'Almuerzo', '2024-10-28', 'EXPENSE', 'CARD');

-- Presupuesto válido
INSERT INTO budgets (user_id, category_id, limit_amount, month, year) 
VALUES (1, 5, 400.00, 10, 2024);
```

### ❌ Casos Inválidos (Rechazados)

```sql
-- Email inválido
INSERT INTO users (email, username, password_hash, first_name, last_name) 
VALUES ('not-an-email', 'user', '$2a$10$...', 'Test', 'User');
-- ERROR: violates check constraint "check_email_format"

-- Username muy corto
INSERT INTO users (email, username, password_hash, first_name, last_name) 
VALUES ('test@example.com', 'ab', '$2a$10$...', 'Test', 'User');
-- ERROR: violates check constraint "check_username_length"

-- Transacción con monto cero
INSERT INTO transactions (..., amount, ...) VALUES (..., 0, ...);
-- ERROR: violates check constraint "check_amount_not_zero"

-- Transacción futura
INSERT INTO transactions (..., transaction_date, ...) 
VALUES (..., '2025-12-31', ...);
-- ERROR: violates check constraint "check_transaction_date_not_future"

-- Inconsistencia tipo-monto (INCOME con monto negativo)
INSERT INTO transactions (..., amount, transaction_type, ...) 
VALUES (..., -100, 'INCOME', ...);
-- ERROR: violates check constraint "check_amount_sign"

-- Color hexadecimal inválido
INSERT INTO categories (..., color, ...) VALUES (..., 'blue', ...);
-- ERROR: violates check constraint "check_color_format"

-- Mes inválido
INSERT INTO budgets (..., month, year, ...) VALUES (..., 13, 2024, ...);
-- ERROR: violates check constraint "check_month_valid"

-- Umbral de alerta fuera de rango
INSERT INTO budgets (..., alert_threshold, ...) VALUES (..., 150, ...);
-- ERROR: violates check constraint "check_alert_threshold_valid"
```

---

## 🔍 Verificación de Constraints

### Listar todos los constraints de una tabla

```sql
SELECT
    tc.constraint_name,
    tc.constraint_type,
    cc.check_clause
FROM information_schema.table_constraints tc
LEFT JOIN information_schema.check_constraints cc 
    ON tc.constraint_name = cc.constraint_name
WHERE tc.table_name = 'transactions'
ORDER BY tc.constraint_type, tc.constraint_name;
```

### Verificar foreign keys

```sql
SELECT
    tc.constraint_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name,
    rc.update_rule,
    rc.delete_rule
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
JOIN information_schema.referential_constraints AS rc
    ON rc.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY' 
  AND tc.table_name = 'transactions';
```

---

## 📝 Mejores Prácticas

### ✅ Hacer

1. **Validar en múltiples capas:** Base de datos + Aplicación + Frontend
2. **Usar nombres descriptivos:** `check_amount_not_zero` mejor que `chk_amt`
3. **Documentar constraints complejos:** Comentarios SQL explican el "por qué"
4. **Probar casos límite:** Valores extremos, nulos, vacíos
5. **Mantener consistencia:** Patrones similares en todas las tablas

### ❌ Evitar

1. **Validaciones solo en aplicación:** La DB puede ser accedida directamente
2. **Constraints demasiado restrictivos:** Dificultan cambios legítimos
3. **Regex complejas:** Difíciles de mantener y debuggear
4. **Eliminar constraints "molestos":** Existen por una razón
5. **Ignorar errores de constraint:** Indican problemas de lógica

---

## 🧪 Testing de Validaciones

### Suite de Tests Recomendada

```sql
-- Test Suite: Validaciones de Transacciones

-- Test 1: Rechazar monto cero
BEGIN;
    INSERT INTO transactions (..., amount, ...) VALUES (..., 0, ...);
    -- Esperado: ERROR
ROLLBACK;

-- Test 2: Rechazar fecha futura
BEGIN;
    INSERT INTO transactions (..., transaction_date, ...) 
    VALUES (..., CURRENT_DATE + 1, ...);
    -- Esperado: ERROR
ROLLBACK;

-- Test 3: Aceptar transacción válida
BEGIN;
    INSERT INTO transactions (..., amount, transaction_type, ...) 
    VALUES (..., -50.00, 'EXPENSE', ...);
    -- Esperado: SUCCESS
ROLLBACK;
```

---

## 🔄 Mantenimiento de Constraints

### Agregar nuevo constraint

```sql
ALTER TABLE transactions 
ADD CONSTRAINT check_description_not_empty 
CHECK (LENGTH(TRIM(description)) > 0);
```

### Eliminar constraint

```sql
ALTER TABLE transactions 
DROP CONSTRAINT check_amount_not_zero;
```

### Modificar constraint

```sql
-- No se puede modificar directamente, hay que eliminar y recrear
ALTER TABLE budgets DROP CONSTRAINT check_year_valid;
ALTER TABLE budgets ADD CONSTRAINT check_year_valid 
CHECK (year >= 2020 AND year <= 2100);
```

---

## 📚 Referencias

- [PostgreSQL Constraints Documentation](https://www.postgresql.org/docs/current/ddl-constraints.html)
- [SQL ENUM Types](https://www.postgresql.org/docs/current/datatype-enum.html)
- [Referential Integrity](https://www.postgresql.org/docs/current/tutorial-fk.html)

---

**Última actualización:** Octubre 2024  
**Mantenido por:** Equipo de Desarrollo ExpenseTracker

