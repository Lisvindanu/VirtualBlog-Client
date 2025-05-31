package com.virtualsblog.project.domain.usecase.auth

import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.util.Resource
import com.virtualsblog.project.util.ValidationUtil
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<String> {
        val validation = ValidationUtil.validateEmail(email.trim())
        if (!validation.isValid) {
            return Resource.Error(validation.errorMessage ?: "Email tidak valid")
        }
        return repository.forgetPassword(email.trim())
    }
}