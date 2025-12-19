#!/bin/bash

# Script para iniciar backend y frontend en desarrollo

echo "🚀 Iniciando ExpenseTracker en modo desarrollo..."
echo ""

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Verificar que el backend esté construido
if [ ! -d "backend/build" ]; then
    echo "${BLUE}📦 Construyendo backend por primera vez...${NC}"
    cd backend
    ./gradlew build -x test
    cd ..
fi

# Iniciar backend en background
echo "${GREEN}🔧 Iniciando Backend (Spring Boot)...${NC}"
cd backend
./gradlew bootRun > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Esperar a que el backend esté listo
echo "⏳ Esperando a que el backend esté listo..."
sleep 10

# Iniciar frontend en background
echo "${GREEN}🎨 Iniciando Frontend (Next.js)...${NC}"
cd frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

echo ""
echo "${GREEN}✅ Aplicación iniciada!${NC}"
echo ""
echo "📍 URLs:"
echo "   Backend:  http://localhost:8080/api/v1"
echo "   Frontend: http://localhost:3000"
echo "   Swagger:  http://localhost:8080/swagger-ui.html"
echo ""
echo "📝 Logs:"
echo "   Backend:  tail -f backend.log"
echo "   Frontend: tail -f frontend.log"
echo ""
echo "🛑 Para detener los servicios:"
echo "   kill $BACKEND_PID $FRONTEND_PID"
echo ""
echo "   O usa: pkill -f 'bootRun|next dev'"
echo ""

# Guardar PIDs en archivo para fácil acceso
echo "$BACKEND_PID" > .backend.pid
echo "$FRONTEND_PID" > .frontend.pid

# Mantener el script corriendo
wait

