package com.virtualsblog.project.util

object Constants {
    // API Configuration
    const val BASE_URL = "https://be-prakmob.kodingin.id/api/v1/"
    const val API_KEY = "NpeW7lQ2SlZUCC9mI4G7E26NMRtoK8mW"

    // Preferences Keys
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_USERNAME = "username"
    const val PREF_FULLNAME = "fullname"
    const val PREF_EMAIL = "email"

    // Validation Constants
    const val MIN_FULLNAME_LENGTH = 3
    const val MIN_USERNAME_LENGTH = 6
    const val MIN_PASSWORD_LENGTH = 6

    // Error Messages - Bahasa Indonesia
    const val ERROR_NETWORK = "Tidak dapat terhubung ke server"
    const val ERROR_UNKNOWN = "Terjadi kesalahan yang tidak diketahui"
    const val ERROR_VALIDATION = "Data yang dimasukkan tidak valid"
    const val ERROR_UNAUTHORIZED = "Sesi Anda telah berakhir, silakan masuk kembali"
    const val ERROR_USERNAME_EXISTS = "Username sudah digunakan"
    const val ERROR_EMAIL_EXISTS = "Email sudah digunakan"
    const val ERROR_USERNAME_INVALID = "Username tidak valid"
    const val ERROR_PASSWORD_MISMATCH = "Password dan konfirmasi password tidak sama"
    const val ERROR_LOGIN_FAILED = "Username atau password salah"
    const val ERROR_REGISTER_FAILED = "Gagal membuat akun baru"
    const val ERROR_PROFILE_UPDATE_FAILED = "Gagal memperbarui profil"
    const val ERROR_REQUIRED_FIELDS = "Semua field harus diisi"

    // Success Messages - Bahasa Indonesia
    const val SUCCESS_LOGIN = "Berhasil masuk"
    const val SUCCESS_REGISTER = "Pendaftaran berhasil"
    const val SUCCESS_PROFILE_UPDATE = "Profil berhasil diperbarui"
    const val SUCCESS_LOGOUT = "Berhasil keluar"

    // Form Validation Messages - Bahasa Indonesia
    const val VALIDATION_FULLNAME_REQUIRED = "Nama lengkap harus diisi"
    const val VALIDATION_FULLNAME_MIN_LENGTH = "Nama lengkap minimal $MIN_FULLNAME_LENGTH karakter"
    const val VALIDATION_EMAIL_REQUIRED = "Email harus diisi"
    const val VALIDATION_EMAIL_INVALID = "Format email tidak valid"
    const val VALIDATION_USERNAME_REQUIRED = "Username harus diisi"
    const val VALIDATION_USERNAME_MIN_LENGTH = "Username minimal $MIN_USERNAME_LENGTH karakter"
    const val VALIDATION_USERNAME_INVALID_CHARS = "Username hanya boleh mengandung huruf, angka, dan garis bawah"
    const val VALIDATION_PASSWORD_REQUIRED = "Password harus diisi"
    const val VALIDATION_PASSWORD_MIN_LENGTH = "Password minimal $MIN_PASSWORD_LENGTH karakter"
    const val VALIDATION_CONFIRM_PASSWORD_REQUIRED = "Konfirmasi password harus diisi"
    const val VALIDATION_CONFIRM_PASSWORD_MISMATCH = "Password dan konfirmasi password tidak sama"
    const val VALIDATION_TERMS_AGREEMENT = "Anda harus menyetujui syarat dan ketentuan"

    // UI Text - Bahasa Indonesia
    const val APP_NAME = "VirtualsBlog"
    const val LOADING_TEXT = "Memuat..."
    const val RETRY_TEXT = "Coba Lagi"
    const val CANCEL_TEXT = "Batal"
    const val SAVE_TEXT = "Simpan"
    const val EDIT_TEXT = "Edit"
    const val DELETE_TEXT = "Hapus"
    const val SHARE_TEXT = "Bagikan"
    const val LIKE_TEXT = "Suka"
    const val COMMENT_TEXT = "Komentar"
    const val READ_MORE_TEXT = "Baca Selengkapnya"
}