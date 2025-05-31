package com.virtualsblog.project.domain.usecase.auth

import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.util.Resource
import com.virtualsblog.project.util.ValidationUtil
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        tokenId: String,
        otp: String,
        password: String,
        confirmPassword: String
    ): Resource<User> {
        if (tokenId.trim().isEmpty()) {
            return Resource.Error("Token tidak valid")
        }

        if (otp.trim().length != 6) {
            return Resource.Error("Kode OTP harus 6 digit")
        }

        val passwordValidation = ValidationUtil.validatePassword(password)
        if (!passwordValidation.isValid) {
            return Resource.Error(passwordValidation.errorMessage ?: "Password tidak valid")
        }

        val confirmPasswordValidation = ValidationUtil.validateConfirmPassword(password, confirmPassword)
        if (!confirmPasswordValidation.isValid) {
            return Resource.Error(confirmPasswordValidation.errorMessage ?: "Konfirmasi password tidak valid")
        }

        return repository.resetPassword(tokenId.trim(), otp.trim(), password, confirmPassword)
    }
}