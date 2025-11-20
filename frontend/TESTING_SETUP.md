# 🧪 Configuración de Tests Frontend - ExpenseTracker

## 📦 Instalación de Dependencias

Ejecuta este comando en la terminal (en el directorio `frontend`):

```bash
npm install --save-dev jest @testing-library/react @testing-library/jest-dom @testing-library/user-event jest-environment-jsdom msw
```

**Nota:** Si tienes problemas de permisos con npm, ejecuta primero:
```bash
sudo chown -R $(whoami) ~/.npm
```

---

## 🚀 Comandos de Testing

Una vez instaladas las dependencias, puedes usar:

```bash
# Ejecutar tests en modo watch (recomendado para desarrollo)
npm test

# Ejecutar tests una sola vez
npm run test:ci

# Ejecutar tests con reporte de cobertura
npm run test:coverage
```

---

## 📊 Archivos de Configuración Creados

1. **`jest.config.js`** - Configuración principal de Jest
2. **`jest.setup.js`** - Setup global para tests
3. **`__tests__/`** - Directorio con todos los tests

---

## 🧪 Tests Implementados

### ✅ **Tests de Utilidades**
- `__tests__/lib/utils/formatters.test.js` - 30 tests
  - formatCurrency
  - formatDate
  - formatDateForInput
  - formatRelativeDate
  - capitalize
  - truncate

- `__tests__/lib/utils/helpers.test.js` - 25 tests
  - getDateRange
  - isValidAmount
  - normalizeAmount
  - calculatePercentage
  - groupTransactionsByDate
  - cn

### ✅ **Tests de Componentes Comunes**
- `__tests__/components/common/Button.test.js` - 18 tests
  - Renderizado
  - Variantes (primary, secondary, success, danger, outline, ghost)
  - Tamaños (sm, md, lg)
  - Estados (disabled, loading)
  - Eventos (onClick)

---

## 📈 Cobertura Esperada

Configurado con umbrales mínimos de:
- **Branches:** 70%
- **Functions:** 70%
- **Lines:** 70%
- **Statements:** 70%

---

## 🔧 Troubleshooting

### Error: "Cannot find module '@testing-library/react'"
```bash
npm install --save-dev @testing-library/react @testing-library/jest-dom
```

### Error: "Cannot find module 'jest-environment-jsdom'"
```bash
npm install --save-dev jest-environment-jsdom
```

### Tests no corren
1. Verifica que estés en el directorio `frontend`
2. Verifica que `package.json` tenga los scripts de test
3. Ejecuta `npm test` para ver errores específicos

---

## 📝 Próximos Tests a Implementar

- [ ] Card Component
- [ ] Input Component
- [ ] Select Component
- [ ] Modal Component
- [ ] Alert Component
- [ ] Loading Component
- [ ] Custom Hooks (useTransactions, useCategories, useDashboard)
- [ ] Componentes del Dashboard
- [ ] Componentes de Transacciones
- [ ] Tests de Integración de Páginas

---

## 🎯 Ejemplo de Ejecución

Después de instalar las dependencias:

```bash
cd frontend
npm test
```

Deberías ver algo como:

```
PASS  __tests__/lib/utils/formatters.test.js
PASS  __tests__/lib/utils/helpers.test.js
PASS  __tests__/components/common/Button.test.js

Test Suites: 3 passed, 3 total
Tests:       73 passed, 73 total
Snapshots:   0 total
Time:        3.456 s
```

---

## ✅ Estado Actual

**Archivos de configuración:** ✅ Completados
**Tests de utilidades:** ✅ Completados  
**Tests de componentes:** 🚧 En progreso (1/7)
**Tests de hooks:** ⏳ Pendiente
**Tests de integración:** ⏳ Pendiente

---

**¿Dudas?** Revisa los archivos de test como ejemplo y sigue el mismo patrón.

