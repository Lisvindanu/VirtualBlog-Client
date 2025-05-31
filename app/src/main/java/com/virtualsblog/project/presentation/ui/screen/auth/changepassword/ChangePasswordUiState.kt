package com.virtualsblog.project.presentation.ui.screen.auth.changepassword

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)