# ExpenseTracker Backend API

API REST desarrollada con Spring Boot 3 y Java 17 para la gestión de gastos e ingresos personales.

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL 14**
- **Flyway** (migraciones de BD)
- **Gradle** (gestión de dependencias)
- **Lombok** (reducción de boilerplate)
- **SpringDoc OpenAPI** (documentación Swagger)
- **JWT** (autenticación - próxima fase)

## 📋 Prerrequisitos

- Java 17 o superior
- Docker y Docker Compose (para BD local)
- Gradle 8.x (wrapper incluido)

## 🔧 Configuración Local

### 1. Levantar la base de datos

```bash
# Desde la raíz del proyecto
docker-compose up -d
```

Esto levantará:
- PostgreSQL en `localhost:5432`
- pgAdmin en `localhost:5050`

### 2. Ejecutar migraciones (primera vez)

Las migraciones se ejecutan automáticamente al iniciar la aplicación gracias a Flyway.

Alternativamente, puedes usar el script manual:

```bash
chmod +x setup-database.sh
./setup-database.sh
```

### 3. Ejecutar la aplicación

```bash
cd backend
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`

## 📚 Documentación API

Una vez la aplicación esté ejecutándose, accede a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🧪 Endpoints Principales

### Health Check
```bash
GET /api/v1/health
GET /api/v1/health/ping
```

### Transacciones
```bash
POST   /api/v1/transactions              # Crear transacción
GET    /api/v1/transactions              # Listar todas (paginado)
GET    /api/v1/transactions/{id}         # Obtener por ID
PUT    /api/v1/transactions/{id}         # Actualizar
DELETE /api/v1/transactions/{id}         # Eliminar (soft delete)
GET    /api/v1/transactions/recent       # Transacciones recientes
GET    /api/v1/transactions/date-range   # Filtrar por fechas
GET    /api/v1/transactions/category/{id} # Filtrar por categoría
GET    /api/v1/transactions/summary/totals # Totales
GET    /api/v1/transactions/summary/by-category # Resumen por categoría
```

### Categorías
```bash
GET /api/v1/categories     # Listar categorías del usuario
GET /api/v1/categories/{id} # Obtener categoría por ID
```

## 🗂️ Estructura del Proyecto

```
backend/src/main/java/com/expensetracker/
├── config/              # Configuraciones (JPA, OpenAPI, CORS)
├── controller/          # Controladores REST
├── dto/                 # DTOs (Request/Response)
│   ├── request/
│   └── response/
├── entity/              # Entidades JPA
│   └── enums/
├── exception/           # Excepciones personalizadas
├── repository/          # Repositorios Spring Data JPA
├── service/             # Lógica de negocio
│   └── impl/
└── ExpenseTrackerApplication.java
```

## 🔒 Autenticación (Próximamente)

Actualmente, la API usa un usuario demo temporal para desarrollo:
- Email: `demo@expensetracker.com`
- ID: 1

La autenticación JWT será implementada en la siguiente fase.

## 🛠️ Comandos Útiles

```bash
# Compilar el proyecto
./gradlew build

# Ejecutar tests
./gradlew test

# Limpiar build
./gradlew clean

# Ver dependencias
./gradlew dependencies
```

## 🗄️ Base de Datos

### Credenciales (Development)

**PostgreSQL:**
- Host: `localhost`
- Puerto: `5432`
- Database: `expense_tracker`
- Usuario: `expense_user`
- Password: `expense_secure_password`

**pgAdmin:**
- URL: http://localhost:5050
- Email: `admin@expensetracker.com`
- Password: `admin123`

### Usuario Demo

Un usuario demo se crea automáticamente con los datos de seed:
- Email: `demo@expensetracker.com`
- Username: `demo`
- Password: `Demo1234!` (hash almacenado)

## 📊 Características Implementadas

✅ CRUD completo de transacciones  
✅ Categorías predeterminadas  
✅ Paginación y ordenamiento  
✅ Filtros por fecha y categoría  
✅ Cálculos de totales (ingresos, gastos, balance)  
✅ Resúmenes por categoría  
✅ Validaciones de negocio  
✅ Manejo centralizado de errores  
✅ Documentación OpenAPI/Swagger  
✅ Soft delete (eliminación lógica)  
✅ Auditoría automática (created_at, updated_at)  
✅ Configuración CORS para frontend  

## 🚧 Pendiente

- [ ] Autenticación JWT completa
- [ ] Tests unitarios
- [ ] Tests de integración
- [ ] Gestión de presupuestos (Budget CRUD)
- [ ] Notificaciones de límites de presupuesto
- [ ] Reportes avanzados

## 📝 Notas de Desarrollo

- Las migraciones de Flyway se encuentran en `src/main/resources/db/migration/`
- Los logs de la aplicación usan SLF4J + Logback
- Bean Validation está habilitado para validaciones automáticas
- Los timestamps se manejan automáticamente con JPA Auditing
- Los montos de gastos se almacenan como negativos en BD

## 🐛 Troubleshooting

### Error de conexión a BD
```bash
# Verificar que Docker esté ejecutándose
docker ps

# Reiniciar contenedores
docker-compose restart

# Ver logs de PostgreSQL
docker-compose logs postgres
```

### Puerto 8080 ya en uso
```bash
# Cambiar puerto en application.yml
server:
  port: 8081
```

### Flyway migration failed
```bash
# Limpiar BD y volver a ejecutar migraciones
docker-compose down -v
docker-compose up -d
./setup-database.sh
```

## 📄 Licencia

MIT License - ExpenseTracker © 2024

