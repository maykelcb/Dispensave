# ASIGNACIÓN OFICIAL DE SECCIONES DEL INFORME ACADÉMICO
## DispenSave – Plataforma de Rescate de Comida

El informe formal del proyecto se divide equitativamente entre los 4 integrantes del equipo. Cada colaborador es responsable de redactar su sección asignada en el informe de Word y presentar las evidencias correspondientes.

---

### RESUMEN DE ASIGNACIÓN POR INTEGRANTE

| Integrante | Rol en el Proyecto | Secciones del Informe que Debe Redactar | Historias de Usuario | Sprint Asignado | Guía Técnica en GitHub |
|:---|:---|:---|:---:|:---:|:---|
| **Maykel Bernaola** | Líder Técnico e Integrador | **4.1** Arquitectura de la aplicación<br>**VI.1** Evidencias P1 | **HU01 a HU04** | Sprint 1 | [docs/documento_parte1_dispensave.md](documento_parte1_dispensave.md) |
| **Rubén** | Desarrollador UI / UX | **4.4** Diseño de interfaces y flujo de navegación<br>**VI.2** Evidencias P2 | **HU05 a HU08** | Sprint 2 | [docs/parte2_ruben.md](parte2_ruben.md) |
| **Luis** | Desarrollador Backend Local & Métricas | **4.3** Diseño de base de datos (Modelo ER y Diccionario)<br>**VI.3** Evidencias P3 | **HU09 a HU12** | Sprint 3 | [docs/parte3_luis.md](parte3_luis.md) |
| **Andy** | Desarrollador Cloud & Servicios | **4.2** Tecnologías utilizadas (Backend, Cloud y APIs)<br>**VI.4** Evidencias P4 | **HU13 a HU16** | Sprint 4 | [docs/parte4_andy.md](parte4_andy.md) |

---

## DETALLE DE LO QUE DEBE HACER CADA COLABORADOR

### 1. MAYKEL BERNAOLA (Parte 1)
- **Sección 4.1: Arquitectura de la aplicación:**
  - Descripción de la arquitectura seleccionada: MVVM + Clean Architecture con Jetpack Compose.
  - Diagrama de arquitectura con flujos y nombres en español.
  - Descripción de componentes principales (Vistas, ViewModel, Dominio, Repositorios).
  - Flujo general de información (UDF - Unidirectional Data Flow).
  - Justificación técnica de la arquitectura frente a MVC y MVP.
- **Sección V.1: Historias de Usuario:**
  - Tabla con HU01 (Registro con Rol y Tipo de Local), HU02 (Navegación contextual), HU03 (Catálogo e inventario libre), HU04 (Publicación de ofertas con validación numérica).
- **Sección V.2 & V.3: Planificación y Evidencias de Seguimiento:**
  - Sprint 1 (Objetivo, funcionalidades y actividades ejecutadas).
  - Metodología de seguimiento con Tablero Kanban (GitHub Projects / Trello).
- **Sección VI.1: Evidencias de Implementación:**
  - Estructura de paquetes en el repositorio (`com.example.dispensave`).
  - Evidencia de compilación exitosa (`BUILD SUCCESSFUL`).
  - Capturas de pantallas funcionales.

---

### 2. RUBÉN (Parte 2)
- **Sección 4.4: Diseño de interfaces:**
  - Presentar el diseño visual actualizado de la aplicación para el cliente.
  - Diagrama de flujo de navegación del usuario (Wireframes / Mockups).
  - Descripción de pantallas principales: Explorar con buscador y filtros, Ficha detallada de la oferta, Pestaña de Favoritos y Diálogo de recordatorio.
- **Sección V.1: Historias de Usuario:**
  - Redactar en tabla las 4 Historias de Usuario:
    - **HU05:** Búsqueda reactiva de ofertas y filtrado por categoría (Panaderías, Cafeterías, Restaurantes, Pastelerías).
    - **HU06:** Ficha detallada de la oferta con carrusel de fotos, descripción y botón de reserva.
    - **HU07:** Guardar comercios y ofertas en lista de Favoritos.
    - **HU08:** Notificaciones locales y recordatorios de horario de recojo.
- **Sección V.2: Sprint 2 de Desarrollo:**
  - Objetivo del Sprint 2, funcionalidades implementadas y actividades ejecutadas.
- **Sección VI.2: Evidencias de Implementación (Parte 2):**
  - Capturas de las nuevas pantallas implementadas en emulador.
  - Fragmentos de código de los Composables creados (`SearchTopBar`, `ProductDetailScreen`, etc.).

---

### 3. LUIS (Parte 3)
- **Sección 4.3: Diseño de base de datos:**
  - **Modelo Entidad-Relación (ER):** Diagrama visual o estructurado con entidades y cardinalidades (1:N, N:M).
  - **Diccionario de datos completo:** Tablas detalladas para `usuarios`, `ofertas_rescate`, `pedidos`, `productos_comercio` e `impacto_ecologico` (nombre de campo, tipo de dato, nulo, llave primaria/foránea y descripción).
  - **Relaciones entre entidades:** Explicación técnica de la integridad referencial.
  - **Justificación del diseño:** Por qué se usa base de datos local SQLite mediante la librería Room.
- **Sección V.1: Historias de Usuario:**
  - Redactar en tabla las 4 Historias de Usuario:
    - **HU09:** Cálculo matemático de impacto ecológico (CO₂ evitado, agua ahorrada y ahorro económico).
    - **HU10:** Gamificación: niveles de rescatista (Novato, Guardián, Héroe) e insignias ganadas.
    - **HU11:** Historial completo y detalle de pedidos de rescate completados.
    - **HU12:** Función de compartir logros ambientales en redes sociales.
- **Sección V.2: Sprint 3 de Desarrollo:**
  - Objetivo del Sprint 3, funcionalidades y actividades ejecutadas.
- **Sección VI.3: Evidencias de Implementación (Parte 3):**
  - Código de las entidades Room (`@Entity`, `@Dao`, `@Database`) y capturas del funcionamiento de la pantalla de Impacto CO₂.

---

### 4. ANDY (Parte 4)
- **Sección 4.2: Tecnologías utilizadas (Backend, Cloud y Servicios API):**
  - Describir técnicamente las herramientas de desarrollo en la nube:
    - Lenguaje: Kotlin
    - Framework: Jetpack Compose
    - Backend / Base de Datos Remota: Firebase Cloud Firestore
    - Autenticación: Firebase Authentication
    - Almacenamiento multimedia: Firebase Cloud Storage
    - Servicios de Notificaciones: Firebase Cloud Messaging (FCM)
    - APIs de Geolocalización: Google Location Services (FusedLocationProviderClient)
  - Justificar técnicamente cada elección (tiempo real, escalabilidad, costo cero inicial).
- **Sección V.1: Historias de Usuario:**
  - Redactar en tabla las 4 Historias de Usuario:
    - **HU13:** Autenticación remota y persistencia en la nube con Firebase Auth.
    - **HU14:** Sincronización en tiempo real de ofertas y control atómico de stock en Firestore.
    - **HU15:** Notificaciones push remotas (nuevas ofertas publicadas y recordatorios de entrega).
    - **HU16:** Modo sin conexión (offline-first) con sincronización bidireccional Room + Firestore.
- **Sección V.2: Sprint 4 de Desarrollo:**
  - Objetivo del Sprint 4, despliegue e integración cloud.
- **Sección VI.4: Evidencias de Implementación (Parte 4):**
  - Estructura NoSQL de documentos y colecciones en Firestore.
  - Capturas de la consola de Firebase con usuarios registrados y ofertas sincronizadas.
