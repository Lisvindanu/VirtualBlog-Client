package com.virtualsblog.project.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    indices = [
        Index(value = ["authorId"]),
        Index(value = ["categoryId"]),
        Index(value = ["createdAt"])
    ]
)
data class PostEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val authorId: String,
    val authorUsername: String,
    val authorImage: String? = null,
    val category: String,
    val categoryId: String,
    val createdAt: String,
    val updatedAt: String,
    // HAPUS likes, comments, dan isLiked dari sini
    val image: String? = null,
    val slug: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isStale: Boolean = false
)