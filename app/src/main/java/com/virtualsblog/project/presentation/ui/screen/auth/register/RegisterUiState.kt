package com.virtualsblog.project.presentation.ui.screen.auth.register

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fullname: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)