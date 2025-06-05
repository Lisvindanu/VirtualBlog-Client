package com.virtualsblog.project.presentation.ui.screen.authorposts

import com.virtualsblog.project.domain.model.Post

data class AuthorPostsUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val authorName: String = "",
    val error: String? = null,
    val likingPostIds: Set<String> = emptySet() // For like button loading state
)