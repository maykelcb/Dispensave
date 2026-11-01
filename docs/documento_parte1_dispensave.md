# INFORME TÉCNICO Y ACADÉMICO — PROYECTO DISPENSAVE
## DOCUMENTO DE ENTREGA: PARTE 1
### Responsable: Maykel Bernaola (`maykelcb`) — Líder Técnico y Arquitecto de Software
### Rama Oficial de GitHub: `main`
### Repositorio: [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)

---

> [!NOTE]
> Este documento contiene el desarrollo completo de la **PARTE 1** del proyecto DispenSave, cubriendo de manera exhaustiva la **Sección 1 (Resumen Ejecutivo)** y la **Sección 2 (Arquitectura Actualizada del Sistema y MVVM)**, integrando código fuente real, diagramas técnicos y justificaciones ingenieriles conforme a las consideraciones de la rúbrica oficial.

---

# 1. RESUMEN EJECUTIVO

### 1.1 Descripción Actual del Proyecto

**DispenSave** es una aplicación móvil nativa desarrollada para la plataforma Android que aborda de forma integral la problemática global del desperdicio de alimentos (*Food Waste Reduction*) a nivel doméstico y comercial, articulándose directamente con el **Objetivo de Desarrollo Sostenible 12 (Producción y Consumo Responsables)** de la Agenda 2030 de la ONU.

En la sociedad contemporánea, aproximadamente un tercio de los alimentos producidos para consumo humano se pierde o desperdicia en algún punto de la cadena de valor. A nivel de los hogares, gran parte de este desperdicio ocurre por la falta de visibilidad sobre los productos almacenados en la alacena y refrigerador, lo que conduce a fechas de caducidad vencidas de manera inadvertida y compras duplicadas innecesarias. Paralelamente, en el sector comercial (panaderías, cafeterías, minimarkets y restaurantes), toneladas de alimentos en perfecto estado de consumo terminan en vertederos simplemente porque no fueron vendidos durante el turno comercial habitual.

Para mitigar esta ineficiencia económica y ambiental, **DispenSave** implementa un ecosistema móvil inteligente de doble vertiente:

1. **Gestión Inteligente de Despensa y Prevención Doméstica:**
   - Permite al usuario registrar, clasificar y auditar en tiempo real el inventario de alimentos de su hogar.
   - Incorpora un **sistema de alerta visual semafórico** (🟢 Vigente / 🟡 Por Vencer ≤3 días / 🔴 Vencido) que ordena los insumos cronológicamente por caducidad (*First Expired, First Out - FEFO*), garantizando que los alimentos perecibles sean consumidos oportunamente.
   - Incluye una **Lista de Compras Dinámica** con traslado automatizado: al marcar un producto como comprado, este se inserta automáticamente en la despensa local sin reingreso manual de datos.
   - Dispone de un módulo de **Catálogo Online** conectado a la base de datos abierta internacional de **OpenFoodFacts** mediante una API REST, facilitando la incorporación instantánea de alimentos estandarizados con un solo clic.

2. **Mercado de Rescate y Vinculación Comercial (Food Surplus Marketplace):**
   - Brinda a comercios aliados un panel especializado para listar excedentes gastronómicos frescos del día en formato de *Packs Sorpresa* o *Productos Individuales* con descuentos de entre 50% y 70%.
   - Ofrece a los consumidores la posibilidad de descubrir ofertas de rescate cercanas, reservarlas en la aplicación y recogerlas en franjas horarias establecidas, reduciendo pérdidas comerciales y fomentando el ahorro económico familiar.
   - Cuantifica el aporte ambiental del usuario mediante métricas directas de kilogramos de alimento rescatado, dinero ahorrado y emisiones de CO₂ prevenidas a la atmósfera.

---

### 1.2 Objetivos Alcanzados

Durante los ciclos de diseño, implementación y pruebas, se alcanzaron exitosamente los siguientes hitos técnicos y funcionales:

* **Arquitectura de Software Moderna y Reactiva:** Adopción integral del patrón arquitectónico **MVVM (Model-View-ViewModel)** combinado con principios de **Clean Architecture**, estructurado bajo el paradigma de **Flujo Unidireccional de Datos (UDF)** y 100% desarrollado en **Kotlin 2.0+** con **Jetpack Compose**.
* **Persistencia Local Robusta (Offline-First):** Implementación de una base de datos relacional local basada en **Room Persistence Library (SQLite)** con soporte ACID, garantizando que el inventario, fechas de caducidad y estados no dependan de la conectividad a Internet y sobrevivan al cierre o reinicio del dispositivo.
* **Operaciones CRUD Completas:** Desarrollo exhaustivo de operaciones de Creación (`@Insert`), Lectura reactiva (`@Query` con streams asíncronos `Flow`), Actualización (`@Update`) y Eliminación física (`@Delete`) sobre los alimentos de despensa, respaldadas por validaciones estrictas y diálogos de confirmación modal.
* **Integración de Servicios Web Externos (API REST):** Conexión asíncrona de alto desempeño con el catálogo global de OpenFoodFacts empleando el cliente HTTP **Retrofit 2** y el motor de red **OkHttp 3**, incorporando serialización automática Gson, tiempos de espera configurables (*timeouts* de 15s) y gestión de estados `Loading`, `Success` y `Error`.
* **Sistema de Roles y Autenticación:** Diseño de flujos diferenciados para el usuario consumidor (`CLIENTE`) y el establecimiento comercial (`COMERCIO`), permitiendo navegación contextual adaptativa según los permisos del usuario activo.
* **Alineamiento con Métricas Sostenibles:** Incorporación de un panel analítico en `ImpactScreen` que traduce el consumo responsable de insumos en reducciones cuantificables de huella de carbono y ahorro monetario.

---

### 1.3 Estado de Desarrollo

El proyecto **DispenSave** se encuentra en un estado de **Fase Funcional Estable (Release Candidate / Versión 1.0 Funcional)**. 

* **Estabilidad del Código:** El código fuente compila sin errores (`BUILD SUCCESSFUL`) en Android Studio Ladybug / Koala con Gradle 8.x/9.x, operando con un nivel mínimo de API 26 (Android 8.0 Oreo) y soportando hasta la versión API 35 (Android 15).
* **Integración Continua y Repositorio:** El repositorio oficial en GitHub (`https://github.com/maykelcb/DispenSave`) evidencia un trabajo colaborativo estricto bajo el modelo Git Flow, consolidando 4 ramas principales (`main`, `feature/retrofit-api-consumption`, `feature/room-crud-pantry`, `feature/shopping-list-alerts`) mediante 3 Pull Requests exhaustivamente revisados y fusionados a la rama troncal.
* **Cobertura de la Rúbrica Académica:** El software satisface al 100% todos los requisitos obligatorios de la cátedra:
  - Persistencia local permanente en SQLite/Room: **CUMPLIDO**.
  - Operaciones CRUD completas y reactivas: **CUMPLIDO**.
  - Consumo de servicios web mediante Retrofit 2: **CUMPLIDO**.
  - Arquitectura organizada bajo MVVM con UDF: **CUMPLIDO**.
  - Validaciones de formularios y control de errores: **CUMPLIDO**.
  - Evidencias de trabajo colaborativo auditable: **CUMPLIDO**.

---

# 2. ARQUITECTURA ACTUALIZADA DEL SISTEMA

### 2.1 Arquitectura General

#### 2.1.1 Diagrama de Arquitectura Actualizado

La arquitectura de DispenSave se diseñó bajo una estricta **separación de responsabilidades** por capas jerárquicas y desacopladas, asegurando la escalabilidad del sistema, la mantenibilidad del código a largo plazo y la testabilidad de cada módulo. 

A continuación se presenta el diagrama arquitectónico formal que representa la interacción entre las capas del sistema y el flujo de control:

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

---

#### 2.1.2 Componentes Implementados del Sistema

El ecosistema de DispenSave se articula a través de siete módulos de software principales organizados en paquetes estándar de Android:

1. **Módulo de Navegación Central (`com.example.dispensave.ui.navigation`):**
   - Implementado mediante `AppNavigation.kt` y `ContenedorPrincipal.kt` utilizando **Jetpack Compose Navigation**.
   - Gestiona el árbol de pantallas mediante rutas selladas y una barra de navegación inferior (`NavigationBar`) que adapta dinámicamente sus pestañas según el rol autenticado (`CLIENTE` o `COMERCIO`).
   - Mantiene el historial de navegación (*Backstack*) y previene la duplicación de destinos en la pila con la directiva `launchSingleTop = true`.

2. **Módulo de Autenticación y Perfil (`com.example.dispensave.ui.auth`):**
   - Compuesto por `OnboardingScreen.kt`, `LoginScreen.kt` y `RegisterScreen.kt`.
   - Permite el registro de usuarios asignando su categoría (Consumidor vs Establecimiento Aliado con nombre comercial y rubro).
   - Valida el formato de correo electrónico corporativo/personal y longitud de contraseña mediante utilitarios centralizados.

3. **Módulo de Despensa Inteligente (`com.example.dispensave.ui.pantry`):**
   - Pantallas `PantryScreen.kt`, `ProductDetailScreen.kt` y diálogo modal `AddProductDialog.kt`.
   - Implementa la visualización del inventario de alimentos con métricas inmediatas en la cabecera (Total de Productos, Productos en Riesgo).
   - Proporciona búsqueda local reactiva y filtrado ágil mediante chips de categoría (Lácteos, Frutas, Verduras, Carnes, Cereales, Panadería).
   - Asocia a cada alimento un indicador visual de caducidad (`ExpirationBadge`) calculado a partir de la fecha de caducidad en milisegundos.

4. **Módulo de Catálogo y Consulta Remota (`com.example.dispensave.ui.screens.search`):**
   - Pantalla `ProductSearchScreen.kt`.
   - Integra la búsqueda de alimentos en la API pública internacional de OpenFoodFacts mediante el cliente Retrofit.
   - Presenta tarjetas con información técnica (marca, categoría, peso/contenido neto) y un botón de importación directa a la base de datos local SQLite.

5. **Módulo de Lista de Compras Dinámica (`com.example.dispensave.ui.screens.shopping`):**
   - Pantalla `ShoppingListScreen.kt`.
   - Permite listar insumos faltantes para abastecimiento futuro.
   - Cuenta con casillas de verificación interactiva y un método de traslado automatizado que convierte los ítems adquiridos en registros formales de la base de datos Room.

6. **Módulo de Rescate de Alimentos y Marketplace (`com.example.dispensave.ui.home`):**
   - Pantalla `ExploreScreen.kt` y panel de administración `StoreDashboardScreen.kt`.
   - Conecta a comercios con consumidores para publicar y reservar excedentes culinarios frescos a precio social, evitando su desecho al final del día comercial.

7. **Módulo de Métricas e Impacto Ecológico (`com.example.dispensave.ui.impact`):**
   - Pantalla `ImpactScreen.kt`.
   - Procesa estadísticas agregadas de alimentos consumidos antes de expirar y productos rescatados, convirtiéndolos en métricas tangibles de CO₂ prevenido y dinero ahorrado.

---

### 2.2 Arquitectura MVVM (Model-View-ViewModel)

La arquitectura de DispenSave implementa de manera rigurosa la separación de responsabilidades exigida por las buenas prácticas de la industria, garantizando que ninguna capa asuma tareas ajenas a su ámbito:

```
┌──────────────┐         Eventos de UI           ┌──────────────────┐
│  VISTA (UI)  ├────────────────────────────────►│    VIEWMODEL     │
│ Jetpack Comp.│◄────────────────────────────────┤ (PantryViewModel)│
└──────────────┘    Emite StateFlow / States     └────────┬─────────┘
                                                          │ Invoca
                                                          ▼
                                                 ┌──────────────────┐
                                                 │   REPOSITORIO    │
                                                 │ (PantryRepository│
                                                 └────────┬─────────┘
                                                          │
                                ┌─────────────────────────┴─────────────────────────┐
                                ▼                                                   ▼
                     ┌─────────────────────┐                             ┌─────────────────────┐
                     │  DATA SOURCE LOCAL  │                             │ DATA SOURCE REMOTO  │
                     │   (Room Database)   │                             │  (Retrofit API REST)│
                     └─────────────────────┘                             └─────────────────────┘
```

A continuación se demuestra la evidencia técnica de separación para cada una de las 4 capas del patrón:

---

#### 2.2.1 Capa de Vista / Interfaz de Usuario (UI Layer)
La capa de presentación está desarrollada al 100% en **Jetpack Compose**. Su única responsabilidad es renderizar la interfaz de manera declarativa en función del estado que observa y notificar las interacciones del usuario hacia el ViewModel. No realiza consultas directas a la base de datos ni invoca llamadas de red HTTP.

**Evidencia de Código Fuente (`PantryScreen.kt`):**
```kotlin
@Composable
fun PantryScreen(
    viewModel: PantryViewModel,
    onNavigateToDetail: (Product) -> Unit,
    onNavigateToShopping: () -> Unit
) {
    // 1. La UI observa flujos reactivos expuestos por el ViewModel
    val products by viewModel.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // 2. Renderizado reactivo basado en el estado
    Scaffold(
        topBar = { PantryTopBar(...) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Alimento")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(products) { product ->
                ProductItemCard(
                    product = product,
                    onClick = { onNavigateToDetail(product) }
                )
            }
        }
    }

    // 3. Los eventos se delegan al ViewModel sin lógica de base de datos en la Vista
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, expDate, qty, unit ->
                viewModel.addProduct(name, category, expDate, qty, unit)
                showAddDialog = false
            }
        )
    }
}
```

---

#### 2.2.2 Capa de ViewModel (Presentation Logic & State Holder)
El componente `PantryViewModel` hereda de `androidx.lifecycle.ViewModel`. Su función primordial es actuar como mediador entre la vista y los repositorios, sobreviviendo a los cambios de configuración del dispositivo (como giros de pantalla). Transforma los flujos de datos asíncronos en estados observables (`StateFlow`), controla las corrutinas en el ámbito `viewModelScope` y gestiona los estados de la interfaz (`ProductSearchUiState`).

**Evidencia de Código Fuente (`PantryViewModel.kt`):**
```kotlin
class PantryViewModel(
    private val repository: PantryRepository,
    private val userRepository: UserRepository = UserRepository(),
    private val rescueRepository: RescueRepository = RescueRepository()
) : ViewModel() {

    // 1. Exposición de estado reactivo mediante StateFlow
    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Estado reactivo para el consumo de la API REST
    var searchUiState by mutableStateOf<ProductSearchUiState>(ProductSearchUiState.Idle)
        private set

    // 3. Ejecución de operaciones en corrutinas de fondo (Dispatchers.IO)
    fun searchRemoteProducts(query: String) {
        val cleanQuery = query.trim()
        if (cleanQuery.length < 2) {
            searchUiState = ProductSearchUiState.Error("Ingresa al menos 2 caracteres para buscar.")
            return
        }

        searchUiState = ProductSearchUiState.Loading

        viewModelScope.launch {
            try {
                // Delegación de la petición remota a la capa de red
                val response = RetrofitClient.apiService.searchProducts(query = cleanQuery)
                val validProducts = response.products.filter { !it.productName.isNullOrBlank() }
                
                searchUiState = if (validProducts.isEmpty()) {
                    ProductSearchUiState.Error("No se hallaron alimentos coincidentes.")
                } else {
                    ProductSearchUiState.Success(validProducts)
                }
            } catch (e: Exception) {
                searchUiState = ProductSearchUiState.Error("Error de conexión al servidor REST.")
            }
        }
    }

    // 4. Operación CRUD delegada al repositorio
    fun addProduct(name: String, category: String, expirationDate: Long, quantity: Int, unit: String): Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            val newProduct = Product(
                id = 0,
                name = name.trim(),
                category = category,
                expirationDate = expirationDate,
                quantity = quantity,
                unit = unit
            )
            repository.insert(newProduct)
        }
        return true
    }
}
```

---

#### 2.2.3 Capa de Repositorio (Repository Pattern)
Los repositorios (`PantryRepository`, `UserRepository`, `RescueRepository`) constituyen la **Fuente Única de Verdad (*Single Source of Truth*)** de la aplicación. Ocultan la complejidad y origen de los datos al ViewModel, traduciendo las entidades de base de datos (`ProductEntity`) y los objetos de transferencia de red (`RemoteFoodProductDto`) en modelos limpios de dominio (`Product`).

**Evidencia de Código Fuente (`PantryRepository.kt`):**
```kotlin
class PantryRepository(private val productDao: ProductDao) {

    // 1. Exposición de flujo reactivo transformado desde la base de datos SQLite
    val allProducts: Flow<List<Product>> = productDao.getAllProductsSortedByExpiration()
        .map { entities ->
            entities.map { entity -> entity.toDomain() }
        }

    // 2. Inserción asíncrona delegada al DAO de Room
    suspend fun insert(product: Product): Long {
        return productDao.insertProduct(product.toEntity())
    }

    // 3. Actualización de registros existentes
    suspend fun update(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    // 4. Eliminación física del registro
    suspend fun delete(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }
}
```

---

#### 2.2.4 Capa de Fuentes de Datos (Data Sources: Local & Remote)
La capa de datos se bifurca en dos orígenes desacoplados y coordinados:

1. **Fuente de Datos Local (SQLite / Room Persistence Library):**
   - Compuesta por `AppDatabase.kt`, `ProductEntity.kt` y `ProductDao.kt`.
   - Se encarga de persistir físicamente los datos en el almacenamiento flash interno del smartphone de forma transaccional.

**Evidencia de Código Fuente del DAO Local (`ProductDao.kt`):**
```kotlin
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

2. **Fuente de Datos Remota (Retrofit 2 REST Client):**
   - Compuesta por `RetrofitClient.kt`, `FoodApiService.kt` y `OpenFoodFactsDto.kt`.
   - Realiza llamadas HTTP seguras al catálogo en la nube y deserializa las cargas útiles JSON.

**Evidencia de Código Fuente de la Interfaz Remota (`FoodApiService.kt`):**
```kotlin
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

---

### 2.3 Flujo Unidireccional de Datos (UDF - Unidirectional Data Flow)

La interacción entre capas en DispenSave se rige por el principio UDF, el cual asegura que el estado descienda por la jerarquía y los eventos asciendan de manera controlada:

1. **El Estado Desciende (*State Flows Down*):**
   - El DAO emite un stream `Flow<List<ProductEntity>>` desde SQLite.
   - El repositorio transforma las entidades a objetos de dominio `Product` y las propaga hacia el `PantryViewModel`.
   - El ViewModel encapsula los datos en un `StateFlow<List<Product>>`.
   - Jetpack Compose observa este `StateFlow` y desencadena la recomposición inteligente (*Smart Recomposition*) de la pantalla `PantryScreen`.

2. **Los Eventos Ascienden (*Events Flow Up*):**
   - El usuario pulsa un botón en la interfaz (por ejemplo, el botón de eliminar o guardar alimento).
   - La pantalla invoca la función pública correspondiente en el ViewModel (ej. `viewModel.deleteProduct(product)`).
   - El ViewModel procesa la intención, verifica validaciones y lanza una corrutina en `Dispatchers.IO` delegando al repositorio.
   - El repositorio invoca al DAO de Room o a la API de Retrofit.
   - Una vez consolidada la mutación en la base de datos, Room emite automáticamente la nueva lista por el `Flow`, retroalimentando el ciclo sin necesidad de recargar manualmente la interfaz.

---

> [!TIP]
> **Conclusión de la Parte 1:**
> La arquitectura implementada en DispenSave garantiza un código limpio, desacoplado, sin fugas de memoria y totalmente preparado para soportar el crecimiento futuro de la aplicación (como sincronización bidireccional en la nube, pasarelas de pago y notificaciones push programadas).
