package com.virtualsblog.project.data.repository

import com.virtualsblog.project.data.mapper.CategoryMapper
import com.virtualsblog.project.data.mapper.PostMapper
import com.virtualsblog.project.data.remote.api.BlogApi
import com.virtualsblog.project.domain.model.Category
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.DateUtil
import com.virtualsblog.project.util.Resource
import com.virtualsblog.project.util.FileUtils
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

            // Validasi input sebelum upload - sama dengan AuthRepositoryImpl
            if (title.trim().isEmpty()) {
                emit(Resource.Error("Judul tidak boleh kosong"))
                return@flow
            }
            
            if (content.trim().isEmpty()) {
                emit(Resource.Error("Konten tidak boleh kosong"))
                return@flow
            }
            
            if (categoryId.trim().isEmpty()) {
                emit(Resource.Error("Kategori harus dipilih"))
                return@flow
            }

            // Validasi file seperti AuthRepositoryImpl
            if (!photo.exists()) {
                emit(Resource.Error("File gambar tidak ditemukan"))
                return@flow
            }
            
            if (photo.length() == 0L) {
                emit(Resource.Error("File gambar kosong atau rusak"))
                return@flow
            }
            
            if (photo.length() > Constants.MAX_IMAGE_SIZE) {
                emit(Resource.Error("File terlalu besar (maksimal 10MB)"))
                return@flow
            }
            
            if (!FileUtils.isValidImageFile(photo)) {
                emit(Resource.Error("File harus berupa gambar JPG, JPEG, atau PNG yang valid"))
                return@flow
            }

            // PERSIS seperti AuthRepositoryImpl - pattern yang sudah proven work!
            // Create multipart request body
            val titleBody = title.trim().toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.trim().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.trim().toRequestBody("text/plain".toMediaTypeOrNull())
            
            // File handling PERSIS seperti uploadProfilePicture di AuthRepositoryImpl
            val mimeType = FileUtils.getImageMimeType(photo)
            val requestFile = photo.asRequestBody(mimeType.toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photo.name, requestFile)

            // API call dengan pattern AuthApi
            val response = blogApi.createPost(
                apiKey = Constants.API_KEY,
                authorization = "${Constants.BEARER_PREFIX}$token", 
                title = titleBody,
                content = contentBody,
                categoryId = categoryIdBody,
                photo = photoPart
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Mapping response menggunakan fungsi yang sudah ada - pattern AuthRepositoryImpl
                    val post = PostMapper.mapResponseToDomain(body)
                    emit(Resource.Success(post))
                } else {
                    emit(Resource.Error("Response body kosong dari server"))
                }
            } else {
                // Error handling seperti AuthRepositoryImpl - simplified
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    400 -> "Data yang dikirim tidak valid. Periksa semua field"
                    401 -> Constants.ERROR_UNAUTHORIZED
                    403 -> "Tidak memiliki akses untuk membuat postingan"
                    413 -> "File terlalu besar (maksimal 10MB)"
                    415 -> "Tipe file tidak didukung. Gunakan JPG atau PNG"
                    422 -> "Data validasi gagal. Periksa kategori dan format file"
                    500 -> "Terjadi kesalahan pada server. Coba lagi nanti"
                    else -> "Terjadi kesalahan. Coba lagi nanti"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            // Error handling seperti AuthRepositoryImpl
            val errorMessage = when {
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    "Request timeout - coba lagi"
                e.message?.contains("connect", ignoreCase = true) == true -> 
                    Constants.ERROR_NETWORK
                e.message?.contains("file", ignoreCase = true) == true ->
                    "Error dengan file gambar"
                else -> "Tidak dapat terhubung ke server"
            }
            emit(Resource.Error(errorMessage))
        }
    }
}