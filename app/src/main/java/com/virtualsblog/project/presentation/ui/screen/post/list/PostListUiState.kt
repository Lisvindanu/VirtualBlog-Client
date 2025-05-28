package com.virtualsblog.project.presentation.ui.screen.post.list

import com.virtualsblog.project.domain.model.Post

data class PostListUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null
)