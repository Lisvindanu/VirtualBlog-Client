package com.virtualsblog.project.data.repository

import com.virtualsblog.project.data.remote.api.AuthApi
import com.virtualsblog.project.data.remote.dto.request.LoginRequest
import com.virtualsblog.project.data.remote.dto.request.RegisterRequest
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
            val response = api.login(LoginRequest(username, password))

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                if (authResponse.success) {
                    val userResponse = authResponse.data.user
                    val accessToken = authResponse.data.accessToken

                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )

                    // Save user session
                    userPreferences.saveUserSession(
                        accessToken = accessToken,
                        userId = user.id,
                        username = user.username
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

    override suspend fun register(username: String, password: String, confirmPassword: String): Resource<User> {
        return try {
            val response = api.register(
                RegisterRequest(username, password, confirmPassword)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
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

            val response = api.getProfile("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
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

    override suspend fun updateProfile(username: String): Resource<User> {
        return try {
            val token = userPreferences.getAccessToken()
            if (token.isNullOrEmpty()) {
                return Resource.Error(Constants.ERROR_UNAUTHORIZED)
            }

            val requestBody = mapOf("username" to username)
            val response = api.updateProfile("Bearer $token", requestBody)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val userResponse = apiResponse.data
                    val user = User(
                        id = userResponse.id,
                        username = userResponse.username,
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )

                    // Update username in preferences
                    userPreferences.updateUsername(user.username)

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

    override suspend fun logout() {
        userPreferences.clearUserSession()
    }

    override fun getCurrentUser(): Flow<User?> {
        return userPreferences.userData.map { (token, userId, username) ->
            if (token != null && userId != null && username != null) {
                User(
                    id = userId,
                    username = username,
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
                // Parse validation errors
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<com.virtualsblog.project.data.remote.dto.response.ApiResponse<List<ValidationError>>>() {}.type
                        val errorResponse: com.virtualsblog.project.data.remote.dto.response.ApiResponse<List<ValidationError>> = gson.fromJson(errorBody, errorType)

                        val firstError = errorResponse.data.firstOrNull()
                        val message = firstError?.msg ?: Constants.ERROR_VALIDATION
                        Resource.Error(message)
                    } catch (e: Exception) {
                        Resource.Error(Constants.ERROR_VALIDATION)
                    }
                } else {
                    Resource.Error(Constants.ERROR_VALIDATION)
                }
            }
            500 -> {
                // Parse server error message
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<com.virtualsblog.project.data.remote.dto.response.ApiResponse<String>>() {}.type
                        val errorResponse: com.virtualsblog.project.data.remote.dto.response.ApiResponse<String> = gson.fromJson(errorBody, errorType)
                        Resource.Error(errorResponse.message)
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