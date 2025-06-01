package com.virtualsblog.project.domain.model

data class Comment(
    val id: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorImage: String? = null,
    val postId: String,
    val createdAt: String,
    val updatedAt: String
)