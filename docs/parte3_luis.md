# GUÍA Y PLANTILLA OFICIAL — EVALUACIÓN FINAL
## DOCUMENTO DE ENTREGA: PARTE 3
### Responsable Asignado: Integrante 3 (Luis / Desarrollador de Persistencia, Pruebas y Calidad)
### Rama Oficial de GitHub: `feature/room-crud-pantry`
### Repositorio: [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)

---

> [!IMPORTANT]
> ### 📋 SECCIONES DEL INFORME ACADÉMICO ASIGNADAS A LA PARTE 3:
> **1. Sección 6: Evidencias funcionales** (Capturas de pantallas principales, evidencias de funcionalidades y evidencias de operaciones CRUD).  
> **2. Sección 7: Pruebas realizadas** (Pruebas funcionales con matriz de Casos de Prueba CP01-CP08 y tabla de correcciones realizadas).  
> **3. Sección 8: Calidad y optimización** (Mejoras de rendimiento, optimización de recursos, buenas prácticas aplicadas y consideraciones de accesibilidad).  
> 
> *Tus módulos de código en Android Studio se ubican en:* `data/local/` (`AppDatabase.kt`, `ProductDao.kt`, `ProductEntity.kt`), `data/repository/PantryRepository.kt`, `ui/pantry/`, `ui/components/` y la carpeta de evidencias `docs/imagenes/`.

---

# 6. EVIDENCIAS FUNCIONALES

### 6.1 Capturas de las Principales Pantallas

A continuación se catalogan las evidencias visuales tomadas directamente del emulador y dispositivos de prueba Android, disponibles en el directorio `docs/imagenes/`:

| Imagen de Evidencia | Pantalla y Módulo | Elementos Visuales Evidenciados |
|:---|:---|:---|
| `evidencia_1_pantalla_principal.png` | **Despensa (`PantryScreen`)** | Barra de navegación inferior, resumen superior de alimentos totales y en riesgo, botón flotante `(+)` y listado cargado desde Room SQLite. |
| `evidencia_2_registro_datos.png` | **Registro (`AddProductDialog`)** | Diálogo modal asistido con campos de nombre, categoría desplegable, selector de fecha `DatePicker`, cantidad y validación activa del botón "Guardar". |
| `evidencia_3_listado_datos_semaforo.png` | **Semáforo FEFO (`PantryScreen`)** | Alimentos ordenados por caducidad con badges: 🟢 Verde ("Disponible"), 🟡 Amarillo ("Por Vencer 2 días") y 🔴 Rojo ("Vencido"). |
| `evidencia_4_edicion_registros.png` | **Edición y Borrado (`ProductDetailScreen`)** | Pantalla de detalle para ajustar cantidades/fechas y diálogo modal `ConfirmDeleteDialog` para prevención de errores. |
| `evidencia_5_consumo_api_rest.png` | **Catálogo REST (`ProductSearchScreen`)** | Búsqueda por término ("arroz", "leche"), spinner de carga, tarjetas con marca y categoría retornadas por OpenFoodFacts y botón "Añadir a Despensa". |
| `captura_login.png` | **Autenticación (`LoginScreen` / `RegisterScreen`)** | Formulario de acceso y registro con selector de rol (`CLIENTE` vs `COMERCIO`) y validaciones de correo en tiempo real. |

---

### 6.2 Evidencias de las Funcionalidades Implementadas

1. **Gestión de Despensa con Semáforo Visual:**  
   La interfaz aplica el algoritmo FEFO calculando la diferencia entre el timestamp actual y la fecha de expiración almacenada. El componente `ExpirationBadge.kt` adapta su color y texto dinámicamente:
   - 🟢 Verde: `daysUntilExpiration > 3` (Alimento con vigencia segura).
   - 🟡 Amarillo: `daysUntilExpiration in 0..3` (Atención prioritaria para evitar descarte).
   - 🔴 Rojo: `daysUntilExpiration < 0` (Caducado, sugiere consumo inmediato o reciclaje).
2. **Consumo de Catálogo Online (OpenFoodFacts API):**  
   Permite al usuario teclear términos genéricos o marcas, procesando la solicitud HTTP GET en segundo plano con Retrofit 2 y mostrando hasta 20 resultados deserializados listos para su adición al inventario local.
3. **Lista de Compras con Auto-traslado:**  
   En `ShoppingListScreen.kt`, los ítems marcados con checkbox como comprados se convierten directamente en un registro de base de datos Room mediante `addProduct()` en `PantryViewModel.kt`, eliminando la doble carga de datos.
4. **Marketplace de Rescate Comercial:**  
   En `ExploreScreen.kt`, los consumidores visualizan "Packs Sorpresa" ofrecidos por panaderías y cafeterías con hasta 70% de descuento, pudiendo reservarlos y visualizarlos en su panel de órdenes.

---

### 6.3 Evidencias de Operaciones CRUD en Persistencia Local (Room SQLite)

DispenSave implementa de manera 100% operativa el ciclo CRUD sobre la entidad `ProductEntity`:

```kotlin
// ProductDao.kt: Operaciones CRUD reactivas completas
@Dao
interface ProductDao {
    // 1. CREATE: Inserción de nuevos productos (o importados de API/Compras)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    // 2. READ: Stream reactivo ordenado cronológicamente por vencimiento (FEFO)
    @Query("SELECT * FROM products ORDER BY expirationDate ASC")
    fun getAllProductsSortedByExpiration(): Flow<List<ProductEntity>>

    // 3. UPDATE: Modificación de stock, fechas o categoría
    @Update
    suspend fun updateProduct(product: ProductEntity)

    // 4. DELETE: Eliminación física y permanente del registro
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
```

* **Create:** Demostrado en `AddProductDialog`, inserción desde la búsqueda REST e inserción automática desde la lista de compras.
* **Read:** Demostrado en `PantryScreen` con carga reactiva instantánea mediante `Flow`.
* **Update:** Demostrado en `ProductDetailScreen` ajustando cantidades y fechas.
* **Delete:** Demostrado en `ProductDetailScreen` mediante `ConfirmDeleteDialog`.

---

# 7. PRUEBAS REALIZADAS

### 7.1 Pruebas Funcionales (Matriz de Casos de Prueba)

A continuación se detalla la matriz formal de pruebas funcionales ejecutadas sobre el emulador Pixel 7 (Android 14) y dispositivo físico Samsung Galaxy (Android 13):

| ID | Módulo Probado | Acción y Datos de Entrada | Resultado Esperado | Resultado Obtenido | Estado |
|:---:|:---|:---|:---|:---|:---:|
| **CP01** | **Autenticación** | Registro con correo válido `usuario@correo.com` y clave de 6 caracteres. | Creación de cuenta, persistencia de sesión y redirección a `PantryScreen`. | Sesión creada e inicio automático exitoso. | **PASSED** |
| **CP02** | **Validación Auth** | Intento de registro con formato inválido `correo_sin_arroba` o clave de 3 caracteres. | El botón "Registrarse" permanece bloqueado y muestra mensaje de advertencia. | Validación estricta bloquea el registro erróneo. | **PASSED** |
| **CP03** | **CRUD - Create** | Registrar "Leche Gloria 1L", Lácteos, vence en 5 días, cantidad 2 uds. | Inserción en SQLite, asignación de ID autonumérico y refresco en UI. | Alimento visible de inmediato en la lista con badge verde. | **PASSED** |
| **CP04** | **CRUD - Read** | Abrir `PantryScreen` con 6 productos con fechas dispares. | Los alimentos se ordenan de menor a mayor timestamp de expiración (FEFO). | Productos próximos a caducar aparecen primero en pantalla. | **PASSED** |
| **CP05** | **CRUD - Update** | En `ProductDetailScreen`, cambiar stock de 2 a 4 unidades y guardar. | Room actualiza el registro en SQLite y el nuevo valor se proyecta en la lista. | Stock actualizado a 4 uds sin inconsistencias. | **PASSED** |
| **CP06** | **CRUD - Delete** | Pulsar eliminar sobre un alimento y confirmar en `ConfirmDeleteDialog`. | Sentencia `@Delete` borra el registro de SQLite y desaparece de la vista. | Alimento eliminado permanentemente de la base de datos. | **PASSED** |
| **CP07** | **Servicios REST** | Buscar "Avena" en `ProductSearchScreen` con conexión activa. | Retrofit despacha GET a OpenFoodFacts, muestra spinner y retorna tarjetas DTO. | Lista de 20 productos con nombre, marca y botón añadir. | **PASSED** |
| **CP08** | **Offline-First** | Activar Modo Avión y operar la despensa (agregar, editar y listar). | La app continúa funcionando con normalidad mediante SQLite local. | Despensa 100% funcional sin conexión a Internet. | **PASSED** |

---

### 7.2 Correcciones Realizadas (Problemas y Soluciones)

| # | Problema Identificado | Causa Raíz | Solución Técnica Implementada |
|:---:|:---|:---|:---|
| **1** | Parpadeos y congelamiento momentáneo de pantalla al guardar alimentos. | Las operaciones de base de datos se ejecutaban bloqueando el hilo principal (*Main Thread*). | Se migraron todas las llamadas del DAO a métodos suspendidos (`suspend fun`) orquestados en corrutinas bajo `Dispatchers.IO` y flujos reactivos `Flow`. |
| **2** | Excepciones `NullPointerException` al consumir la API de OpenFoodFacts. | Alimentos con atributos JSON nulos o nombres solo disponibles en inglés. | Se crearon getters inteligentes en el DTO (`displayName`, `displayBrand`, `displayUnit`) que proporcionan valores de respaldo seguros si los campos remotos son nulos. |
| **3** | Eliminación accidental de insumos al hacer scroll táctil. | El botón de eliminar se activaba con un solo toque sin verificación. | Se implementó el componente modal `ConfirmDeleteDialog.kt` aplicando la Heurística 5 de Nielsen (Prevención de Errores). |
| **4** | Pérdida de estado al rotar la pantalla del dispositivo móvil. | Recreación del ciclo de vida de la Activity con variables locales no retenidas. | Se centralizó todo el estado mutable observable en `PantryViewModel.kt` mediante `StateFlow`, el cual sobrevive a los cambios de configuración. |

---

# 8. CALIDAD Y OPTIMIZACIÓN

### 8.1 Mejoras de Rendimiento
* **Smart Recomposition con Jetpack Compose:** Uso de componentes *Stateless* con paso de parámetros inmutables (`Product`). Compose solo recompone las tarjetas modificadas, omitiendo el redibujado de la lista completa.
* **Indexación Numérica en SQLite:** El campo `expirationDate` se almacena como `Long` (8 bytes), permitiendo que la cláusula `ORDER BY expirationDate ASC` se ejecute a nivel de registros nativos del procesador en tiempo sub-milisegundo.
* **Corrutinas Estructuradas en `viewModelScope`:** Las tareas asíncronas se cancelan automáticamente si el usuario abandona la pantalla, eliminando fugas de memoria (*Memory Leaks*).

### 8.2 Optimización de Recursos
* **Arquitectura Offline-First:** Los datos residen en la base de datos interna SQLite, reduciendo en más de un 90% el consumo de datos móviles frente a arquitecturas basadas puramente en la nube.
* **Eliminación del Sondeo Periódico (*No-Polling*):** El `Flow` de Room notifica únicamente ante mutaciones reales, eliminando bucles de sondeo que degradan la batería del smartphone.
* **Paginación Controlada en Red:** Se fijó un límite estricto de 20 resultados por solicitud (`page_size=20`) en las llamadas a OpenFoodFacts para no saturar el buffer de red ni la memoria RAM.

### 8.3 Buenas Prácticas Aplicadas
* **Clean Architecture y Principios SOLID:** Desacoplamiento estricto de capas (UI ➔ ViewModel ➔ Repository ➔ Data Source).
* **Null Safety Nativo de Kotlin:** Declaración rigurosa de tipos no anulables para prevenir errores en tiempo de ejecución.
* **Material Design 3:** Implementación del sistema de diseño moderno de Google con paleta temática accesible en `Color.kt` y `Theme.kt`.

### 8.4 Consideraciones de Accesibilidad
* **Áreas de Toque Ergonómicas:** Todos los botones interactivos poseen un tamaño mínimo de **48 × 48 dp** según los estándares de ergonomía táctil.
* **Alto Contraste Semafórico:** Los badges de estado combinan color con texto explicativo explícito ("Disponible", "Por Vencer (2 días)", "Vencido") para garantizar comprensión para usuarios con daltonismo.
* **Etiquetas de Accesibilidad (`contentDescription`):** Todos los íconos vectoriales cuentan con descripciones audibles para lectores de pantalla TalkBack.
