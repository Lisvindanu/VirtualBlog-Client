package com.virtualsblog.project.data.remote.dto.response

data class AuthResponse(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: LoginData
)
