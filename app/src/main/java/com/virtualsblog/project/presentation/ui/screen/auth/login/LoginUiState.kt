package com.virtualsblog.project.presentation.ui.screen.auth.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    // Ganti 'email' dengan 'username'
    val username: String = "",
    val password: String = ""
)