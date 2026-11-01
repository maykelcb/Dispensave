# GUÍA Y PLANTILLA OFICIAL — PROYECTO DISPENSAVE
## DOCUMENTO DE ENTREGA: PARTE 4
### Responsable Asignado: Persona 4 (Anderson Abanto / Andy — UX, Sostenibilidad ODS 12 & QA)
### Rama Oficial de GitHub: `feature/shopping-list-alerts`
### Repositorio: [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)

---

> [!IMPORTANT]
> ### 📋 SECCIONES DEL INFORME ACADÉMICO ASIGNADAS A LA PARTE 4:
> **1. Sección 8: Impacto Social y ODS 12** (Uso responsable de la información, rescate de comida, consumo tecnológico sostenible, optimización de SQLite y red).  
> **2. Sección 9: Resultados Obtenidos** (Módulos completados con evidencias, funcionalidades pendientes para fase 2, lecciones aprendidas con retos y soluciones).  
> **3. Sección 10: Anexos** (Álbum de capturas, evidencias de pruebas, enlace al repositorio GitHub y enlace al tablero de trabajo Jira/Trello).  
> 
> *Tu código en Android Studio se ubica en:* `ui/screens/shopping/ShoppingListScreen.kt`, `ui/impact/ImpactScreen.kt`, componentes de UI semafórica (`ExpirationBadge.kt`, `ConfirmDeleteDialog.kt`) y documentación de calidad.

---

# 8. IMPACTO SOCIAL Y ODS 12

### 8.1 Uso Responsable de la Información

El proyecto **DispenSave** fue concebido como una respuesta tecnológica directa al **Objetivo de Desarrollo Sostenible 12 (ODS 12: Producción y Consumo Responsables)** de la Organización de las Naciones Unidas (ONU), particularmente a la meta 12.3:
> *"Para 2030, reducir a la mitad el desperdicio de alimentos per cápita mundial en la venta al por menor y a nivel de los consumidores, y reducir las pérdidas de alimentos a lo largo de las cadenas de producción y suministro"*.

#### Estrategias Implementadas:
1. **Transparencia e Inteligencia sobre el Inventario Doméstico:**
   - La aplicación empodera a las familias brindándoles una visibilidad completa de sus existencias alimentarias. Esto erradica el fenómeno común de compras redundantes o impulsivas en los supermercados.
2. **Priorización de Consumo mediante el Algoritmo FEFO (*First Expired, First Out*):**
   - El sistema semafórico categoriza los alimentos por riesgo de caducidad:
     - 🟢 **Verde (Vigente):** Más de 3 días para su caducidad.
     - 🟡 **Amarillo (Por Vencer):** Faltan 3 días o menos. Alerta de consumo preferente.
     - 🔴 **Rojo (Vencido):** Fecha superada. Promueve su revisión inmediata o aprovechamiento en recetas de reciclaje culinario.
3. **Métricas de Conciencia Ambiental en Tiempo Real (`ImpactScreen.kt`):**
   - Transforma los datos de consumo en indicadores tangibles de impacto ecológico:
     - **Kilogramos de comida salvada:** Volumen acumulado que no terminó en vertederos.
     - **Ahorro Financiero Directo:** Dinero retenido en el presupuesto familiar por evitar la pérdida de alimentos o al adquirir Packs de Rescate a mitad de precio.
     - **Emisiones de CO₂ Prevenidas:** Cálculo empírico basado en el factor de 2.5 kg de CO₂ equivalente emitido por cada kilogramo de residuo orgánico en descomposición.
4. **Alianza de Economía Circular (Consumidor - Comercio):**
   - Los comercios aliados evitan desechar mermas y excedentes comerciales al final de su jornada, facilitando su venta a precios sociales para la comunidad.

---

### 8.2 Consumo Responsable de Recursos Tecnológicos

La sostenibilidad de DispenSave no se limita a su propósito social, sino que abarca su propia **arquitectura de software verde (*Green Computing / Green Software Engineering*)**, minimizando la huella energética y de procesamiento del smartphone:

#### 1. Optimización de Consultas en Base de Datos:
- **Indexación Numérica Eficiente:** Las fechas de expiración se almacenan como marcas de tiempo numéricas enteras de 64 bits (`Long` / 8 bytes) en lugar de cadenas de texto alfanuméricas ISO-8601 (20+ bytes). Las comparaciones numéricas `ORDER BY expirationDate ASC` se ejecutan a nivel de registros del procesador en tiempo sub-milisegundo.
- **Streams Asíncronos sin Sondeo (*No-Polling*):** El uso de `Flow` de Room notifica únicamente cuando los datos cambian. Se elimina el sondeo periódico (*polling*), suprimiendo ciclos inútiles de CPU que degradan la batería del usuario.

#### 2. Optimización de Almacenamiento y Tráfico de Red:
- **Arquitectura Offline-First:** Al retener los datos localmente en SQLite, la aplicación opera al 100% sin necesidad de efectuar peticiones continuas al servidor remoto, reduciendo el consumo de megabytes en planes de datos móviles en más de un 90%.
- **Paginación y Filtrado de Payloads:** Las consultas a la API de OpenFoodFacts imponen un límite estricto de 20 resultados por solicitud (`page_size=20`), evitando la descarga y serialización de payloads JSON excesivos.

#### 3. Buenas Prácticas de Desarrollo Sostenible:
- **Renderizado Eficiente con Smart Recomposition:** Jetpack Compose reconstruye únicamente los nodos del árbol de UI cuyo estado ha variado, evitando repintados completos de la pantalla y disminuyendo la demanda de la GPU del teléfono.

---

# 9. RESULTADOS OBTENIDOS

### 9.1 Funcionalidades Completadas

El desarrollo iterativo e incremental del proyecto permitió finalizar al 100% los siguientes módulos operativos:

| Módulo Desarrollado | Estado de Implementación | Evidencia Técnica en el Repositorio |
| :--- | :---: | :--- |
| **1. Arquitectura Base y Navegación** | **Completado (100%)** | `AppNavigation.kt` y `ContenedorPrincipal.kt` con barra dinámica por roles (`main`). |
| **2. Autenticación y Control de Roles** | **Completado (100%)** | `LoginScreen.kt` y `RegisterScreen.kt` con validación estricta de credenciales y selector de rol. |
| **3. Despensa CRUD con Room SQLite** | **Completado (100%)** | `AppDatabase.kt`, `ProductDao.kt` y `PantryRepository.kt` con persistencia permanente (`feature/room-crud-pantry`). |
| **4. Semáforo de Caducidad y Alertas** | **Completado (100%)** | `ExpirationBadge.kt` con cálculo dinámico de días y colores de advertencia. |
| **5. Consumo de API REST con Retrofit** | **Completado (100%)** | `RetrofitClient.kt`, `FoodApiService.kt` y `ProductSearchScreen.kt` (`feature/retrofit-api-consumption`). |
| **6. Lista de Compras con Auto-traslado** | **Completado (100%)** | `ShoppingListScreen.kt` con checkboxes interactivos y guardado directo en Room (`feature/shopping-list-alerts`). |
| **7. Diálogos Modales Anti-errores** | **Completado (100%)** | `ConfirmDeleteDialog.kt` aplicando la Heurística 5 de Nielsen (prevención de errores destructivos). |
| **8. Mercado de Rescate y Excedentes** | **Completado (100%)** | `ExploreScreen.kt` con packs sorpresa de comercios y panel de control comercial. |
| **9. Métricas de Impacto y Ahorro** | **Completado (100%)** | `ImpactScreen.kt` con cuantificación de CO₂ evitado y dinero ahorrado. |

---

### 9.2 Funcionalidades Pendientes (Roadmap para la Fase 2)

Para la siguiente etapa de evolución comercial y técnica de la plataforma, se han previsto las siguientes extensiones:
1. **Notificaciones Push Programadas en Segundo Plano:**
   - Integración de **Android WorkManager** para programar un *Worker* periódico diario a las 09:00 AM que verifique alimentos próximos a caducar en las próximas 48 horas y despache notificaciones del sistema (`NotificationCompat.Builder`).
2. **Escaneo Automático de Códigos de Barras con la Cámara:**
   - Incorporación de **Google ML Kit Barcode Scanning** y **CameraX** para leer códigos de barras EAN-13 físicos en empaques de alimentos e invocar automáticamente la búsqueda en OpenFoodFacts sin tecleo.
3. **Pasarela de Pagos Digitales para Rescates:**
   - Conexión con APIs de billeteras digitales (Yape, Plin, Mercado Pago o Stripe) para el prepago seguro de los Packs Sorpresa desde la app.
4. **Sincronización Multidispositivo en la Nube:**
   - Integración de autenticación remota y sincronización en tiempo real (Cloud Firestore) para permitir que los miembros de una misma familia compartan la despensa.

---

### 9.3 Lecciones Aprendidas

Durante el ciclo de desarrollo se superaron importantes retos técnicos:

#### Reto 1: Integración Reactiva entre Room SQLite y Jetpack Compose
- *Problema:* El manejo imperativo clásico de SQLite provocaba problemas de desincronización y parpadeos en pantalla al insertar registros.
- *Solución:* Se adoptó el patrón reactivo con `Flow<List<ProductEntity>>` en el DAO y su transformación a `StateFlow` en el `PantryViewModel`, permitiendo que Compose recomponga suavemente la UI al detectar emisiones.

#### Reto 2: Deserialización de JSONs Heterogéneos de OpenFoodFacts
- *Problema:* La API pública internacional de alimentos contiene campos nulos, formatos inconsistentes o idiomas mixtos (inglés/español).
- *Solución:* Se diseñaron modelos DTO con valores por defecto no nulos y getters inteligentes (`displayName`, `displayBrand`) que garantizan información legible aun cuando los datos remotos estén incompletos.

#### Reto 3: Prevención de Eliminaciones Accidentales
- *Problema:* En pruebas preliminares con usuarios, se observó que el botón de eliminar producto se pulsaba ocasionalmente por error al hacer scroll.
- *Solución:* Se implementó el componente modal `ConfirmDeleteDialog`, exigiendo confirmación explícita y cumpliendo con las heurísticas de usabilidad móvil.

---

# 10. ANEXOS

### 10.1 Capturas Adicionales de la Aplicación

Para el informe formal en Word/PDF se adjunta el álbum completo de capturas del emulador:
1. **Flujo de Entrada y Registro:** `captura_login.png` demostrando validaciones y selector de rol.
2. **Despensa Reactiva:** `evidencia_1_pantalla_principal.png` con métricas superiores y lista de alimentos.
3. **Formulario Modal Asistido:** `evidencia_2_registro_datos.png` con datepicker y validación activa.
4. **Semáforo Visual de Riesgo:** `evidencia_3_listado_datos_semaforo.png` ilustrando los badges de color.
5. **Edición y Confirmación:** `evidencia_4_edicion_registros.png` con diálogo modal de borrado.
6. **Consumo de Servicio Web:** `evidencia_5_consumo_api_rest.png` con resultados obtenidos de OpenFoodFacts.

### 10.2 Evidencias de Pruebas y Compilación
- **Resultado de Compilación:** `BUILD SUCCESSFUL in 12s` (Android Gradle Plugin 8.7 / Kotlin 2.0).
- **Pruebas de Compatibilidad:** Verificado en emuladores Pixel 7 con Android 11 (API 30), Android 13 (API 33) y Android 15 (API 35).

### 10.3 Enlace Oficial al Repositorio de GitHub
* **URL Pública:** [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)
* **Historial de Commits:** Más de 17 commits semánticos auditables.
* **Pull Requests Fusionados:**
  - Pull Request #1: `feature/shopping-list-alerts` ➔ `main` (Merged)
  - Pull Request #2: `feature/retrofit-api-consumption` ➔ `main` (Merged)
  - Pull Request #3: `feature/room-crud-pantry` ➔ `main` (Merged)

### 10.4 Enlace al Tablero de Gestión Ágil (Kanban / Jira / Trello)
* **Tablero Oficial del Proyecto:** [DispenSave Scrum Board en GitHub Projects / Jira](https://github.com/users/maykelcb/projects/1)
* **Métricas de Gestión:** 4 Sprints completados, 16 Historias de Usuario verificadas con criterios Gherkin y 100% de tareas en columna *Done*.
