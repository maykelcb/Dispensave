> [!IMPORTANT]
> ### 📋 SECCIÓN DEL INFORME ACADÉMICO — RUBÉN (Parte 3 - Room SQLite CRUD):
> **Secciones redactadas:**
> 1. **Sección 4.3: Diseño de Base de Datos** — Modelo ER, Diccionario de Datos y justificación de Room/SQLite.
> 2. **Sección V.1: Historias de Usuario HU09 a HU12** — Persistencia permanente, ordenamiento por caducidad, edición y eliminación.
> 3. **Sección V.2: Sprint 3** — Objetivo, actividades y tablero Kanban.
> 4. **Sección VI.3: Evidencias** — Código Room, DAOs y capturas de persistencia.

---

# Parte 3 — Persistencia Local SQLite con Room Database
**Responsable:** Rubén  
**Rama Oficial en GitHub:** `feature/room-crud-pantry`  
**Estado:** **IMPLEMENTADO Y SUBIDO A GITHUB** (Commits en `feature/room-crud-pantry`)

---

## IV. DISEÑO DE LA SOLUCIÓN

### 4.3 Diseño de Base de Datos Local (SQLite con Room)

#### 4.3.1 Justificación de Room / SQLite

DispenSave utiliza **Room Database**, la librería oficial de persistencia de Android que actúa como capa de abstracción sobre SQLite. Room fue seleccionado por tres razones técnicas fundamentales:

1. **Integración nativa con Kotlin y Coroutines:** Room soporta `Flow<List<T>>` de manera nativa, lo que permite que la pantalla de despensa se actualice reactivamente en tiempo real cada vez que cambia la base de datos, sin necesidad de polling manual.
2. **Verificación en tiempo de compilación:** A diferencia del SQLite raw, Room valida las consultas SQL en la fase de compilación del proyecto, reduciendo el riesgo de errores en tiempo de ejecución.
3. **Separación de capas (Clean Architecture):** Room trabaja en la capa `data/local/`, y los modelos de dominio (`Product`) se mantienen limpios y desacoplados de la implementación de la base de datos a través de funciones de conversión (`toProduct()` / `toEntity()`) en `PantryRepository.kt`.

#### 4.3.2 Modelo Entidad-Relación (ER)

```
┌─────────────────────────────────────────┐
│           TABLA: products               │
├─────────────┬────────────┬──────────────┤
│ Columna     │ Tipo SQL   │ Restricción  │
├─────────────┼────────────┼──────────────┤
│ id          │ INTEGER    │ PK AUTO INC  │
│ userId      │ TEXT       │ DEFAULT      │
│ name        │ TEXT       │ NOT NULL     │
│ category    │ TEXT       │ NOT NULL     │
│ expirationDate │ INTEGER │ NOT NULL     │
│ quantity    │ INTEGER    │ NOT NULL     │
│ unit        │ TEXT       │ NOT NULL     │
│ status      │ TEXT       │ DEFAULT      │
└─────────────┴────────────┴──────────────┘
```

#### 4.3.3 Diccionario de Datos

| Campo | Tipo | Descripción |
|:------|:-----|:-----------|
| `id` | `Int (PK)` | Identificador único autoincremental. Room lo asigna automáticamente al insertar. |
| `userId` | `String` | Identificador del usuario propietario del producto. Valor por defecto: `"user_default"`. |
| `name` | `String` | Nombre descriptivo del alimento (ej: "Pechuga de Pollo San Fernando"). |
| `category` | `String` | Categoría del alimento (Carnes, Lácteos, Abarrotes, Frescos, Bebidas). |
| `expirationDate` | `Long` | Fecha de caducidad en formato Unix timestamp (milisegundos). Se convierte a `dd/MM/yyyy` para la UI. |
| `quantity` | `Int` | Cantidad disponible en el inventario de despensa del usuario. |
| `unit` | `String` | Unidad de medida (kg, litros, unidades, botella, paquete, etc.). |
| `status` | `String` | Estado semafórico calculado: `"Disponible"`, `"Por Vencer"`, `"Vencido"`. |

---

## V. HISTORIAS DE USUARIO — Parte 3 (HU09 – HU12)

### 5.1 Historias de Usuario

| ID | Historia de Usuario | Criterio de Aceptación | Sprint | Estado |
|:---|:--------------------|:----------------------|:------:|:------:|
| **HU09** | Como usuario cliente, quiero que los alimentos que agrego a mi despensa se guarden permanentemente en mi dispositivo, para que no se pierdan cuando cierro la aplicación. | Los productos persisten en la base de datos SQLite después de cerrar y reabrir la app. Room inserta en la tabla `products` con `insertProduct()`. | Sprint 3 | ✅ |
| **HU10** | Como usuario cliente, quiero ver mis alimentos ordenados por fecha de caducidad (del más próximo al más lejano), para priorizar el consumo de los que vencen antes. | La pantalla muestra los productos en orden ascendente de `expirationDate`. La consulta Room usa `ORDER BY expirationDate ASC`. | Sprint 3 | ✅ |
| **HU11** | Como usuario cliente, quiero poder editar la cantidad y demás datos de un alimento existente en mi despensa, para mantener actualizado mi inventario. | El usuario puede modificar un producto y los cambios se reflejan en la base de datos con `updateProduct()`. | Sprint 3 | ✅ |
| **HU12** | Como usuario cliente, quiero eliminar definitivamente un alimento de mi despensa cuando ya lo consumí o lo deseché, para mantener un inventario limpio. | El producto se borra permanentemente de SQLite con `deleteProduct()`. Un diálogo de confirmación previene borrados accidentales. | Sprint 3 | ✅ |

### 5.2 Sprint 3 — Persistencia Room SQLite

**Sprint Goal:** Conectar la despensa virtual con almacenamiento local SQLite real mediante Room Database, garantizando que todos los datos persistan entre sesiones de la aplicación.

**WBS de Actividades:**

| Tarea | Descripción | Estado |
|:------|:-----------|:------:|
| T3.1 | Revisar y completar `ProductEntity.kt` con todos los campos necesarios | ✅ |
| T3.2 | Completar `ProductDao.kt` con CRUD completo y ordenamiento por caducidad | ✅ |
| T3.3 | Actualizar `AppDatabase.kt` con singleton thread-safe y `fallbackToDestructiveMigration()` | ✅ |
| T3.4 | Reescribir `PantryRepository.kt` para usar `ProductDao` en lugar de listas en memoria | ✅ |
| T3.5 | Actualizar `MainActivity.kt` para inyectar `AppDatabase` y `PantryRepository` con Room | ✅ |
| T3.6 | Agregar KSP (Kotlin Symbol Processing) + `room-compiler` al `build.gradle.kts` | ✅ |
| T3.7 | Verificar compilación `BUILD SUCCESSFUL` con Room integrado | ✅ |

---

## VI.3 EVIDENCIAS DE IMPLEMENTACIÓN — Parte 3 (Rubén)

### Código de Entidad Room (`ProductEntity.kt`)

```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "user_default",
    val name: String,
    val category: String,
    val expirationDate: Long,
    val quantity: Int,
    val unit: String,
    val status: String = "Disponible"
)
```

### Código del DAO (`ProductDao.kt`)

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

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)
}
```

### Código del Repositorio con Room (`PantryRepository.kt`)

```kotlin
class PantryRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<Product>> = productDao
        .getAllProductsSortedByExpiration()
        .map { entities -> entities.map { it.toProduct() } }

    suspend fun addProduct(product: Product): Long {
        return productDao.insertProduct(product.toEntity())
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }
}
```

### Inyección de Dependencias en `MainActivity.kt`

```kotlin
val database = AppDatabase.getDatabase(this)
val productDao = database.productDao()
val pantryRepository = PantryRepository(productDao)
```

### Dependencias Gradle Configuradas

```kotlin
// build.gradle.kts (app)
val roomVersion = "2.6.1"
implementation("androidx.room:room-runtime:$roomVersion")
implementation("androidx.room:room-ktx:$roomVersion")
ksp("androidx.room:room-compiler:$roomVersion")
```

---

*DispenSave — Parte 3: Persistencia Local SQLite con Room Database*
*Responsable: Rubén — Rama: `feature/room-crud-pantry`*
