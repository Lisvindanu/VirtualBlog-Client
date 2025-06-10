package com.virtualsblog.project.util
import com.virtualsblog.project.BuildConfig

object Constants {
    // Konfigurasi API
    val BASE_URL: String = BuildConfig.BASE_URL
    val API_KEY: String = BuildConfig.API_KEY


    // API Endpoints
    const val POSTS_ENDPOINT = "posts"

    // Pagination & Limits
    const val HOME_POSTS_LIMIT = 10

    // Headers
    const val HEADER_API_KEY = "x-api-key"
    const val HEADER_AUTHORIZATION = "Authorization"
    const val BEARER_PREFIX = "Bearer "

    // Kunci Preferensi
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_USERNAME = "username"
    const val PREF_FULLNAME = "fullname"
    const val PREF_EMAIL = "email"
    const val PREF_USER_IMAGE = "user_image"

    // Konstanta Validasi
    const val MIN_FULLNAME_LENGTH = 3
    const val MIN_USERNAME_LENGTH = 6
    const val MIN_PASSWORD_LENGTH = 6

    // Pesan Error - Bahasa Indonesia
    const val ERROR_NETWORK = "Tidak dapat terhubung ke server"
    const val ERROR_UNKNOWN = "Terjadi kesalahan yang tidak diketahui"
    const val ERROR_VALIDATION = "Data yang dimasukkan tidak valid"
    const val ERROR_UNAUTHORIZED = "Sesi Anda telah berakhir, silakan masuk kembali"
    const val ERROR_USERNAME_EXISTS = "Nama pengguna sudah terdaftar"
    const val ERROR_EMAIL_EXISTS = "Email sudah digunakan"
    const val ERROR_USERNAME_INVALID = "Nama pengguna tidak valid"
    const val ERROR_PASSWORD_MISMATCH = "Kata sandi dan konfirmasi kata sandi tidak sama"
    const val ERROR_LOGIN_FAILED = "Nama pengguna atau kata sandi salah"
    const val ERROR_REGISTER_FAILED = "Gagal membuat akun baru"
    const val ERROR_PROFILE_UPDATE_FAILED = "Gagal memperbarui profil"
    const val ERROR_REQUIRED_FIELDS = "Semua bidang harus diisi"
    const val ERROR_POST_NOT_FOUND = "Postingan tidak ditemukan"
    const val ERROR_FAILED_LOAD_POST = "Gagal memuat postingan"
    const val ERROR_FAILED_LOAD_PROFILE = "Gagal memuat profil"
    const val ERROR_POST_UPDATE_FAILED = "Gagal memperbarui postingan"
    const val ERROR_POST_DELETE_FAILED = "Gagal menghapus postingan"

    // Pesan Sukses - Bahasa Indonesia
    const val SUCCESS_LOGIN = "Berhasil masuk"
    const val SUCCESS_REGISTER = "Pendaftaran berhasil"
    const val SUCCESS_PROFILE_UPDATE = "Profil berhasil diperbarui"
    const val SUCCESS_LOGOUT = "Berhasil keluar"
    const val SUCCESS_POST_CREATED = "🎉\nPostingan berhasil dibuat!"
    const val SUCCESS_POST_UPDATED = "Postingan berhasil diperbarui!"
    const val SUCCESS_POST_DELETED = "Postingan berhasil dihapus."
    const val SUCCESS_PASSWORD_CHANGED = "Kata sandi berhasil diubah!"
    const val SUCCESS_OTP_SENT = "Kode verifikasi telah dikirim"

    // Pesan Validasi Form - Bahasa Indonesia
    const val VALIDATION_FULLNAME_REQUIRED = "Nama lengkap harus diisi"
    const val VALIDATION_FULLNAME_MIN_LENGTH = "Nama lengkap minimal $MIN_FULLNAME_LENGTH karakter"
    const val VALIDATION_EMAIL_REQUIRED = "Email harus diisi"
    const val VALIDATION_EMAIL_INVALID = "Format email tidak valid"
    const val VALIDATION_USERNAME_REQUIRED = "Nama pengguna harus diisi"
    const val VALIDATION_USERNAME_MIN_LENGTH = "Nama pengguna minimal $MIN_USERNAME_LENGTH karakter"
    const val VALIDATION_USERNAME_INVALID_CHARS = "Nama pengguna hanya boleh mengandung huruf, angka, dan garis bawah"
    const val VALIDATION_PASSWORD_REQUIRED = "Kata sandi harus diisi"
    const val VALIDATION_PASSWORD_MIN_LENGTH = "Kata sandi minimal $MIN_PASSWORD_LENGTH karakter"
    const val VALIDATION_CONFIRM_PASSWORD_REQUIRED = "Konfirmasi kata sandi harus diisi"
    const val VALIDATION_CONFIRM_PASSWORD_MISMATCH = "Kata sandi dan konfirmasi kata sandi tidak sama"
    const val VALIDATION_TERMS_AGREEMENT = "Anda harus menyetujui syarat dan ketentuan"
    const val VALIDATION_POST_TITLE_REQUIRED = "Judul postingan harus diisi"
    const val VALIDATION_POST_TITLE_MIN_LENGTH = "Judul postingan minimal 3 karakter"
    const val VALIDATION_POST_CONTENT_REQUIRED = "Konten postingan harus diisi"
    const val VALIDATION_POST_CONTENT_MIN_LENGTH = "Konten postingan minimal 10 karakter"

    // Teks UI - Bahasa Indonesia
    const val APP_NAME = "VirtualsBlog"
    const val LOADING_TEXT = "Memuat..."
    const val RETRY_TEXT = "Coba Lagi"
    const val CANCEL_TEXT = "Batal"
    const val SAVE_TEXT = "Simpan"
    const val EDIT_TEXT = "Ubah"
    const val DELETE_TEXT = "Hapus"
    const val SHARE_TEXT = "Bagikan"
    const val LIKE_TEXT = "Suka"
    const val COMMENT_TEXT = "Komentar"
    const val READ_MORE_TEXT = "Baca Selengkapnya"
    const val BACK_TEXT = "Kembali"
    const val NEXT_TEXT = "Selanjutnya"
    const val DONE_TEXT = "Selesai"

    // Teks Navigasi
    const val LOGIN_TEXT = "Masuk"
    const val REGISTER_TEXT = "Daftar"
    const val LOGOUT_TEXT = "Keluar"
    const val FORGOT_PASSWORD_TEXT = "Lupa Kata Sandi"
    const val RESET_PASSWORD_TEXT = "Reset Kata Sandi"
    const val CHANGE_PASSWORD_TEXT = "Ubah Kata Sandi"

    // Teks Postingan
    const val CREATE_POST_TEXT = "Tulis Postingan Baru"
    const val EDIT_POST_TEXT = "Ubah Postingan"
    const val DELETE_POST_TEXT = "Hapus Postingan"
    const val POST_TITLE_TEXT = "Judul Postingan"
    const val POST_CONTENT_TEXT = "Konten Postingan"
    const val POST_CATEGORY_TEXT = "Kategori"
    const val PUBLISH_TEXT = "Terbitkan"
    const val DRAFT_TEXT = "Simpan Draft"

    // Kategori Postingan
    const val CATEGORY_TECHNOLOGY = "Teknologi"
    const val CATEGORY_LIFESTYLE = "Gaya Hidup"
    const val CATEGORY_FOOD_DRINK = "Makanan & Minuman"
    const val CATEGORY_TRAVEL = "Perjalanan"
    const val CATEGORY_FINANCE = "Keuangan"
    const val CATEGORY_HEALTH = "Kesehatan"
    const val CATEGORY_EDUCATION = "Pendidikan"
    const val CATEGORY_ENTERTAINMENT = "Hiburan"
    const val CATEGORY_SPORTS = "Olahraga"
    const val CATEGORY_OTHER = "Lainnya"

    // Format Tanggal
    const val DATE_FORMAT_DETAIL = "dd MMMM systematics 'pukul' HH:mm"
    const val DATE_FORMAT_CARD = "dd MMM systematics"
    const val INVALID_DATE = "Tanggal tidak valid"

    // Pesan Profil
    const val PROFILE_TEXT = "Profil"
    const val EDIT_PROFILE_TEXT = "Ubah Profil"
    const val ACCOUNT_INFO_TEXT = "Informasi Akun"
    const val USER_ID_TEXT = "ID Pengguna"
    const val JOINED_SINCE_TEXT = "Bergabung Sejak"
    const val FULL_NAME_TEXT = "Nama Lengkap"
    const val EMAIL_TEXT = "Email"
    const val USERNAME_TEXT = "Nama Pengguna"

    // Pesan Home
    const val WELCOME_TEXT = "Selamat Datang"
    const val LATEST_POSTS_TEXT = "Postingan Terbaru"
    const val VIEW_ALL_TEXT = "Lihat Semua"
    const val NO_POSTS_TEXT = "Belum Ada Postingan"
    const val EMPTY_POSTS_MESSAGE = "Jadilah yang pertama membuat postingan di VirtualsBlog!"
    const val CREATE_FIRST_POST_TEXT = "Tulis Postingan Pertama"
    const val LATEST_POSTS_DESCRIPTION = "Menampilkan postingan dari yang terakhir dibuat"
    const val ALL_POSTS_DESCRIPTION = "Diurutkan dari yang terakhir dibuat"

    // Pesan Auth
    const val WELCOME_BACK_TEXT = "Selamat Datang Kembali!"
    const val LOGIN_SUBTITLE = "Masuk untuk melanjutkan perjalanan Anda"
    const val CREATE_ACCOUNT_TEXT = "Buat Akun Baru"
    const val REGISTER_SUBTITLE = "Bergabung dengan komunitas penulis kami"
    const val ALREADY_HAVE_ACCOUNT = "Sudah punya akun?"
    const val NO_ACCOUNT_YET = "Belum punya akun?"
    const val AGREE_TERMS = "Saya setuju dengan"
    const val TERMS_CONDITIONS = "Syarat dan Ketentuan"

    // Placeholder Teks
    const val ENTER_FULLNAME = "Masukkan nama lengkap Anda"
    const val ENTER_EMAIL = "Masukkan alamat email Anda"
    const val ENTER_USERNAME = "Masukkan nama pengguna Anda"
    const val ENTER_PASSWORD = "Masukkan kata sandi Anda"
    const val ENTER_NEW_PASSWORD = "Masukkan kata sandi baru"
    const val CONFIRM_PASSWORD_HINT = "Masukkan ulang kata sandi Anda"
    const val POST_TITLE_HINT = "Masukkan judul yang menarik..."
    const val POST_CONTENT_HINT = "Tulis konten postingan Anda di sini..."
    const val SEARCH_HINT = "Cari postingan..."

    // Image URLs
    const val IMAGE_BASE_URL = "https://be-prakmob.kodingin.id"
    const val PROFILE_IMAGE_PATH = "/uploads/photo-profile/"

    // Placeholder values
    const val DEFAULT_AVATAR = "default_avatar.png"

    // Image picker constants
    const val MAX_IMAGE_SIZE = 10 * 1024 * 1024 // 10MB
    const val IMAGE_QUALITY = 80

    // Validation constants
    const val MIN_TITLE_LENGTH = 3
    const val MIN_CONTENT_LENGTH = 10
    const val MAX_TITLE_LENGTH = 200
    const val MAX_CONTENT_LENGTH = 5000
}