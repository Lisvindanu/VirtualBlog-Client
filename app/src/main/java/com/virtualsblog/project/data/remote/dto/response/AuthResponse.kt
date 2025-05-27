package com.virtualsblog.project.data.remote.dto.response

data class UserResponse(
    val id: String,
    val username: String,
    val createdAt: String,
    val updatedAt: String
)

data class LoginData(
    val user: UserResponse,
    val accessToken: String
)

data class AuthResponse(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: LoginData
)