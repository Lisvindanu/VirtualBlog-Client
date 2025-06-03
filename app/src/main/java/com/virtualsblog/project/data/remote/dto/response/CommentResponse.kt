package com.virtualsblog.project.data.remote.dto.response

import com.google.gson.annotations.SerializedName



// New Comment API Response Classes
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

data class CommentDetailResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("postId")
    val postId: String,
    @SerializedName("user")
    val user: CommentUserResponse,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

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

// Like Toggle Response Classes
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