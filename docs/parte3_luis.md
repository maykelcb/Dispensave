# GUÍA Y PLANTILLA OFICIAL — PROYECTO DISPENSAVE
## DOCUMENTO DE ENTREGA: PARTE 3
### Responsable Asignado: Persona 3 (Luis / Desarrollador Backend Local & Geolocalización)
### Rama Oficial de GitHub: `feature/room-crud-pantry`
### Repositorio: [https://github.com/maykelcb/DispenSave](https://github.com/maykelcb/DispenSave)

---

> [!IMPORTANT]
> ### 📋 SECCIONES DEL INFORME ACADÉMICO ASIGNADAS A LA PARTE 3:
> **1. Sección 4: Implementación de Operaciones CRUD** (Persistencia con Room Database SQLite, DAO, Create/Read/Update/Delete, evidencias funcionales, casos de prueba y gestión de estados Loading/Success/Error).  
> **2. Sección 6: Geolocalización (Opcional / Módulo de Rescate)** (Google Maps SDK, obtención de ubicación, visualización de mapa, marcadores de comercios aliados, permisos `ACCESS_FINE_LOCATION` y evidencias).  
> 
> *Tu código en Android Studio se ubica en:* `data/local/` (`AppDatabase.kt`, `ProductDao.kt`, `ProductEntity.kt`), `data/repository/PantryRepository.kt`, `ui/pantry/` y `ui/home/ExploreScreen.kt`.

---

# 4. IMPLEMENTACIÓN DE OPERACIONES CRUD

### 4.0 Descripción de la Persistencia Local

Para dar estricto cumplimiento a la consideración obligatoria:
> *"Persistencia local: Datos almacenados mediante Room, SQLite o DataStore. CRUD: Al menos una entidad completamente operativa."*

DispenSave implementa la librería oficial **Room Persistence Library (v2.6.1)**, la cual proporciona una capa de abstracción fluida y fuertemente tipada sobre el motor relacional **SQLite nativo de Android**.

**Estructura de la Entidad Principal (`ProductEntity.kt`):**
```kotlin
package com.example.dispensave.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dispensave.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "user_default",
    val name: String,
    val category: String,
    val expirationDate: Long, // Timestamp en milisegundos para orden numérico eficiente
    val quantity: Int,
    val unit: String,
    val status: String = "Disponible"
) {
    fun toDomain(): Product = Product(
        id = id,
        name = name,
        category = category,
        expirationDate = expirationDate,
        quantity = quantity,
        unit = unit
    )
}
```

---

### 4.1 Operaciones Desarrolladas (CRUD)

Todas las operaciones sobre la tabla `products` están declaradas en la interfaz `@Dao` (`ProductDao.kt`) y orquestadas mediante corrutinas y flujos reactivos (`Flow`):

```kotlin
package com.example.dispensave.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // 1. READ: Obtención de todos los alimentos ordenados cronológicamente por caducidad (FEFO)
    @Query("SELECT * FROM products ORDER BY expirationDate ASC")
    fun getAllProductsSortedByExpiration(): Flow<List<ProductEntity>>

    // 2. CREATE: Inserción de un nuevo producto registrado o importado desde la API REST
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    // 3. UPDATE: Actualización de stock, unidad o fecha de caducidad de un producto
    @Update
    suspend fun updateProduct(product: ProductEntity)

    // 4. DELETE: Eliminación física y permanente del alimento de la despensa
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
```

#### Detalle Técnico por Operación:
1. **CREATE (Inserción):**
   - El usuario abre el diálogo modal `AddProductDialog` o pulsa "Añadir a Despensa" desde la búsqueda de OpenFoodFacts o la Lista de Compras.
   - El ViewModel invoca `repository.insert(product)`.
   - Room genera una sentencia `INSERT INTO products (name, category, expirationDate, quantity, unit, status) VALUES (...)` asignando un ID autonumérico único.
2. **READ (Lectura Reactiva en Tiempo Real):**
   - La consulta `SELECT * FROM products ORDER BY expirationDate ASC` retorna un stream asíncrono `Flow<List<ProductEntity>>`.
   - Cuando cualquier fila de la tabla sufre una alteración (inserción, cambio o borrado), Room notifica automáticamente al `PantryViewModel`, y este recompone la pantalla `PantryScreen` instantáneamente.
3. **UPDATE (Actualización):**
   - Desde `ProductDetailScreen`, el usuario ajusta el contador de unidades o reprograma la fecha de vencimiento.
   - Room ejecuta `UPDATE products SET quantity = ?, expirationDate = ? WHERE id = ?`.
4. **DELETE (Eliminación Segura):**
   - El usuario pulsa el icono de cesto de basura. Para evitar eliminaciones accidentales, se despliega el componente modal `ConfirmDeleteDialog`. Al confirmar, Room despacha `DELETE FROM products WHERE id = ?`.

---

### 4.2 Evidencias Funcionales

#### 4.2.1 Capturas de Ejecución
Para la versión final en Word/PDF se incluyen las capturas tomadas del emulador Android:

| Captura de Pantalla | Módulo / Operación | Elementos Visuales Evidenciados |
| :--- | :--- | :--- |
| **Evidencia 1** (`docs/imagenes/evidencia_1_pantalla_principal.png`) | **Read** (Pantalla Principal) | Lista de alimentos recuperados de Room SQLite ordenados por fecha, métricas de resumen superior (Total y En Riesgo). |
| **Evidencia 2** (`docs/imagenes/evidencia_2_registro_datos.png`) | **Create** (Formulario Modal) | Diálogo `AddProductDialog` con campos de nombre, categoría desplegable, selector de fecha y validación del botón "Guardar". |
| **Evidencia 3** (`docs/imagenes/evidencia_3_listado_datos_semaforo.png`) | **Read & Semáforo** | Badges de caducidad en colores: Rojo (Vencido), Amarillo (Por Vencer ≤3d) y Verde (Vigente). |
| **Evidencia 4** (`docs/imagenes/evidencia_4_edicion_registros.png`) | **Update & Delete** | Pantalla `ProductDetailScreen` con modificación de cantidad y diálogo de confirmación `ConfirmDeleteDialog`. |

#### 4.2.2 Casos de Prueba Realizados

A continuación se detalla la matriz de pruebas funcionales ejecutadas sobre el motor CRUD de persistencia SQLite:

| ID Caso | Operación | Datos de Entrada / Acción | Resultado Esperado | Resultado Obtenido | Estado |
| :---: | :---: | :--- | :--- | :--- | :---: |
| **CP01** | **Create** | Alimento: *"Leche Gloria 1L"*, Cat: *"Lácteos"*, Vence: Hoy + 5 días, Cant: 2. | Inserción en tabla `products`, asignación de ID autonumérico, actualización de UI. | Producto listado con badge verde y stock 2. | **EXITOSO (PASSED)** |
| **CP02** | **Read** | Apertura de `PantryScreen` con 5 productos en base de datos. | Listado ordenado cronológicamente según timestamp ascendente (`expirationDate ASC`). | Alimentos más próximos a caducar aparecen al inicio. | **EXITOSO (PASSED)** |
| **CP03** | **Update** | Modificación en `ProductDetailScreen`: cambiar cantidad de 2 a 5 unidades. | Sentencia `@Update` ejecuta cambio en SQLite, reflejándose inmediatamente en la despensa. | Cantidad actualizada a 5 uds en la lista principal. | **EXITOSO (PASSED)** |
| **CP04** | **Delete** | Presionar eliminar sobre *"Yogurt Fresa"* y pulsar "Confirmar Eliminación". | Sentencia `@Delete` borra el registro de SQLite y el ítem desaparece del `LazyColumn`. | Registro removido permanentemente de la base de datos. | **EXITOSO (PASSED)** |
| **CP05** | **Persistencia** | Cerrar la aplicación desde el gestor de tareas de Android y reabrir. | Los datos no se borran al destruir la `Activity` ni al reiniciar el proceso. | Todos los registros se conservan intactos. | **EXITOSO (PASSED)** |
| **CP06** | **Validación** | Intentar guardar alimento con nombre en blanco o cantidad en cero. | El botón "Guardar" permanece deshabilitado previniendo filas corruptas. | Validación reactiva bloquea el envío erróneo. | **EXITOSO (PASSED)** |

---

### 4.3 Gestión de Estados

Para garantizar una experiencia de usuario responsiva sin congelamientos de pantalla, el flujo de datos maneja tres estados fundamentales:

1. **Estado de Carga (`Loading`):**
   - Mientras Room compila y abre la conexión SQLite o cuando se ejecuta una consulta inicial, se proyecta un indicador de progreso (`CircularProgressIndicator`) o un esqueleto visual (*Shimmer Placeholder*).
2. **Estado Exitoso (`Success`):**
   - Una vez que el stream reactivo `Flow` emite la lista de entidades, Jetpack Compose renderiza de inmediato las tarjetas interactivas de alimentos (`ProductItemCard`) con su semáforo de caducidad.
3. **Estado de Error (`Error`):**
   - Si se produce una anomalía en la lectura/escritura (por ejemplo, espacio insuficiente en disco o bloqueo de base de datos), el sistema captura la excepción y notifica mediante un `Snackbar` no invasivo con opción de reintento.

---

# 6. GEOLOCALIZACIÓN (MÓDULO OPCIONAL / RESCATE DE COMERCIOS)

### 6.1 Implementación de Google Maps

Para potenciar el mercado de rescate de comida (*Food Surplus Marketplace*) y facilitar que los usuarios localicen comercios aliados cercanos que ofrecen *Packs Sorpresa* con hasta 70% de descuento, se diseñó la integración con el **Google Maps SDK for Android** y la librería declarativa **Google Maps Compose**.

#### 6.1.1 Configuración de la API y Dependencias
1. **Registro en Google Cloud Console:** Se habilita la API *Maps SDK for Android* y se asocia la clave con la huella digital SHA-1 del certificado de depuración del proyecto (`debug.keystore`).
2. **Declaración en `build.gradle.kts`:**
   ```kotlin
   implementation("com.google.maps.android:maps-compose:4.3.3")
   implementation("com.google.android.gms:play-services-maps:18.2.0")
   implementation("com.google.android.gms:play-services-location:21.2.0")
   ```
3. **Configuración en `AndroidManifest.xml`:**
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${MAPS_API_KEY}" />
   ```

---

### 6.2 Funcionalidades Implementadas

1. **Obtención de Ubicación Actual del Usuario:**
   - Empleo de `FusedLocationProviderClient` para determinar la posición geográfica actual (Latitud y Longitud) con balance óptimo de precisión y consumo energético.
2. **Visualización de Mapas Interactivos:**
   - Componente `GoogleMap` con soporte para gestos táctiles (zoom, desplazamiento y rotación), centrado en la posición del usuario.
3. **Marcadores de Comercios Aliados (*Markers*):**
   - Se sitúan pines geográficos que representan restaurantes, panaderías y cafeterías aliadas a DispenSave (ej. *"Panadería La Baguette"*, *"Pastelería San Antonio"*, *"EcoMarket Miraflores"*).
   - Al pulsar un marcador, se despliega una ventana informativa (*InfoWindow*) detallando:
     - Nombre comercial del establecimiento.
     - Cantidad de Packs Sorpresa disponibles hoy.
     - Ventana horaria de recogida (ej. *"20:00 - 21:00 hrs"*).
     - Botón directo para reservar.
4. **Gestión de Permisos en Tiempo de Ejecución:**
   - Solicitud dinámica de permisos `ACCESS_FINE_LOCATION` (GPS de alta precisión) y `ACCESS_COARSE_LOCATION` (redes celulares/Wi-Fi), garantizando que si el usuario deniega el permiso, la aplicación continúe operando con un catálogo en modo lista.

---

### 6.3 Evidencias y Pruebas Realizadas

* **Pruebas de Concesión de Permisos:** Se verificó el flujo en emuladores con Android 14/15, comprobando que al aceptar el diálogo *"Permitir mientras la app está en uso"*, la cámara del mapa se traslada automáticamente hacia las coordenadas del usuario.
* **Pruebas de Marcadores Interactivos:** Se validó que al pulsar un marcador de comercio aliado, se filtre la lista inferior mostrando los packs de rescate específicos de ese establecimiento.
