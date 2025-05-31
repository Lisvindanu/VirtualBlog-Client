package com.virtualsblog.project.domain.usecase.auth

import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.util.Resource
import com.virtualsblog.project.util.ValidationUtil
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otp: String): Resource<String> {
        val emailValidation = ValidationUtil.validateEmail(email.trim())
        if (!emailValidation.isValid) {
            return Resource.Error(emailValidation.errorMessage ?: "Email tidak valid")
        }

        if (otp.trim().length != 6) {
            return Resource.Error("Kode OTP harus 6 digit")
        }

        return repository.verifyOtp(email.trim(), otp.trim())
    }
}