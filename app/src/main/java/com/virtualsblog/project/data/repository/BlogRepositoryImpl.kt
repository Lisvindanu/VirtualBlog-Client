package com.virtualsblog.project.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.virtualsblog.project.data.mapper.CategoryMapper
import com.virtualsblog.project.data.mapper.CommentMapper
import com.virtualsblog.project.data.mapper.PostMapper
import com.virtualsblog.project.data.remote.api.BlogApi
import com.virtualsblog.project.data.remote.dto.request.CreateCommentRequest
import com.virtualsblog.project.data.remote.dto.response.ApiResponse
import com.virtualsblog.project.data.remote.dto.response.PostResponse
import com.virtualsblog.project.data.remote.dto.response.ValidationError
import com.virtualsblog.project.domain.model.Category
import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.DateUtil
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlogRepositoryImpl @Inject constructor(
    private val blogApi: BlogApi,
    private val authRepository: AuthRepository,
    private val gson: Gson // Added Gson for consistent error parsing
) : BlogRepository {

    override suspend fun getAllPosts(): Flow<Resource<List<Post>>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getAllPosts(
                authorization = "${Constants.BEARER_PREFIX}$token"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val posts = PostMapper.mapResponseListToDomain(body.data)
                    // Sort posts by creation date descending (newest first)
                    val sortedPosts = posts.sortedByDescending { post ->
                        DateUtil.getTimestamp(post.createdAt)
                    }
                    emit(Resource.Success(sortedPosts))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_FAILED_LOAD_POST))
                }
            } else {
                // Using the new generic error handler
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun getPostsForHome(): Flow<Resource<List<Post>>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getAllPosts(
                authorization = "${Constants.BEARER_PREFIX}$token"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val posts = PostMapper.mapResponseListToDomain(body.data)
                    val sortedPosts = posts.sortedByDescending { post ->
                        DateUtil.getTimestamp(post.createdAt)
                    }
                    val limitedPosts = sortedPosts.take(Constants.HOME_POSTS_LIMIT)
                    emit(Resource.Success(limitedPosts))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_FAILED_LOAD_POST))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun getTotalPostsCount(): Flow<Resource<Int>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getAllPosts(
                authorization = "${Constants.BEARER_PREFIX}$token"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    emit(Resource.Success(body.data.size))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_FAILED_LOAD_POST))
                }
            } else {
                // Changed to use generic error handler
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun getPostById(postId: String): Flow<Resource<Post>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getPostById(
                postId = postId,
                authorization = "${Constants.BEARER_PREFIX}$token"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val post = PostMapper.mapDetailResponseToDomain(body.data)
                    emit(Resource.Success(post))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_POST_NOT_FOUND))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun getCategories(): Flow<Resource<List<Category>>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getCategories(
                authorization = "${Constants.BEARER_PREFIX}$token",
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val categories: List<Category> = CategoryMapper.mapResponseListToDomain(body.data)
                    emit(Resource.Success(categories))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal memuat kategori"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun createPost(
        title: String,
        content: String,
        categoryId: String,
        photo: File
    ): Flow<Resource<Post>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            if (!photo.exists() || photo.length() == 0L) {
                emit(Resource.Error("File gambar tidak valid"))
                return@flow
            }
            if (photo.length() > Constants.MAX_IMAGE_SIZE) {
                emit(Resource.Error("Ukuran file maksimal 10MB"))
                return@flow
            }
            val allowedExtensions = listOf("jpg", "jpeg", "png")
            val fileExtension = photo.extension.lowercase()
            if (!allowedExtensions.contains(fileExtension)) {
                emit(Resource.Error("Tipe file tidak diizinkan. Gunakan JPG, JPEG, atau PNG"))
                return@flow
            }
            val mimeType = when (fileExtension) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                else -> "application/octet-stream" // Fallback to prevent crash, API might reject
            }

            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestFile = photo.asRequestBody(mimeType.toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photo.name, requestFile)

            val response = blogApi.createPost(
                authorization = "Bearer $token",
                title = titleBody,
                content = contentBody,
                categoryId = categoryIdBody,
                photo = photoPart
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val post = PostMapper.mapResponseToDomain(body.data)
                    emit(Resource.Success(post))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal membuat postingan"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    // --- New Methods for Update and Delete Post ---
    override suspend fun updatePost(
        postId: String,
        title: String,
        content: String,
        categoryId: String, // <<< ADDED PARAMETER
        photo: File?
    ): Flow<Resource<Post>> = flow {
        emit(Resource.Loading())
        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.toRequestBody("text/plain".toMediaTypeOrNull()) // <<< CREATE REQUEST BODY
            var photoPart: MultipartBody.Part? = null

            if (photo != null) {
                if (!photo.exists() || photo.length() == 0L) {
                    emit(Resource.Error("File gambar baru tidak valid atau kosong."))
                    return@flow
                }
                if (photo.length() > Constants.MAX_IMAGE_SIZE) {
                    emit(Resource.Error("Ukuran file baru maksimal 10MB."))
                    return@flow
                }
                val allowedExtensions = listOf("jpg", "jpeg", "png")
                val fileExtension = photo.extension.lowercase()
                if (!allowedExtensions.contains(fileExtension)) {
                    emit(Resource.Error("Tipe file gambar baru tidak diizinkan (JPG, JPEG, PNG)."))
                    return@flow
                }
                val mimeType = when (fileExtension) {
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    else -> "application/octet-stream"
                }
                val requestFile = photo.asRequestBody(mimeType.toMediaTypeOrNull())
                photoPart = MultipartBody.Part.createFormData("photo", photo.name, requestFile)
            }

            val response = blogApi.updatePost(
                postId = postId,
                authorization = "${Constants.BEARER_PREFIX}$token",
                title = titleBody,
                content = contentBody,
                categoryId = categoryIdBody, // <<< PASS TO API
                photo = photoPart
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val updatedPost = PostMapper.mapResponseToDomain(body.data)
                    emit(Resource.Success(updatedPost))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_POST_UPDATE_FAILED))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun deletePost(postId: String): Flow<Resource<Post>> = flow {
        emit(Resource.Loading())
        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.deletePost(
                postId = postId,
                authorization = "${Constants.BEARER_PREFIX}$token"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    // API docs state it returns the deleted post data
                    val deletedPostData = PostMapper.mapResponseToDomain(body.data)
                    emit(Resource.Success(deletedPostData))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_POST_DELETE_FAILED))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }
    // --- End of New Methods ---


    override suspend fun createComment(
        postId: String,
        content: String
    ): Flow<Resource<Comment>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.createComment(
                postId = postId,
                authorization = "${Constants.BEARER_PREFIX}$token",
                request = CreateCommentRequest(content.trim())
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val comment = CommentMapper.mapResponseToDomain(body.data)
                    emit(Resource.Success(comment))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal membuat komentar"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun deleteComment(
        commentId: String
    ): Flow<Resource<Comment>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.deleteComment(
                commentId = commentId,
                authorization = "${Constants.BEARER_PREFIX}$token"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val comment = CommentMapper.mapResponseToDomain(body.data)
                    emit(Resource.Success(comment))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal menghapus komentar"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    // Excerpt from BlogRepositoryImpl.kt - Updated toggleLike method

    override suspend fun toggleLike(
        postId: String
    ): Flow<Resource<Pair<Boolean, Int>>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.toggleLike(
                postId = postId,
                authorization = "${Constants.BEARER_PREFIX}$token"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    // IMPROVED: Better message parsing for like status
                    val message = body.message.lowercase()
                    val isLiked = when {
                        // Check for like keywords first
                        message.contains("berhasil dikirim") ||
                                message.contains("ditambahkan") ||
                                message.contains("like berhasil") ||
                                message.contains("disuka") -> true

                        // Check for unlike keywords
                        message.contains("dihapus") ||
                                message.contains("dibatalkan") ||
                                message.contains("unlike") ||
                                message.contains("batal") ||
                                message.contains("removed") -> false

                        // Fallback: check HTTP status code
                        body.status == 201 -> true  // Created = liked
                        body.status == 200 -> false // OK = unliked

                        // Default assume liked if API returns success but unclear message
                        else -> true
                    }

                    // DEBUG: Log the response for debugging
                    println("Like API Response - Status: ${body.status}, Message: '${body.message}', Parsed as liked: $isLiked")

                    emit(Resource.Success(Pair(isLiked, 0))) // 0 indicates count handled by UI
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal toggle like"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    // Generic HTTP error handler for BlogRepository, consistent with previous suggestions
    private fun <T> handleHttpError(code: Int, errorBody: String?): Resource<T> {
        val specificMessage = when (code) {
            400 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        // Attempt to parse specific error messages for 400 if backend provides them
                        val errorType = object : TypeToken<ApiResponse<List<ValidationError>>>() {}.type
                        val errorResponse: ApiResponse<List<ValidationError>> = gson.fromJson(errorBody, errorType)
                        errorResponse.data.firstOrNull()?.msg ?: errorResponse.message ?: "Permintaan tidak valid atau data input salah."
                    } catch (e: Exception) {
                        // Fallback if error body parsing fails or format is different
                        if (errorBody.contains("File type not allowed", ignoreCase = true)) "Tipe file tidak diizinkan. Gunakan JPG, JPEG, atau PNG"
                        else if (errorBody.contains("photo wajib diisi", ignoreCase = true)) "Gambar wajib diupload"
                        else "Permintaan tidak valid atau data input salah."
                    }
                } else {
                    "Permintaan tidak valid atau data input salah."
                }
            }
            401 -> Constants.ERROR_UNAUTHORIZED
            403 -> "Anda tidak memiliki izin untuk melakukan tindakan ini."
            404 -> "Sumber daya tidak ditemukan."
            413 -> "File terlalu besar (maksimal 10MB)."
            422 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<ApiResponse<List<ValidationError>>>() {}.type
                        val errorResponse: ApiResponse<List<ValidationError>> = gson.fromJson(errorBody, errorType)
                        val firstError = errorResponse.data.firstOrNull()?.msg
                        "Validasi gagal: ${firstError ?: Constants.ERROR_VALIDATION}"
                    } catch (e: Exception) {
                        Constants.ERROR_VALIDATION
                    }
                } else {
                    Constants.ERROR_VALIDATION
                }
            }
            500 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<ApiResponse<String>>() {}.type // Assuming data is a simple string for some 500 errors
                        val errorResponse: ApiResponse<String> = gson.fromJson(errorBody, errorType)
                        when {
                            errorResponse.data?.contains("File type not allowed", ignoreCase = true) == true -> "Tipe file tidak diizinkan. Gunakan JPG, JPEG, atau PNG"
                            errorResponse.data?.contains("Failed to upload file", ignoreCase = true) == true -> "Gagal mengunggah file ke server."
                            else -> errorResponse.message ?: Constants.ERROR_UNKNOWN // Use the main message if data is not specific
                        }
                    } catch (e: Exception) {
                        "Terjadi kesalahan pada server. Coba lagi nanti."
                    }
                } else {
                    "Terjadi kesalahan pada server. Coba lagi nanti."
                }
            }
            else -> "Terjadi kesalahan: HTTP $code. Pesan: ${errorBody ?: "Tidak ada detail."}"
        }
        return Resource.Error(specificMessage)
    }
}