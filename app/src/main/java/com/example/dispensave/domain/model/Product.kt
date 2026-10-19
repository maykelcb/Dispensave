package com.example.dispensave.domain.model

import java.text.SimpleDateFormat
import java.util.*

data class Product(
    val id: Int = 0,
    val name: String,
    val category: String,
    val expirationDate: Long,
    val quantity: Int,
    val unit: String
) {
    val formattedExpirationDate: String
        get() {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(Date(expirationDate))
        }

    val isExpired: Boolean
        get() {
            val now = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val exp = Calendar.getInstance().apply {
                timeInMillis = expirationDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            return exp < now || expirationDate < System.currentTimeMillis()
        }

    val daysUntilExpiration: Long
        get() {
            val now = System.currentTimeMillis()
            val diffMs = expirationDate - now
            return diffMs / (1000 * 60 * 60 * 24)
        }

    val calculatedStatus: ProductStatus
        get() {
            if (isExpired) {
                return ProductStatus.VENCIDO
            }
            val days = daysUntilExpiration
            return when {
                days <= 3 -> ProductStatus.POR_VENCER
                else -> ProductStatus.DISPONIBLE
            }
        }
}

enum class ProductStatus(val label: String) {
    DISPONIBLE("Disponible"),
    POR_VENCER("Por Vencer"),
    VENCIDO("Vencido"),
    CONSUMIDO("Consumido")
}