package com.virtualsblog.project.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["authorId"]),
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
    val authorImage: String?,
    val category: String,
    val categoryId: String,
    val createdAt: String,
    val updatedAt: String,
    val likes: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false,
    val image: String?,
    val slug: String,
    val isCached: Boolean = false,
    val lastSyncTime: Long = System.currentTimeMillis()
)