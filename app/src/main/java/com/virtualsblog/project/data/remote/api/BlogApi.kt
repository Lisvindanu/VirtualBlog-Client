package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.response.PostsApiResponse
import com.virtualsblog.project.data.remote.dto.response.PostDetailApiResponse
import com.virtualsblog.project.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface BlogApi {
    
    @GET("posts")
    suspend fun getAllPosts(
        @Header(Constants.HEADER_API_KEY) apiKey: String = Constants.API_KEY,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<PostsApiResponse>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: String,
        @Header(Constants.HEADER_API_KEY) apiKey: String = Constants.API_KEY,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<PostDetailApiResponse>
}