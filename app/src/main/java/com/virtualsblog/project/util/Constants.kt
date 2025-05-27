package com.virtualsblog.project.util

object Constants {
    // API Configuration
    const val BASE_URL = "https://be-prakmob.kodingin.id/api/v1/"
    const val API_KEY = "NpeW7lQ2SlZUCC9mI4G7E26NMRtoK8mW"

    // Preferences Keys
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_USERNAME = "username"

    // Validation
    const val MIN_USERNAME_LENGTH = 6
    const val MIN_PASSWORD_LENGTH = 6

    // Error Messages
    const val ERROR_NETWORK = "Tidak dapat terhubung ke server"
    const val ERROR_UNKNOWN = "Terjadi kesalahan yang tidak diketahui"
    const val ERROR_VALIDATION = "Data yang dimasukkan tidak valid"
    const val ERROR_UNAUTHORIZED = "Sesi Anda telah berakhir, silakan login kembali"

    // Success Messages
    const val SUCCESS_LOGIN = "Login berhasil"
    const val SUCCESS_REGISTER = "Registrasi berhasil"
    const val SUCCESS_PROFILE_UPDATE = "Profil berhasil diperbarui"
}