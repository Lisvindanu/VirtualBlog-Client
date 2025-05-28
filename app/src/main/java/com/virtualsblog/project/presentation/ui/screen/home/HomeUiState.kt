package com.virtualsblog.project.presentation.ui.screen.home

import com.virtualsblog.project.domain.model.Post

data class HomeUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val isLoggedIn: Boolean = true,
    val username: String = ""
)