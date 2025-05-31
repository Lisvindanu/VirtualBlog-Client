package com.virtualsblog.project.data.remote.dto.response

data class UserResponse(
    val id: String,
    val username: String,
    val fullname: String,
    val email: String,
    val image: String? = null,
    val createdAt: String,
    val updatedAt: String
)

data class LoginData(
    val user: UserResponse,
    val accessToken: String
)