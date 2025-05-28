package com.virtualsblog.project.domain.usecase.auth

import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.util.Resource
import com.virtualsblog.project.util.ValidationUtil
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        username: String, // Sekarang ini adalah username tradisional
        password: String
    ): Resource<Pair<User, String>> {
        val validation = ValidationUtil.validateLoginForm(username.trim(), password)
        if (!validation.isValid) {
            return Resource.Error(validation.errorMessage ?: "Data tidak valid")
        }
        return repository.login(username.trim(), password)
    }
}