package com.virtualsblog.project.domain.repository

import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.util.Resource
import okhttp3.MultipartBody

interface UserRepository {
    suspend fun getProfile(): Resource<User>
    suspend fun updateProfile(
        fullname: String,
        email: String,
        username: String
    ): Resource<User>
    suspend fun uploadProfilePicture(photoPart: MultipartBody.Part): Resource<User>
}