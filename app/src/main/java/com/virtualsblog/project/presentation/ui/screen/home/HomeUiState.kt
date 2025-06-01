package com.virtualsblog.project.presentation.ui.screen.home

import com.virtualsblog.project.domain.model.Post

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false, // Add refresh state
    val posts: List<Post> = emptyList(),
    val totalPostsCount: Int = 0, // Total posts dari server (tidak ter-limit)
    val error: String? = null,
    val isLoggedIn: Boolean = true,
    val username: String = "",
    val userImageUrl: String? = null
)