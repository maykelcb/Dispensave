package com.example.dispensave.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.Database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base de Datos Local SQLite de DispenSave — Parte 3 (Rubén)
 * Implementación robusta Offline-First sobre motor relacional nativo SQLite.
 * Provee persistencia permanente con emisión reactiva Flow para Jetpack Compose.
 */
@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = RoomSqliteDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private class RoomSqliteDatabase(context: Context) : AppDatabase() {
        private val dbHelper = DatabaseHelper(context)
        private val dao = SqliteProductDao(dbHelper)

        override fun productDao(): ProductDao = dao

        private class DatabaseHelper(context: Context) :
            SQLiteOpenHelper(context, "dispensave_database.db", null, 1) {

            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS products (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        expirationDate INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        unit TEXT NOT NULL,
                        status TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                // Datos iniciales de demostración para visualización inmediata de semáforo
                val now = System.currentTimeMillis()
                val oneDayMs = 24L * 60L * 60L * 1000L

                val demoProducts = listOf(
                    arrayOf("user_default", "Leche Entera Gloria 1L", "Lácteos", (now + 5 * oneDayMs).toString(), "2", "uds", "Disponible"),
                    arrayOf("user_default", "Yogurt Fresa 1L", "Lácteos", (now + 1 * oneDayMs).toString(), "1", "uds", "Por Vencer"),
                    arrayOf("user_default", "Pan de Molde Blanco", "Panadería", (now - 1 * oneDayMs).toString(), "1", "paquete", "Vencido")
                )

                demoProducts.forEach { p ->
                    val cv = ContentValues().apply {
                        put("userId", p[0])
                        put("name", p[1])
                        put("category", p[2])
                        put("expirationDate", p[3].toLong())
                        put("quantity", p[4].toInt())
                        put("unit", p[5])
                        put("status", p[6])
                    }
                    db.insert("products", null, cv)
                }
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                db.execSQL("DROP TABLE IF EXISTS products")
                onCreate(db)
            }
        }

        private class SqliteProductDao(private val dbHelper: DatabaseHelper) : ProductDao {
            private val _productsFlow = MutableStateFlow<List<ProductEntity>>(emptyList())

            init {
                refreshFlow()
            }

            private fun refreshFlow() {
                val list = mutableListOf<ProductEntity>()
                val db = dbHelper.readableDatabase
                val cursor: Cursor = db.rawQuery(
                    "SELECT id, userId, name, category, expirationDate, quantity, unit, status FROM products ORDER BY expirationDate ASC",
                    null
                )
                cursor.use {
                    val idCol = it.getColumnIndexOrThrow("id")
                    val userCol = it.getColumnIndexOrThrow("userId")
                    val nameCol = it.getColumnIndexOrThrow("name")
                    val catCol = it.getColumnIndexOrThrow("category")
                    val expCol = it.getColumnIndexOrThrow("expirationDate")
                    val qtyCol = it.getColumnIndexOrThrow("quantity")
                    val unitCol = it.getColumnIndexOrThrow("unit")
                    val statusCol = it.getColumnIndexOrThrow("status")

                    while (it.moveToNext()) {
                        list.add(
                            ProductEntity(
                                id = it.getInt(idCol),
                                userId = it.getString(userCol),
                                name = it.getString(nameCol),
                                category = it.getString(catCol),
                                expirationDate = it.getLong(expCol),
                                quantity = it.getInt(qtyCol),
                                unit = it.getString(unitCol),
                                status = it.getString(statusCol)
                            )
                        )
                    }
                }
                _productsFlow.value = list
            }

            override fun getAllProductsSortedByExpiration(): Flow<List<ProductEntity>> {
                return _productsFlow.asStateFlow()
            }

            override suspend fun insertProduct(product: ProductEntity): Long {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    if (product.id != 0) {
                        put("id", product.id)
                    }
                    put("userId", product.userId)
                    put("name", product.name)
                    put("category", product.category)
                    put("expirationDate", product.expirationDate)
                    put("quantity", product.quantity)
                    put("unit", product.unit)
                    put("status", product.status)
                }
                val id = db.insertWithOnConflict("products", null, values, SQLiteDatabase.CONFLICT_REPLACE)
                refreshFlow()
                return id
            }

            override suspend fun updateProduct(product: ProductEntity) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put("userId", product.userId)
                    put("name", product.name)
                    put("category", product.category)
                    put("expirationDate", product.expirationDate)
                    put("quantity", product.quantity)
                    put("unit", product.unit)
                    put("status", product.status)
                }
                db.update("products", values, "id = ?", arrayOf(product.id.toString()))
                refreshFlow()
            }

            override suspend fun deleteProduct(product: ProductEntity) {
                val db = dbHelper.writableDatabase
                db.delete("products", "id = ?", arrayOf(product.id.toString()))
                refreshFlow()
            }

            override suspend fun deleteProductById(id: Int) {
                val db = dbHelper.writableDatabase
                db.delete("products", "id = ?", arrayOf(id.toString()))
                refreshFlow()
            }
        }
    }
}
