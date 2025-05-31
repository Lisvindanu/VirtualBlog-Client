package com.virtualsblog.project.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.virtualsblog.project.data.remote.api.AuthApi // Masih menggunakan AuthApi karena endpoint profil ada di sana
import com.virtualsblog.project.data.remote.dto.request.UpdateProfileRequest
import com.virtualsblog.project.data.remote.dto.response.ApiResponse // Pastikan ini dari paket yang benar
import com.virtualsblog.project.data.remote.dto.response.UserResponse // Pastikan ini dari paket yang benar
import com.virtualsblog.project.data.remote.dto.response.ValidationError
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.UserRepository
import com.virtualsblog.project.preferences.UserPreferences
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.first // Pastikan import ini ada
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: AuthApi, // Nanti bisa diganti UserApi jika endpoint dipisah
    private val userPreferences: UserPreferences,
    private val gson: Gson
) : UserRepository {

    override suspend fun getProfile(): Resource<User> {
        return try {
            val token = userPreferences.getAccessToken() // Mengambil token dari DataStore
            if (token.isNullOrEmpty()) {
                return Resource.Error(Constants.ERROR_UNAUTHORIZED)
            }

            // Menggunakan 'api' yang merupakan AuthApi karena endpoint 'getProfile' ada di sana
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
                        image = userResponse.image.ifEmpty { null }, // Sesuai update Anda
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )
                    // Update UserPreferences dengan data terbaru dari server
                    userPreferences.updateProfile(
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email,
                        image = user.image // Menyimpan image ke preferences
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
                // userPreferences.clearUserSession() // Pertimbangkan apakah logout otomatis di sini atau di level atas
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
                        image = user.image // Menyimpan image ke preferences
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
                // userPreferences.clearUserSession()
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

    override suspend fun uploadProfilePicture(photoPart: MultipartBody.Part): Resource<User> {
        return try {
            val token = userPreferences.getAccessToken()
            if (token.isNullOrEmpty()) {
                return Resource.Error(Constants.ERROR_UNAUTHORIZED)
            }

            // Menggunakan 'api' (AuthApi) karena endpoint 'uploadProfilePicture' ada di sana
            val response = api.uploadProfilePicture(
                apiKey = Constants.API_KEY,
                token = "Bearer $token",
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
                    userPreferences.updateProfile(
                        username = updatedUser.username,
                        fullname = updatedUser.fullname,
                        email = updatedUser.email,
                        image = updatedUser.image // Menyimpan image baru ke preferences
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
                // userPreferences.clearUserSession()
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

    // Fungsi handleHttpError disalin dari AuthRepositoryImpl yang Anda berikan.
    // Anda mungkin ingin mengekstrak ini ke kelas utilitas jika digunakan di banyak tempat.
    private fun <T> handleHttpError(code: Int, errorBody: String?): Resource<T> {
        return when (code) {
            401 -> Resource.Error(Constants.ERROR_UNAUTHORIZED)
            400 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<ApiResponse<Any>>() {}.type
                        val errorResponse: ApiResponse<Any> = gson.fromJson(errorBody, errorType)
                        return Resource.Error(errorResponse.message ?: "File tidak valid atau tipe file tidak didukung.")
                    } catch (e: Exception) {
                        return Resource.Error("File tidak valid atau tipe file tidak didukung.")
                    }
                }
                Resource.Error("File tidak valid atau tipe file tidak didukung.")
            }
            422 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<ApiResponse<List<ValidationError>>>() {}.type
                        val errorResponse: ApiResponse<List<ValidationError>> = gson.fromJson(errorBody, errorType)
                        val firstError = errorResponse.data.firstOrNull()
                        val message = when (firstError?.msg) {
                            "Username sudah terdaftar" -> Constants.ERROR_USERNAME_EXISTS
                            "Email sudah terdaftar" -> Constants.ERROR_EMAIL_EXISTS
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
                        val errorType = object : TypeToken<ApiResponse<String>>() {}.type
                        val errorResponse: ApiResponse<String> = gson.fromJson(errorBody, errorType)
                        val message = when {
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