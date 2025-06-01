package com.virtualsblog.project.domain.model

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val authorUsername: String? = null,
    val authorImage: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val category: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean,
    val image: String? = null,
    val slug: String? = null
)