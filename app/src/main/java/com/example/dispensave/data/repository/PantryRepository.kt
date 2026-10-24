package com.example.dispensave.data.repository

import com.example.dispensave.data.local.ProductDao
import com.example.dispensave.data.local.ProductEntity
import com.example.dispensave.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio de Despensa — Parte 3 (Rubén)
 * Conecta la capa de presentación con la base de datos local SQLite (Room).
 * Reemplaza el almacenamiento en memoria por persistencia real en el dispositivo.
 * Implementa las operaciones CRUD: Create, Read, Update, Delete.
 */
class PantryRepository(private val productDao: ProductDao) {

    // Flow reactivo: emite la lista de productos ordenados por fecha de caducidad
    // Cada vez que la BD cambia, las pantallas se actualizan automáticamente
    val allProducts: Flow<List<Product>> = productDao
        .getAllProductsSortedByExpiration()
        .map { entities -> entities.map { it.toProduct() } }

    /**
     * Inserta un nuevo producto en la base de datos Room (SQLite).
     * Retorna el ID generado automáticamente por Room.
     */
    suspend fun addProduct(product: Product): Long {
        return productDao.insertProduct(product.toEntity())
    }

    /**
     * Actualiza un producto existente en la base de datos.
     * Usa el ID del producto para identificar el registro a modificar.
     */
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    /**
     * Elimina un producto de la base de datos Room de forma permanente.
     */
    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }

    // --- Funciones de conversión entre capas ---

    private fun ProductEntity.toProduct(): Product = Product(
        id = this.id,
        name = this.name,
        category = this.category,
        expirationDate = this.expirationDate,
        quantity = this.quantity,
        unit = this.unit
    )

    private fun Product.toEntity(): ProductEntity = ProductEntity(
        id = this.id,
        name = this.name,
        category = this.category,
        expirationDate = this.expirationDate,
        quantity = this.quantity,
        unit = this.unit
    )
}
