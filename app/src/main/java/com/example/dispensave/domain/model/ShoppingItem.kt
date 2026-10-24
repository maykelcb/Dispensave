package com.example.dispensave.domain.model

data class ShoppingItem(
    val id: Int = 0,
    val name: String,
    val category: String = "Abarrotes",
    val quantity: Int = 1,
    val unit: String = "unidad",
    val isPurchased: Boolean = false,
    val defaultShelfDays: Int = 7
)
