package com.virtualsblog.project.util

// import android.util.Patterns; // Tidak lagi diperlukan untuk validasi username ini

object ValidationUtil {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    // Fungsi ini sekarang akan memvalidasi username tradisional sesuai aturan API
    fun validateApiUsername(username: String): ValidationResult {
        return when {
            username.isBlank() -> ValidationResult(false, "Username tidak boleh kosong")
            // Validasi panjang minimal sesuai API
            username.length < Constants.MIN_USERNAME_LENGTH ->
                ValidationResult(false, "Username minimal ${Constants.MIN_USERNAME_LENGTH} karakter")
            // Regex untuk memastikan username hanya mengandung huruf, angka, dan underscore
            // Sesuaikan regex ini jika API memperbolehkan karakter lain atau memiliki aturan berbeda
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

    // 'usernameFromUi' adalah username yang diinput pengguna
    fun validateLoginForm(usernameFromUi: String, password: String): ValidationResult {
        val usernameValidation = validateApiUsername(usernameFromUi)
        if (!usernameValidation.isValid) return usernameValidation

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) return passwordValidation

        return ValidationResult(true)
    }

    // 'usernameFromUi' adalah username yang diinput pengguna
    fun validateRegisterForm(
        usernameFromUi: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        val usernameValidation = validateApiUsername(usernameFromUi)
        if (!usernameValidation.isValid) return usernameValidation

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) return passwordValidation

        val confirmPasswordValidation = validateConfirmPassword(password, confirmPassword)
        if (!confirmPasswordValidation.isValid) return confirmPasswordValidation

        return ValidationResult(true)
    }
}