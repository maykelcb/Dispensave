package com.example.dispensave.domain.model

enum class UserRole(val displayName: String) {
    CLIENTE("Cliente / Consumidor"),
    COMERCIO("Comercio (Restaurante o Panadería)")
}

data class User(
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole = UserRole.CLIENTE,
    val storeName: String? = null,
    val storeCategory: StoreCategory? = null,
    val registeredAt: Long = System.currentTimeMillis()
)
