package com.virtualsblog.project.domain.model

data class AuthState(
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val accessToken: String? = null
)