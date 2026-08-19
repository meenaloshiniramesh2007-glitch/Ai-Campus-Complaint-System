package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val complaintId: Long,
    val studentId: Long,
    val rating: Int, // 1 to 5
    val feedback: String,
    val createdAt: Long = System.currentTimeMillis()
)
