# 📚 Documentación de Base de Datos - ExpenseTracker

## Índice de Documentos

Esta carpeta contiene toda la documentación relacionada con el esquema de base de datos de ExpenseTracker.

---

## 📄 Documentos Disponibles

### 1. [Schema Overview](./schema_overview.md)
**Resumen completo del esquema de base de datos**

Contenido:
- Diagrama de relaciones (ER Diagram)
- Descripción detallada de las 5 tablas principales
- Relaciones entre entidades
- Estrategia de indexación
- Estimaciones de almacenamiento
- Consideraciones de seguridad

**Audiencia:** Desarrolladores, arquitectos, DBAs  
**Cuándo usar:** Para entender la estructura general de la base de datos

---

### 2. [Constraints and Validations](./constraints_and_validations.md)
**Guía completa de restricciones y validaciones**

Contenido:
- Constraints por tabla (PK, FK, UNIQUE, CHECK)
- Políticas de eliminación (CASCADE vs RESTRICT)
- Ejemplos de casos válidos e inválidos
- Enumeraciones (ENUM types)
- Suite de tests de validación

**Audiencia:** Desarrolladores backend, QA  
**Cuándo usar:** Al insertar/actualizar datos, debugging de errores de constraint

---

### 3. [Indexes and Performance](./indexes_and_performance.md)
**Estrategia de optimización y performance**

Contenido:
- 25 índices detallados con justificación
- Consultas optimizadas con EXPLAIN ANALYZE
- Queries de monitoreo de performance
- Anti-patrones a evitar
- Benchmarks y métricas objetivo
- Mantenimiento de índices

**Audiencia:** DBAs, desarrolladores senior, performance engineers  
**Cuándo usar:** Para optimizar queries, investigar problemas de performance

---

### 4. [Setup Instructions](./setup_instructions.md)
**Guía paso a paso para configurar la base de datos**

Contenido:
- Instalación de PostgreSQL (local y Docker)
- Configuración inicial de usuario y base de datos
- Ejecución de migraciones (manual, Flyway, scripts)
- Verificación de instalación
- Docker Compose setup
- Troubleshooting común

**Audiencia:** Desarrolladores nuevos en el proyecto, DevOps  
**Cuándo usar:** Al configurar entorno de desarrollo por primera vez

---

## 🗄️ Estructura de la Base de Datos

### Tablas Principales (5)

| Tabla | Registros Típicos | Propósito |
|-------|-------------------|-----------|
| **users** | ~10,000 | Usuarios registrados |
| **categories** | ~150,000 | Categorías (15 por usuario) |
| **transactions** | ~3,000,000 | Transacciones financieras |
| **budgets** | ~120,000 | Presupuestos mensuales |
| **user_sessions** | ~50,000 | Sesiones activas |

**Total de índices:** 25  
**Motor:** PostgreSQL 14+  
**Sistema de migraciones:** Flyway

---

## 🚀 Quick Start

### Setup Rápido con Docker

```bash
# 1. Clonar el repositorio
cd finalproject-jelh

# 2. Iniciar PostgreSQL con Docker Compose
docker-compose up -d

# 3. Ejecutar migraciones
./setup-database.sh

# 4. Verificar
psql -h localhost -U expense_user -d expense_tracker -c "\dt"
```

### Credenciales de Demo

- **Email:** demo@expensetracker.com
- **Password:** Demo1234!
- **Username:** demo_user

---

## 📊 Arquitectura de Datos

```
┌─────────────┐
│    USERS    │
└──────┬──────┘
       │
       ├──────────┐
       │          │
       ▼          ▼
┌───────────┐ ┌──────────────┐
│CATEGORIES │ │ USER_SESSIONS│
└─────┬─────┘ └──────────────┘
      │
      ├─────────┐
      │         │
      ▼         ▼
┌─────────────┐ ┌─────────┐
│TRANSACTIONS │ │ BUDGETS │
└─────────────┘ └─────────┘
```

---

## 🔧 Scripts de Migración

### Ubicación
```
backend/src/main/resources/db/migration/
├── V1__Initial_Schema.sql      # Creación de tablas
├── V2__Seed_Data.sql          # Datos de prueba
└── V3__Add_Indexes.sql        # Índices de optimización
```

### Orden de Ejecución
1. **V1** - Crea las 5 tablas con constraints
2. **V2** - Inserta usuario demo, categorías y transacciones de prueba
3. **V3** - Agrega índices para optimización

---

## 📈 Métricas de Performance

| Operación | Target | Estado |
|-----------|--------|--------|
| Login | < 10ms | ✅ |
| Dashboard load | < 100ms | ✅ |
| Transaction creation | < 50ms | ✅ |
| Monthly report | < 200ms | ✅ |
| Budget calculation | < 150ms | ✅ |

---

## 🔒 Seguridad

- ✅ Passwords hasheados con BCrypt (cost factor 10)
- ✅ Tokens JWT únicos con expiración
- ✅ Soft deletes (campo `active`)
- ✅ Validaciones a nivel de base de datos
- ✅ Foreign keys con políticas definidas
- ✅ Índices en campos sensibles

---

## 🧪 Testing

### Verificación Rápida

```sql
-- Contar registros en cada tabla
SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'categories', COUNT(*) FROM categories
UNION ALL
SELECT 'transactions', COUNT(*) FROM transactions
UNION ALL
SELECT 'budgets', COUNT(*) FROM budgets
UNION ALL
SELECT 'user_sessions', COUNT(*) FROM user_sessions;
```

### Test de Performance

```sql
-- Test de query del dashboard
EXPLAIN ANALYZE
SELECT t.*, c.name as category_name
FROM transactions t
JOIN categories c ON t.category_id = c.id
WHERE t.user_id = 1
ORDER BY t.transaction_date DESC
LIMIT 10;
```

---

## 🔄 Mantenimiento

### Tareas Recomendadas

| Frecuencia | Tarea | Comando |
|------------|-------|---------|
| Diaria | Limpieza de sesiones expiradas | Ver setup_instructions.md |
| Semanal | Análisis de estadísticas | `ANALYZE;` |
| Mensual | Revisión de índices no usados | Ver indexes_and_performance.md |
| Trimestral | Reindexación | `REINDEX DATABASE expense_tracker;` |
| Trimestral | Backup completo | `pg_dump ...` |

---

## 📞 Soporte y Recursos

### Documentación Relacionada
- [README Principal del Proyecto](../../readme.md)
- [Prompts de Desarrollo](../../prompts.md)

### Enlaces Útiles
- [PostgreSQL Documentation](https://www.postgresql.org/docs/14/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Data JPA Guide](https://spring.io/projects/spring-data-jpa)

### Problemas Comunes
Ver sección de **Troubleshooting** en [Setup Instructions](./setup_instructions.md)

---

## 📝 Changelog

### Versión 3.0 (Octubre 2024)
- ✅ Creación de esquema inicial (5 tablas)
- ✅ Implementación de constraints y validaciones
- ✅ Optimización con 25 índices estratégicos
- ✅ Datos de prueba (seed data)
- ✅ Documentación completa

### Próximas Versiones
- [ ] Tabla de notificaciones
- [ ] Particionamiento de transactions
- [ ] Materialized views para reportes
- [ ] Full-text search en descriptions

---

## 🤝 Contribución

Al modificar el esquema de base de datos:

1. Crear nueva migración con naming: `V{N}__{Description}.sql`
2. Actualizar documentación correspondiente
3. Ejecutar tests de validación
4. Actualizar este README si es necesario

---

**Última actualización:** Octubre 2024  
**Versión del esquema:** 3.0  
**Mantenido por:** Equipo de Desarrollo ExpenseTracker

---

## 📋 Checklist de Setup

- [ ] PostgreSQL 14+ instalado
- [ ] Base de datos `expense_tracker` creada
- [ ] Usuario `expense_user` configurado
- [ ] Migración V1 ejecutada exitosamente
- [ ] Migración V2 ejecutada exitosamente
- [ ] Migración V3 ejecutada exitosamente
- [ ] Usuario demo puede hacer login
- [ ] Queries de dashboard funcionan correctamente
- [ ] Todos los índices creados
- [ ] Documentación leída y comprendida

---

**¿Primera vez configurando? → Comienza con [Setup Instructions](./setup_instructions.md)**

**¿Problemas de performance? → Revisa [Indexes and Performance](./indexes_and_performance.md)**

**¿Errores de validación? → Consulta [Constraints and Validations](./constraints_and_validations.md)**

**¿Quieres entender la estructura? → Lee [Schema Overview](./schema_overview.md)**

