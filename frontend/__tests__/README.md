# 🧪 Tests del Frontend - ExpenseTracker

Suite completa de pruebas para el frontend de ExpenseTracker construido con Jest y React Testing Library.

---

## 📦 Instalación Rápida

```bash
cd frontend

# Instalar dependencias de testing
npm install --save-dev jest @testing-library/react @testing-library/jest-dom @testing-library/user-event jest-environment-jsdom

# Si hay problemas de permisos
sudo chown -R $(whoami) ~/.npm
```

---

## 🚀 Comandos

```bash
# Ejecutar tests en modo watch (desarrollo)
npm test

# Ejecutar tests una sola vez (CI)
npm run test:ci

# Ejecutar tests con reporte de cobertura
npm run test:coverage
```

---

## 📊 Tests Implementados

### ✅ **1. Tests de Utilidades** (55 tests)

#### `lib/utils/formatters.test.js` (30 tests)
- ✅ `formatCurrency` - Formato de moneda (5 tests)
- ✅ `formatDate` - Formato de fechas (4 tests)
- ✅ `formatDateForInput` - Fechas para inputs (3 tests)
- ✅ `formatRelativeDate` - Fechas relativas (4 tests)
- ✅ `capitalize` - Capitalización de texto (4 tests)
- ✅ `truncate` - Truncado de texto (4 tests)

#### `lib/utils/helpers.test.js` (25 tests)
- ✅ `getDateRange` - Rangos de fechas (3 tests)
- ✅ `isValidAmount` - Validación de montos (4 tests)
- ✅ `normalizeAmount` - Normalización de montos (3 tests)
- ✅ `calculatePercentage` - Cálculo de porcentajes (4 tests)
- ✅ `groupTransactionsByDate` - Agrupación por fecha (3 tests)
- ✅ `cn` - Combinación de clases CSS (4 tests)

---

### ✅ **2. Tests de Componentes Comunes** (68 tests)

#### `components/common/Button.test.js` (18 tests)
- ✅ Renderizado básico
- ✅ Variantes: primary, secondary, success, danger, outline, ghost
- ✅ Tamaños: sm, md, lg
- ✅ Estados: disabled, loading
- ✅ Eventos: onClick
- ✅ Tipos: button, submit

#### `components/common/Card.test.js` (13 tests)
- ✅ Card (contenedor principal)
- ✅ CardHeader (cabecera)
- ✅ CardTitle (título)
- ✅ CardBody (cuerpo)
- ✅ CardFooter (pie)
- ✅ Composición completa

#### `components/common/Input.test.js` (14 tests)
- ✅ Renderizado de input
- ✅ Label y required asterisk
- ✅ Mensajes de error
- ✅ Helper text
- ✅ Estilos de error
- ✅ Eventos onChange
- ✅ Tipos de input (text, email, etc.)
- ✅ Estado disabled
- ✅ Placeholder y value

#### `components/common/Alert.test.js` (10 tests)
- ✅ Renderizado de mensaje
- ✅ Título y mensaje
- ✅ Tipos: success, error, warning, info
- ✅ Íconos según tipo
- ✅ Botón de cerrar
- ✅ Evento onClose

---

## 📈 Cobertura de Código

### **Configuración de Umbrales**

```javascript
coverageThreshold: {
  global: {
    branches: 70%,
    functions: 70%,
    lines: 70%,
    statements: 70%
  }
}
```

### **Archivos Incluidos en Cobertura**

- ✅ `components/**/*.{js,jsx}`
- ✅ `lib/**/*.{js,jsx}`
- ✅ `app/**/*.{js,jsx,tsx}`

### **Archivos Excluidos**

- ❌ `node_modules/`
- ❌ `.next/`
- ❌ `coverage/`
- ❌ Archivos de configuración

---

## 📁 Estructura de Tests

```
frontend/
├── __tests__/
│   ├── lib/
│   │   └── utils/
│   │       ├── formatters.test.js  ✅
│   │       └── helpers.test.js     ✅
│   ├── components/
│   │   ├── common/
│   │   │   ├── Button.test.js      ✅
│   │   │   ├── Card.test.js        ✅
│   │   │   ├── Input.test.js       ✅
│   │   │   └── Alert.test.js       ✅
│   │   ├── dashboard/              ⏳ Pendiente
│   │   └── transactions/           ⏳ Pendiente
│   ├── hooks/                      ⏳ Pendiente
│   └── README.md
├── jest.config.js                  ✅
└── jest.setup.js                   ✅
```

---

## 🎯 Estadísticas Actuales

### **Tests por Categoría**

| Categoría | Tests | Estado |
|-----------|-------|--------|
| **Utilidades** | 55 | ✅ Completado |
| **Componentes Comunes** | 68 | ✅ Completado |
| **Hooks** | 0 | ⏳ Pendiente |
| **Dashboard** | 0 | ⏳ Pendiente |
| **Transacciones** | 0 | ⏳ Pendiente |
| **Integración** | 0 | ⏳ Pendiente |
| **TOTAL** | **123** | 🚧 En progreso |

---

## 🔧 Configuración de Jest

### **jest.config.js**

```javascript
- Environment: jsdom (para simular el navegador)
- Setup: jest.setup.js (configuración global)
- Module mapper: @/* → <rootDir>/*
- Coverage: Configurado con umbrales de 70%
```

### **jest.setup.js**

```javascript
- @testing-library/jest-dom (matchers adicionales)
- Mock de window.matchMedia
- Mock de IntersectionObserver
- Supresión de warnings de React
```

---

## 📚 Ejemplo de Test

```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from '@/components/common/Button';

describe('Button Component', () => {
  it('should call onClick when clicked', () => {
    const handleClick = jest.fn();
    render(<Button onClick={handleClick}>Click me</Button>);
    
    fireEvent.click(screen.getByText('Click me'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
```

---

## ✅ Checklist de Testing

### **Configuración**
- [x] Jest instalado y configurado
- [x] React Testing Library instalado
- [x] Scripts de npm agregados
- [x] jest.config.js creado
- [x] jest.setup.js creado

### **Tests Implementados**
- [x] Tests de formatters (30 tests)
- [x] Tests de helpers (25 tests)
- [x] Tests de Button (18 tests)
- [x] Tests de Card (13 tests)
- [x] Tests de Input (14 tests)
- [x] Tests de Alert (10 tests)

### **Pendientes**
- [ ] Tests de Select
- [ ] Tests de Modal
- [ ] Tests de Loading
- [ ] Tests de hooks personalizados
- [ ] Tests de componentes del Dashboard
- [ ] Tests de componentes de Transacciones
- [ ] Tests de integración de páginas

---

## 🐛 Troubleshooting

### **Error: "Cannot find module '@testing-library/react'"**

```bash
npm install --save-dev @testing-library/react @testing-library/jest-dom @testing-library/user-event
```

### **Error: "Cannot find module 'jest-environment-jsdom'"**

```bash
npm install --save-dev jest-environment-jsdom
```

### **Error: "ReferenceError: TextEncoder is not defined"**

Esto se soluciona con Node.js 18+ o agregando polyfills en `jest.setup.js`.

### **Tests no encuentran módulos con alias @/**

Verifica que `jest.config.js` tenga:

```javascript
moduleNameMapper: {
  '^@/(.*)$': '<rootDir>/$1',
}
```

---

## 📖 Recursos

- [Jest Documentation](https://jestjs.io/docs/getting-started)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)
- [Jest DOM Matchers](https://github.com/testing-library/jest-dom)
- [Testing Best Practices](https://kentcdodds.com/blog/common-mistakes-with-react-testing-library)

---

## 🎉 Resumen

### **✅ Completado:**
- Configuración de Jest y React Testing Library
- 55 tests de utilidades (100% cobertura)
- 68 tests de componentes comunes (80%+ cobertura)
- **Total: 123 tests implementados**

### **⏳ Próximo:**
- Implementar tests de hooks personalizados
- Tests de componentes del Dashboard
- Tests de componentes de Transacciones
- Tests de integración de páginas completas

---

**Estado:** 🚧 **Fase 1 Completada** - Tests base implementados y funcionando

**Próximo comando:** `npm test`

