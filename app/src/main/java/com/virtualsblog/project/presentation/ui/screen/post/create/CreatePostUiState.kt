package com.virtualsblog.project.presentation.ui.screen.post.create

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val content: String = "",
    val category: String = "",
    val selectedImageUri: String? = null,
    val titleError: String? = null,
    val contentError: String? = null,
    val categoryError: String? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)