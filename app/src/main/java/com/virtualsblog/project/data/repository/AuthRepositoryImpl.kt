package com.virtualsblog.project.data.repository

import com.virtualsblog.project.data.remote.api.AuthApi
import com.virtualsblog.project.data.remote.dto.request.*
import com.virtualsblog.project.data.remote.dto.response.ValidationError
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.preferences.UserPreferences
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first // Pastikan import ini ada
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody // Pastikan import ini ada
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val userPreferences: UserPreferences,
    private val gson: Gson
) : AuthRepository {

    override suspend fun login(username: String, password: String): Resource<Pair<User, String>> {
        return try {
            val response = api.login(
                apiKey = Constants.API_KEY,
                request = LoginRequest(username, password)
            )

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                if (authResponse.success) {
                    val userResponse = authResponse.data.user
                    val accessToken = authResponse.data.accessToken

                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        fullname = userResponse.fullname,
                        email = userResponse.email,
                        image = userResponse.image.ifEmpty { null },
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )

                    userPreferences.saveUserSession(
                        accessToken = accessToken,
                        userId = user.id,
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email,
                        image = user.image
                    )

                    Resource.Success(Pair(user, accessToken))
                } else {
                    Resource.Error(authResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            handleHttpError(e.code(), e.response()?.errorBody()?.string())
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }

    override suspend fun register(
        fullname: String,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Resource<User> {
        return try {
            val response = api.register(
                apiKey = Constants.API_KEY,
                request = RegisterRequest(fullname, email, username, password, confirmPassword)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        fullname = userResponse.fullname,
                        email = userResponse.email,
                        image = userResponse.image.ifEmpty { null },
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )
                    Resource.Success(user)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            handleHttpError(e.code(), e.response()?.errorBody()?.string())
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }


    override suspend fun getProfile(): Resource<User> {
        return try {
            val token = userPreferences.getAccessToken()
            if (token.isNullOrEmpty()) {
                return Resource.Error(Constants.ERROR_UNAUTHORIZED)
            }

            val response = api.getProfile(
                apiKey = Constants.API_KEY,
                token = "Bearer $token"
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        fullname = userResponse.fullname,
                        email = userResponse.email,
                        image = userResponse.image.ifEmpty { null },
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )

                    userPreferences.updateProfile(
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email,
                        image = user.image
                    )

                    Resource.Success(user)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                logout()
                Resource.Error(Constants.ERROR_UNAUTHORIZED)
            } else {
                handleHttpError(e.code(), e.response()?.errorBody()?.string())
            }
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }

    override suspend fun updateProfile(
        fullname: String,
        email: String,
        username: String
    ): Resource<User> {
        return try {
            val token = userPreferences.getAccessToken()
            if (token.isNullOrEmpty()) {
                return Resource.Error(Constants.ERROR_UNAUTHORIZED)
            }

            val response = api.updateProfile(
                apiKey = Constants.API_KEY,
                token = "Bearer $token",
                request = UpdateProfileRequest(fullname, email, username)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        fullname = userResponse.fullname,
                        email = userResponse.email,
                        image = userResponse.image.ifEmpty { null },
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )

                    userPreferences.updateProfile(
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email,
                        image = user.image
                    )

                    Resource.Success(user)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                logout()
                Resource.Error(Constants.ERROR_UNAUTHORIZED)
            } else {
                handleHttpError(e.code(), e.response()?.errorBody()?.string())
            }
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }

    override suspend fun changePassword(
        prevPassword: String,
        password: String,
        confirmPassword: String
    ): Resource<User> {
        return try {
            val token = userPreferences.getAccessToken()
            if (token.isNullOrEmpty()) {
                return Resource.Error(Constants.ERROR_UNAUTHORIZED)
            }

            val response = api.changePassword(
                apiKey = Constants.API_KEY,
                token = "Bearer $token",
                request = ChangePasswordRequest(prevPassword, password, confirmPassword)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        fullname = userResponse.fullname,
                        email = userResponse.email,
                        image = userResponse.image.ifEmpty { null },
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )
                    userPreferences.updateProfile(
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email,
                        image = user.image
                    )
                    Resource.Success(user)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                logout()
                Resource.Error(Constants.ERROR_UNAUTHORIZED)
            } else {
                handleHttpError(e.code(), e.response()?.errorBody()?.string())
            }
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }

    override suspend fun forgetPassword(email: String): Resource<String> {
        return try {
            val response = api.forgetPassword(
                apiKey = Constants.API_KEY,
                request = ForgetPasswordRequest(email)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Resource.Success(apiResponse.message)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            handleHttpError(e.code(), e.response()?.errorBody()?.string())
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): Resource<String> {
        return try {
            val response = api.verifyOtp(
                apiKey = Constants.API_KEY,
                request = VerifyOtpRequest(email, otp)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Resource.Success(apiResponse.data.id) // Return token ID
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            handleHttpError(e.code(), e.response()?.errorBody()?.string())
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }

    override suspend fun resetPassword(
        tokenId: String,
        otp: String,
        password: String,
        confirmPassword: String
    ): Resource<User> {
        return try {
            val response = api.resetPassword(
                apiKey = Constants.API_KEY,
                request = ResetPasswordRequest(tokenId, otp, password, confirmPassword)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        fullname = userResponse.fullname,
                        email = userResponse.email,
                        image = userResponse.image.ifEmpty { null },
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )
                    Resource.Success(user)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            handleHttpError(e.code(), e.response()?.errorBody()?.string())
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN)
        }
    }


    override suspend fun uploadProfilePicture(photoPart: MultipartBody.Part): Resource<User> {
        return try {
            val token = userPreferences.getAccessToken() // Mengambil token dari DataStore
            if (token.isNullOrEmpty()) {
                return Resource.Error(Constants.ERROR_UNAUTHORIZED)
            }

            val response = api.uploadProfilePicture(
                apiKey = Constants.API_KEY,
                token = "Bearer $token", // Menambahkan "Bearer " prefix
                photo = photoPart
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val updatedUser = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        fullname = userResponse.fullname,
                        email = userResponse.email,
                        image = userResponse.image.ifEmpty { null },
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )
                    // Update preferences dengan data user yang baru, termasuk image URL
                    userPreferences.updateProfile(
                        username = updatedUser.username,
                        fullname = updatedUser.fullname,
                        email = updatedUser.email,
                        image = updatedUser.image
                    )
                    Resource.Success(updatedUser)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                handleHttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                logout() // Clear invalid session jika token tidak valid
                Resource.Error(Constants.ERROR_UNAUTHORIZED)
            } else {
                handleHttpError(e.code(), e.response()?.errorBody()?.string())
            }
        } catch (e: IOException) {
            Resource.Error(Constants.ERROR_NETWORK) // Kesalahan koneksi
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_UNKNOWN) // Kesalahan umum
        }
    }

    override suspend fun logout() {
        userPreferences.clearUserSession()
    }

    override fun getCurrentUser(): Flow<User?> {
        return userPreferences.userData.map { userData ->
            if (userData.accessToken != null && userData.userId != null &&
                userData.username != null && userData.fullname != null &&
                userData.email != null
            ) {
                User(
                    id = userData.userId,
                    username = userData.username,
                    fullname = userData.fullname,
                    email = userData.email,
                    image = userData.image, // Ambil image dari UserData
                    createdAt = "", // Diisi dari API jika perlu, atau biarkan kosong jika hanya untuk sesi
                    updatedAt = ""  // Diisi dari API jika perlu
                )
            } else {
                null
            }
        }
    }


    override fun getAccessToken(): Flow<String?> {
        return userPreferences.accessToken
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return userPreferences.isLoggedIn
    }

    private fun <T> handleHttpError(code: Int, errorBody: String?): Resource<T> {
        return when (code) {
            401 -> Resource.Error(Constants.ERROR_UNAUTHORIZED)
            400 -> { // Menangani Bad Request, seringkali karena validasi file
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        // Coba parse sebagai ApiResponse<Any> atau struktur error umum API Anda
                        val errorType = object : TypeToken<com.virtualsblog.project.data.remote.dto.response.ApiResponse<Any>>() {}.type
                        val errorResponse: com.virtualsblog.project.data.remote.dto.response.ApiResponse<Any> = gson.fromJson(errorBody, errorType)
                        return Resource.Error(errorResponse.message ?: "File tidak valid atau tipe file tidak didukung.")
                    } catch (e: Exception) {
                        // Jika parsing gagal, kembalikan pesan generik
                        return Resource.Error("File tidak valid atau tipe file tidak didukung.")
                    }
                }
                Resource.Error("File tidak valid atau tipe file tidak didukung.")
            }
            422 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<com.virtualsblog.project.data.remote.dto.response.ApiResponse<List<ValidationError>>>() {}.type
                        val errorResponse: com.virtualsblog.project.data.remote.dto.response.ApiResponse<List<ValidationError>> = gson.fromJson(errorBody, errorType)
                        val firstError = errorResponse.data.firstOrNull()
                        val message = when (firstError?.msg) {
                            "Username sudah terdaftar" -> Constants.ERROR_USERNAME_EXISTS
                            "Email sudah terdaftar" -> Constants.ERROR_EMAIL_EXISTS
                            // Tambahkan pesan error spesifik lainnya jika ada
                            else -> firstError?.msg ?: Constants.ERROR_VALIDATION
                        }
                        Resource.Error(message)
                    } catch (e: Exception) {
                        Resource.Error(Constants.ERROR_VALIDATION)
                    }
                } else {
                    Resource.Error(Constants.ERROR_VALIDATION)
                }
            }
            500 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<com.virtualsblog.project.data.remote.dto.response.ApiResponse<String>>() {}.type
                        val errorResponse: com.virtualsblog.project.data.remote.dto.response.ApiResponse<String> = gson.fromJson(errorBody, errorType)
                        val message = when {
                            errorResponse.data.contains("Username atau password salah", ignoreCase = true) -> Constants.ERROR_LOGIN_FAILED
                            errorResponse.data.contains("Failed to upload file", ignoreCase = true) -> "Gagal mengunggah file ke server."
                            else -> errorResponse.message ?: "Terjadi kesalahan pada server."
                        }
                        Resource.Error(message)
                    } catch (e: Exception) {
                        Resource.Error("Terjadi kesalahan pada server.")
                    }
                } else {
                    Resource.Error("Terjadi kesalahan pada server.")
                }
            }
            else -> Resource.Error("Terjadi kesalahan: HTTP $code")
        }
    }
}