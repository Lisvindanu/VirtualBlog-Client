package com.virtualsblog.project.presentation.ui.screen.post.detail

import com.virtualsblog.project.domain.model.Post

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val error: String? = null,
    val isLikeLoading: Boolean = false
)