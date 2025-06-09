package com.virtualsblog.project.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
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
    val likes: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false,
    val image: String? = null,
    val slug: String,

    // Cache metadata
    val lastUpdated: Long = System.currentTimeMillis(),
    val isStale: Boolean = false // Mark for refresh priority
)
