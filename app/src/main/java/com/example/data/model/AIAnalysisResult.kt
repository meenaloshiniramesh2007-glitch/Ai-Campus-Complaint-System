package com.example.data.model

data class AIAnalysisResult(
    val category: String,
    val priority: String, // LOW, MEDIUM, HIGH, CRITICAL
    val department: String,
    val summary: String,
    val confidence: Float,
    val sentiment: String = "Urgent",
    val potentialDuplicateId: Long? = null,
    val duplicateNotice: String? = null
)
