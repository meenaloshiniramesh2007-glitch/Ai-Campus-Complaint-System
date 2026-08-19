package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val studentName: String,
    val studentIdNumber: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: String, // LOW, MEDIUM, HIGH, CRITICAL
    val department: String,
    val location: String,
    val imageUrl: String? = null,
    val status: String = "SUBMITTED", // SUBMITTED, PENDING, IN_PROGRESS, RESOLVED, CLOSED, REOPENED
    val aiSummary: String = "",
    val aiConfidence: Float = 0.90f,
    val sentiment: String = "Urgent", // Frustrated, Urgent, Neutral
    val isDuplicateWarning: Boolean = false,
    val duplicateSimilarToId: Long? = null,
    val assignedStaffId: Long? = null,
    val assignedStaffName: String? = null,
    val staffRemarks: String? = null,
    val resolutionProofNote: String? = null,
    val resolutionProofImage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)
