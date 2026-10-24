# ASIGNACIÓN OFICIAL DE RAMAS Y SECCIONES DEL PROYECTO
## DispenSave — Plataforma de Rescate de Comida

El repositorio oficial de GitHub cuenta con **4 ramas exactas** (la rama principal `main` y 3 ramas de trabajo paralelas para los integrantes). Cada colaborador tiene asignada una rama específica de Git, sus módulos de código y sus secciones correspondientes para el informe académico formal en Word/PDF.

---

### TABLA MAESTRA DE ASIGNACIÓN (RAMAS DE GITHUB Y ROLES)

| Integrante | Rol Oficial | Rama Real de Git en GitHub | Secciones del Informe a Redactar | Historias de Usuario | Sprint Asignado | Guía Detallada en Repo |
|:---|:---|:---:|:---:|:---:|:---:|:---|
| **Maykel Bernaola** (`maykelcb`) | Líder Técnico e Integrador de Arquitectura | `main` | **4.1** Arquitectura de la aplicación<br>**VI.1** Evidencias P1 | **HU01 a HU04** | Sprint 1 | [docs/documento_parte1_dispensave.md](documento_parte1_dispensave.md) |
| **Rubén** | Desarrollador APIs & Consumo REST | `feature/retrofit-api-consumption` | **4.2** Tecnologías y Servicios API REST<br>**VI.2** Evidencias P2 | **HU05 a HU08** | Sprint 2 | [docs/parte2_ruben.md](parte2_ruben.md) |
| **Luis** | Desarrollador Backend Local & Persistencia Room | `feature/room-crud-pantry` | **4.3** Diseño de base de datos (Modelo ER y Diccionario)<br>**VI.3** Evidencias P3 | **HU09 a HU12** | Sprint 3 | [docs/parte3_luis.md](parte3_luis.md) |
| **Anderson Abanto** (`Anderson08102001` / Andy) | Desarrollador UI/UX & Lista de Compras | `feature/shopping-list-alerts` | **4.4** Diseño de interfaces (Wireflow y Heurísticas)<br>**VI.4** Evidencias P4 | **HU13 a HU16** | Sprint 4 | [docs/parte4_shopping_alerts.md](parte4_shopping_alerts.md) |

---

## MAPEO DETALLADO POR INTEGRANTE Y RAMA

### 1. MAYKEL BERNAOLA — Rama: `main`
- **Rol:** Líder Técnico e Integrador de Arquitectura (Parte 1).
- **Código en Android Studio:**
  - Arquitectura MVVM + Clean Architecture en Jetpack Compose.
  - Contenedor principal con barra de navegación dinámica por roles (`AppNavigation.kt`).
  - Módulos de autenticación: `OnboardingScreen`, `LoginScreen`, `RegisterScreen`.
  - Catálogo propio de comercios, publicación de ofertas (Packs e Individuales) y validaciones estrictas (`ValidationUtils.kt`).
- **Capítulos del Informe Escrito:**
  - **4.1 Arquitectura de la aplicación:** Descripción MVVM, diagrama UDF sin cruce de textos, componentes de capas y justificación técnica.
  - **5.1 Historias de Usuario:** HU01 (Registro con rol), HU02 (Navegación contextual), HU03 (Catálogo propio), HU04 (Publicación de ofertas con validación).
  - **5.2 Sprint 1:** Sprint Goal, tablero Kanban inicial y actividades ejecutadas.
  - **VI.1 Evidencias:** Árbol de paquetes `com.example.dispensave`, evidencia de compilación `BUILD SUCCESSFUL` y capturas de pantalla de la app.

---

### 2. RUBÉN — Rama: `feature/retrofit-api-consumption`
- **Rol:** Desarrollador APIs & Consumo de Servicios Web (Parte 2).
- **Código en Android Studio:**
  - Integración obligatoria por rúbrica: *Consumo de al menos un servicio REST o API*.
  - Configuración de **Retrofit + OkHttp** y converter Gson/Moshi.
  - Modelos de red (DTO) y consumo de API externa de catálogo/alimentos (ej. búsqueda de información nutricional, alérgenos o catálogo externo).
  - Manejo de estados de red: carga (`Loading`), éxito (`Success`) y error (`Error`) con retry.
- **Capítulos del Informe Escrito:**
  - **4.2 Tecnologías utilizadas:** Detalle técnico de Retrofit, OkHttp, manejo asíncrono con Coroutines/Flow y arquitectura de red.
  - **5.1 Historias de Usuario:** HU05 a HU08 (Búsqueda remota de productos, catálogo API, validación de conectividad y cache temporal).
  - **5.2 Sprint 2:** Sprint Goal de integración de API REST y actividades.
  - **VI.2 Evidencias:** Fragmentos de interfaces Retrofit (`@GET`), llamadas HTTP exitosas (código 200) y capturas del consumo de API.

---

### 3. LUIS — Rama: `feature/room-crud-pantry`
- **Rol:** Desarrollador Backend Local & Persistencia SQLite (Parte 3).
- **Código en Android Studio:**
  - Reemplazo de datos en memoria por persistencia SQLite nativa mediante **Room Database**.
  - Entidades y DAOs: `AppDatabase.kt`, `ProductEntity.kt`, `ProductDao.kt`.
  - Operaciones CRUD completas (Crear alimento, Listar ordenado por vencimiento, Actualizar cantidad/unidad, Eliminar).
- **Capítulos del Informe Escrito:**
  - **4.3 Diseño de base de datos:** Modelo Entidad-Relación (ER), Diccionario de datos formal de todas las tablas y justificación de SQLite/Room.
  - **5.1 Historias de Usuario:** HU09 a HU12 (Persistencia local permanente, orden cronológico de caducidad, edición y borrado de stock).
  - **5.2 Sprint 3:** Sprint Goal de persistencia y base de datos local.
  - **VI.3 Evidencias:** Clases `@Dao`, estructura `@Database` y capturas de persistencia de datos tras cerrar y reabrir la app.

---

### 4. ANDERSON ABANTO — Rama: `feature/shopping-list-alerts`
- **Rol:** Desarrollador UI/UX & Lista de Compras (Parte 4).
- **Código en Android Studio (Ya implementado por Anderson):**
  - Módulo de Lista de Compras: `ShoppingListScreen.kt` con checkboxes animados y filtros.
  - Traslado automático a despensa: `ShoppingItem.kt` y método `toggleShoppingItemPurchased()` en `PantryViewModel.kt`.
  - Semáforo visual de caducidad: `ExpirationBadge.kt` (🟢 Vigente >7d, 🟡 Por vencer ≤3d, 🔴 Vencido).
  - Prevención de errores destructivos: `ConfirmDeleteDialog.kt` (Modal Material 3 antes de eliminar).
- **Capítulos del Informe Escrito:**
  - **4.4 Diseño de interfaces:** Wireflow completo del flujo de compras a despensa y evaluación de Heurísticas de Usabilidad de Nielsen.
  - **5.1 Historias de Usuario:** HU13 a HU16 (o HU10 a HU12 en su documento: Lista interactiva, traslado automatizado, alertas semafóricas y confirmación modal).
  - **5.2 Sprint 4:** Sprint Goal de experiencia de usuario y alertas preventivas.
  - **VI.4 Evidencias:** Capturas de la lista de compras, del diálogo de confirmación y badges semafóricos.
  - Documentación técnica lista en: [docs/parte4_shopping_alerts.md](parte4_shopping_alerts.md).

---

## CÓMO TRABAJAR EN GIT CON LAS RAMAS OFICIALES

Para ubicarse en la rama correcta en Android Studio o por terminal:

```bash
# 1. Clonar el repositorio
git clone https://github.com/maykelcb/DispenSave.git
cd DispenSave

# 2. Cada integrante cambia a su rama asignada:
# Maykel (Líder Técnico):
git checkout main

# Anderson (Andy - UX & Compras):
git checkout feature/shopping-list-alerts

# Luis (Backend Local & Room):
git checkout feature/room-crud-pantry

# Rubén (APIs & Retrofit):
git checkout feature/retrofit-api-consumption
```
