# 🚀 ExpenseTracker - Guía de Inicio Rápido

Esta guía te ayudará a levantar la aplicación completa en minutos.

## 📋 Prerequisitos

- **Java 17+** - Para el backend
- **PostgreSQL** - Base de datos (via Docker)
- **Node.js 18+** y **npm** - Para el frontend
- **Docker & Docker Compose** - Para la base de datos

## ⚡ Inicio Rápido (3 pasos)

### 1️⃣ Levantar la Base de Datos

```bash
cd database
./setup-database.sh
```

Esto creará y configurará PostgreSQL con pgAdmin en Docker.

**Verificar:**
- PostgreSQL: `http://localhost:5432`
- pgAdmin: `http://localhost:5050` (admin@admin.com / admin)

---

### 2️⃣ Iniciar el Backend

```bash
cd backend
./gradlew bootRun
```

El backend estará disponible en:
- API: `http://localhost:8080/api/v1`
- Swagger: `http://localhost:8080/swagger-ui.html`

**Prueba rápida:**
```bash
curl http://localhost:8080/api/v1/health
# Debería retornar: {"status":"UP"}
```

---

### 3️⃣ Iniciar el Frontend

```bash
cd frontend
npm run dev
```

El frontend estará disponible en:
- **App: `http://localhost:3000`**

---

## 🎯 Uso de la Aplicación

### Dashboard (`/`)
1. Visualiza el resumen de tus finanzas:
   - Ingresos totales
   - Gastos totales
   - Balance
2. Gráfico de gastos por categoría
3. Transacciones recientes
4. Filtra por rango de fechas

### Transacciones (`/transactions`)
1. Clic en "Nueva Transacción"
2. Completa el formulario:
   - Tipo (Ingreso/Gasto)
   - Descripción
   - Monto
   - Categoría
   - Método de pago
   - Fecha
3. Guarda la transacción
4. Edita o elimina transacciones existentes

---

## 🛠️ Comandos Útiles

### Base de Datos

```bash
# Resetear base de datos (elimina todos los datos)
cd database
./reset-database.sh

# Ver logs de Docker
docker-compose -f database/docker-compose.yml logs -f

# Detener base de datos
docker-compose -f database/docker-compose.yml down
```

### Backend

```bash
cd backend

# Ejecutar tests
./gradlew test

# Ver cobertura de tests
./gradlew testCoverage

# Construir JAR
./gradlew build

# Limpiar build
./gradlew clean
```

### Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev

# Construir para producción
npm run build

# Ejecutar en producción
npm start

# Linter
npm run lint
```

---

## 🐛 Solución de Problemas

### ❌ El backend no arranca

**Error:** Puerto 8080 ocupado
```bash
# Encontrar y matar el proceso
lsof -ti:8080 | xargs kill -9
```

**Error:** Base de datos no conecta
```bash
# Verificar que Docker esté corriendo
docker ps

# Si no hay contenedores, levantar BD
cd database
./setup-database.sh
```

---

### ❌ El frontend no conecta con el backend

1. Verificar que el backend esté corriendo:
   ```bash
   curl http://localhost:8080/api/v1/health
   ```

2. Verificar archivo `.env.local`:
   ```bash
   cat frontend/.env.local
   # Debería tener:
   # NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api/v1
   ```

3. Revisar CORS en el backend (ya está configurado)

---

### ❌ Error en dependencias del frontend

```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

---

### ❌ Puerto 3000 ocupado

```bash
# Matar proceso en puerto 3000
lsof -ti:3000 | xargs kill -9
```

---

## 📊 Datos de Prueba

La base de datos viene con datos de prueba pre-cargados:

- **Usuario Demo:**
  - Email: `demo@expensetracker.com`
  - Username: `demo`

- **Categorías:** Comida, Transporte, Entretenimiento, Salud, etc.
- **Transacciones de ejemplo** para los últimos 3 meses

---

## 🔗 URLs de Referencia

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Frontend** | http://localhost:3000 | - |
| **Backend API** | http://localhost:8080/api/v1 | - |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| **PostgreSQL** | localhost:5432 | expense_tracker / password123 |
| **pgAdmin** | http://localhost:5050 | admin@admin.com / admin |

---

## 📚 Documentación Completa

- [README Principal](./readme.md) - Arquitectura y especificaciones
- [Backend README](./backend/README.md) - Documentación del backend
- [Frontend README](./frontend/README.md) - Documentación del frontend
- [Database Setup](./database/README.md) - Setup y migraciones
- [API Documentation](http://localhost:8080/swagger-ui.html) - Endpoints

---

## 🎉 ¡Listo!

Tu aplicación ExpenseTracker está corriendo. Abre http://localhost:3000 y empieza a gestionar tus finanzas.

**¿Preguntas?** Revisa la documentación completa o los logs de cada servicio.

