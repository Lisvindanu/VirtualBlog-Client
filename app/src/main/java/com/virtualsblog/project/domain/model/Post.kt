package com.virtualsblog.project.domain.model

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: String,
    val updatedAt: String,
    val category: String = "",
    val tags: List<String> = emptyList(),
    val likes: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false
)