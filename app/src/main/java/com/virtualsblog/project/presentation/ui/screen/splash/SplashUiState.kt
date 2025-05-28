package com.virtualsblog.project.presentation.ui.screen.splash

data class SplashUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val shouldNavigate: Boolean = false
)
