package com.example.dispensave.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY expirationDate ASC")
    fun getAllProductsSortedByExpiration(): Flow<List<ProductEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long
    @Update
    suspend fun updateProduct(product: ProductEntity)
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)
}