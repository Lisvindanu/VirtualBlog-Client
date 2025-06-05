package com.virtualsblog.project.data.remote.dto.response

import com.google.gson.annotations.SerializedName

// This DTO directly matches the 'data' object in the search API response
data class SearchResponseData(
    @SerializedName("users")
    val users: List<UserResponse> = emptyList(),
    @SerializedName("categories")
    val categories: List<CategoryResponse> = emptyList(),
    @SerializedName("posts")
    val posts: List<PostResponse> = emptyList()
)