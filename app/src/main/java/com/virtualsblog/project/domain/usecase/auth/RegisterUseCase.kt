package com.virtualsblog.project.domain.usecase.auth

import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.util.Resource
import com.virtualsblog.project.util.ValidationUtil
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        fullname: String,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Resource<User> {
        val validation = ValidationUtil.validateRegisterForm(
            fullname.trim(),
            email.trim(),
            username.trim(),
            password,
            confirmPassword
        )
        if (!validation.isValid) {
            return Resource.Error(validation.errorMessage ?: "Data tidak valid")
        }
        return repository.register(
            fullname.trim(),
            email.trim(),
            username.trim(),
            password,
            confirmPassword
        )
    }
}