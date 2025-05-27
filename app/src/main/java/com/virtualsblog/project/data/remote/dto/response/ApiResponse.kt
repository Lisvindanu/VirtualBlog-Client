package com.virtualsblog.project.data.remote.dto.response

data class ApiResponse<T>(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: T
)

data class ValidationError(
    val type: String,
    val value: String,
    val msg: String,
    val path: String,
    val location: String
)