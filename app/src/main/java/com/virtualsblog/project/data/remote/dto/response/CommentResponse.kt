package com.virtualsblog.project.data.remote.dto.response

import com.google.gson.annotations.SerializedName

// Respons utama dari API create comment
data class CommentApiResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: CommentDetailResponse
)

// DTO untuk objek data di dalam CommentApiResponse (respons setelah create comment)
data class CommentDetailResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("content")
    val content: String?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("postId")
    val postId: String,
    @SerializedName("user")
    val user: CommentUserResponse,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

// Detail user yang ada di dalam CommentDetailResponse
data class CommentUserResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("fullname")
    val fullname: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

// UPDATED: Like Response berdasarkan API aktual
data class LikeToggleResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: LikeEntityResponse // Changed to actual response structure
)

// UPDATED: Response data untuk like berdasarkan API aktual
data class LikeEntityResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("postId")
    val postId: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

// DEPRECATED: Struktur ini sesuai dokumentasi tapi tidak sesuai response aktual
// Tetap disimpan jika API berubah sesuai dokumentasi
data class LikeDataResponse(
    @SerializedName("liked")
    val liked: Boolean,
    @SerializedName("totalLikes")
    val totalLikes: Int
)