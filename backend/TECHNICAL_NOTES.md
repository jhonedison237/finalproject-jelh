# Notas Técnicas - Backend API ExpenseTracker

## 📋 Resumen de Implementación

Este documento describe las decisiones técnicas y detalles de implementación del backend de ExpenseTracker.

## 🏗️ Arquitectura

### Patrón de Capas

El proyecto sigue una arquitectura de capas estándar de Spring Boot:

```
┌─────────────────────────────┐
│  Controller Layer           │  ← REST endpoints, validación de entrada
├─────────────────────────────┤
│  Service Layer              │  ← Lógica de negocio
├─────────────────────────────┤
│  Repository Layer           │  ← Acceso a datos (Spring Data JPA)
├─────────────────────────────┤
│  Database (PostgreSQL)      │  ← Persistencia
└─────────────────────────────┘
```

### Componentes Principales

#### 1. **Entities (JPA)**
- Representan las tablas de la base de datos
- Anotadas con `@Entity`, `@Table`, etc.
- Incluyen validaciones a nivel de BD
- Auditoría automática con `@CreatedDate` y `@LastModifiedDate`

#### 2. **DTOs (Data Transfer Objects)**
- **Request DTOs**: Validación de entrada con Bean Validation
- **Response DTOs**: Formato de salida controlado, evita exposición de datos sensibles
- Separación clara entre modelo de dominio y API

#### 3. **Repositories**
- Interfaces que extienden `JpaRepository`
- Query methods generados automáticamente
- Queries personalizadas con `@Query` para agregaciones complejas

#### 4. **Services**
- Lógica de negocio aislada
- Transacciones gestionadas con `@Transactional`
- Logging detallado para debugging

#### 5. **Controllers**
- Endpoints RESTful
- Validación automática con `@Valid`
- Documentación con anotaciones OpenAPI

## 🔐 Seguridad

### Estado Actual (Development)

- **CSRF**: Deshabilitado (apropiado para API REST)
- **Autenticación**: Temporalmente deshabilitada (permite todas las peticiones)
- **Usuario Demo**: Se usa un usuario hardcodeado (ID=1) en los controllers

### Implementación Futura

En la siguiente fase se implementará:
- JWT (JSON Web Tokens) para autenticación stateless
- Spring Security Filter Chain para validar tokens
- UserDetails custom para integración con User entity
- Refresh tokens para mantener sesiones

## 💾 Base de Datos

### Estrategia de Migraciones

- **Flyway**: Gestión de versiones de esquema
- **Convención de nomenclatura**: `V{version}__{description}.sql`
- **Orden de ejecución**:
  1. V1: Schema inicial (tablas, constraints)
  2. V2: Datos semilla (categorías, usuario demo)
  3. V3: Índices para optimización

### Decisiones de Diseño

#### Soft Delete
- Se usa `active = false` en lugar de `DELETE`
- Permite auditoría y recuperación de datos
- Queries filtran automáticamente registros inactivos

#### Timestamps Automáticos
- `created_at`: Se establece al crear el registro
- `updated_at`: Se actualiza en cada modificación
- Gestionado por JPA Auditing (`@EntityListeners`)

#### Montos de Transacciones
- **Ingresos**: Se almacenan como valores positivos
- **Gastos**: Se almacenan como valores negativos
- Ventajas:
  - Cálculo de balance simplificado: `SUM(amount)`
  - Diferenciación clara en BD
  - Queries de agregación más eficientes

### Índices Estratégicos

```sql
-- Transacciones por usuario
CREATE INDEX idx_transactions_user_id ON transactions(user_id);

-- Filtros por fecha (queries más frecuentes)
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);

-- Filtros por categoría
CREATE INDEX idx_transactions_category_id ON transactions(category_id);

-- Búsquedas de categorías
CREATE INDEX idx_categories_user_id ON categories(user_id);

-- Presupuestos por período
CREATE INDEX idx_budgets_user_period ON budgets(user_id, year, month);
```

## 🎯 Validaciones

### Niveles de Validación

#### 1. **Nivel de BD** (constraints SQL)
```sql
CHECK (amount != 0)
CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$')
UNIQUE (email)
```

#### 2. **Nivel de Entity** (JPA)
```java
@PrePersist
@PreUpdate
private void validateAmountSign() {
    // Validar coherencia entre tipo y signo
}
```

#### 3. **Nivel de DTO** (Bean Validation)
```java
@NotNull
@DecimalMin("0.01")
@NotBlank
@Size(min = 3, max = 100)
```

#### 4. **Nivel de Service** (Business Logic)
```java
if (startDate.isAfter(endDate)) {
    throw new BadRequestException("...");
}
```

## 📊 Manejo de Errores

### Estrategia Centralizada

- `GlobalExceptionHandler` con `@ControllerAdvice`
- Mapeo de excepciones a HTTP status codes:
  - `ResourceNotFoundException` → 404
  - `BadRequestException` → 400
  - `UnauthorizedException` → 401
  - `BusinessValidationException` → 422
  - `Exception` → 500

### Formato de Respuesta de Error

```json
{
  "timestamp": "2024-12-09T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Transaction with id 999 not found",
  "path": "/api/v1/transactions/999"
}
```

## 🔄 Paginación

### Implementación

- Spring Data `Pageable` para abstracción
- Parámetros soportados:
  - `page`: Número de página (0-indexed)
  - `size`: Elementos por página
  - `sortBy`: Campo de ordenamiento
  - `sortDir`: Dirección (ASC/DESC)

### Respuesta Paginada

```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 150,
  "totalPages": 8,
  "last": false
}
```

## 📈 Optimizaciones

### Query Performance

#### 1. **Lazy Loading**
- Relaciones `@ManyToOne` son lazy por defecto
- Evita N+1 queries
- Se cargan solo cuando se accede a la propiedad

#### 2. **Proyecciones**
- `TransactionSummaryDTO` para listados (menos campos)
- `TransactionResponseDTO` para detalles (todos los campos)
- Reduce transferencia de datos

#### 3. **Queries Personalizadas**
- Agregaciones en BD (más eficiente que en memoria):
  ```java
  @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t ...")
  ```

#### 4. **Índices Compuestos**
- Para queries con múltiples condiciones WHERE
- Ej: `(user_id, transaction_date)` para filtros por fecha

### Caching (Futuro)

Candidatos para caching:
- Categorías (cambian poco)
- Totales mensuales (calculados frecuentemente)
- Configuraciones de usuario

## 🧪 Testing (Pendiente)

### Estrategia Planificada

#### Tests Unitarios
- Services: Mock de repositories
- Validaciones de lógica de negocio
- Cobertura objetivo: 80%+

#### Tests de Integración
- Controllers + Services + Repositories
- Base de datos H2 en memoria
- `@SpringBootTest` + `@AutoConfigureMockMvc`

#### Tests de API
- RestAssured o MockMvc
- Validación de contratos OpenAPI
- Escenarios de error

## 📝 Logging

### Niveles Utilizados

- `DEBUG`: Entrada/salida de métodos importantes
- `INFO`: Operaciones exitosas (create, update, delete)
- `WARN`: Situaciones inesperadas pero manejables
- `ERROR`: Errores que requieren atención

### Ejemplo

```java
log.debug("Creating transaction for user: {}", user.getId());
log.info("Transaction created successfully with ID: {}", savedTransaction.getId());
log.error("Failed to process transaction: {}", e.getMessage(), e);
```

## 🚀 Deployment (Futuro)

### Consideraciones

#### Perfiles de Spring
- `dev`: Base de datos local, logs verbosos
- `prod`: Base de datos remota, logs mínimos, seguridad estricta

#### Variables de Entorno
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/expense_tracker
SPRING_DATASOURCE_USERNAME=${DB_USER}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
JWT_SECRET=${JWT_SECRET_KEY}
```

#### Contenedorización
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/expensetracker.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📚 Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Bean Validation](https://beanvalidation.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Flyway Documentation](https://flywaydb.org/documentation/)

## 🔮 Próximos Pasos

1. **Tests**: Implementar suite completa de pruebas
2. **JWT**: Completar autenticación y autorización
3. **Budgets**: Implementar CRUD de presupuestos
4. **Reportes**: Endpoints para gráficos y estadísticas avanzadas
5. **Notificaciones**: Sistema de alertas de presupuestos
6. **Exportación**: CSV/PDF de transacciones
7. **Filtros avanzados**: Búsqueda por múltiples criterios
8. **Caching**: Redis para datos frecuentes
9. **Monitoring**: Actuator + Prometheus metrics
10. **CI/CD**: GitHub Actions para deploy automático

---

**Última actualización**: Diciembre 2024  
**Versión**: 1.0.0-SNAPSHOT

