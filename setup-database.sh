#!/bin/bash

# ============================================================================
# ExpenseTracker - Database Setup Script
# Description: Automated database setup with migrations
# ============================================================================

# Configuración
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="expense_tracker"
DB_USER="expense_user"
DB_PASSWORD="expense_secure_password"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo ""
echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  ExpenseTracker - Database Setup      ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Verificar que los scripts de migración existen
if [ ! -f "backend/src/main/resources/db/migration/V1__Initial_Schema.sql" ]; then
    echo -e "${RED}✗ Error: No se encontraron los scripts de migración${NC}"
    echo -e "${YELLOW}  Verifica que estás en la raíz del proyecto${NC}"
    exit 1
fi

# Verificar conexión
echo -e "${BLUE}[1/4]${NC} Verificando conexión a PostgreSQL..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c '\q' 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}      ✓ Conexión exitosa a $DB_HOST:$DB_PORT${NC}"
else
    echo -e "${RED}      ✗ Error de conexión${NC}"
    echo -e "${YELLOW}      Asegúrate de que PostgreSQL está corriendo:${NC}"
    echo -e "${YELLOW}      docker-compose up -d${NC}"
    exit 1
fi

# Ejecutar migraciones
echo ""
echo -e "${BLUE}[2/4]${NC} Ejecutando migraciones..."

# V1: Initial Schema
echo -e "      ${YELLOW}→${NC} V1: Initial Schema (creando tablas)..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -f backend/src/main/resources/db/migration/V1__Initial_Schema.sql \
    -v ON_ERROR_STOP=1 \
    -q 2>&1 | grep -i error

if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo -e "      ${GREEN}✓ V1 completado${NC}"
else
    echo -e "${RED}      ✗ Error en V1${NC}"
    exit 1
fi

# V2: Seed Data
echo -e "      ${YELLOW}→${NC} V2: Seed Data (insertando datos de prueba)..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -f backend/src/main/resources/db/migration/V2__Seed_Data.sql \
    -v ON_ERROR_STOP=1 \
    -q 2>&1 | grep -i error

if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo -e "      ${GREEN}✓ V2 completado${NC}"
else
    echo -e "${RED}      ✗ Error en V2${NC}"
    exit 1
fi

# V3: Indexes
echo -e "      ${YELLOW}→${NC} V3: Indexes (optimizando performance)..."
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -f backend/src/main/resources/db/migration/V3__Add_Indexes.sql \
    -v ON_ERROR_STOP=1 \
    -q 2>&1 | grep -i error

if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo -e "      ${GREEN}✓ V3 completado${NC}"
else
    echo -e "${RED}      ✗ Error en V3${NC}"
    exit 1
fi

# Verificación
echo ""
echo -e "${BLUE}[3/4]${NC} Verificando instalación..."

# Contar tablas
TABLE_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';" | xargs)

echo -e "      Tablas creadas: ${GREEN}$TABLE_COUNT${NC}"

if [ "$TABLE_COUNT" -eq "5" ]; then
    echo -e "      ${GREEN}✓ Todas las tablas creadas correctamente${NC}"
else
    echo -e "${RED}      ✗ Número incorrecto de tablas (esperado: 5, encontrado: $TABLE_COUNT)${NC}"
    exit 1
fi

# Contar índices
INDEX_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -t -c "SELECT COUNT(*) FROM pg_indexes WHERE schemaname = 'public';" | xargs)

echo -e "      Índices creados: ${GREEN}$INDEX_COUNT${NC}"

# Verificar datos de prueba
USER_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -t -c "SELECT COUNT(*) FROM users;" | xargs)

CATEGORY_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -t -c "SELECT COUNT(*) FROM categories;" | xargs)

TRANSACTION_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
    -t -c "SELECT COUNT(*) FROM transactions;" | xargs)

echo -e "      Usuarios: ${GREEN}$USER_COUNT${NC} | Categorías: ${GREEN}$CATEGORY_COUNT${NC} | Transacciones: ${GREEN}$TRANSACTION_COUNT${NC}"

# Información de acceso
echo ""
echo -e "${BLUE}[4/4]${NC} Información de acceso:"
echo ""
echo -e "      ${GREEN}✓ Base de datos configurada exitosamente${NC}"
echo ""
echo -e "      📊 ${BLUE}Usuario Demo:${NC}"
echo -e "         Email:    ${YELLOW}demo@expensetracker.com${NC}"
echo -e "         Password: ${YELLOW}Demo1234!${NC}"
echo -e "         Username: ${YELLOW}demo_user${NC}"
echo ""
echo -e "      🗄️  ${BLUE}PostgreSQL:${NC}"
echo -e "         Host:     ${YELLOW}localhost:5432${NC}"
echo -e "         Database: ${YELLOW}$DB_NAME${NC}"
echo -e "         User:     ${YELLOW}$DB_USER${NC}"
echo ""
echo -e "      🔧 ${BLUE}pgAdmin (opcional):${NC}"
echo -e "         URL:      ${YELLOW}http://localhost:5050${NC}"
echo -e "         Email:    ${YELLOW}admin@expensetracker.com${NC}"
echo -e "         Password: ${YELLOW}admin123${NC}"
echo ""
echo -e "${GREEN}╔════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║  🎉 Setup completado exitosamente      ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════╝${NC}"
echo ""

