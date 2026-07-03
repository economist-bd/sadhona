package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val adviceId: Int,
    val userName: String,
    val commentText: String,
    val timestamp: Long = System.currentTimeMillis()
)
