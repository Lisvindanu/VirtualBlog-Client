package com.virtualsblog.project.domain.model

data class User(
    val id: String,
    val username: String,
    val fullname: String,
    val email: String,
    val image: String? = null,
    val createdAt: String,
    val updatedAt: String
)
