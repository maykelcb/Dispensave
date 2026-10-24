package com.example.dispensave.data.remote

import com.example.dispensave.data.model.FoodSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de servicio Retrofit 2 que consume la API REST pública de Open Food Facts.
 */
interface FoodApiService {

    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = "code,product_name,product_name_es,brands,categories,quantity,image_front_small_url"
    ): FoodSearchResponseDto
}

