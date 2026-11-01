# 📋 PLAN MAESTRO DE DISTRIBUCIÓN COLABORATIVA DEL PROYECTO DISPENSAVE
## Asignación Equitativa en 4 Partes (GitHub + Informe Académico Oficial)

---

### DATOS GENERALES DEL PROYECTO
- **Proyecto:** DispenSave — Plataforma Inteligente de Rescate de Alimentos y Gestión de Despensa
- **Repositorio Oficial en GitHub:** [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)
- **Líder Técnico e Integrador:** Maykel Bernaola (`maykelcb`)
- **Estructura del Equipo:** 4 Integrantes con roles diferenciados, ramas dedicadas en Git y secciones específicas del documento.

---

## 1. TABLA MAESTRA DE ASIGNACIÓN (ROLES, GITHUB Y SECCIONES)

| Integrante / Asignación | Rol en el Proyecto | Rama Oficial en GitHub | Secciones Asignadas del Informe | Módulos de Código y Responsabilidad Técnica | Guía Detallada en Repo |
| :---: | :--- | :---: | :--- | :--- | :---: |
| **PARTE 1**<br>**Maykel Bernaola**<br>(`maykelcb`) | **Líder Técnico & Arquitecto de Software** | `main` | **1. Resumen Ejecutivo**<br>• 1.1 Descripción actual del proyecto<br>• 1.2 Objetivos alcanzados<br>• 1.3 Estado de desarrollo<br><br>**2. Arquitectura Actualizada del Sistema**<br>• 2.1 Arquitectura general (diagrama + componentes)<br>• 2.2 Arquitectura MVVM (evidencias de separación UI, ViewModel, Repository, Data Sources) | • Arquitectura base MVVM + Jetpack Compose<br>• Grafo de navegación central (`AppNavigation.kt`)<br>• Contenedor con barra dinámica (`ContenedorPrincipal.kt`)<br>• ViewModel central (`PantryViewModel.kt`)<br>• Autenticación y roles (`RegisterScreen`, `LoginScreen`)<br>• Integración y revisión de Pull Requests en `main` | [docs/documento_parte1_dispensave.md](documento_parte1_dispensave.md) |
| **PARTE 2**<br>**Persona 2**<br>*(Rubén / Servicios Web)* | **Desarrollador de Conectividad Web & Seguridad** | `feature/retrofit-api-consumption` | **3. Tecnología para el Consumo de Servicios Web**<br>• 3.1 Diseño de la API (Endpoints, HTTP GET, JSON DTOs)<br>• 3.2 Consumo de servicios (RetrofitClient, respuestas, errores)<br><br>**7. Privacidad y Seguridad**<br>• 7.1 Protección de datos almacenados y en tránsito<br>• 7.2 Seguridad de la app (validaciones `ValidationUtils`, permisos `INTERNET`, sanitización) | • Configuración Retrofit 2 + OkHttp + Gson<br>• Endpoint OpenFoodFacts (`FoodApiService.kt`)<br>• Modelos de transferencia DTO (`OpenFoodFactsDto.kt`)<br>• Búsqueda online (`ProductSearchScreen.kt`)<br>• Validaciones estrictas (`ValidationUtils.kt`)<br>• Permisos de red en `AndroidManifest.xml` | [docs/parte2_ruben.md](parte2_ruben.md) |
| **PARTE 3**<br>**Persona 3**<br>*(Luis / Persistencia)* | **Desarrollador Backend Local & Geolocalización** | `feature/room-crud-pantry` | **4. Implementación de Operaciones CRUD**<br>• 4.1 Create, Read, Update, Delete con Room/SQLite<br>• 4.2 Evidencias funcionales y casos de prueba<br>• 4.3 Gestión de estados (Loading, Success, Error)<br><br>**6. Geolocalización (Opcional / Rescate)**<br>• 6.1 Implementación Google Maps API<br>• 6.2 Ubicación, mapa, marcadores de comercios, permisos<br>• 6.3 Evidencias y pruebas de geolocalización | • Base de datos Room SQLite (`AppDatabase.kt`)<br>• Entidad y DAO (`ProductEntity.kt`, `ProductDao.kt`)<br>• Repositorio reactivo (`PantryRepository.kt`)<br>• Pantalla de detalle y edición (`ProductDetailScreen.kt`)<br>• Formulario modal (`AddProductDialog.kt`)<br>• Módulo de ubicación de aliados (`ExploreScreen.kt`) | [docs/parte3_luis.md](parte3_luis.md) |
| **PARTE 4**<br>**Persona 4**<br>*(Anderson Abanto / Andy)* | **Desarrollador UX, Sostenibilidad ODS 12 & QA** | `feature/shopping-list-alerts` | **8. Impacto Social y ODS 12**<br>• 8.1 Uso responsable de la información y rescate<br>• 8.2 Consumo responsable de recursos técnicos (optimización Room, red, desarrollo sostenible)<br><br>**9. Resultados Obtenidos**<br>• Funcionalidades completadas (evidencias)<br>• Funcionalidades pendientes (roadmap fase 2)<br>• Lecciones aprendidas (retos y soluciones)<br><br>**10. Anexos**<br>• Capturas adicionales, pruebas, links a Repo y Tablero | • Módulo de Lista de Compras (`ShoppingListScreen.kt`)<br>• Traslado automatizado a despensa SQLite<br>• Alertas semafóricas visuales (`ExpirationBadge.kt`)<br>• Diálogo modal confirmación (`ConfirmDeleteDialog.kt`)<br>• Métricas de ahorro y CO₂ (`ImpactScreen.kt`)<br>• Consolidación de evidencias visuales y tablero Kanban | [docs/parte4_andy.md](parte4_andy.md) |

---

## 2. FLUJO DE TRABAJO EN GITHUB (GIT FLOW)

Para trabajar de manera ordenada en Android Studio o por terminal de comandos:

```bash
# 1. Clonar el repositorio
git clone https://github.com/maykelcb/DispenSave.git
cd DispenSave

# 2. Maykel (Líder Técnico & Integrador):
git checkout main
git pull origin main

# 3. Persona 2 (Servicios Web & Seguridad):
git checkout feature/retrofit-api-consumption
git pull origin feature/retrofit-api-consumption

# 4. Persona 3 (Room CRUD & Geolocalización):
git checkout feature/room-crud-pantry
git pull origin feature/room-crud-pantry

# 5. Persona 4 (UX Compras, ODS 12 & Resultados):
git checkout feature/shopping-list-alerts
git pull origin feature/shopping-list-alerts
```

---

## 3. CHECKLIST DE CUMPLIMIENTO POR INTEGRANTE

### Parte 1 — Maykel Bernaola (`main`)
- [x] Redactar **Sección 1: Resumen Ejecutivo** completo (descripción, objetivos alcanzados y estado de desarrollo).
- [x] Redactar **Sección 2: Arquitectura Actualizada del Sistema** (diagrama integral y componentes).
- [x] Documentar con código fuente la **Arquitectura MVVM**: separación de UI (Compose), ViewModel (`PantryViewModel`), Repository (`PantryRepository`) y Data Sources (Room y Retrofit).
- [x] Coordinar la integración general y validar la compilación exitosa (`BUILD SUCCESSFUL`).

### Parte 2 — Persona 2 (`feature/retrofit-api-consumption`)
- [ ] Redactar **Sección 3: Tecnología para el Consumo de Servicios Web** con Retrofit 2 + OkHttp.
- [ ] Detallar diseño de API: Endpoint `cgi/search.pl`, parámetros HTTP GET y modelos DTO JSON (`OpenFoodFactsResponseDto`).
- [ ] Explicar configuración de `RetrofitClient`, parseo Gson, timeouts y manejo de errores/reintentos.
- [ ] Redactar **Sección 7: Privacidad y Seguridad** (protección de datos locales en SQLite, validaciones de `ValidationUtils.kt`, permisos de Android y buenas prácticas de seguridad).

### Parte 3 — Persona 3 (`feature/room-crud-pantry`)
- [ ] Redactar **Sección 4: Implementación de Operaciones CRUD** completas con Room Persistence Library.
- [ ] Documentar Create (`@Insert`), Read (`@Query` reactivo con Flow), Update (`@Update`) y Delete (`@Delete`).
- [ ] Elaborar la matriz de casos de prueba unitarios/funcionales y capturas de persistencia.
- [ ] Explicar la gestión de estados: Loading, Success y Error en la base de datos local.
- [ ] Redactar **Sección 6 (Opcional): Geolocalización** con Google Maps, obtención de coordenadas, marcadores de tiendas de rescate y permisos de ubicación.

### Parte 4 — Persona 4 (`feature/shopping-list-alerts`)
- [ ] Redactar **Sección 8: Impacto Social y ODS 12** (Uso responsable de la información, rescate de excedentes, cálculo de CO₂ evitado).
- [ ] Documentar el consumo responsable de recursos tecnológicos: optimización de queries SQL, minimización de llamadas de red y arquitectura sostenible.
- [ ] Redactar **Sección 9: Resultados Obtenidos** (Módulos completados, funcionalidades pendientes para fase 2, lecciones aprendidas con retos y soluciones).
- [ ] Consolidar la **Sección 10: Anexos** (Álbum de capturas, evidencias de pruebas, enlace al repositorio GitHub y enlace al tablero de trabajo Jira/Trello).
