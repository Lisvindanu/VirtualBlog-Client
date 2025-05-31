package com.virtualsblog.project.domain.usecase.auth

import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.util.Resource
import com.virtualsblog.project.util.ValidationUtil
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        prevPassword: String,
        password: String,
        confirmPassword: String
    ): Resource<User> {
        if (prevPassword.trim().isEmpty()) {
            return Resource.Error("Password lama harus diisi")
        }

        val passwordValidation = ValidationUtil.validatePassword(password)
        if (!passwordValidation.isValid) {
            return Resource.Error(passwordValidation.errorMessage ?: "Password baru tidak valid")
        }

        val confirmPasswordValidation = ValidationUtil.validateConfirmPassword(password, confirmPassword)
        if (!confirmPasswordValidation.isValid) {
            return Resource.Error(confirmPasswordValidation.errorMessage ?: "Konfirmasi password tidak valid")
        }

        return repository.changePassword(prevPassword.trim(), password, confirmPassword)
    }
}