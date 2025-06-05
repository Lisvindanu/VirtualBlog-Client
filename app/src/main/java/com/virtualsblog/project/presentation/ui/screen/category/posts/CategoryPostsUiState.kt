package com.virtualsblog.project.presentation.ui.screen.category.posts

import com.virtualsblog.project.domain.model.Post

data class CategoryPostsUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val categoryName: String = "",
    val error: String? = null,
    val likingPostIds: Set<String> = emptySet()
)