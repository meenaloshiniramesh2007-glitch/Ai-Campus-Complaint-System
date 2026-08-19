package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "complaint_history")
data class ComplaintHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val complaintId: Long,
    val oldStatus: String,
    val newStatus: String,
    val changedBy: String,
    val remarks: String,
    val timestamp: Long = System.currentTimeMillis()
)
