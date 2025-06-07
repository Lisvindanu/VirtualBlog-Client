package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.request.*
import com.virtualsblog.project.data.remote.dto.response.*
import okhttp3.MultipartBody
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

    // NEW: Check login status endpoint
    @GET("check-login")
    suspend fun checkLogin(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserResponse>>

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserResponse>>

    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserResponse>>

    @PUT("profile/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<UserResponse>>

    @POST("forget-password")
    suspend fun forgetPassword(
        @Body request: ForgetPasswordRequest
    ): Response<ApiResponse<Any>>

    @POST("verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<ApiResponse<VerifyOtpResponse>>

    @POST("reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ApiResponse<UserResponse>>

    @Multipart
    @PUT("profile/upload")
    suspend fun uploadProfilePicture(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Response<ApiResponse<UserResponse>>
}