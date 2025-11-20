# ExpenseTracker Frontend

Frontend de la aplicación ExpenseTracker construido con Next.js, React y TailwindCSS.

## 🚀 Tecnologías

- **Next.js 16** - Framework de React con App Router
- **React 19** - Biblioteca de UI
- **TailwindCSS 4** - Framework de CSS
- **Axios** - Cliente HTTP
- **Recharts** - Gráficos y visualizaciones
- **date-fns** - Manejo de fechas
- **Lucide React** - Iconos
- **React Hook Form** - Formularios
- **Zod** - Validación de esquemas

## 📁 Estructura del Proyecto

```
frontend/
├── app/                      # Páginas (App Router)
│   ├── page.tsx             # Dashboard (Home)
│   ├── transactions/        # Página de transacciones
│   └── layout.tsx           # Layout raíz
├── components/              # Componentes reutilizables
│   ├── common/              # Componentes comunes (Button, Card, Input, etc.)
│   ├── dashboard/           # Componentes del dashboard
│   ├── transactions/        # Componentes de transacciones
│   └── layout/              # Componentes de layout (Navbar, Sidebar)
├── lib/                     # Lógica de negocio
│   ├── api/                 # Clientes de API
│   ├── hooks/               # Custom hooks
│   └── utils/               # Utilidades y helpers
└── public/                  # Archivos estáticos
```

## 🛠️ Variables de Entorno

Crear un archivo `.env.local` con:

```bash
# API Configuration
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api/v1

# App Configuration
NEXT_PUBLIC_APP_NAME=ExpenseTracker
NEXT_PUBLIC_DEFAULT_USER_ID=1
```

## 📦 Instalación

```bash
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

## 🌐 URLs

- **Desarrollo**: http://localhost:3000
- **API Backend**: http://localhost:8080/api/v1

## 📱 Páginas

### 1. Dashboard (`/`)
- Tarjetas de resumen (Ingresos, Gastos, Balance)
- Gráfico de gastos por categoría (Pie Chart)
- Lista de transacciones recientes
- Filtro por rango de fechas

### 2. Transacciones (`/transactions`)
- Lista completa de transacciones con paginación
- Crear nueva transacción
- Editar transacción existente
- Eliminar transacción (soft delete)
- Filtro por rango de fechas
- Búsqueda y filtros

## 🎨 Componentes Principales

### Componentes Comunes
- `Button` - Botón reutilizable con variantes
- `Card` - Tarjeta contenedora
- `Input` - Campo de entrada
- `Select` - Selector dropdown
- `Loading` - Indicador de carga
- `Alert` - Mensajes de alerta
- `Modal` - Modal reutilizable

### Componentes de Dashboard
- `SummaryCard` - Tarjeta de resumen financiero
- `ExpensesChart` - Gráfico de gastos por categoría
- `RecentTransactions` - Lista de transacciones recientes

### Componentes de Transacciones
- `TransactionForm` - Formulario para crear/editar transacciones
- `TransactionList` - Lista de transacciones con paginación
- `TransactionItem` - Item individual de transacción

### Layout
- `Navbar` - Barra de navegación superior
- `Sidebar` - Barra lateral de navegación
- `MainLayout` - Layout principal de la aplicación

## 🔧 Custom Hooks

### `useTransactions`
Gestiona el estado y operaciones CRUD de transacciones.

```javascript
const {
  transactions,
  loading,
  error,
  pagination,
  createTransaction,
  updateTransaction,
  deleteTransaction,
  changePage,
  updateDateRange,
} = useTransactions();
```

### `useCategories`
Gestiona las categorías del usuario.

```javascript
const {
  categories,
  loading,
  error,
  getCategoryById,
  refreshCategories,
} = useCategories();
```

### `useDashboard`
Gestiona los datos del dashboard (totales, gráficos, transacciones recientes).

```javascript
const {
  totals,
  expensesByCategory,
  recentTransactions,
  loading,
  error,
  updateDateRange,
} = useDashboard();
```

## 🎯 Características

### ✅ Implementadas
- [x] Dashboard con resumen financiero
- [x] Gestión completa de transacciones (CRUD)
- [x] Visualización de gastos por categoría
- [x] Filtros por rango de fechas
- [x] Paginación de transacciones
- [x] Responsive design (mobile-first)
- [x] Manejo de errores
- [x] Estados de carga

### 🔮 Pendientes
- [ ] Autenticación JWT
- [ ] Gestión de categorías personalizadas
- [ ] Gestión de presupuestos
- [ ] Exportación de datos (CSV, PDF)
- [ ] Gráficos adicionales (línea temporal, barras)
- [ ] Dark mode
- [ ] PWA (Progressive Web App)
- [ ] Tests unitarios e integración

## 🐛 Solución de Problemas

### El frontend no se conecta al backend
- Verificar que el backend esté corriendo en `http://localhost:8080`
- Verificar las variables de entorno en `.env.local`
- Verificar CORS en el backend

### Error de módulos no encontrados
```bash
npm install
```

### Error de compilación de TypeScript
El proyecto está configurado para aceptar archivos `.js` y `.tsx`. Asegúrate de que `tsconfig.json` tenga `"allowJs": true`.

## 📄 Licencia

Este proyecto es parte de un proyecto académico/personal.
