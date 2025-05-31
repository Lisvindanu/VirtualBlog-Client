// AuthRepository.kt
package com.virtualsblog.project.domain.repository

import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): Resource<Pair<User, String>>
    suspend fun register(
        fullname: String,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Resource<User>
    suspend fun getProfile(): Resource<User>
    suspend fun updateProfile(
        fullname: String,
        email: String,
        username: String
    ): Resource<User>
    suspend fun changePassword(
        prevPassword: String,
        password: String,
        confirmPassword: String
    ): Resource<User>
    suspend fun forgetPassword(email: String): Resource<String>
    suspend fun verifyOtp(email: String, otp: String): Resource<String>
    suspend fun resetPassword(
        tokenId: String,
        otp: String,
        password: String,
        confirmPassword: String
    ): Resource<User>
    suspend fun logout()
    fun getCurrentUser(): Flow<User?>
    fun getAccessToken(): Flow<String?>
    fun isLoggedIn(): Flow<Boolean>
}