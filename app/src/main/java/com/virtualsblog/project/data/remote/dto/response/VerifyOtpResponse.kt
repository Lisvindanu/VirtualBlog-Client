package com.virtualsblog.project.data.remote.dto.response

data class VerifyOtpResponse(
    val redirect: Boolean,
    val id: String,
    val createdAt: String,
    val updatedAt: String
)
