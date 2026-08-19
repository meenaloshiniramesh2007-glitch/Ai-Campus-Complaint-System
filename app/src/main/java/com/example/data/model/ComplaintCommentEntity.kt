package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "complaint_comments")
data class ComplaintCommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val complaintId: Long,
    val userId: Long,
    val userName: String,
    val userRole: String,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)
