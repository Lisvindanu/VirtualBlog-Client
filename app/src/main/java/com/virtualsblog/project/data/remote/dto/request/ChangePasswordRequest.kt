package com.virtualsblog.project.data.remote.dto.request

data class ChangePasswordRequest(
    val prev_password: String,
    val password: String,
    val confirm_password: String
)