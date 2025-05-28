package com.virtualsblog.project.presentation.ui.screen.post.create

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)