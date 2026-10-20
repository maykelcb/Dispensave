package com.example.dispensave.data.repository

import com.example.dispensave.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PantryRepository {

    private val _products = MutableStateFlow(
        listOf(
            Product(
                id = 1,
                name = "Pechuga de Pollo San Fernando",
                category = "Carnes",
                expirationDate = System.currentTimeMillis() + (1000 * 60 * 60 * 4), // Vence hoy
                quantity = 1,
                unit = "kg"
            ),
            Product(
                id = 2,
                name = "Yogurt Danlac Vainilla",
                category = "Lácteos",
                expirationDate = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 2), // En 2 días
                quantity = 1,
                unit = "botella"
            ),
            Product(
                id = 3,
                name = "Arroz Costeño Familiar",
                category = "Abarrotes",
                expirationDate = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 15), // En 15 días
                quantity = 2,
                unit = "kg"
            ),
            Product(
                id = 4,
                name = "Plátanos de Seda",
                category = "Frescos",
                expirationDate = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 4), // En 4 días
                quantity = 6,
                unit = "unidades"
            ),
            Product(
                id = 5,
                name = "Huevos de corral San Jorge",
                category = "Abarrotes",
                expirationDate = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 8), // En 8 días
                quantity = 12,
                unit = "unidades"
            )
        )
    )
    val allProducts: Flow<List<Product>> = _products.asStateFlow()

    suspend fun addProduct(product: Product): Long {
        val current = _products.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
        current.add(0, product.copy(id = newId))
        _products.value = current
        return newId.toLong()
    }

    suspend fun updateProduct(product: Product) {
        val current = _products.value.toMutableList()
        val index = current.indexOfFirst { it.id == product.id }
        if (index != -1) {
            current[index] = product
            _products.value = current
        }
    }

    suspend fun deleteProduct(product: Product) {
        val current = _products.value.toMutableList()
        current.removeAll { it.id == product.id }
        _products.value = current
    }
}
