package com.virtualsblog.project.util

import android.util.Patterns

object ValidationUtil {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validateFullname(fullname: String): ValidationResult {
        return when {
            fullname.isBlank() -> ValidationResult(false, Constants.VALIDATION_FULLNAME_REQUIRED)
            fullname.length < Constants.MIN_FULLNAME_LENGTH ->
                ValidationResult(false, Constants.VALIDATION_FULLNAME_MIN_LENGTH)
            else -> ValidationResult(true)
        }
    }

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, Constants.VALIDATION_EMAIL_REQUIRED)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult(false, Constants.VALIDATION_EMAIL_INVALID)
            else -> ValidationResult(true)
        }
    }

    fun validateUsername(username: String): ValidationResult {
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
        val usernameValidation = validateUsername(username)
        if (!usernameValidation.isValid) return usernameValidation

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) return passwordValidation

        return ValidationResult(true)
    }

    fun validateRegisterForm(
        fullname: String,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        val fullnameValidation = validateFullname(fullname)
        if (!fullnameValidation.isValid) return fullnameValidation

        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) return emailValidation

        val usernameValidation = validateUsername(username)
        if (!usernameValidation.isValid) return usernameValidation

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) return passwordValidation

        val confirmPasswordValidation = validateConfirmPassword(password, confirmPassword)
        if (!confirmPasswordValidation.isValid) return confirmPasswordValidation

        return ValidationResult(true)
    }

    fun validateUpdateProfileForm(
        fullname: String,
        email: String,
        username: String
    ): ValidationResult {
        val fullnameValidation = validateFullname(fullname)
        if (!fullnameValidation.isValid) return fullnameValidation

        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) return emailValidation

        val usernameValidation = validateUsername(username)
        if (!usernameValidation.isValid) return usernameValidation

        return ValidationResult(true)
    }
}