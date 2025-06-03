package com.virtualsblog.project.data.repository

import com.virtualsblog.project.data.mapper.CategoryMapper
import com.virtualsblog.project.data.mapper.CommentMapper
import com.virtualsblog.project.data.mapper.PostMapper
import com.virtualsblog.project.data.remote.api.BlogApi
import com.virtualsblog.project.data.remote.dto.request.CreateCommentRequest
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
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlogRepositoryImpl @Inject constructor(
    private val blogApi: BlogApi,
    private val authRepository: AuthRepository
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
                when (response.code()) {
                    401 -> emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                    403 -> emit(Resource.Error("Tidak memiliki akses"))
                    404 -> emit(Resource.Error(Constants.ERROR_POST_NOT_FOUND))
                    500 -> emit(Resource.Error("Server error, coba lagi nanti"))
                    else -> emit(Resource.Error("Error: ${response.code()} - ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
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
                    // Sort posts by creation date descending (newest first)
                    val sortedPosts = posts.sortedByDescending { post ->
                        DateUtil.getTimestamp(post.createdAt)
                    }
                    // Limit posts to maximum 10 for home screen after sorting
                    val limitedPosts = sortedPosts.take(Constants.HOME_POSTS_LIMIT)
                    emit(Resource.Success(limitedPosts))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_FAILED_LOAD_POST))
                }
            } else {
                when (response.code()) {
                    401 -> emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                    403 -> emit(Resource.Error("Tidak memiliki akses"))
                    404 -> emit(Resource.Error(Constants.ERROR_POST_NOT_FOUND))
                    500 -> emit(Resource.Error("Server error, coba lagi nanti"))
                    else -> emit(Resource.Error("Error: ${response.code()} - ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
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
                emit(Resource.Error("Gagal mengambil jumlah postingan"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
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
                when (response.code()) {
                    401 -> emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                    403 -> emit(Resource.Error("Tidak memiliki akses"))
                    404 -> emit(Resource.Error(Constants.ERROR_POST_NOT_FOUND))
                    500 -> emit(Resource.Error("Server error, coba lagi nanti"))
                    else -> emit(Resource.Error("Error: ${response.code()} - ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
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
                apiKey = Constants.API_KEY
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
                when (response.code()) {
                    401 -> emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                    403 -> emit(Resource.Error("Tidak memiliki akses"))
                    500 -> emit(Resource.Error("Server error, coba lagi nanti"))
                    else -> emit(Resource.Error("Error: ${response.code()} - ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
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

            // Validasi file gambar
            if (!photo.exists() || photo.length() == 0L) {
                emit(Resource.Error("File gambar tidak valid"))
                return@flow
            }

            if (photo.length() > Constants.MAX_IMAGE_SIZE) {
                emit(Resource.Error("Ukuran file maksimal 10MB"))
                return@flow
            }

            // Validasi ekstensi file
            val allowedExtensions = listOf("jpg", "jpeg", "png")
            val fileExtension = photo.extension.lowercase()
            if (!allowedExtensions.contains(fileExtension)) {
                emit(Resource.Error("Tipe file tidak diizinkan. Gunakan JPG, JPEG, atau PNG"))
                return@flow
            }

            // MIME type sesuai dengan yang digunakan di AuthRepositoryImpl
            val mimeType = when (fileExtension) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                else -> "image/jpeg"
            }

            // Create multipart request body
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.toRequestBody("text/plain".toMediaTypeOrNull())

            // File handling sama persis dengan uploadProfilePicture
            val requestFile = photo.asRequestBody(mimeType.toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photo.name, requestFile)

            // API call
            val response = blogApi.createPost(
                apiKey = Constants.API_KEY,
                authorization = "Bearer $token",
                title = titleBody,
                content = contentBody,
                categoryId = categoryIdBody,
                photo = photoPart
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    // FIXED: Access data from ApiResponse wrapper
                    val post = PostMapper.mapResponseToDomain(body.data)
                    emit(Resource.Success(post))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal membuat postingan"))
                }
            } else {
                // Error handling yang konsisten dengan AuthRepositoryImpl
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    400 -> {
                        when {
                            errorBody?.contains("File type not allowed") == true ->
                                "Tipe file tidak diizinkan. Gunakan JPG, JPEG, atau PNG"
                            errorBody?.contains("photo wajib diisi") == true ->
                                "Gambar wajib diupload"
                            else -> "Data yang dikirim tidak valid"
                        }
                    }
                    401 -> Constants.ERROR_UNAUTHORIZED
                    403 -> "Tidak memiliki akses untuk membuat postingan"
                    413 -> "File terlalu besar (maksimal 10MB)"
                    422 -> {
                        when {
                            errorBody?.contains("Judul wajib minimal") == true ->
                                "Judul postingan minimal 3 karakter"
                            errorBody?.contains("Konten wajib minimal") == true ->
                                "Konten postingan minimal 10 karakter"
                            errorBody?.contains("Category ID") == true ->
                                "Kategori tidak valid"
                            else -> "Data tidak valid"
                        }
                    }
                    500 -> {
                        when {
                            errorBody?.contains("File type not allowed", ignoreCase = true) == true ->
                                "Tipe file tidak diizinkan. Gunakan JPG, JPEG, atau PNG"
                            errorBody?.contains("Failed to upload file", ignoreCase = true) == true ->
                                "Gagal mengunggah file ke server"
                            else -> "Terjadi kesalahan pada server"
                        }
                    }
                    else -> "Error: ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    "Request timeout - coba lagi"
                e.message?.contains("connect", ignoreCase = true) == true ->
                    Constants.ERROR_NETWORK
                e.message?.contains("json", ignoreCase = true) == true ->
                    "Error parsing response dari server"
                else -> "${Constants.ERROR_NETWORK}: ${e.localizedMessage}"
            }
            emit(Resource.Error(errorMessage))
        }
    }

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
                val errorMessage = when (response.code()) {
                    400 -> "Data komentar tidak valid"
                    401 -> Constants.ERROR_UNAUTHORIZED
                    404 -> "Postingan tidak ditemukan"
                    422 -> "Komentar tidak boleh kosong"
                    else -> "Error: ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
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
                val errorMessage = when (response.code()) {
                    401 -> Constants.ERROR_UNAUTHORIZED
                    403 -> "Tidak memiliki izin untuk menghapus komentar ini"
                    404 -> "Komentar tidak ditemukan"
                    else -> "Error: ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        }
    }

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
                    val isLiked = body.data.liked
                    val totalLikes = body.data.totalLikes
                    emit(Resource.Success(Pair(isLiked, totalLikes)))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal toggle like"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> Constants.ERROR_UNAUTHORIZED
                    404 -> "Postingan tidak ditemukan"
                    else -> "Error: ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
        }
    }

}