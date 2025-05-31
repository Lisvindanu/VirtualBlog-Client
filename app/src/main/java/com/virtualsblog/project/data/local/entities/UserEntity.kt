package com.virtualsblog.project.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val fullname: String,
    val email: String,
    val image: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val isCurrent: Boolean = false // Untuk menandai user yang sedang login
)