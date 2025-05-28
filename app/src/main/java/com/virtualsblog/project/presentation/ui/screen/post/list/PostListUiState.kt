// PostListUiState.kt - Update existing empty class  
package com.virtualsblog.project.presentation.ui.screen.post.list

data class PostListUiState(
    val isLoading: Boolean = false,
    val posts: List<com.virtualsblog.project.domain.model.Post> = emptyList(),
    val error: String? = null
)