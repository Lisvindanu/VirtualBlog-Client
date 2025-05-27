package com.virtualsblog.project.util

object ValidationUtil {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validateUsername(username: String): ValidationResult {
        return when {
            username.isBlank() -> ValidationResult(false, "Username tidak boleh kosong")
            username.length < Constants.MIN_USERNAME_LENGTH ->
                ValidationResult(false, "Username minimal ${Constants.MIN_USERNAME_LENGTH} karakter")
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) ->
                ValidationResult(false, "Username hanya boleh mengandung huruf, angka, dan underscore")
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "Password tidak boleh kosong")
            password.length < Constants.MIN_PASSWORD_LENGTH ->
                ValidationResult(false, "Password minimal ${Constants.MIN_PASSWORD_LENGTH} karakter")
            else -> ValidationResult(true)
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult(false, "Konfirmasi password tidak boleh kosong")
            password != confirmPassword -> ValidationResult(false, "Password dan konfirmasi password tidak sama")
            else -> ValidationResult(true)
        }
    }

    fun validateLoginForm(username: String, password: String): ValidationResult {
        val usernameValidation = validateUsername(username)
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
        val usernameValidation = validateUsername(username)
        if (!usernameValidation.isValid) return usernameValidation

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) return passwordValidation

        val confirmPasswordValidation = validateConfirmPassword(password, confirmPassword)
        if (!confirmPasswordValidation.isValid) return confirmPasswordValidation

        return ValidationResult(true)
    }
}