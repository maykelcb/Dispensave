package com.example.dispensave.domain.model

enum class OrderStatus(val displayName: String) {
    PENDIENTE_RECOJO("Pendiente de recojo"),
    RECOGIDO("Rescate completado"),
    CANCELADO("Cancelado")
}

data class Order(
    val id: Int,
    val bagId: Int,
    val storeName: String,
    val bagTitle: String,
    val rescuePrice: Double,
    val pickupTimeWindow: String,
    val reservationCode: String,
    val status: OrderStatus = OrderStatus.PENDIENTE_RECOJO,
    val createdAt: Long = System.currentTimeMillis()
)
