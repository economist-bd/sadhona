package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advices")
data class AdviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val category: String,
    val author: String = "সাদনা কোচ",
    val likesCount: Int = 0,
    val liked: Boolean = false,
    val thumbed: Boolean = false,
    val clapped: Boolean = false,
    val mindblown: Boolean = false,
    val isCustom: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
