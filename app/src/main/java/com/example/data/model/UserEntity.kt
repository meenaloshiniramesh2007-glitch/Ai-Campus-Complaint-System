package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val role: String, // STUDENT, ADMIN, STAFF
    val studentOrStaffId: String,
    val department: String? = null,
    val avatarUrl: String = ""
)
