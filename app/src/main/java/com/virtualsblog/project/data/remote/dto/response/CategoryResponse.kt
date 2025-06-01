package com.virtualsblog.project.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CategoriesApiResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<CategoryResponse>
)

data class CategoryResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("Post")
    val posts: List<Any> = emptyList()
)
