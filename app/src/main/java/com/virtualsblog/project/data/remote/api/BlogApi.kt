// app/src/main/java/com/virtualsblog/project/data/remote/api/BlogApi.kt
package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.request.CreateCommentRequest
import com.virtualsblog.project.data.remote.dto.response.*
import com.virtualsblog.project.util.Constants
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

    @GET("categories/{id}")
    suspend fun getPostsByCategoryId(
        @Path("id") categoryId: String,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<PostsApiResponse>

    @GET("search")
    suspend fun search(
        @Query("keyword") keyword: String,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<ApiResponse<SearchResponseData>>

    // *** Endpoint for getting posts by author ID (as per API documentation) ***
    @GET("authors/{id}")
    suspend fun getPostsByAuthorId(
        @Path("id") authorId: String,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<PostsApiResponse>

    @Multipart
    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<ApiResponse<PostResponse>>

    @Multipart
    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<ApiResponse<PostResponse>>

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String
    ): Response<ApiResponse<PostResponse>>

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

    @POST("posts/{id}/likes")
    suspend fun toggleLike(
        @Path("id") postId: String,
        @Header(Constants.HEADER_AUTHORIZATION) authorization: String
    ): Response<LikeToggleResponse>
}