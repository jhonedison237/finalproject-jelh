> Detalla en esta sección los prompts principales utilizados durante la creación del proyecto, que justifiquen el uso de asistentes de código en todas las fases del ciclo de vida del desarrollo. Esperamos un máximo de 3 por sección, principalmente los de creación inicial o  los de corrección o adición de funcionalidades que consideres más relevantes.
Puedes añadir adicionalmente la conversación completa como link o archivo adjunto si así lo consideras


## Índice

1. [Descripción general del producto](#1-descripción-general-del-producto)
2. [Arquitectura del sistema](#2-arquitectura-del-sistema)
3. [Modelo de datos](#3-modelo-de-datos)
4. [Especificación de la API](#4-especificación-de-la-api)
5. [Historias de usuario](#5-historias-de-usuario)
6. [Tickets de trabajo](#6-tickets-de-trabajo)
7. [Pull requests](#7-pull-requests)

---

## 1. Descripción general del producto

**Prompt 1:**
```
Quiero desarrollar un Gestor de gastos personales, quiero usar un stack con Spring Boot para el back y React para el front. Ayúdame a definir el objetivo del producto, qué valor aporta, qué soluciona y para quién está dirigido. También necesito las características y funcionalidades principales que debería tener.
```

**Prompt 2:**
```
Para mi aplicación de gestión de gastos personales con Spring Boot y React, necesito definir el diseño y experiencia de usuario. Describe el flujo principal del usuario desde que llega a la aplicación, los principios de UX que debería aplicar, y cómo debería ser la navegación entre las funcionalidades principales como dashboard, registro de transacciones y gestión de presupuestos.
```

**Prompt 3:**
```
Ayúdame a definir las instrucciones de instalación para mi proyecto ExpenseTracker. El stack es Spring Boot con Java 17, PostgreSQL, React con Vite, y quiero usar Docker. Necesito los prerrequisitos, comandos básicos para levantar backend, frontend y base de datos en entorno de desarrollo local.
```

---

## 2. Arquitectura del Sistema

### **2.1. Diagrama de arquitectura:**

**Prompt 1:**
```
Crea un diagrama de arquitectura en formato Mermaid para mi aplicación ExpenseTracker. La arquitectura debe mostrar la separación entre frontend (React), backend (Spring Boot), base de datos (PostgreSQL) y cache (Redis). Incluye las capas principales: presentación, lógica de negocio, acceso a datos, y servicios externos como email.
```

**Prompt 2:**
```
Justifica por qué elegiste una arquitectura en capas para ExpenseTracker. Explica los beneficios principales como escalabilidad, mantenibilidad y testabilidad, así como los sacrificios o déficits que implica esta decisión arquitectónica comparado con otras alternativas.
```


### **2.2. Descripción de componentes principales:**

**Prompt 1:**
```
Describe los componentes principales del frontend React para ExpenseTracker: tecnologías específicas como React Router, Axios, Material-UI, Context API, y Vite. Explica el propósito de cada uno y cómo se integran en la arquitectura.
```

**Prompt 2:**
```
Detalla los componentes del backend Spring Boot: Spring MVC, Spring Security, Spring Data JPA, Hibernate, y cómo se integra PostgreSQL y Redis. Incluye también OpenAPI/Swagger para documentación de la API.
```

**Prompt 3:**
```
Explica la configuración de la base de datos: por qué PostgreSQL como base principal, Redis para cache, y Flyway para control de versiones de base de datos. Incluye los beneficios de cada decisión tecnológica.
```

### **2.3. Descripción de alto nivel del proyecto y estructura de ficheros**

**Prompt 1:**
```
Crea la estructura de carpetas completa para mi proyecto ExpenseTracker con separación backend/frontend. El backend debe seguir la estructura estándar de Spring Boot con capas (controller, service, repository, entity, dto, config). El frontend debe seguir buenas prácticas de React con separación de componentes, pages, hooks, services y utils.
```

**Prompt 2:**
```
Explica qué patrón arquitectónico sigue la estructura del proyecto. ¿Es Domain-Driven Design? ¿Arquitectura hexagonal? Justifica por qué esta estructura es apropiada para un proyecto de gestión de gastos personales.
```

**Prompt 3:**
```
Añade a la estructura las carpetas para documentación, Docker, migraciones de base de datos, y tests. Explica brevemente el propósito de cada carpeta principal.
```

### **2.4. Infraestructura y despliegue**

**Prompt 1:**
```
Diseña un diagrama de infraestructura y proceso de despliegue para ExpenseTracker usando Docker, GitHub Actions para CI/CD, y un entorno de producción. Incluye desarrollo local, staging y producción.
```

**Prompt 2:**
```
Detalla el proceso de despliegue paso a paso: desde push al repositorio hasta producción. Incluye build automatizado, testing, generación de imágenes Docker, y despliegue automático en staging con aprobación manual para producción.
```

**Prompt 3:**
```
Especifica la configuración de Docker Compose para desarrollo local, incluyendo todos los servicios: React app con Nginx, Spring Boot, PostgreSQL y Redis. Incluye persistencia de volúmenes y configuración de red.
```

### **2.5. Seguridad**

**Prompt 1:**
```
Define las prácticas de seguridad para ExpenseTracker: autenticación JWT, hash de contraseñas con BCrypt, autorización basada en roles, y protección de endpoints. Incluye ejemplos de código con anotaciones de Spring Security.
```

**Prompt 2:**
```
Explica las medidas de protección de la API: configuración CORS, rate limiting, validación de inputs con Bean Validation, y prevención de SQL injection. Proporciona ejemplos prácticos de implementación.
```

**Prompt 3:**
```
Detalla la protección contra vulnerabilidades web: XSS prevention, HTTPS only, sanitización de inputs en frontend, y buenas prácticas de almacenamiento de tokens JWT en el cliente.
```

### **2.6. Tests**

**Prompt 1:**
```
Define la estrategia de testing para ExpenseTracker backend: tests unitarios con JUnit 5, tests de integración con Spring Boot Test, y tests de repositorio con @DataJpaTest. Incluye ejemplos de código para cada tipo.
```

**Prompt 2:**
```
Especifica el testing del frontend React: tests unitarios con Jest, tests de componentes con React Testing Library, y tests end-to-end con Cypress. Proporciona ejemplos de casos de prueba.
```

**Prompt 3:**
```
Añade testing de API: contract testing con OpenAPI specification validation, y performance testing con JMeter para los endpoints críticos. Explica qué métricas medir y qué umbrales establecer.
```

---

### 3. Modelo de Datos

**Prompt 1:**
```
Diseña el modelo de datos completo para ExpenseTracker usando un diagrama ER en formato Mermaid. Necesito las entidades principales: USER, CATEGORY, TRANSACTION, BUDGET, USER_SESSION y AUDIT_LOG. Incluye todos los atributos con tipos de datos, claves primarias, foráneas, y relaciones entre entidades.
```

**Prompt 2:**
```
Para cada entidad del modelo de datos de ExpenseTracker, proporciona una descripción detallada incluyendo: propósito de la entidad, descripción de cada atributo, tipos de datos específicos de PostgreSQL, restricciones (NOT NULL, UNIQUE, CHECK), y relaciones con otras entidades.
```

**Prompt 3:**
```
Añade las restricciones de negocio al modelo de datos: validaciones como montos diferentes de cero en transacciones, límites positivos en presupuestos, fechas no futuras, combinaciones únicas como (user_id, category_id, month, year) en budgets, y índices para optimizar consultas frecuentes.
```

---

### 4. Especificación de la API

**Prompt 1:**
```
Crea la especificación OpenAPI 3.0 para el endpoint de gestión de transacciones de ExpenseTracker. Incluye POST para crear transacciones y GET para listar con paginación y filtros. Especifica todos los parámetros, request/response bodies, códigos de estado HTTP, y ejemplos de uso.
```

**Prompt 2:**
```
Diseña la especificación OpenAPI para el endpoint de autenticación (/api/v1/auth/login). Incluye el request body con email y password, response con JWT token, información del usuario, tiempo de expiración, y manejo de errores para credenciales inválidas.
```

**Prompt 3:**
```
Crea el endpoint de dashboard (/api/v1/dashboard/summary) en formato OpenAPI. Debe retornar resumen financiero del usuario: balance, ingresos/gastos totales, distribución por categorías, y estado de presupuestos. Incluye parámetros de filtro por mes/año y ejemplos de respuesta.
```

---

### 5. Historias de Usuario

**Prompt 1:**
```
Crea 3 historias de usuario principales para ExpenseTracker siguiendo el formato estándar "Como [rol], quiero [funcionalidad] para [beneficio]". Incluye: 1) Registro de transacciones, 2) Dashboard financiero, 3) Gestión de presupuestos. Para cada una añade criterios de aceptación detallados usando formato Given-When-Then.
```

**Prompt 2:**
```
Para cada historia de usuario de ExpenseTracker, añade la Definición de Terminado (DoD) con criterios técnicos específicos: implementación de componentes, tests requeridos, documentación, code review, y integración. También incluye prioridad, estimación en Story Points, y Epic al que pertenece.
```

**Prompt 3:**
```
Completa las historias de usuario con información de gestión de proyecto: dependencias entre historias, metodología de priorización utilizada (MoSCoW), métricas de éxito para cada historia, y notas sobre estimación con Planning Poker.
```

---

### 6. Tickets de Trabajo

**Prompt 1:**
```
Crea un ticket de trabajo detallado para implementar la API de gestión de transacciones en el backend de ExpenseTracker. Incluye: ID del ticket, descripción, contexto, tareas técnicas específicas (entidad JPA, repository, service, controller, DTOs), criterios de aceptación, definición de terminado, dependencias, y archivos a crear/modificar.
```

**Prompt 2:**
```
Diseña un ticket de trabajo para desarrollar el dashboard de gastos en React. Especifica: componentes a crear (Dashboard, BalanceCard, ExpenseChart, TransactionList), integración con API, manejo de estado, responsive design, testing, y estructura de archivos. Incluye estimación en Story Points y dependencias.
```

**Prompt 3:**
```
Crea un ticket de infraestructura para configurar el esquema de base de datos con PostgreSQL y Flyway. Incluye: scripts de migración, constraints de integridad, índices estratégicos, datos semilla, configuración de backup/recovery, validaciones de negocio a nivel de DB, y documentación del esquema.
```

---

### 7. Pull Requests

**Prompt 1: Planificación y construcción de base de datos**
```
ahora procedamos a construir lo referente a la base de datos, antes de ejecutar dame un plan detallado y yo te doy el visto bueno para proceder, basemonos en el plan del ticket 3
```

**Respuesta del asistente:** Proporcionó un plan detallado de 5 fases:
- Fase 0: Preparación del entorno (estructura de carpetas)
- Fase 1: Script de esquema inicial (V1__Initial_Schema.sql con 5 tablas)
- Fase 2: Script de datos semilla (V2__Seed_Data.sql)
- Fase 3: Script de índices (V3__Add_Indexes.sql con 25 índices)
- Fase 4: Documentación completa (4 archivos markdown)

**Prompt 2: Corrección de documentación**
```
para esta parte inicial de la documentación no es necesario por ahora especificar Log de Auditoría asi que eso lo podemos obviar por ahora
```

**Respuesta del asistente:** Eliminó todas las referencias a la tabla AUDIT_LOG del modelo de datos y documentación, manteniendo solo las 5 tablas principales necesarias.

**Prompt 3: Completar infraestructura Docker**
```
en el @README.md dices que iniciar el PostgreSQL con Docker Compose pero no hay ningún archivo docker
```

**Respuesta del asistente:** Creó los archivos faltantes:
- docker-compose.yml: Configuración de PostgreSQL 14 y pgAdmin
- setup-database.sh: Script automatizado de instalación
- Actualizó .gitignore con exclusiones de Docker

**Prompt 4: Commit y documentación**
```
haz el commit hasta aquí colocando un mensaje descriptivo de esta fase
```

**Respuesta del asistente:** Intentó hacer commit pero encontró problemas con pre-commit hooks. Usuario realizó commit manual.

**Prompt 5: Corrección del mensaje de commit**
```
ya hice el commit pero me quedó muy corto, y también hice push, como hago para corregir y agregar toda la descripción que tenías lista?
```

**Respuesta del asistente:** Ejecutó `git commit --amend` seguido de `git push --force-with-lease` para reescribir el commit con un mensaje descriptivo completo que incluía:
- Estructura de base de datos detallada
- Scripts de migración Flyway
- Infraestructura Docker
- Documentación completa
- Características técnicas
- Datos de prueba
- Performance targets
