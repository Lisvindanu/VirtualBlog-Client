package com.virtualsblog.project.data.remote.api

import com.virtualsblog.project.data.remote.dto.request.*
import com.virtualsblog.project.data.remote.dto.response.*
import com.virtualsblog.project.util.Constants
import okhttp3.MultipartBody // Pastikan import ini ada
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
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserResponse>>

    @PUT("profile/change-password")
    suspend fun changePassword(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<UserResponse>>

    @POST("forget-password")
    suspend fun forgetPassword(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Body request: ForgetPasswordRequest
    ): Response<ApiResponse<Any>> // Atau tipe data spesifik jika ada

    @POST("verify-otp")
    suspend fun verifyOtp(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Body request: VerifyOtpRequest
    ): Response<ApiResponse<VerifyOtpResponse>>

    @POST("reset-password")
    suspend fun resetPassword(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Body request: ResetPasswordRequest
    ): Response<ApiResponse<UserResponse>>

    @Multipart // Annotation untuk multipart request
    @PUT("profile/upload") // Endpoint sesuai dokumentasi API
    suspend fun uploadProfilePicture(
        @Header("X-API-KEY") apiKey: String = Constants.API_KEY,
        @Header("Authorization") token: String, // Token autentikasi
        @Part photo: MultipartBody.Part // Bagian file gambar
    ): Response<ApiResponse<UserResponse>> // Response berisi data user yang terupdate
}