package com.example.dispensave.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String = "user_default",

    val name: String,

    val description: String = ""
)
