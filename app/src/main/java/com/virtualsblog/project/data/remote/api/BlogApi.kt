package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.request.CreateCommentRequest
import com.virtualsblog.project.data.remote.dto.response.*
import com.virtualsblog.project.util.Constants // Still needed for BEARER_PREFIX etc.
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BlogApi {

    @GET("posts")
    suspend fun getAllPosts(
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<PostsApiResponse>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: String,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<PostDetailApiResponse>

    @GET("categories")
    suspend fun getCategories(
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<CategoriesApiResponse>

    @Multipart
    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<ApiResponse<PostResponse>>

    // COMMENT ENDPOINTS
    @POST("posts/{id}/comments")
    suspend fun createComment(
        @Path("id") postId: String,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String,
        @Body request: CreateCommentRequest
    ): Response<CommentApiResponse>

    @DELETE("comments/{id}")
    suspend fun deleteComment(
        @Path("id") commentId: String,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<CommentApiResponse>

    // LIKE ENDPOINTS
    @POST("posts/{id}/likes")
    suspend fun toggleLike(
        @Path("id") postId: String,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<LikeToggleResponse>
}