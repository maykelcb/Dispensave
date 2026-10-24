package com.example.dispensave.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO principal que representa la respuesta JSON de búsqueda en Open Food Facts API.
 */
data class FoodSearchResponseDto(
    @SerializedName("count")
    val count: Int = 0,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("products")
    val products: List<RemoteFoodProductDto> = emptyList()
)

/**
 * DTO que representa cada producto alimenticio recibido desde el servicio REST externo.
 */
data class RemoteFoodProductDto(
    @SerializedName("code")
    val barcode: String? = null,

    @SerializedName("product_name")
    val productName: String? = null,

    @SerializedName("product_name_es")
    val productNameEs: String? = null,

    @SerializedName("brands")
    val brands: String? = null,

    @SerializedName("categories")
    val categories: String? = null,

    @SerializedName("quantity")
    val quantity: String? = null,

    @SerializedName("image_front_small_url")
    val imageUrl: String? = null
) {
    val displayName: String
        get() = when {
            !productNameEs.isNullOrBlank() -> productNameEs.trim()
            !productName.isNullOrBlank() -> productName.trim()
            else -> "Producto alimenticio sin nombre"
        }

    val displayBrand: String
        get() = if (!brands.isNullOrBlank()) brands.split(",").first().trim() else "Marca genérica"

    val displayCategory: String
        get() = if (!categories.isNullOrBlank()) {
            categories.split(",").first().replace("en:", "").replace("es:", "").trim()
        } else {
            "Abarrotes"
        }

    val displayUnit: String
        get() = if (!quantity.isNullOrBlank()) quantity.trim() else "unidad"
}

