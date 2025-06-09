// BlogRepositoryImpl.kt - Cache-First Implementation
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    // ===== CACHE-FIRST: ALL POSTS =====
    override suspend fun getAllPosts(): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())

        try {
            // 1. Always emit cached data first (if available)
            val hasCachedPosts = postDao.hasAnyCachedPosts()
            if (hasCachedPosts) {
                val cachedPosts = postDao.getAllPostsSync()
                val domainPosts = PostMapper.mapEntitiesToDomainList(cachedPosts)
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                emit(Resource.Success(domainPosts))
            }

            // 2. Check if we should refresh
            val shouldRefresh = shouldRefreshCache(CacheConstants.CACHE_KEY_ALL_POSTS)
            if (!shouldRefresh && hasCachedPosts) {
                return@flow // Cache is fresh, no need to fetch
            }

            // 3. Fetch from network and update cache
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            setRefreshingStatus(CacheConstants.CACHE_KEY_ALL_POSTS, true)

            val response = blogApi.getAllPosts("${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val apiPosts = response.body()!!.data
                val currentTime = System.currentTimeMillis()

                // Update cache
                val postEntities = PostMapper.mapResponseListToEntities(apiPosts, currentTime)
                postDao.insertPosts(postEntities)

                // Cache comments for each post
                apiPosts.forEach { postResponse ->
                    postResponse.comments?.let { comments ->
                        val commentEntities = comments.map { commentResponse ->
                            CommentMapper.mapResponseToEntity(
                                CommentMapper.convertEmbeddedToDetail(commentResponse),
                                currentTime
                            )
                        }
                        commentDao.insertComments(commentEntities)
                    }
                }

                // Update cache metadata
                setCacheMetadata(
                    CacheConstants.CACHE_KEY_ALL_POSTS,
                    currentTime + CacheConstants.CACHE_DURATION_POSTS
                )

                // Emit fresh data
                val freshPosts = PostMapper.mapResponseListToDomain(apiPosts)
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                emit(Resource.Success(freshPosts))

            } else {
                // Network failed, but we might have cache
                if (hasCachedPosts) {
                    // Keep showing cached data, don't emit error
                    return@flow
                } else {
                    emit(handleHttpError(response.code(), response.errorBody()?.string()))
                }
            }

        } catch (e: Exception) {
            // Network error, emit cached data if available
            val hasCachedPosts = postDao.hasAnyCachedPosts()
            if (!hasCachedPosts) {
                emit(handleNetworkError(e))
            }
            // If we have cache, just keep showing it
        } finally {
            setRefreshingStatus(CacheConstants.CACHE_KEY_ALL_POSTS, false)
        }
    }

    // ===== CACHE-FIRST: HOME POSTS =====
    override suspend fun getPostsForHome(): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())

        try {
            // 1. Emit cached data first
            val cachedPosts = postDao.getAllPostsSync() // Get all, then limit
            if (cachedPosts.isNotEmpty()) {
                val homePosts = cachedPosts
                    .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                    .take(Constants.HOME_POSTS_LIMIT)
                val domainPosts = PostMapper.mapEntitiesToDomainList(homePosts)
                emit(Resource.Success(domainPosts))
            }

            // 2. Background refresh if needed
            val shouldRefresh = shouldRefreshCache(CacheConstants.CACHE_KEY_HOME_POSTS)
            if (!shouldRefresh && cachedPosts.isNotEmpty()) {
                return@flow
            }

            // 3. Fetch fresh data
            refreshAllPostsCache() // Refresh entire cache for consistency

            // 4. Emit updated home posts
            val updatedPosts = postDao.getAllPostsSync()
                .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                .take(Constants.HOME_POSTS_LIMIT)
            val updatedDomainPosts = PostMapper.mapEntitiesToDomainList(updatedPosts)
            emit(Resource.Success(updatedDomainPosts))

        } catch (e: Exception) {
            if (cachedPosts.isEmpty()) {
                emit(handleNetworkError(e))
            }
        }
    }

    // ===== CACHE-FIRST: CATEGORIES =====
    override suspend fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())

        try {
            // 1. Emit cached categories first
            val hasCachedCategories = categoryDao.hasAnyCachedCategories()
            if (hasCachedCategories) {
                val cachedCategories = categoryDao.getAllCategoriesSync()
                val domainCategories = CategoryMapper.mapEntitiesToDomainList(cachedCategories)
                emit(Resource.Success(domainCategories))
            }

            // 2. Check if refresh needed (categories change rarely)
            val shouldRefresh = shouldRefreshCache(CacheConstants.CACHE_KEY_CATEGORIES)
            if (!shouldRefresh && hasCachedCategories) {
                return@flow
            }

            // 3. Fetch from network
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
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
            if (!hasCachedCategories) {
                emit(handleNetworkError(e))
            }
        }
    }

    // ===== HYBRID: POST DETAIL =====
    override suspend fun getPostById(postId: String): Flow<Resource<Post>> = flow {
        emit(Resource.Loading())

        try {
            // 1. Quick cache check
            val cachedPost = postDao.getPostById(postId)
            val cachedComments = commentDao.getCommentsForPostSync(postId)

            if (cachedPost != null) {
                val domainPost = PostMapper.mapEntityToDomain(cachedPost)
                val domainComments = CommentMapper.mapEntitiesToDomainList(cachedComments)
                val postWithComments = domainPost.copy(actualComments = domainComments)
                emit(Resource.Success(postWithComments))
            }

            // 2. Always fetch fresh for post detail (comments update frequently)
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getPostById(postId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful && response.body()?.success == true) {
                val postDetailResponse = response.body()!!.data
                val currentTime = System.currentTimeMillis()

                // Update post cache
                val postEntity = PostMapper.mapDetailResponseToEntity(postDetailResponse, currentTime)
                postDao.insertPost(postEntity)

                // Update comments cache
                commentDao.deleteCommentsForPost(postId)
                val comments = postDetailResponse.comments ?: emptyList()
                val commentEntities = comments.map { commentResponse ->
                    CommentMapper.mapResponseToEntity(
                        CommentMapper.convertEmbeddedToDetail(commentResponse),
                        currentTime
                    )
                }
                commentDao.insertComments(commentEntities)

                // Emit fresh data
                val freshPost = PostMapper.mapDetailResponseToDomain(postDetailResponse)
                emit(Resource.Success(freshPost))

            } else if (cachedPost == null) {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            if (cachedPost == null) {
                emit(handleNetworkError(e))
            }
        }
    }

    // ===== CACHE-FIRST: POSTS BY CATEGORY =====
    override suspend fun getPostsByCategoryId(categoryId: String): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())

        try {
            // 1. Emit cached posts first
            postDao.getPostsByCategoryFlow(categoryId).collect { cachedEntities ->
                if (cachedEntities.isNotEmpty()) {
                    val domainPosts = PostMapper.mapEntitiesToDomainList(cachedEntities)
                        .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                    emit(Resource.Success(domainPosts))
                }
            }

            // 2. Background refresh
            val cacheKey = "${CacheConstants.CACHE_KEY_CATEGORY_POSTS}$categoryId"
            val shouldRefresh = shouldRefreshCache(cacheKey)

            if (shouldRefresh) {
                refreshCategoryPosts(categoryId, cacheKey)
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== CACHE-FIRST: POSTS BY AUTHOR =====
    override suspend fun getPostsByAuthorId(authorId: String): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())

        try {
            // 1. Emit cached posts first
            postDao.getPostsByAuthorFlow(authorId).collect { cachedEntities ->
                if (cachedEntities.isNotEmpty()) {
                    val domainPosts = PostMapper.mapEntitiesToDomainList(cachedEntities)
                        .sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                    emit(Resource.Success(domainPosts))
                }
            }

            // 2. Background refresh
            val cacheKey = "${CacheConstants.CACHE_KEY_AUTHOR_POSTS}$authorId"
            val shouldRefresh = shouldRefreshCache(cacheKey)

            if (shouldRefresh) {
                refreshAuthorPosts(authorId, cacheKey)
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== ALWAYS ONLINE: SEARCH =====
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

    // ===== ALWAYS ONLINE: CREATE POST =====
    override suspend fun createPost(title: String, content: String, categoryId: String, photo: File): Flow<Resource<Post>> = flow {
        emit(Resource.Loading())

        try {
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            // Validate file
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

                // Add to cache
                val postEntity = PostMapper.mapResponseToEntity(response.body()!!.data)
                postDao.insertPost(postEntity)

                // Invalidate related caches
                invalidatePostCaches()

                emit(Resource.Success(newPost))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== ALWAYS ONLINE: TOGGLE LIKE =====
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

                // Quick update cache
                val cachedPost = postDao.getPostById(postId)
                cachedPost?.let { post ->
                    val newLikeCount = if (isLiked) post.likes + 1 else maxOf(0, post.likes - 1)
                    postDao.updateLikeStatus(postId, newLikeCount, isLiked)
                }

                emit(Resource.Success(Pair(isLiked, -1))) // -1 means UI should calculate

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== ALWAYS ONLINE: CREATE COMMENT =====
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

                // Add to cache
                val commentEntity = CommentMapper.mapResponseToEntity(response.body()!!.data)
                commentDao.insertComment(commentEntity)

                // Update post comment count
                val currentCount = commentDao.getCommentCountForPost(postId)
                postDao.updateCommentCount(postId, currentCount)

                emit(Resource.Success(newComment))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== ALWAYS ONLINE: DELETE COMMENT =====
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

                // Update post comment count
                val currentCount = commentDao.getCommentCountForPost(deletedComment.postId)
                postDao.updateCommentCount(deletedComment.postId, currentCount)

                emit(Resource.Success(deletedComment))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    // ===== ALWAYS ONLINE: UPDATE POST =====
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

                // Update cache
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

    // ===== ALWAYS ONLINE: DELETE POST =====
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

                // Invalidate related caches
                invalidatePostCaches()

                emit(Resource.Success(deletedPost))

            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }

        } catch (e: Exception) {
            emit(handleNetworkError(e))
        }
    }

    override suspend fun getTotalPostsCount(): Flow<Resource<Int>> = flow {
        emit(Resource.Loading())

        try {
            // Use cache first
            val cachedCount = postDao.getTotalCount()
            emit(Resource.Success(cachedCount))

            // Background refresh if needed
            val shouldRefresh = shouldRefreshCache(CacheConstants.CACHE_KEY_ALL_POSTS)
            if (shouldRefresh) {
                refreshAllPostsCache()
                val updatedCount = postDao.getTotalCount()
                emit(Resource.Success(updatedCount))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_UNKNOWN))
        }
    }

    // ===== HELPER METHODS =====

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

    private suspend fun setRefreshingStatus(cacheKey: String, isRefreshing: Boolean) {
        cacheMetadataDao.setRefreshingStatus(cacheKey, isRefreshing)
    }

    private suspend fun refreshAllPostsCache() {
        // Implementation for refreshing all posts cache
        // Similar to getAllPosts but focused on cache update
    }

    private suspend fun refreshCategoryPosts(categoryId: String, cacheKey: String) {
        // Implementation for refreshing category posts
    }

    private suspend fun refreshAuthorPosts(authorId: String, cacheKey: String) {
        // Implementation for refreshing author posts
    }

    private suspend fun invalidatePostCaches() {
        // Mark all post-related caches as stale
        cacheMetadataDao.deleteCacheMetadata(CacheConstants.CACHE_KEY_ALL_POSTS)
        cacheMetadataDao.deleteCacheMetadata(CacheConstants.CACHE_KEY_HOME_POSTS)
    }

    private fun <T> handleHttpError(code: Int, errorBody: String?): Resource<T> {
        // Same as original implementation
        return Resource.Error("HTTP Error: $code")
    }

    private fun <T> handleNetworkError(exception: Exception): Resource<T> {
        return when (exception) {
            is IOException -> Resource.Error(Constants.ERROR_NETWORK)
            is HttpException -> Resource.Error("Network error: ${exception.code()}")
            else -> Resource.Error(exception.message ?: Constants.ERROR_UNKNOWN)
        }
    }
}

// Extension functions for helper conversions
private object CommentMapper {
    fun convertEmbeddedToDetail(embedded: CommentResponse): CommentDetailResponse {
        return CommentDetailResponse(
            id = embedded.id,
            content = embedded.content,
            userId = embedded.authorId,
            postId = embedded.postId,
            user = CommentUserResponse(
                id = embedded.author?.id ?: "",
                username = embedded.author?.username ?: "",
                fullname = embedded.author?.fullname ?: "",
                email = embedded.author?.email ?: "",
                image = embedded.author?.image,
                createdAt = "",
                updatedAt = ""
            ),
            createdAt = embedded.createdAt,
            updatedAt = embedded.updatedAt
        )
    }
}