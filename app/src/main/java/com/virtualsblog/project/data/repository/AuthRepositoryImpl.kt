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
import kotlinx.coroutines.flow.map
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
                        image = userResponse.image,
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )

                    // Save user session dengan data lengkap
                    userPreferences.saveUserSession(
                        accessToken = accessToken,
                        userId = user.id,
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email
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
                        image = userResponse.image,
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
                        image = userResponse.image,
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )

                    // Update local preferences dengan data terbaru
                    userPreferences.updateProfile(
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email
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
                logout() // Clear invalid session
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
                        image = userResponse.image,
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )

                    // Update preferences
                    userPreferences.updateProfile(
                        username = user.username,
                        fullname = user.fullname,
                        email = user.email
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
                        image = userResponse.image,
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
                        image = userResponse.image,
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

    override suspend fun logout() {
        userPreferences.clearUserSession()
    }

    override fun getCurrentUser(): Flow<User?> {
        return userPreferences.userData.map { userData ->
            if (userData.accessToken != null && userData.userId != null &&
                userData.username != null && userData.fullname != null &&
                userData.email != null) {
                User(
                    id = userData.userId,
                    username = userData.username,
                    fullname = userData.fullname,
                    email = userData.email,
                    image = null, // Image not stored in preferences
                    createdAt = "",
                    updatedAt = ""
                )
            } else null
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
            422 -> {
                // Parse validation errors sesuai API spec
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<com.virtualsblog.project.data.remote.dto.response.ApiResponse<List<ValidationError>>>() {}.type
                        val errorResponse: com.virtualsblog.project.data.remote.dto.response.ApiResponse<List<ValidationError>> = gson.fromJson(errorBody, errorType)

                        val firstError = errorResponse.data.firstOrNull()
                        val message = when (firstError?.msg) {
                            "Username sudah terdaftar" -> Constants.ERROR_USERNAME_EXISTS
                            "Email sudah terdaftar" -> Constants.ERROR_EMAIL_EXISTS
                            "Username minimal 6 karakter" -> Constants.VALIDATION_USERNAME_MIN_LENGTH
                            "Password minimal 6 karakter" -> Constants.VALIDATION_PASSWORD_MIN_LENGTH
                            "Nama lengkap minimal 3 karakter" -> Constants.VALIDATION_FULLNAME_MIN_LENGTH
                            "Email tidak valid" -> Constants.VALIDATION_EMAIL_INVALID
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
                // Parse server error message sesuai API spec
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<com.virtualsblog.project.data.remote.dto.response.ApiResponse<String>>() {}.type
                        val errorResponse: com.virtualsblog.project.data.remote.dto.response.ApiResponse<String> = gson.fromJson(errorBody, errorType)

                        val message = when {
                            errorResponse.data.contains("Username atau password salah", ignoreCase = true) ->
                                Constants.ERROR_LOGIN_FAILED
                            errorResponse.data.contains("Database", ignoreCase = true) ->
                                "Terjadi kesalahan pada database"
                            else -> errorResponse.message
                        }
                        Resource.Error(message)
                    } catch (e: Exception) {
                        Resource.Error("Terjadi kesalahan pada server")
                    }
                } else {
                    Resource.Error("Terjadi kesalahan pada server")
                }
            }
            else -> Resource.Error("Terjadi kesalahan: HTTP $code")
        }
    }
}