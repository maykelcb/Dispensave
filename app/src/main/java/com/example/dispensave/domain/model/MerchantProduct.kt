package com.example.dispensave.domain.model

data class MerchantProduct(
    val id: Int,
    val name: String,
    val stock: Int = 0,
    val unit: String = "unidad"
)
