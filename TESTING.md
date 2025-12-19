# 🧪 ExpenseTracker - Guía de Pruebas

Esta guía te ayudará a probar toda la aplicación de forma sistemática.

---

## 🚀 Pre-requisitos

Asegúrate de tener todo corriendo:

```bash
# Terminal 1: Base de Datos
cd database
./setup-database.sh

# Terminal 2: Backend
cd backend
./gradlew bootRun

# Terminal 3: Frontend
cd frontend
npm run dev
```

✅ **Verificar:**
- PostgreSQL: http://localhost:5432
- Backend: http://localhost:8080/api/v1/health
- Frontend: http://localhost:3000

---

## 📋 Plan de Pruebas

### 1️⃣ Dashboard (Página Principal)

#### Visualización Inicial
1. Abre http://localhost:3000
2. Deberías ver:
   - ✅ Navbar con logo "ExpenseTracker"
   - ✅ Sidebar con menú (Dashboard, Transacciones, Configuración)
   - ✅ 3 tarjetas de resumen (Ingresos, Gastos, Balance)
   - ✅ Gráfico de pie con gastos por categoría
   - ✅ Lista de transacciones recientes

#### Filtro de Fechas
1. Selecciona "Este Mes" en el dropdown de fechas
2. ✅ Los datos deberían actualizarse
3. Selecciona "Esta Semana"
4. ✅ Los datos deberían cambiar
5. Selecciona "Personalizado"
6. ✅ Deberían aparecer 2 inputs de fecha
7. Selecciona un rango de fechas
8. ✅ Los datos deberían filtrarse correctamente

#### Navegación
1. Haz clic en "Ver todas →" en Transacciones Recientes
2. ✅ Deberías ser redirigido a `/transactions`
3. Haz clic en "Nueva Transacción" (botón azul)
4. ✅ Deberías ser redirigido a `/transactions`

#### Responsive
1. Reduce el tamaño de la ventana (< 1024px)
2. ✅ El sidebar debería ocultarse
3. ✅ Debería aparecer un ícono de menú hamburguesa
4. Haz clic en el menú hamburguesa
5. ✅ El sidebar debería aparecer como overlay
6. Haz clic fuera del sidebar
7. ✅ El sidebar debería cerrarse

---

### 2️⃣ Página de Transacciones

#### Visualización
1. Navega a http://localhost:3000/transactions
2. Deberías ver:
   - ✅ Título "Transacciones"
   - ✅ Botón "Nueva Transacción"
   - ✅ Selector de rango de fechas
   - ✅ Lista de transacciones agrupadas por fecha
   - ✅ Paginación (si hay más de 20 transacciones)

#### Crear Transacción (Ingreso)
1. Haz clic en "Nueva Transacción"
2. ✅ Debería abrirse un modal
3. Completa el formulario:
   - Tipo: **Ingreso**
   - Descripción: **Salario mensual**
   - Monto: **3000**
   - Categoría: **Salario**
   - Método de pago: **Transferencia Bancaria**
   - Fecha: (hoy)
   - Notas: **Pago de noviembre**
4. Haz clic en "Guardar Transacción"
5. ✅ El modal debería cerrarse
6. ✅ Debería aparecer un mensaje de éxito verde
7. ✅ La transacción debería aparecer en la lista
8. ✅ El monto debería aparecer en verde con signo "+"

#### Crear Transacción (Gasto)
1. Haz clic en "Nueva Transacción"
2. Completa el formulario:
   - Tipo: **Gasto**
   - Descripción: **Compra de supermercado**
   - Monto: **150.50**
   - Categoría: **Comida**
   - Método de pago: **Tarjeta de Débito**
   - Fecha: (hoy)
   - Notas: **Mercado semanal**
3. Haz clic en "Guardar Transacción"
4. ✅ El modal debería cerrarse
5. ✅ Debería aparecer un mensaje de éxito verde
6. ✅ La transacción debería aparecer en la lista
7. ✅ El monto debería aparecer en rojo (sin signo "+")

#### Validación de Formulario
1. Haz clic en "Nueva Transacción"
2. Deja todos los campos vacíos
3. Haz clic en "Guardar Transacción"
4. ✅ Deberían aparecer errores de validación en rojo debajo de cada campo
5. Completa solo el campo "Descripción"
6. ✅ El error de "Descripción" debería desaparecer
7. ✅ Los demás errores deberían permanecer

#### Editar Transacción
1. En la lista, busca una transacción
2. Haz clic en el ícono de lápiz (editar)
3. ✅ Debería abrirse el modal con los datos pre-cargados
4. Cambia la descripción
5. Haz clic en "Actualizar Transacción"
6. ✅ El modal debería cerrarse
7. ✅ Debería aparecer mensaje "Transacción actualizada exitosamente"
8. ✅ Los cambios deberían reflejarse en la lista

#### Eliminar Transacción
1. En la lista, busca una transacción
2. Haz clic en el ícono de papelera (eliminar)
3. ✅ Debería abrirse un modal de confirmación
4. Haz clic en "Cancelar"
5. ✅ El modal debería cerrarse sin eliminar
6. Haz clic nuevamente en el ícono de papelera
7. Haz clic en "Eliminar"
8. ✅ El modal debería cerrarse
9. ✅ Debería aparecer mensaje "Transacción eliminada exitosamente"
10. ✅ La transacción debería desaparecer de la lista

#### Filtro por Fechas
1. Selecciona "Este Mes"
2. ✅ Deberían aparecer solo transacciones del mes actual
3. Selecciona "Mes Pasado"
4. ✅ Deberían aparecer solo transacciones del mes anterior
5. Selecciona "Personalizado"
6. Elige un rango de fechas específico
7. ✅ Deberían aparecer solo transacciones en ese rango

#### Paginación
1. Si hay más de 20 transacciones:
   - ✅ Debería aparecer "Página 1 de X"
   - ✅ El botón "Anterior" debería estar deshabilitado
   - ✅ El botón "Siguiente" debería estar habilitado
2. Haz clic en "Siguiente"
3. ✅ Deberías ver la página 2
4. ✅ El botón "Anterior" debería estar habilitado
5. Haz clic en "Anterior"
6. ✅ Deberías volver a la página 1

---

### 3️⃣ Integración Dashboard ↔ Transacciones

#### Flujo Completo
1. Ve al Dashboard (http://localhost:3000)
2. ✅ Anota los totales actuales:
   - Ingresos: $_____
   - Gastos: $_____
   - Balance: $_____
3. Ve a Transacciones
4. Crea un INGRESO de $500
5. Vuelve al Dashboard
6. ✅ Los "Ingresos" deberían haber aumentado $500
7. ✅ El "Balance" debería haber aumentado $500
8. Ve a Transacciones
9. Crea un GASTO de $100
10. Vuelve al Dashboard
11. ✅ Los "Gastos" deberían haber aumentado $100
12. ✅ El "Balance" debería haber disminuido $100

#### Gráfico de Gastos
1. En el Dashboard, observa el gráfico de pie
2. ✅ Debería mostrar las categorías con más gastos
3. ✅ Los porcentajes deberían sumar 100%
4. ✅ Al pasar el mouse, debería mostrar tooltip con detalles
5. Ve a Transacciones y crea varios gastos en una categoría
6. Vuelve al Dashboard
7. ✅ El gráfico debería actualizarse
8. ✅ La categoría con más gastos debería tener el porcentaje más alto

---

### 4️⃣ Manejo de Errores

#### Backend Detenido
1. Detén el backend (Ctrl+C en la terminal)
2. En el frontend, intenta crear una transacción
3. ✅ Debería aparecer un error: "No se pudo conectar con el servidor"
4. Ve al Dashboard
5. ✅ Debería mostrar un mensaje de error
6. Inicia el backend nuevamente
7. Recarga la página
8. ✅ Todo debería funcionar normalmente

#### Conexión Lenta (Simulación)
1. En el navegador, abre DevTools (F12)
2. Ve a la pestaña "Network"
3. Selecciona "Slow 3G"
4. Recarga la página
5. ✅ Deberías ver spinners de carga
6. ✅ Los componentes deberían cargarse gradualmente
7. Restaura "No throttling"

---

### 5️⃣ Pruebas de UI

#### Teclado
1. En la página de transacciones, haz clic en "Nueva Transacción"
2. Usa **Tab** para navegar entre campos
3. ✅ Debería funcionar la navegación por teclado
4. Presiona **Escape**
5. ✅ El modal debería cerrarse

#### Formato de Moneda
1. Crea una transacción con monto: **1234.56**
2. ✅ En la lista debería mostrarse como: **$1,234.56**
3. En el Dashboard, verifica las tarjetas de resumen
4. ✅ Todos los montos deberían tener formato de moneda

#### Formato de Fecha
1. En la lista de transacciones, observa las fechas
2. ✅ Deberían estar agrupadas por fecha
3. ✅ El formato debería ser legible (ej: "Martes, 12 noviembre 2024")
4. En transacciones recientes del Dashboard
5. ✅ Las fechas deberían ser más cortas (ej: "12/11/2024")

---

### 6️⃣ Responsive Design

#### Mobile (< 640px)
1. Reduce la ventana a ~375px de ancho
2. ✅ El sidebar debería ocultarse
3. ✅ Las tarjetas de resumen deberían apilarse verticalmente
4. ✅ El gráfico debería ser scrollable horizontalmente
5. ✅ Los botones deberían ocupar todo el ancho
6. ✅ La lista de transacciones debería ser legible

#### Tablet (640px - 1024px)
1. Ajusta la ventana a ~768px de ancho
2. ✅ Las tarjetas de resumen deberían mostrar 2 por fila
3. ✅ El sidebar debería seguir oculto
4. ✅ Los formularios deberían usar 2 columnas

#### Desktop (> 1024px)
1. Expande la ventana a tamaño completo
2. ✅ El sidebar debería estar siempre visible
3. ✅ Las tarjetas de resumen deberían mostrar 3 por fila
4. ✅ El contenido debería estar centrado con max-width

---

## ✅ Checklist de Pruebas

### Funcionalidad Core
- [ ] Dashboard muestra datos correctos
- [ ] Crear transacción (ingreso)
- [ ] Crear transacción (gasto)
- [ ] Editar transacción
- [ ] Eliminar transacción
- [ ] Filtro por fechas funciona
- [ ] Paginación funciona
- [ ] Gráfico de gastos se actualiza

### UI/UX
- [ ] Todos los botones responden
- [ ] Los formularios validan correctamente
- [ ] Los errores se muestran claramente
- [ ] Los mensajes de éxito aparecen
- [ ] Los spinners de carga funcionan
- [ ] Los modales se abren y cierran
- [ ] La navegación funciona

### Responsive
- [ ] Mobile (< 640px) se ve bien
- [ ] Tablet (640-1024px) se ve bien
- [ ] Desktop (> 1024px) se ve bien
- [ ] Sidebar responsive funciona

### Integración
- [ ] Frontend conecta con Backend
- [ ] Datos persisten en la base de datos
- [ ] Actualizaciones se reflejan inmediatamente
- [ ] Errores de API se manejan bien

---

## 🐛 Reporte de Bugs

Si encuentras algún problema, documenta:

1. **Qué esperabas:**
2. **Qué obtuviste:**
3. **Pasos para reproducir:**
4. **Navegador y versión:**
5. **Screenshots (si aplica):**

---

## 🎉 Todo Funciona?

Si todas las pruebas pasan: **¡FELICITACIONES! 🎉**

Tu aplicación ExpenseTracker está completamente funcional y lista para ser usada.

**Próximo paso:** Crear datos de prueba realistas y explorar la aplicación libremente.

