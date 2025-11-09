#!/bin/bash

# Script para resetear completamente la base de datos
# Útil para desarrollo cuando se cambia el esquema inicial

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "🔄 ExpenseTracker - Database Reset"
echo "=================================="
echo ""
echo -e "${YELLOW}⚠️  ADVERTENCIA: Esto eliminará TODOS los datos de la base de datos!${NC}"
echo ""
read -p "¿Estás seguro de continuar? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo -e "${RED}✗ Operación cancelada${NC}"
    exit 0
fi

echo ""
echo "🗑️  Eliminando contenedores y volúmenes..."
docker-compose down -v

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Contenedores y volúmenes eliminados${NC}"
else
    echo -e "${RED}✗ Error al eliminar contenedores${NC}"
    exit 1
fi

echo ""
echo "🚀 Levantando base de datos fresca..."
docker-compose up -d

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Base de datos levantada${NC}"
else
    echo -e "${RED}✗ Error al levantar la base de datos${NC}"
    exit 1
fi

echo ""
echo "⏳ Esperando a que PostgreSQL esté listo..."
sleep 5

echo ""
echo "✅ Reset completado!"
echo ""
echo "📝 Próximos pasos:"
echo "   1. cd backend"
echo "   2. ./gradlew bootRun"
echo ""
echo "Las migraciones de Flyway se ejecutarán automáticamente al iniciar la aplicación."

