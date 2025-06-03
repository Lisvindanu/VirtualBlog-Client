package com.virtualsblog.project.domain.model

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val authorId: String,
    val authorUsername: String,
    val authorImage: String? = null,
    val category: String, // Category Name
    val categoryId: String,
    val createdAt: String,
    val updatedAt: String,
    val likes: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false,
    val image: String? = null,
    val slug: String,
    val actualComments: List<Comment> = emptyList()
)