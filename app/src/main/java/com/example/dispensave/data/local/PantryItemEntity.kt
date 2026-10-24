package com.example.dispensave.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry_items")
data class PantryItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String = "user_default",

    val name: String,

    val category: String,

    val expirationDate: Long,

    val quantity: Int,

    val unit: String,

    val status: String = "Disponible"
)
