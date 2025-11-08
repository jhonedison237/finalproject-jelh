# ExpenseTracker - Instrucciones de Setup de Base de Datos

## 📋 Información General

Esta guía te ayudará a configurar la base de datos PostgreSQL para ExpenseTracker desde cero, incluyendo instalación, configuración y ejecución de migraciones.

---

## 🎯 Prerrequisitos

### Opción 1: PostgreSQL Local

- **PostgreSQL 14 o superior** instalado
- **Cliente psql** (incluido con PostgreSQL)
- Usuario con privilegios de creación de base de datos

### Opción 2: Docker (Recomendado para desarrollo)

- **Docker** instalado y corriendo
- **Docker Compose** (opcional pero recomendado)

---

## 🚀 Instalación de PostgreSQL

### macOS (con Homebrew)

```bash
# Instalar PostgreSQL
brew install postgresql@14

# Iniciar el servicio
brew services start postgresql@14

# Verificar instalación
psql --version
```

### Ubuntu/Debian

```bash
# Actualizar repositorios
sudo apt update

# Instalar PostgreSQL
sudo apt install postgresql-14 postgresql-contrib

# Iniciar el servicio
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Verificar estado
sudo systemctl status postgresql
```

### Windows

1. Descargar instalador desde [postgresql.org](https://www.postgresql.org/download/windows/)
2. Ejecutar instalador y seguir wizard
3. Recordar la contraseña del usuario `postgres`

### Docker (Todas las plataformas)

```bash
# Pull de la imagen oficial
docker pull postgres:14

# Ejecutar contenedor
docker run --name expensetracker-postgres \
  -e POSTGRES_PASSWORD=postgres123 \
  -e POSTGRES_USER=expense_user \
  -e POSTGRES_DB=expense_tracker \
  -p 5432:5432 \
  -v pgdata:/var/lib/postgresql/data \
  -d postgres:14

# Verificar que está corriendo
docker ps
```

---

## 🗄️ Configuración Inicial

### 1. Crear Usuario y Base de Datos

#### PostgreSQL Local

```bash
# Conectar como usuario postgres
sudo -u postgres psql

# O en macOS/Windows
psql -U postgres
```

```sql
-- Crear usuario para la aplicación
CREATE USER expense_user WITH PASSWORD 'expense_secure_password';

-- Crear base de datos
CREATE DATABASE expense_tracker OWNER expense_user;

-- Otorgar privilegios
GRANT ALL PRIVILEGES ON DATABASE expense_tracker TO expense_user;

-- Conectar a la nueva base de datos
\c expense_tracker

-- Otorgar privilegios en el schema
GRANT ALL ON SCHEMA public TO expense_user;

-- Salir
\q
```

#### Docker

```bash
# Ejecutar comandos SQL en el contenedor
docker exec -it expensetracker-postgres psql -U expense_user -d expense_tracker
```

Los comandos SQL son los mismos que arriba.

---

### 2. Verificar Conexión

```bash
# Test de conexión
psql -h localhost -U expense_user -d expense_tracker

# Si conecta exitosamente, verás:
# expense_tracker=>
```

Si hay problemas de autenticación, edita `pg_hba.conf`:

```bash
# Ubicación típica en Linux
sudo nano /etc/postgresql/14/main/pg_hba.conf

# Agregar o modificar línea para localhost
local   all             expense_user                              md5
host    all             expense_user    127.0.0.1/32              md5
```

Luego reiniciar PostgreSQL:

```bash
sudo systemctl restart postgresql
```

---

## 📦 Ejecución de Migraciones

### Opción 1: Manual con psql

```bash
# Navegar a la carpeta del proyecto
cd /path/to/finalproject-jelh

# Ejecutar migraciones en orden
psql -h localhost -U expense_user -d expense_tracker -f backend/src/main/resources/db/migration/V1__Initial_Schema.sql

psql -h localhost -U expense_user -d expense_tracker -f backend/src/main/resources/db/migration/V2__Seed_Data.sql

psql -h localhost -U expense_user -d expense_tracker -f backend/src/main/resources/db/migration/V3__Add_Indexes.sql
```

### Opción 2: Con Flyway (Recomendado para Spring Boot)

**1. Agregar dependencia en pom.xml (Maven):**

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

**2. Configurar application.yml:**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/expense_tracker
    username: expense_user
    password: expense_secure_password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate  # Importante: NO usar "create" con Flyway
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    schemas: public
```

**3. Ejecutar aplicación:**

```bash
# Con Maven
./mvnw spring-boot:run

# Flyway ejecutará las migraciones automáticamente
```

### Opción 3: Script Bash Automatizado

Crear archivo `setup-database.sh`:

```bash
#!/bin/bash

# Configuración
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="expense_tracker"
DB_USER="expense_user"
DB_PASSWORD="expense_secure_password"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "🚀 ExpenseTracker - Database Setup"
echo "=================================="

# Verificar conexión
echo "Verificando conexión a PostgreSQL..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -U $DB_USER -d $DB_NAME -c '\q' 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Conexión exitosa${NC}"
else
    echo -e "${RED}✗ Error de conexión. Verifica tus credenciales.${NC}"
    exit 1
fi

# Ejecutar migraciones
echo ""
echo "Ejecutando migraciones..."

echo "📄 V1: Initial Schema..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -U $DB_USER -d $DB_NAME \
    -f backend/src/main/resources/db/migration/V1__Initial_Schema.sql \
    -v ON_ERROR_STOP=1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ V1 completado${NC}"
else
    echo -e "${RED}✗ Error en V1${NC}"
    exit 1
fi

echo "📄 V2: Seed Data..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -U $DB_USER -d $DB_NAME \
    -f backend/src/main/resources/db/migration/V2__Seed_Data.sql \
    -v ON_ERROR_STOP=1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ V2 completado${NC}"
else
    echo -e "${RED}✗ Error en V2${NC}"
    exit 1
fi

echo "📄 V3: Indexes..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -U $DB_USER -d $DB_NAME \
    -f backend/src/main/resources/db/migration/V3__Add_Indexes.sql \
    -v ON_ERROR_STOP=1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ V3 completado${NC}"
else
    echo -e "${RED}✗ Error en V3${NC}"
    exit 1
fi

# Verificación
echo ""
echo "Verificando tablas creadas..."
TABLE_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -U $DB_USER -d $DB_NAME \
    -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';")

echo "Tablas creadas: $TABLE_COUNT"

if [ $TABLE_COUNT -eq 5 ]; then
    echo -e "${GREEN}✓ Base de datos configurada correctamente${NC}"
    echo ""
    echo "📊 Usuario demo:"
    echo "   Email: demo@expensetracker.com"
    echo "   Password: Demo1234!"
else
    echo -e "${RED}✗ Número incorrecto de tablas${NC}"
    exit 1
fi

echo ""
echo "🎉 Setup completado exitosamente"
```

Ejecutar:

```bash
chmod +x setup-database.sh
./setup-database.sh
```

---

## ✅ Verificación de la Instalación

### 1. Verificar Tablas Creadas

```sql
-- Conectar a la base de datos
psql -h localhost -U expense_user -d expense_tracker

-- Listar todas las tablas
\dt

-- Deberías ver:
--  public | budgets        | table | expense_user
--  public | categories     | table | expense_user
--  public | transactions   | table | expense_user
--  public | user_sessions  | table | expense_user
--  public | users          | table | expense_user
```

### 2. Verificar Datos Semilla

```sql
-- Contar usuarios
SELECT COUNT(*) FROM users;
-- Resultado esperado: 2

-- Contar categorías
SELECT COUNT(*) FROM categories;
-- Resultado esperado: ~15

-- Contar transacciones
SELECT COUNT(*) FROM transactions;
-- Resultado esperado: ~20

-- Ver usuario demo
SELECT email, username, first_name, last_name 
FROM users 
WHERE email = 'demo@expensetracker.com';
```

### 3. Verificar Índices

```sql
-- Listar todos los índices
SELECT 
    schemaname,
    tablename,
    indexname
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- Deberías ver ~25 índices
```

### 4. Test de Consulta

```sql
-- Probar query típico del dashboard
SELECT 
    t.id,
    t.amount,
    t.description,
    t.transaction_date,
    c.name as category_name
FROM transactions t
JOIN categories c ON t.category_id = c.id
WHERE t.user_id = (SELECT id FROM users WHERE email = 'demo@expensetracker.com')
ORDER BY t.transaction_date DESC
LIMIT 5;
```

---

## 🔧 Configuración de Performance

### postgresql.conf

Ubicación típica:
- Linux: `/etc/postgresql/14/main/postgresql.conf`
- macOS (Homebrew): `/opt/homebrew/var/postgresql@14/postgresql.conf`
- Docker: Configurar con variables de entorno

Configuraciones recomendadas para desarrollo:

```conf
# Memory
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
maintenance_work_mem = 64MB

# Connections
max_connections = 100

# Logging (útil para desarrollo)
log_statement = 'all'
log_duration = on
log_min_duration_statement = 100  # Log queries > 100ms

# Performance
random_page_cost = 1.1  # Para SSD
effective_io_concurrency = 200
```

Reiniciar después de cambios:

```bash
sudo systemctl restart postgresql
```

---

## 🐳 Docker Compose (Recomendado)

Crear `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:14
    container_name: expensetracker-postgres
    environment:
      POSTGRES_DB: expense_tracker
      POSTGRES_USER: expense_user
      POSTGRES_PASSWORD: expense_secure_password
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
      - ./backend/src/main/resources/db/migration:/docker-entrypoint-initdb.d
    networks:
      - expensetracker-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U expense_user -d expense_tracker"]
      interval: 10s
      timeout: 5s
      retries: 5

  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: expensetracker-pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@expensetracker.com
      PGADMIN_DEFAULT_PASSWORD: admin123
    ports:
      - "5050:80"
    networks:
      - expensetracker-network
    depends_on:
      - postgres

volumes:
  pgdata:

networks:
  expensetracker-network:
    driver: bridge
```

Ejecutar:

```bash
# Iniciar servicios
docker-compose up -d

# Verificar logs
docker-compose logs -f postgres

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (reset completo)
docker-compose down -v
```

Acceder a pgAdmin: `http://localhost:5050`

---

## 🔄 Mantenimiento

### Backup de la Base de Datos

```bash
# Backup completo
pg_dump -h localhost -U expense_user -d expense_tracker -F c -b -v -f expense_tracker_backup.dump

# Backup solo schema
pg_dump -h localhost -U expense_user -d expense_tracker --schema-only -f schema_backup.sql

# Backup solo datos
pg_dump -h localhost -U expense_user -d expense_tracker --data-only -f data_backup.sql
```

### Restauración

```bash
# Desde dump custom format
pg_restore -h localhost -U expense_user -d expense_tracker -v expense_tracker_backup.dump

# Desde SQL file
psql -h localhost -U expense_user -d expense_tracker -f schema_backup.sql
```

### Reset de Base de Datos

```bash
# Conectar como postgres
psql -U postgres

# Terminar conexiones activas
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'expense_tracker'
  AND pid <> pg_backend_pid();

# Eliminar y recrear
DROP DATABASE IF EXISTS expense_tracker;
CREATE DATABASE expense_tracker OWNER expense_user;

# Volver a ejecutar migraciones
\q
./setup-database.sh
```

---

## 🐛 Troubleshooting

### Error: "role does not exist"

```bash
# Crear el usuario
sudo -u postgres createuser expense_user
sudo -u postgres psql -c "ALTER USER expense_user WITH PASSWORD 'your_password';"
```

### Error: "database does not exist"

```bash
# Crear la base de datos
sudo -u postgres createdb -O expense_user expense_tracker
```

### Error: "permission denied for schema public"

```sql
-- Conectar como postgres
psql -U postgres -d expense_tracker

-- Otorgar permisos
GRANT ALL ON SCHEMA public TO expense_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO expense_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO expense_user;
```

### Error: "psql: connection refused"

```bash
# Verificar que PostgreSQL está corriendo
sudo systemctl status postgresql

# Iniciar si está detenido
sudo systemctl start postgresql

# Verificar puerto
sudo netstat -plnt | grep 5432
```

### Error: "password authentication failed"

1. Verificar contraseña en `application.yml`
2. Verificar `pg_hba.conf` tiene método `md5` o `scram-sha-256`
3. Reiniciar PostgreSQL después de cambios

---

## 📚 Recursos Adicionales

- [PostgreSQL Official Documentation](https://www.postgresql.org/docs/14/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Boot + PostgreSQL Guide](https://spring.io/guides/gs/accessing-data-postgresql/)
- [Docker PostgreSQL](https://hub.docker.com/_/postgres)

---

## 📞 Soporte

Si encuentras problemas:

1. Verificar logs: `docker-compose logs postgres` o `/var/log/postgresql/`
2. Revisar documentación de constraints: `docs/database/constraints_and_validations.md`
3. Verificar versión de PostgreSQL: `psql --version`

---

**Última actualización:** Octubre 2024  
**Mantenido por:** Equipo de Desarrollo ExpenseTracker

