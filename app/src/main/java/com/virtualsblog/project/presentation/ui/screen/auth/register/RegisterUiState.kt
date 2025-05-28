package com.virtualsblog.project.presentation.ui.screen.auth.register

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    // Hilangkan 'name' dan 'email', ganti dengan 'username'
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)