package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.response.PostsApiResponse
import com.virtualsblog.project.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface BlogApi {
    
    @GET(Constants.POSTS_ENDPOINT)
    suspend fun getAllPosts(
        @Header(Constants.HEADER_API_KEY) apiKey: String = Constants.API_KEY,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<PostsApiResponse>
}