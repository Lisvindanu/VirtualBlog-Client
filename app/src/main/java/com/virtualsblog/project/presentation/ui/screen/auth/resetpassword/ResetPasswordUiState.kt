package com.virtualsblog.project.presentation.ui.screen.auth.resetpassword

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)