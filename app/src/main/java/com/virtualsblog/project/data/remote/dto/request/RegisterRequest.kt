package com.virtualsblog.project.data.remote.dto.request

data class RegisterRequest(
    val username: String,
    val password: String,
    val confirm_password: String
)