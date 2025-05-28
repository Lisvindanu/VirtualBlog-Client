package com.virtualsblog.project.presentation.ui.screen.auth.profile

import com.virtualsblog.project.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val updateSuccess: Boolean = false,
    val isLoggedOut: Boolean = false
)
