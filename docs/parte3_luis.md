> [!IMPORTANT]
> ### 📋 SECCIÓN DEL INFORME ACADÉMICO ASIGNADA A LUIS:
> **Secciones a redactar en el informe para Word:**
> 1. **Sección 4.3: Diseño de Base de Datos** (Modelo Entidad-Relación, Diccionario de datos de usuarios, alimentos, pedidos e impacto, y justificación de Room/SQLite).
> 2. **Sección V.1: Historias de Usuario HU09 a HU12** en formato tabla (Persistencia local permanente, orden cronológico de caducidad, edición y eliminación de stock).
> 3. **Sección V.2: Sprint 3 de Desarrollo** (Objetivo, funcionalidades y actividades ejecutadas).
> 4. **Sección VI.3: Evidencias de Implementación** (Código de entidades Room, DAOs, base de datos y capturas de persistencia de datos).
>
> *Para ver la asignación completa del equipo, revisa [DISTRIBUCION_INFORME_EQUIPO.md](DISTRIBUCION_INFORME_EQUIPO.md).*

---

# Parte 3 — Persistencia Local SQLite con Room Database
**Responsable:** Luis  
**Rama Oficial en GitHub:** `feature/room-crud-pantry`  
**Comando para trabajar:** `git checkout feature/room-crud-pantry`

---

## Objetivo General
Cumplir con el **requisito obligatorio de persistencia local y operaciones CRUD**:
> *"La aplicación deberá demostrar como mínimo: Persistencia local de datos, Operaciones CRUD. Restricciones: No se aceptarán proyectos sin persistencia de datos."*

Actualmente el repositorio cuenta con `AppDatabase.kt` y `ProductDao.kt` en `data/local/`, pero `PantryRepository.kt` almacena los datos en listas en memoria temporal. Tu trabajo técnico es **conectar la base de datos Room SQLite al repositorio** para que los alimentos se guarden de forma permanente en el almacenamiento del dispositivo y no se pierdan al cerrar la aplicación.

---

## Archivos que debes crear / modificar

| Archivo | Ubicación | Qué hacer |
|---|---|---|
| `ProductEntity.kt` | `data/local/` | Definir la entidad `@Entity(tableName = "products")` con sus columnas y llaves |
| `ProductDao.kt` | `data/local/` | Consultas `@Dao`: `getAllProducts()`, `insertProduct()`, `updateProduct()`, `deleteProduct()` |
| `AppDatabase.kt` | `data/local/` | Clase abstracta `@Database` con Singleton y migración |
| `PantryRepository.kt` | `data/repository/` | Modificar para consultar e insertar directamente en el `ProductDao` usando corrutinas y `Flow` |

---

## Tareas Detalladas

### TAREA 3.1 — Revisar / Completar la Entidad Room (`ProductEntity.kt`)
```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val expirationDate: Long,
    val quantity: Int,
    val unit: String
)
```

### TAREA 3.2 — Operaciones CRUD en `ProductDao.kt`
```kotlin
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY expirationDate ASC")
    fun getAllProductsSorted(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
```

### TAREA 3.3 — Conectar con `PantryRepository.kt`
Asegurar que `allProducts: Flow<List<Product>>` emita los datos desde la base de datos Room, manteniendo las mismas firmas de métodos para que `PantryViewModel.kt` siga funcionando de forma transparente.

---

## Flujo de Trabajo en Git

1. Cambia a tu rama asignada:
   ```bash
   git checkout feature/room-crud-pantry
   ```
2. Realiza tus commits pequeños y descriptivos:
   ```bash
   git add -A
   git commit -m "feat(room): integracion de Room SQLite para persistencia CRUD en despensa"
   ```
3. Sube tus cambios a GitHub:
   ```bash
   git push origin feature/room-crud-pantry
   ```
4. Abre un Pull Request hacia `main` y avisa a Maykel para la revisión.
