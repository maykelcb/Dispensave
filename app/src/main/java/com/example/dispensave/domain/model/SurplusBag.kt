package com.example.dispensave.domain.model

enum class StoreCategory(val displayName: String) {
    RESTAURANTE("Restaurante"),
    PANADERIA("Panadería"),
    CAFETERIA("Cafetería"),
    PASTELERIA("Pastelería")
}

enum class OfferType(val displayName: String) {
    INDIVIDUAL("Producto Individual"),
    PACK("Pack del Día")
}

data class SurplusBag(
    val id: Int,
    val storeId: Int,
    val storeName: String,
    val category: StoreCategory,
    val offerType: OfferType = OfferType.PACK,
    val title: String,
    val description: String,
    val includedItems: List<String> = emptyList(),
    val originalPrice: Double,
    val rescuePrice: Double,
    val pickupTimeWindow: String,
    val stockAvailable: Int,
    val rating: Double = 4.8,
    val address: String = "Av. Javier Prado Este 1240, San Isidro",
    val distanceKm: Double = 1.2
) {
    val discountPercent: Int
        get() = if (originalPrice > 0 && rescuePrice < originalPrice) {
            (((originalPrice - rescuePrice) / originalPrice) * 100).toInt()
        } else 0

    val isAvailable: Boolean
        get() = stockAvailable > 0
}
