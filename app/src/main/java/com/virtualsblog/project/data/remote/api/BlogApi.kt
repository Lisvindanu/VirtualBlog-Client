package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.response.CategoriesApiResponse
import com.virtualsblog.project.data.remote.dto.response.PostResponse
import com.virtualsblog.project.data.remote.dto.response.PostsApiResponse
import com.virtualsblog.project.data.remote.dto.response.PostDetailApiResponse
import com.virtualsblog.project.util.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

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

    @GET("categories")
    suspend fun getCategories(
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String,
        @Header(Constants.HEADER_API_KEY) apiKey: String
    ): Response<CategoriesApiResponse>
    
    @Multipart
    @POST("posts")
    suspend fun createPost(
        @Header("X-API-KEY") apiKey: String, // Or however you pass the API Key
        @Header("Authorization") authorization: String, // This is the parameter name
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<PostResponse>
}