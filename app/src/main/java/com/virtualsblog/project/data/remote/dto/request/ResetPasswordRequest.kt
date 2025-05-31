package com.virtualsblog.project.data.remote.dto.request

data class ResetPasswordRequest(
    val tokenId: String,
    val otp: String,
    val password: String,
    val confirm_password: String
)