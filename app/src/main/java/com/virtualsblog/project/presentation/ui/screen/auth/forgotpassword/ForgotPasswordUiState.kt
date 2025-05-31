package com.virtualsblog.project.presentation.ui.screen.auth.forgotpassword

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val email: String = ""
)