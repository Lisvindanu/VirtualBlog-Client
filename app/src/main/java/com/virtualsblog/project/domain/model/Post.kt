package com.virtualsblog.project.domain.model

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val authorUsername: String,
    val authorImage: String? = null,
    val category: String,
    val createdAt: String,
    val updatedAt: String,
    val likes: Int = 0,
    val comments: Int = 0, // Jumlah komentar (count)
    val isLiked: Boolean = false,
    val image: String? = null,
    val slug: String,
    val actualComments: List<Comment> = emptyList() // Field baru untuk daftar detail komentar
)