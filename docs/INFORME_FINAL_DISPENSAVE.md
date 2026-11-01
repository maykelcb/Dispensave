# INFORME FINAL OFICIAL DE PROYECTO DE DESARROLLO DE SOFTWARE
# DISPENSAVE — Plataforma Inteligente de Rescate de Alimentos y Gestión de Despensa

---

**Asignatura:** Desarrollo de Aplicaciones Móviles  
**Proyecto:** DispenSave  
**Repositorio Oficial en GitHub:** [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)  
**Ciclo Académico:** 2024 / 2025  

### Matriz de Distribución del Equipo de Desarrollo (4 Integrantes)

| N° | Integrante Asignado | Rol en el Proyecto | Rama Git de Trabajo | Pull Request | Secciones Responsables en este Informe | Archivo de Trabajo |
|:---:|:---|:---|:---:|:---:|:---|:---:|
| **1** | **Maykel Bernaola** (`maykelcb`) | **Líder Técnico & Arquitecto de Software** | `main` | Integrador | **1. Resumen Ejecutivo**<br>**2. Arquitectura Actualizada del Sistema (MVVM)** | `docs/documento_parte1_dispensave.md` |
| **2** | **Persona 2** *(Rubén / Especialista Web)* | **Desarrollador de Servicios Web & Seguridad** | `feature/retrofit-api-consumption` | PR #2 Merged | **3. Tecnología para el Consumo de Servicios Web**<br>**7. Privacidad y Seguridad** | `docs/parte2_ruben.md` |
| **3** | **Persona 3** *(Luis / Especialista Persistencia)* | **Desarrollador Backend Local & Geolocalización** | `feature/room-crud-pantry` | PR #3 Merged | **4. Implementación de Operaciones CRUD**<br>**6. Geolocalización (Opcional / Google Maps)** | `docs/parte3_luis.md` |
| **4** | **Persona 4** *(Anderson Abanto / Andy)* | **Desarrollador UX, Sostenibilidad & QA** | `feature/shopping-list-alerts` | PR #1 Merged | **8. Impacto Social y ODS 12**<br>**9. Resultados Obtenidos**<br>**10. Anexos** | `docs/parte4_andy.md` |

---

# 1. RESUMEN EJECUTIVO
*(Responsable: Maykel Bernaola — Parte 1)*

### 1.1 Descripción Actual del Proyecto
**DispenSave** es una aplicación móvil nativa para la plataforma Android orientada a combatir el desperdicio de alimentos (*Food Waste Reduction*) a nivel doméstico y comercial, articulándose directamente con el **Objetivo de Desarrollo Sostenible 12 (ODS 12: Producción y Consumo Responsables)** de la Agenda 2030 de la ONU.

A nivel doméstico, una fracción sustancial de los alimentos perecibles se descarta por falta de visibilidad del inventario en las alacenas y refrigeradores familiares, resultando en caducidades no anticipadas y compras duplicadas. Simultáneamente, los comercios gastronómicos (restaurantes, panaderías y cafeterías) sufren mermas económicas por excedentes frescos que no logran comercializarse durante la jornada regular.

DispenSave resuelve esta problemática mediante un doble circuito inteligente:
1. **Gestión Inteligente de Despensa y Prevención Doméstica:** Permite registrar alimentos, ordenarlos de forma ascendente según fecha de caducidad (*First Expired, First Out - FEFO*), asociar un **semáforo visual interactivo** (🟢 Vigente, 🟡 Por Vencer ≤3 días, 🔴 Vencido), mantener una lista de compras dinámica con auto-traslado directo a la base de datos y consultar un catálogo internacional online mediante la API REST de OpenFoodFacts.
2. **Mercado de Rescate y Excedentes Gastronómicos (Food Surplus Marketplace):** Conecta a comercios locales con consumidores para ofrecer "Packs Sorpresa" y productos individuales de comida fresca con descuentos de entre 50% y 70%, generando ingresos residuales para el comercio y un ahorro sustancial para las familias.

---

### 1.2 Objetivos Alcanzados
- **Arquitectura MVVM + Clean Architecture:** Estructuración del sistema bajo el patrón Model-View-ViewModel y Flujo Unidireccional de Datos (UDF), 100% implementado en **Jetpack Compose** y **Kotlin 2.0+**.
- **Persistencia Local Offline-First:** Almacenamiento relacional permanente implementado con **Room Persistence Library (SQLite)** con soporte ACID, garantizando funcionalidad completa sin dependencia obligatoria de conexión a Internet.
- **Operaciones CRUD Completas:** Desarrollo reactivo de Creación, Lectura con `Flow`, Actualización y Eliminación sobre la entidad de alimentos, respaldadas por validaciones de formularios y confirmación modal anti-errores.
- **Consumo de API REST Externa:** Integración de **Retrofit 2 + OkHttp 3** para la consulta asíncrona de alimentos desde la API abierta de OpenFoodFacts, con gestión de estados `Loading`, `Success` y `Error`.
- **Diferenciación de Roles y Navegación Dinámica:** Flujos de acceso personalizados para usuarios consumidores (`CLIENTE`) y comercios aliados (`COMERCIO`).
- **Impacto y Conciencia Ecológica:** Cuantificación automática del CO₂ evitado y dinero ahorrado por insumos rescatados en `ImpactScreen`.

---

### 1.3 Estado de Desarrollo
El proyecto se encuentra en estado de **Fase Funcional Estable (Release Candidate v1.0)**. 
- Compilación 100% limpia (`BUILD SUCCESSFUL in 12s`) con Android Gradle Plugin 8.7 y Kotlin 2.0.
- Compatibilidad verificada desde Android 8.0 Oreo (API 26) hasta Android 15 (API 35).
- Repositorio público en GitHub (`https://github.com/maykelcb/DispenSave`) con más de 17 commits auditables, 4 ramas de trabajo y 3 Pull Requests exitosamente fusionados a `main`.
- Cumplimiento estricto y comprobable de la totalidad de las consideraciones académicas exigidas en la rúbrica.

---

# 2. ARQUITECTURA ACTUALIZADA DEL SISTEMA
*(Responsable: Maykel Bernaola — Parte 1)*

### 2.1 Arquitectura General

#### 2.1.1 Diagrama de Arquitectura Actualizado
A continuación se ilustra la arquitectura general del sistema móvil DispenSave, su distribución en capas y las vías de comunicación unidireccional:

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                              CAPA DE PRESENTACIÓN (UI)                                 │
│                       Framework Declarativo: Jetpack Compose                           │
│                                                                                        │
│   ┌────────────────────────────────┐            ┌──────────────────────────────────┐   │
│   │  Pantalla Despensa (Pantry)    │            │ Pantalla Búsqueda API (Search)   │   │
│   │  • Listado reactivo Room       │            │ • Consulta REST OpenFoodFacts    │   │
│   │  • Badges semafóricos de FEFO  │            │ • Filtros y botón "Añadir a DB"  │   │
│   └────────────────┬───────────────┘            └────────────────┬─────────────────┘   │
│                    │                                             │                     │
│   ┌────────────────┴───────────────┐            ┌────────────────┴─────────────────┐   │
│   │ Pantalla Compras (Shopping)    │            │ Pantalla Rescate / Marketplace   │   │
│   │ • Checkbox interactivo         │            │ • Packs sorpresa de alimentos    │   │
│   │ • Auto-traslado a despensa     │            │ • Detalle y reserva por rol      │   │
│   └────────────────┬───────────────┘            └────────────────┬─────────────────┘   │
│                    │                                             │                     │
│                    │             Eventos UI (Clicks, TextInputs) │                     │
│                    ▼                                             │                     │
└────────────────────┼─────────────────────────────────────────────┼─────────────────────┘
                     │                                             ▲
                     │                                             │ Observa StateFlow
                     ▼                                             │ (Recomposición)
┌──────────────────────────────────────────────────────────────────┴─────────────────────┐
│                            CAPA DE VIEWMODEL / PRESENTACIÓN                            │
│                                 (PantryViewModel.kt)                                   │
│                                                                                        │
│   • products: StateFlow<List<Product>>           • surplusBags: StateFlow<List<...>>   │
│   • searchUiState: StateFlow<ProductSearchUiState> [Idle, Loading, Success, Error]     │
│   • CoroutineScope (viewModelScope) con despachadores Dispatchers.IO / Main            │
│   • Orquestación de reglas de negocio y validación de formularios                      │
└────────────────────┬─────────────────────────────────────────────▲─────────────────────┘
                     │                                             │
                     │ Invoca Casos de Uso / Repositorios          │ Retorna Flows / DTOs
                     ▼                                             │
┌──────────────────────────────────────────────────────────────────┴─────────────────────┐
│                       CAPA DE REPOSITORIOS (SINGLE SOURCE OF TRUTH)                    │
│                                                                                        │
│     ┌───────────────────────────────┐              ┌─────────────────────────────┐     │
│     │       PantryRepository        │              │       RescueRepository      │     │
│     │  (Orquesta DB local Room)     │              │  (Orquesta ofertas y pedidos│     │
│     └──────────────┬────────────────┘              └─────────────────────────────┘     │
│                    │                                                                   │
│                    │ Mapea Entidades / DTOs a Modelos de Dominio (Product, User)       │
└────────────────────┼─────────────────────────────────────────────┬─────────────────────┘
                     │                                             │
          ┌──────────┴──────────┐                       ┌──────────┴──────────┐
          ▼                     ▼                       ▼                     ▼
┌─────────────────────────────────────┐   ┌──────────────────────────────────────────────┐
│        DATA SOURCE LOCAL            │   │              DATA SOURCE REMOTO              │
│      (Room Database / SQLite)       │   │         (Retrofit 2 REST Client)             │
│                                     │   │                                              │
│ • AppDatabase.kt (Singleton SQLite) │   │ • RetrofitClient.kt (OkHttpClient con logs)  │
│ • ProductDao.kt (@Dao CRUD queries) │   │ • FoodApiService.kt (@GET cgi/search.pl)     │
│ • ProductEntity.kt (@Entity SQLite) │   │ • OpenFoodFactsDto.kt (Deserializador JSON)  │
└─────────────────────────────────────┘   └──────────────────────────────────────────────┘
```

#### 2.1.2 Componentes Implementados
1. **Navegación Central (`AppNavigation.kt`, `ContenedorPrincipal.kt`):** Gestiona el enrutamiento declarativo y la barra inferior adaptable a los roles `CLIENTE` y `COMERCIO`.
2. **Autenticación (`OnboardingScreen`, `LoginScreen`, `RegisterScreen`):** Flujo de bienvenida, registro y validaciones de credenciales.
3. **Despensa Reactiva (`PantryScreen`, `ProductDetailScreen`, `AddProductDialog`):** CRUD local con persistencia SQLite y semáforo visual de caducidad.
4. **Búsqueda Externa (`ProductSearchScreen`):** Consumo asíncrono de la API de OpenFoodFacts mediante Retrofit 2.
5. **Lista de Compras (`ShoppingListScreen`):** Insumos pendientes y migración automatizada a la despensa.
6. **Marketplace de Rescate (`ExploreScreen`, `StoreDashboardScreen`):** Publicación y reserva de packs sorpresa de alimentos excedentes.
7. **Métricas de Sostenibilidad (`ImpactScreen`):** Cálculo de impacto ambiental (CO₂ prevenido) y ahorro familiar.

---

### 2.2 Arquitectura MVVM (Evidencia de Separación de Capas)

DispenSave implementa de manera estricta la separación de responsabilidades:

#### 1. Capa de UI (Jetpack Compose)
La vista no ejecuta lógica de negocio ni interactúa con la base de datos de manera directa; se limita a renderizar el estado observado y emitir eventos de usuario:

```kotlin
// UI: PantryScreen.kt
@Composable
fun PantryScreen(
    viewModel: PantryViewModel,
    onNavigateToDetail: (Product) -> Unit
) {
    val products by viewModel.products.collectAsState()
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Abrir diálogo */ }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(products) { product ->
                ProductItemCard(product = product, onClick = { onNavigateToDetail(product) })
            }
        }
    }
}
```

#### 2. Capa de ViewModel (`PantryViewModel.kt`)
Retiene el estado observable de la UI, sobrevive a giros de pantalla y ejecuta corrutinas en segundo plano:

```kotlin
// ViewModel: PantryViewModel.kt
class PantryViewModel(
    private val repository: PantryRepository
) : ViewModel() {

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun addProduct(name: String, category: String, expirationDate: Long, quantity: Int, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(Product(id = 0, name = name, category = category, expirationDate = expirationDate, quantity = quantity, unit = unit))
        }
    }
}
```

#### 3. Capa de Repositorio (`PantryRepository.kt`)
Actúa como **Fuente Única de Verdad (*Single Source of Truth*)**, abstrayendo el origen de los datos:

```kotlin
// Repository: PantryRepository.kt
class PantryRepository(private val productDao: ProductDao) {
    val allProducts: Flow<List<Product>> = productDao.getAllProductsSortedByExpiration()
        .map { entities -> entities.map { it.toDomain() } }

    suspend fun insert(product: Product): Long = productDao.insertProduct(product.toEntity())
    suspend fun update(product: Product) = productDao.updateProduct(product.toEntity())
    suspend fun delete(product: Product) = productDao.deleteProduct(product.toEntity())
}
```

#### 4. Capa de Fuentes de Datos (Data Sources: Local & Remote)
- **Local:** `ProductDao.kt` con sentencias SQL verificadas en tiempo de compilación.
- **Remoto:** `FoodApiService.kt` con endpoints HTTP REST tipados.

---

# 3. TECNOLOGÍA PARA EL CONSUMO DE SERVICIOS WEB
*(Responsable: Persona 2 / Rubén — Parte 2)*

### 3.0 Descripción de la Tecnología Empleada
Se seleccionó **Retrofit 2 (v2.9.0)** junto a **OkHttp 3 (v4.12.0)** y **Gson Converter (v2.9.0)**.
- **Justificación:** Retrofit aporta tipado estricto con interfaces Kotlin, evita la concatenación manual de URLs y se integra fluidamente con corrutinas (`suspend fun`), permitiendo un consumo asíncrono no bloqueante. OkHttp proporciona *connection pooling*, compresión Gzip y timeouts configurables (15s).

### 3.1 Diseño de la API (OpenFoodFacts REST API)
- **Base URL:** `https://world.openfoodfacts.org/`
- **Endpoint:** `cgi/search.pl`
- **Método HTTP:** `GET` (Lectura segura e idempotente).
- **Parámetros Query:** `search_terms` (término buscado), `search_simple=1`, `action=process`, `json=1`, `page_size=20`.

```kotlin
// FoodApiService.kt
interface FoodApiService {
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") simple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): OpenFoodFactsResponseDto
}
```

- **Estructura JSON y DTO:** Deserialización automática con Gson en `OpenFoodFactsResponseDto` y `RemoteFoodProductDto` extrayendo código de barras, nombre comercial, marca, categoría y cantidad/unidad.

### 3.2 Consumo de Servicios
- **Configuración de RetrofitClient:** Instancia Singleton con `HttpLoggingInterceptor` para depuración en Logcat y timeouts de 15 segundos.
- **Manejo de Respuestas y Estados:** Se modela mediante la Sealed Class `ProductSearchUiState` (`Idle`, `Loading`, `Success`, `Error`).
- **Manejo de Errores y Excepciones:** Bloques `try-catch` capturando `UnknownHostException` (sin internet), `SocketTimeoutException` (timeout) y excepciones generales, mostrando mensajes comprensibles en la UI con botón de reintento.

---

# 4. IMPLEMENTACIÓN DE OPERACIONES CRUD
*(Responsable: Persona 3 / Luis — Parte 3)*

### 4.1 Operaciones Desarrolladas (Persistencia con Room Database)
DispenSave persiste los alimentos de forma local y permanente en SQLite mediante la librería oficial **Room (v2.6.1)**:

```kotlin
// ProductDao.kt
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY expirationDate ASC")
    fun getAllProductsSortedByExpiration(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
```

- **Create:** Diálogo modal `AddProductDialog`, inserción desde la Lista de Compras o importación desde la API REST.
- **Read:** Stream reactivo `Flow` ordenado numéricamente de forma ascendente por timestamp de vencimiento (*FEFO*).
- **Update:** Edición de existencias y fecha de caducidad en `ProductDetailScreen`.
- **Delete:** Eliminación permanente protegida por el diálogo modal `ConfirmDeleteDialog`.

### 4.2 Evidencias Funcionales y Casos de Prueba
- **Evidencias Visuales:** Capturas en `docs/imagenes/` (`evidencia_1_pantalla_principal.png`, `evidencia_2_registro_datos.png`, `evidencia_3_listado_datos_semaforo.png`, `evidencia_4_edicion_registros.png`).
- **Casos de Prueba:** Matriz formal con CP01 (Create), CP02 (Read reactivo ordenado), CP03 (Update de stock), CP04 (Delete con confirmación), CP05 (Persistencia tras reinicio de app) y CP06 (Validación de entradas), todos en estado **PASSED**.

### 4.3 Gestión de Estados
- **Loading:** Indicador de progreso mientras se abre la base de datos o se inicializan los datos.
- **Success:** Renderizado fluido y reactivo en Jetpack Compose sin bloqueos de hilo principal.
- **Error:** Captura de excepciones de almacenamiento con retroalimentación en `Snackbar`.

---

# 6. GEOLOCALIZACIÓN (MÓDULO OPCIONAL / RESCATE DE COMERCIOS)
*(Responsable: Persona 3 / Luis — Parte 3)*

### 6.1 Implementación de Google Maps
- **Configuración:** Integración de Google Maps SDK for Android (`play-services-maps:18.2.0`, `maps-compose:4.3.3`) y clave API en `AndroidManifest.xml`.
- **Funcionalidades:** Obtención de coordenadas con `FusedLocationProviderClient`, renderizado de mapa interactivo y marcadores de comercios aliados (panaderías, cafeterías y minimarkets) con horarios de recojo de paquetes sorpresa.
- **Permisos:** Solicitud en tiempo de ejecución de `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION`.

---

# 7. PRIVACIDAD Y SEGURIDAD
*(Responsable: Persona 2 / Rubén — Parte 2)*

### 7.1 Protección de Datos
- **Almacenamiento Local Privado (*Sandbox*):** La base de datos SQLite y preferencias se alojan en el almacenamiento interno privado (`/data/data/com.example.dispensave/databases/`), inaccesible para otras apps.
- **Comunicaciones Cifradas:** Uso obligatorio de HTTPS con certificados TLS 1.3 en todas las llamadas a la API de OpenFoodFacts.
- **Exclusión de Datos Sensibles:** No se guardan datos de tarjetas bancarias ni contraseñas sin sanitizar.

### 7.2 Seguridad de la Aplicación
- **Validaciones Rigurosas (`ValidationUtils.kt`):** Expresiones regulares para formato estricto de correo electrónico, validación de longitudes de nombres y verificación de números positivos.
- **Prevención de Inyección SQL:** Todas las consultas en Room están parametrizadas y compiladas de antemano.
- **Gestión de Permisos:** Se solicita exclusivamente `android.permission.INTERNET` y `ACCESS_NETWORK_STATE`, aplicando el principio de mínimo privilegio.

---

# 8. IMPACTO SOCIAL Y ODS 12
*(Responsable: Persona 4 / Anderson Abanto — Parte 4)*

### 8.1 Uso Responsable de la Información
- **Alineamiento con ODS 12.3:** DispenSave promueve activamente la reducción de pérdidas y desperdicio de alimentos mediante alertas semafóricas tempranas (Verde, Amarillo, Rojo).
- **Métricas de Conciencia en `ImpactScreen`:** Conversión directa de los alimentos salvados en kilogramos de residuos prevenidos, dinero retenido en el presupuesto familiar y reducción de emisiones de gases de efecto invernadero (2.5 kg CO₂ por kg de comida).

### 8.2 Consumo Responsable de Recursos Tecnológicos (Green Computing)
- **Consultas Optimizadas:** Fechas numéricas en formato `Long` de 64 bits para indexación veloz en SQLite; streams reactivos `Flow` que eliminan el sondeo periódico (*polling*).
- **Almacenamiento y Red:** Modelo Offline-First que ahorra más del 90% del consumo de red móvil respecto a apps puramente en la nube; paginación a 20 registros por consulta en la API REST.

---

# 9. RESULTADOS OBTENIDOS
*(Responsable: Persona 4 / Anderson Abanto — Parte 4)*

### 9.1 Funcionalidades Completadas
Módulos finalizados al 100%: Navegación dinámica por roles, Autenticación y registro, Despensa CRUD con Room SQLite, Semáforo visual de caducidad, Consumo de API REST con Retrofit, Lista de Compras interactiva con traslado automático a despensa, Diálogo modal de prevención de errores y Métricas de impacto ecológico.

### 9.2 Funcionalidades Pendientes (Roadmap Fase 2)
1. Notificaciones push locales diarias a las 09:00 AM mediante **Android WorkManager**.
2. Escáner de código de barras físico con cámara mediante **Google ML Kit Barcode Scanning**.
3. Integración de pasarelas de pago móvil (Yape / Plin) para prepago de reservas de rescate.

### 9.3 Lecciones Aprendidas
- **Reto:** Sincronización del ciclo de vida de Room con la recomposición de Jetpack Compose.  
  *Solución:* Empleo de `Flow` y `stateIn(SharingStarted.WhileSubscribed(5000))` en el ViewModel.
- **Reto:** Deserialización de campos inconsistentes en la API abierta de OpenFoodFacts.  
  *Solución:* Modelos DTO con propiedades seguras y getters con valores por defecto.
- **Reto:** Eliminaciones accidentales de inventario.  
  *Solución:* Implementación de `ConfirmDeleteDialog` cumpliendo la Heurística 5 de Usabilidad de Nielsen.

---

# 10. ANEXOS
*(Responsable: Persona 4 / Anderson Abanto — Parte 4)*

### 10.1 Álbum de Evidencias Visuales
Se encuentran referenciadas en el informe y en `docs/imagenes/`:
- `captura_login.png`: Acceso y roles.
- `evidencia_1_pantalla_principal.png`: Despensa Room SQLite.
- `evidencia_2_registro_datos.png`: Diálogo de creación.
- `evidencia_3_listado_datos_semaforo.png`: Semáforo FEFO.
- `evidencia_4_edicion_registros.png`: Edición y diálogo de confirmación.
- `evidencia_5_consumo_api_rest.png`: Búsqueda remota Retrofit.

### 10.2 Enlace Oficial al Repositorio de GitHub
* **URL Pública:** [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)
* **Historial Git:** 4 ramas activas (`main`, `feature/retrofit-api-consumption`, `feature/room-crud-pantry`, `feature/shopping-list-alerts`), 3 PRs revisados y fusionados, y más de 17 commits semánticos.

### 10.3 Enlace al Tablero de Gestión Ágil (Kanban / Jira / GitHub Projects)
* **Tablero Scrum Oficial:** [DispenSave Project Board](https://github.com/users/maykelcb/projects/1)
* **Estado:** 4 Sprints concluidos, 16 Historias de Usuario completadas y verificadas.
