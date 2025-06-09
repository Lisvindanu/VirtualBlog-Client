package com.virtualsblog.project.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String,

    // Cache metadata
    val lastUpdated: Long = System.currentTimeMillis()
)
