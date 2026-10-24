package com.example.dispensave.data.repository

import com.example.dispensave.data.local.PantryDao
import com.example.dispensave.data.local.PantryItemEntity
import com.example.dispensave.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PantryRepository(
    private val pantryDao: PantryDao
) {

    val allProducts: Flow<List<Product>> =
        pantryDao.getAllItems().map { items ->
            items.map { item ->
                Product(
                    id = item.id,
                    name = item.name,
                    category = item.category,
                    expirationDate = item.expirationDate,
                    quantity = item.quantity,
                    unit = item.unit
                )
            }
        }

    suspend fun addProduct(product: Product) {
        pantryDao.insertItem(
            PantryItemEntity(
                name = product.name,
                category = product.category,
                expirationDate = product.expirationDate,
                quantity = product.quantity,
                unit = product.unit
            )
        )
    }

    suspend fun updateProduct(product: Product) {
        pantryDao.updateItem(
            PantryItemEntity(
                id = product.id,
                name = product.name,
                category = product.category,
                expirationDate = product.expirationDate,
                quantity = product.quantity,
                unit = product.unit
            )
        )
    }

    suspend fun deleteProduct(product: Product) {
        pantryDao.deleteItemById(product.id)
    }
}