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
    val data: CommentDetailResponse // Objek data ini yang fieldnya perlu disesuaikan
)

// DTO untuk objek data di dalam CommentApiResponse (respons setelah create comment)
data class CommentDetailResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("content") // Menggunakan "content" sesuai log JSON dari POST /comments
    val content: String?,      // Tetap nullable untuk keamanan jika API bisa mengirim null
    @SerializedName("userId")
    val userId: String?,      // Dibuat nullable untuk keamanan, mapper akan handle
    @SerializedName("postId")
    val postId: String,
    @SerializedName("user")
    val user: CommentUserResponse, // Detail user yang membuat komentar
    // Field "post" dari JSON respons create comment tidak perlu ada di DTO ini
    // jika tidak digunakan langsung untuk mapping ke Comment domain model.
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

// LikeToggleResponse dan LikeDataResponse (tidak berubah dari sebelumnya)
data class LikeToggleResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: LikeDataResponse
)

data class LikeDataResponse(
    @SerializedName("liked")
    val liked: Boolean,
    @SerializedName("totalLikes")
    val totalLikes: Int
)