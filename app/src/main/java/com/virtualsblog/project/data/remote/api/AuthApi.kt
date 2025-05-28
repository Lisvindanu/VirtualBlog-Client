package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.request.LoginRequest
import com.virtualsblog.project.data.remote.dto.request.RegisterRequest
import com.virtualsblog.project.data.remote.dto.response.ApiResponse
import com.virtualsblog.project.data.remote.dto.response.AuthResponse
import com.virtualsblog.project.data.remote.dto.response.UserResponse
import com.virtualsblog.project.util.Constants
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("login")
    suspend fun login(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("register")
    suspend fun register(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Body request: RegisterRequest
    ): Response<ApiResponse<UserResponse>>

    @GET("profile")
    suspend fun getProfile(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserResponse>>

    @PUT("profile")
    suspend fun updateProfile(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<UserResponse>>
}