package com.virtualsblog.project.domain.model

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: String,
    val updatedAt: String,
    val category: String = "",
    val likes: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false,
    val authorId: String = "",
    val tags: List<String> = emptyList(),
    val imageUrl: String? = null,
    val excerpt: String = "", // Short description for preview
    val readTime: Int = 0, // Estimated read time in minutes
    val isPublished: Boolean = true,
    val viewCount: Int = 0
)