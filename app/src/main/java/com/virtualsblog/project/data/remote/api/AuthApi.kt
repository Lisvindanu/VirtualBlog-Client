package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.request.LoginRequest
import com.virtualsblog.project.data.remote.dto.request.RegisterRequest
import com.virtualsblog.project.data.remote.dto.response.ApiResponse
import com.virtualsblog.project.data.remote.dto.response.AuthResponse
import com.virtualsblog.project.data.remote.dto.response.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<UserResponse>>

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserResponse>>

    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<UserResponse>>
}