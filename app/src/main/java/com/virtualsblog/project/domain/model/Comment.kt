package com.virtualsblog.project.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val replies: List<Comment> = emptyList(),
    val isEdited: Boolean = false
)