package com.example.dispensave.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {

    // LISTAR productos de la despensa
    @Query("SELECT * FROM pantry_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<PantryItemEntity>>

    // BUSCAR productos por nombre
    @Query("SELECT * FROM pantry_items WHERE name LIKE '%' || :search || '%'")
    fun searchItems(search: String): Flow<List<PantryItemEntity>>

    // REGISTRAR producto
    @Insert
    suspend fun insertItem(item: PantryItemEntity)

    // ACTUALIZAR producto
    @Update
    suspend fun updateItem(item: PantryItemEntity)

    // ELIMINAR producto
    @Delete
    suspend fun deleteItem(item: PantryItemEntity)

    // ELIMINAR por ID
    @Query("DELETE FROM pantry_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Int)
}
