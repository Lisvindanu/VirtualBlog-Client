// BlogRepositoryImpl.kt - Complete Implementation with Hybrid Cache + API Strategy
package com.virtualsblog.project.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.virtualsblog.project.data.local.CacheConstants
import com.virtualsblog.project.data.local.dao.*
import com.virtualsblog.project.data.local.entities.CacheMetadataEntity
import com.virtualsblog.project.data.mapper.*
import com.virtualsblog.project.data.remote.api.BlogApi
import com.virtualsblog.project.data.remote.dto.request.CreateCommentRequest
import com.virtualsblog.project.data.remote.dto.response.*
import com.virtualsblog.project.domain.model.*
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
    private val postDao: PostDao,
    private val categoryDao: CategoryDao,
    private val commentDao: CommentDao,
    private val cacheMetadataDao: CacheMetadataDao,
    private val gson: Gson
) : BlogRepository {

    // ===== 🚀 HYBRID STRATEGY: CACHE STATIC + API DYNAMIC =====

    override suspend fun getAllPosts(): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            // 1️⃣ FAST: Show cached posts immediately (if available)
            val cachedEntities = postDao.getAllPostsSync()
            if (cachedEntities.isNotEmpty()) {
                val cachedPosts = PostMapper.mapEntitiesToDomainList(cachedEntities)
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                emit(Resource.Success(cachedPosts))
            }

            // 2️⃣ FRESH: Always fetch latest from API for dynamic data
            val response = blogApi.getAllPosts("${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val apiPosts = response.body()!!.data
                val currentTime = System.currentTimeMillis()

                // 3️⃣ UPDATE CACHE: Save static data only
                val postEntities = PostMapper.mapResponseListToEntities(apiPosts, currentTime)
                postDao.insertPosts(postEntities)

                // Cache comments for each post
                apiPosts.forEach { postResponse ->
                    postResponse.comments?.let { comments ->
                        val commentEntities = comments.map { commentResponse ->
                            CommentMapper.mapResponseToEntity(
                                convertEmbeddedToDetail(commentResponse),
                                currentTime
                            )
                        }
                        commentDao.insertComments(commentEntities)
                    }
                }

                // 4️⃣ EMIT FRESH: Complete data with dynamic values
                val freshPosts = PostMapper.mapResponseListToDomain(apiPosts)
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                emit(Resource.Success(freshPosts))

                // 5️⃣ UPDATE METADATA
                setCacheMetadata(
                    CacheConstants.CACHE_KEY_ALL_POSTS,
                    currentTime + CacheConstants.CACHE_DURATION_POSTS
                )

            } else if (cachedEntities.isEmpty()) {
                // Only show error if no cache available
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            // Network error - only emit error if no cached data
            val hasCached = postDao.hasAnyCachedPosts()
            if (!hasCached) {
                emit(handleNetworkError(e))
            }
        }
    }

    override suspend fun getPostsForHome(): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            // 1️⃣ FAST: Show cached posts immediately
            val cachedEntities = postDao.getAllPostsSync()
                .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                .take(Constants.HOME_POSTS_LIMIT)

            if (cachedEntities.isNotEmpty()) {
                val cachedPosts = PostMapper.mapEntitiesToDomainList(cachedEntities)
                emit(Resource.Success(cachedPosts))
            }

            // 2️⃣ FRESH: Get latest dynamic data
            val response = blogApi.getAllPosts("${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val apiPosts = response.body()!!.data
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                    .take(Constants.HOME_POSTS_LIMIT)

                // Update cache with static data
                val postEntities = PostMapper.mapResponseListToEntities(response.body()!!.data)
                postDao.insertPosts(postEntities)

                // Cache comments
                response.body()!!.data.forEach { postResponse ->
                    postResponse.comments?.let { comments ->
                        val commentEntities = comments.map { commentResponse ->
                            CommentMapper.mapResponseToEntity(
                                convertEmbeddedToDetail(commentResponse),
                                System.currentTimeMillis()
                            )
                        }
                        commentDao.insertComments(commentEntities)
                    }
                }

                // Emit fresh complete data
                val freshHomePosts = PostMapper.mapResponseListToDomain(apiPosts)
                emit(Resource.Success(freshHomePosts))

                // Update metadata
                setCacheMetadata(
                    CacheConstants.CACHE_KEY_HOME_POSTS,
                    System.currentTimeMillis() + CacheConstants.CACHE_DURATION_POSTS
                )

            } else if (cachedEntities.isEmpty()) {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            val hasCached = postDao.hasAnyCachedPosts()
            if (!hasCached) {
                emit(handleNetworkError(e))
            }
        }
    }

    override suspend fun getTotalPostsCount(): Flow<Resource<Int>> = flow {
        emit(Resource.Loading())

        try {
            // Quick cache count
            val cachedCount = postDao.getTotalCount()
            emit(Resource.Success(cachedCount))

            // Background refresh for accurate count
            val token = authRepository.getAuthToken()
            if (token != null) {
                try {
                    val response = blogApi.getAllPosts("${Constants.BEARER_PREFIX}$token")
                    if (response.isSuccessful && response.body()?.success == true) {
                        val actualCount = response.body()!!.data.size
                        emit(Resource.Success(actualCount))
                    }
                } catch (e: Exception) {
                    // Ignore background refresh errors
                }
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_UNKNOWN))
        }
    }

    // ===== 🎯 ALWAYS FRESH: POST DETAIL (Comments change frequently) =====

    override suspend fun getPostById(postId: String): Flow<Resource<Post>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            // 1️⃣ QUICK CACHE: Show if available (for instant loading)
            val cachedEntity = postDao.getPostById(postId)
            if (cachedEntity != null) {
                val cachedPost = PostMapper.mapEntityToDomain(cachedEntity)
                emit(Resource.Success(cachedPost))
            }

            // 2️⃣ ALWAYS FRESH: Post detail needs latest comments & likes
            val response = blogApi.getPostById(postId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val postDetailResponse = response.body()!!.data
                val currentTime = System.currentTimeMillis()

                // Update cache (static data only)
                val postEntity = PostMapper.mapDetailResponseToEntity(postDetailResponse, currentTime)
                postDao.insertPost(postEntity)

                // Update comments cache
                commentDao.deleteCommentsForPost(postId)
                val comments = postDetailResponse.comments ?: emptyList()
                val commentEntities = comments.map { commentResponse ->
                    CommentMapper.mapResponseToEntity(
                        convertEmbeddedToDetail(commentResponse),
                        currentTime
                    )
                }
                commentDao.insertComments(commentEntities)

                // Emit complete fresh data
                val freshPost = PostMapper.mapDetailResponseToDomain(postDetailResponse)
                emit(Resource.Success(freshPost))

            } else if (cachedEntity == null) {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            val cachedEntity = postDao.getPostById(postId)
            if (cachedEntity == null) {
                emit(handleNetworkError(e))
            }
        }
    }

    // ===== 📱 AGGRESSIVE CACHE: CATEGORIES (Rarely change) =====

    override suspend fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())

        try {
            // 1️⃣ CACHE FIRST: Categories rarely change
            val hasCachedCategories = categoryDao.hasAnyCachedCategories()
            if (hasCachedCategories) {
                val cachedCategories = categoryDao.getAllCategoriesSync()
                val domainCategories = CategoryMapper.mapEntitiesToDomainList(cachedCategories)
                emit(Resource.Success(domainCategories))
            }

            // 2️⃣ SMART REFRESH: Only if cache is old
            val shouldRefresh = shouldRefreshCache(CacheConstants.CACHE_KEY_CATEGORIES)
            if (!shouldRefresh && hasCachedCategories) {
                return@flow // Cache is fresh enough
            }

            // 3️⃣ BACKGROUND REFRESH
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                if (!hasCachedCategories) {
                    emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                }
                return@flow
            }

            val response = blogApi.getCategories("${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val apiCategories = response.body()!!.data
                val currentTime = System.currentTimeMillis()

                // Update cache
                categoryDao.clearAllCategories()
                val categoryEntities = CategoryMapper.mapResponseListToEntities(apiCategories, currentTime)
                categoryDao.insertCategories(categoryEntities)

                setCacheMetadata(
                    CacheConstants.CACHE_KEY_CATEGORIES,
                    currentTime + CacheConstants.CACHE_DURATION_CATEGORIES
                )

                val freshCategories = CategoryMapper.mapResponseListToDomain(apiCategories)
                emit(Resource.Success(freshCategories))

            } else if (!hasCachedCategories) {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            val hasCached = categoryDao.hasAnyCachedCategories()
            if (!hasCached) {
                emit(handleNetworkError(e))
            }
        }
    }

    // ===== 🔄 SMART REFRESH: POSTS BY CATEGORY =====

    override suspend fun getPostsByCategoryId(categoryId: String): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            // 1️⃣ CACHE FIRST: Show cached posts
            val cachedEntities = postDao.getAllPostsSync().filter { it.categoryId == categoryId }
            if (cachedEntities.isNotEmpty()) {
                val cachedPosts = PostMapper.mapEntitiesToDomainList(cachedEntities)
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                emit(Resource.Success(cachedPosts))
            }

            // 2️⃣ FRESH API: Always get latest for dynamic data
            val response = blogApi.getPostsByCategoryId(categoryId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val apiPosts = response.body()!!.data

                // Update cache
                val postEntities = PostMapper.mapResponseListToEntities(apiPosts)
                postDao.insertPosts(postEntities)

                // Cache comments
                apiPosts.forEach { postResponse ->
                    postResponse.comments?.let { comments ->
                        val commentEntities = comments.map { commentResponse ->
                            CommentMapper.mapResponseToEntity(
                                convertEmbeddedToDetail(commentResponse),
                                System.currentTimeMillis()
                            )
                        }
                        commentDao.insertComments(commentEntities)
                    }
                }

                // Emit fresh data with dynamic values
                val freshPosts = PostMapper.mapResponseListToDomain(apiPosts)
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                emit(Resource.Success(freshPosts))

            } else if (cachedEntities.isEmpty()) {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            val hasCached = postDao.getAllPostsSync().any { it.categoryId == categoryId }
            if (!hasCached) {
                emit(handleNetworkError(e))
            }
        }
    }

    override suspend fun getPostsByAuthorId(authorId: String): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            // 1️⃣ CACHE FIRST
            val cachedEntities = postDao.getAllPostsSync().filter { it.authorId == authorId }
            if (cachedEntities.isNotEmpty()) {
                val cachedPosts = PostMapper.mapEntitiesToDomainList(cachedEntities)
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                emit(Resource.Success(cachedPosts))
            }

            // 2️⃣ FRESH API
            val response = blogApi.getPostsByAuthorId(authorId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val apiPosts = response.body()!!.data

                // Update cache
                val postEntities = PostMapper.mapResponseListToEntities(apiPosts)
                postDao.insertPosts(postEntities)

                // Cache comments
                apiPosts.forEach { postResponse ->
                    postResponse.comments?.let { comments ->
                        val commentEntities = comments.map { commentResponse ->
                            CommentMapper.mapResponseToEntity(
                                convertEmbeddedToDetail(commentResponse),
                                System.currentTimeMillis()
                            )
                        }
                        commentDao.insertComments(commentEntities)
                    }
                }

                // Emit fresh data
                val freshPosts = PostMapper.mapResponseListToDomain(apiPosts)
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                emit(Resource.Success(freshPosts))

            } else if (cachedEntities.isEmpty()) {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            val hasCached = postDao.getAllPostsSync().any { it.authorId == authorId }
            if (!hasCached) {
                emit(handleNetworkError(e))
            }
        }
    }

    // ===== 🌐 ALWAYS ONLINE: REAL-TIME OPERATIONS =====

    override suspend fun toggleLike(postId: String): Flow<Resource<Pair<Boolean, Int>>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.toggleLike(postId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val message = response.body()!!.message.lowercase()
                val isLiked = when {
                    message.contains("ditambahkan") ||
                            message.contains("berhasil") && !message.contains("dihapus") -> true
                    message.contains("dihapus") ||
                            message.contains("dibatalkan") ||
                            message.contains("removed") -> false
                    else -> response.body()!!.data != null
                }

                // ✅ NO CACHE UPDATE - UI will refresh from API
                emit(Resource.Success(Pair(isLiked, -1))) // -1 means count from API

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    override suspend fun createComment(postId: String, content: String): Flow<Resource<Comment>> = flow {
        emit(Resource.Loading())

        try {
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

            if (response.isSuccessful && response.body()?.success == true) {
                val newComment = CommentMapper.mapResponseToDomain(response.body()!!.data)

                // Update comments cache
                val commentEntity = CommentMapper.mapResponseToEntity(response.body()!!.data)
                commentDao.insertComment(commentEntity)

                // ✅ NO COUNT UPDATE - UI will refresh from API
                emit(Resource.Success(newComment))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    override suspend fun deleteComment(commentId: String): Flow<Resource<Comment>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.deleteComment(commentId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val deletedComment = CommentMapper.mapResponseToDomain(response.body()!!.data)

                // Remove from cache
                commentDao.deleteCommentById(commentId)

                // ✅ NO COUNT UPDATE - UI will refresh from API
                emit(Resource.Success(deletedComment))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== SEARCH: ALWAYS ONLINE =====

    override suspend fun search(keyword: String): Flow<Resource<SearchData>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.search(keyword, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val searchResponse = response.body()!!.data

                val users = searchResponse.users.map { userResponse ->
                    User(
                        id = userResponse.id,
                        username = userResponse.username,
                        fullname = userResponse.fullname,
                        email = userResponse.email,
                        image = userResponse.image.ifEmpty { null },
                        createdAt = userResponse.createdAt,
                        updatedAt = userResponse.updatedAt
                    )
                }

                val categories = CategoryMapper.mapResponseListToDomain(searchResponse.categories)
                val posts = PostMapper.mapResponseListToDomain(searchResponse.posts)

                val searchData = SearchData(users, categories, posts)
                emit(Resource.Success(searchData))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== POST CRUD: ALWAYS ONLINE =====

    override suspend fun createPost(title: String, content: String, categoryId: String, photo: File): Flow<Resource<Post>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            if (!photo.exists() || photo.length() == 0L) {
                emit(Resource.Error("File gambar tidak valid"))
                return@flow
            }

            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.toRequestBody("text/plain".toMediaTypeOrNull())

            val mimeType = when (photo.extension.lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                else -> "application/octet-stream"
            }

            val requestFile = photo.asRequestBody(mimeType.toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", photo.name, requestFile)

            val response = blogApi.createPost(
                authorization = "Bearer $token",
                title = titleBody,
                content = contentBody,
                categoryId = categoryIdBody,
                photo = photoPart
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val newPost = PostMapper.mapResponseToDomain(response.body()!!.data)

                // Add to cache (static data only)
                val postEntity = PostMapper.mapResponseToEntity(response.body()!!.data)
                postDao.insertPost(postEntity)

                // Invalidate caches to force refresh
                invalidatePostCaches()

                emit(Resource.Success(newPost))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    override suspend fun updatePost(postId: String, title: String, content: String, categoryId: String, photo: File?): Flow<Resource<Post>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.toRequestBody("text/plain".toMediaTypeOrNull())

            var photoPart: MultipartBody.Part? = null
            if (photo != null && photo.exists() && photo.length() > 0L) {
                val mimeType = when (photo.extension.lowercase()) {
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
                categoryId = categoryIdBody,
                photo = photoPart
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val updatedPost = PostMapper.mapResponseToDomain(response.body()!!.data)

                // Update cache (static data only)
                val postEntity = PostMapper.mapResponseToEntity(response.body()!!.data)
                postDao.insertPost(postEntity)

                emit(Resource.Success(updatedPost))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
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

            val response = blogApi.deletePost(postId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val deletedPost = PostMapper.mapResponseToDomain(response.body()!!.data)

                // Remove from cache
                postDao.deletePostById(postId)
                commentDao.deleteCommentsForPost(postId)

                // Invalidate caches
                invalidatePostCaches()

                emit(Resource.Success(deletedPost))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== 🛠️ HELPER METHODS =====

    private suspend fun shouldRefreshCache(cacheKey: String): Boolean {
        val metadata = cacheMetadataDao.getCacheMetadata(cacheKey)
        return metadata == null || metadata.expiresAt <= System.currentTimeMillis()
    }

    private suspend fun setCacheMetadata(cacheKey: String, expiresAt: Long) {
        val metadata = CacheMetadataEntity(
            key = cacheKey,
            lastRefresh = System.currentTimeMillis(),
            expiresAt = expiresAt,
            isRefreshing = false
        )
        cacheMetadataDao.setCacheMetadata(metadata)
    }

    private suspend fun invalidatePostCaches() {
        // Mark all post-related caches as expired
        cacheMetadataDao.deleteCacheMetadata(CacheConstants.CACHE_KEY_ALL_POSTS)
        cacheMetadataDao.deleteCacheMetadata(CacheConstants.CACHE_KEY_HOME_POSTS)
    }

    private fun <T> handleHttpError(code: Int, errorBody: String?): Resource<T> {
        return when (code) {
            401 -> Resource.Error(Constants.ERROR_UNAUTHORIZED)
            400 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<ApiResponse<Any>>() {}.type
                        val errorResponse: ApiResponse<Any> = gson.fromJson(errorBody, errorType)
                        return Resource.Error(errorResponse.message ?: "Permintaan tidak valid.")
                    } catch (e: Exception) {
                        return Resource.Error("Permintaan tidak valid.")
                    }
                }
                Resource.Error("Permintaan tidak valid.")
            }
            404 -> Resource.Error("Data tidak ditemukan")
            500 -> Resource.Error("Terjadi kesalahan pada server")
            else -> Resource.Error("Terjadi kesalahan: HTTP $code")
        }
    }

    private fun <T> handleNetworkError(exception: Exception): Resource<T> {
        return when (exception) {
            is IOException -> Resource.Error(Constants.ERROR_NETWORK)
            is HttpException -> Resource.Error("Network error: ${exception.code()}")
            else -> Resource.Error(exception.message ?: Constants.ERROR_UNKNOWN)
        }
    }
}

// ===== 🔧 HELPER FUNCTION =====
private fun convertEmbeddedToDetail(embedded: CommentResponse): CommentDetailResponse {
    return CommentDetailResponse(
        id = embedded.id,
        content = embedded.content,
        userId = embedded.authorId,
        postId = embedded.postId,
        user = CommentUserResponse(
            id = embedded.author?.id ?: "",
            username = embedded.author?.username ?: "",
            fullname = embedded.author?.fullname ?: "",
            email = "",
            image = embedded.author?.image,
            createdAt = "",
            updatedAt = ""
        ),
        createdAt = embedded.createdAt,
        updatedAt = embedded.updatedAt
    )
}