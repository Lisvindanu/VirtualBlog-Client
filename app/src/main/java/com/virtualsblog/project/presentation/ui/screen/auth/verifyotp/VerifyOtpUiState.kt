package com.virtualsblog.project.presentation.ui.screen.auth.verifyotp

data class VerifyOtpUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val tokenId: String? = null
)