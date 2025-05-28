package com.virtualsblog.project.util

object ValidationUtil {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validateApiUsername(username: String): ValidationResult {
        return when {
            username.isBlank() -> ValidationResult(false, Constants.VALIDATION_USERNAME_REQUIRED)
            username.length < Constants.MIN_USERNAME_LENGTH ->
                ValidationResult(false, Constants.VALIDATION_USERNAME_MIN_LENGTH)
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) ->
                ValidationResult(false, Constants.VALIDATION_USERNAME_INVALID_CHARS)
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, Constants.VALIDATION_PASSWORD_REQUIRED)
            password.length < Constants.MIN_PASSWORD_LENGTH ->
                ValidationResult(false, Constants.VALIDATION_PASSWORD_MIN_LENGTH)
            else -> ValidationResult(true)
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult(false, Constants.VALIDATION_CONFIRM_PASSWORD_REQUIRED)
            password != confirmPassword -> ValidationResult(false, Constants.VALIDATION_CONFIRM_PASSWORD_MISMATCH)
            else -> ValidationResult(true)
        }
    }

    fun validateLoginForm(username: String, password: String): ValidationResult {
        val usernameValidation = validateApiUsername(username)
        if (!usernameValidation.isValid) return usernameValidation

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) return passwordValidation

        return ValidationResult(true)
    }

    fun validateRegisterForm(
        username: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        val usernameValidation = validateApiUsername(username)
        if (!usernameValidation.isValid) return usernameValidation

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) return passwordValidation

        val confirmPasswordValidation = validateConfirmPassword(password, confirmPassword)
        if (!confirmPasswordValidation.isValid) return confirmPasswordValidation

        return ValidationResult(true)
    }
}