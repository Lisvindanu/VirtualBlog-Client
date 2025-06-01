package com.virtualsblog.project.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PostsApiResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<PostResponse>
)

data class PostResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("authorId")
    val authorId: String,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("author")
    val author: AuthorResponse,
    @SerializedName("category")
    val category: CategoryResponse,
    @SerializedName("_count")
    val count: PostCountResponse,
    @SerializedName("like")
    val like: Boolean
)

data class AuthorResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("fullname")
    val fullname: String,
    @SerializedName("username")
    val username: String
)

data class CategoryResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class PostCountResponse(
    @SerializedName("Comment")
    val comment: Int,
    @SerializedName("Like")
    val like: Int
)