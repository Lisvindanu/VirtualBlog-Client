package com.virtualsblog.project.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.virtualsblog.project.data.local.dao.*
import com.virtualsblog.project.data.mapper.*
import com.virtualsblog.project.data.remote.api.BlogApi
import com.virtualsblog.project.data.remote.dto.request.CreateCommentRequest
import com.virtualsblog.project.data.remote.dto.response.ApiResponse
import com.virtualsblog.project.data.remote.dto.response.ValidationError
import com.virtualsblog.project.domain.model.*
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.DateUtil
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.*
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
    private val gson: Gson,
    // Room DAOs
    private val postDao: PostDao,
    private val categoryDao: CategoryDao,
    private val commentDao: CommentDao
) : BlogRepository {

    companion object {
        private const val CACHE_TIMEOUT = 5 * 60 * 1000L // 5 minutes
    }

    // Cache-first strategy for posts
    override suspend fun getAllPosts(): Flow<Resource<List<Post>>> = flow {
        try {
            emit(Resource.Loading())

            // First emit cached data
            val cachedPosts = postDao.getAllPosts().first()
            if (cachedPosts.isNotEmpty()) {
                emit(Resource.Success(PostMapper.mapEntityListToDomain(cachedPosts)))
            }

            // Then fetch from network
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getAllPosts("${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val posts = PostMapper.mapResponseListToDomain(body.data)

                    // Cache the posts
                    val postEntities = PostMapper.mapResponseListToEntity(body.data, isCached = true)
                    postDao.deleteAllPosts()
                    postDao.insertPosts(postEntities)

                    // Sort and emit
                    val sortedPosts = posts.sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                    emit(Resource.Success(sortedPosts))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_FAILED_LOAD_POST))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: HttpException) {
            emit(handleHttpError(e.code(), e.response()?.errorBody()?.string()))
        } catch (e: IOException) {
            // Network error - return cached data if available
            val cachedPosts = postDao.getAllPosts().first()
            if (cachedPosts.isNotEmpty()) {
                emit(Resource.Success(PostMapper.mapEntityListToDomain(cachedPosts)))
            } else {
                emit(Resource.Error("${Constants.ERROR_NETWORK}: ${e.localizedMessage}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun getPostsForHome(): Flow<Resource<List<Post>>> = flow {
        try {
            emit(Resource.Loading())

            // First emit cached data
            val cachedPosts = postDao.getPostsForHome(Constants.HOME_POSTS_LIMIT).first()
            if (cachedPosts.isNotEmpty()) {
                emit(Resource.Success(PostMapper.mapEntityListToDomain(cachedPosts)))
            }

            // Then fetch from network
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getAllPosts("${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val posts = PostMapper.mapResponseListToDomain(body.data)

                    // Cache the posts
                    val postEntities = PostMapper.mapResponseListToEntity(body.data, isCached = true)
                    postDao.deleteAllPosts()
                    postDao.insertPosts(postEntities)

                    // Sort and limit for home
                    val sortedPosts = posts.sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                    val limitedPosts = sortedPosts.take(Constants.HOME_POSTS_LIMIT)
                    emit(Resource.Success(limitedPosts))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_FAILED_LOAD_POST))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            // Network error - return cached data if available
            val cachedPosts = postDao.getPostsForHome(Constants.HOME_POSTS_LIMIT).first()
            if (cachedPosts.isNotEmpty()) {
                emit(Resource.Success(PostMapper.mapEntityListToDomain(cachedPosts)))
            } else {
                emit(Resource.Error("Network error: ${e.localizedMessage}"))
            }
        }
    }

    override suspend fun getPostById(postId: String): Flow<Resource<Post>> = flow {
        try {
            emit(Resource.Loading())

            // First check local cache
            val cachedPost = postDao.getPostById(postId)
            if (cachedPost != null) {
                // Load comments for the post
                val cachedComments = commentDao.getCommentsByPostId(postId).first()
                val post = PostMapper.mapEntityToDomain(cachedPost).copy(
                    actualComments = CommentMapper.mapEntityListToDomain(cachedComments)
                )
                emit(Resource.Success(post))
            }

            // Then fetch from network
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getPostById(postId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val post = PostMapper.mapDetailResponseToDomain(body.data)

                    // Cache the post
                    val postEntity = PostMapper.mapDomainToEntity(post, isCached = true)
                    postDao.insertPost(postEntity)

                    // Cache comments
                    val commentEntities = CommentMapper.mapDomainListToEntity(post.actualComments)
                    commentDao.deleteCommentsByPostId(postId)
                    commentDao.insertComments(commentEntities)

                    emit(Resource.Success(post))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_POST_NOT_FOUND))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            // Network error - return cached data if available
            val cachedPost = postDao.getPostById(postId)
            if (cachedPost != null) {
                val cachedComments = commentDao.getCommentsByPostId(postId).first()
                val post = PostMapper.mapEntityToDomain(cachedPost).copy(
                    actualComments = CommentMapper.mapEntityListToDomain(cachedComments)
                )
                emit(Resource.Success(post))
            } else {
                emit(Resource.Error("Post not found: ${e.localizedMessage}"))
            }
        }
    }

    override suspend fun getCategories(): Flow<Resource<List<Category>>> = flow {
        try {
            emit(Resource.Loading())

            // First emit cached data
            val cachedCategories = categoryDao.getAllCategories().first()
            if (cachedCategories.isNotEmpty()) {
                emit(Resource.Success(CategoryMapper.mapEntityListToDomain(cachedCategories)))
            }

            // Then fetch from network
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getCategories("${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val categories = CategoryMapper.mapResponseListToDomain(body.data)

                    // Cache categories
                    val categoryEntities = CategoryMapper.mapResponseListToEntity(body.data)
                    categoryDao.deleteAllCategories()
                    categoryDao.insertCategories(categoryEntities)

                    emit(Resource.Success(categories))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal memuat kategori"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            // Network error - return cached data if available
            val cachedCategories = categoryDao.getAllCategories().first()
            if (cachedCategories.isNotEmpty()) {
                emit(Resource.Success(CategoryMapper.mapEntityListToDomain(cachedCategories)))
            } else {
                emit(Resource.Error("Network error: ${e.localizedMessage}"))
            }
        }
    }

    override suspend fun getPostsByCategoryId(categoryId: String): Flow<Resource<List<Post>>> = flow {
        try {
            emit(Resource.Loading())

            // First emit cached data
            val cachedPosts = postDao.getPostsByCategoryId(categoryId).first()
            if (cachedPosts.isNotEmpty()) {
                emit(Resource.Success(PostMapper.mapEntityListToDomain(cachedPosts)))
            }

            // Then fetch from network
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getPostsByCategoryId(categoryId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val posts = PostMapper.mapResponseListToDomain(body.data)

                    // Cache only posts from this category
                    val postEntities = PostMapper.mapResponseListToEntity(body.data, isCached = true)
                    postEntities.forEach { postDao.insertPost(it) }

                    val sortedPosts = posts.sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                    emit(Resource.Success(sortedPosts))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal memuat post dari kategori ini."))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            // Network error - return cached data if available
            val cachedPosts = postDao.getPostsByCategoryId(categoryId).first()
            if (cachedPosts.isNotEmpty()) {
                emit(Resource.Success(PostMapper.mapEntityListToDomain(cachedPosts)))
            } else {
                emit(Resource.Error("Network error: ${e.localizedMessage}"))
            }
        }
    }

    override suspend fun getPostsByAuthorId(authorId: String): Flow<Resource<List<Post>>> = flow {
        try {
            emit(Resource.Loading())

            // First emit cached data
            val cachedPosts = postDao.getPostsByAuthorId(authorId).first()
            if (cachedPosts.isNotEmpty()) {
                emit(Resource.Success(PostMapper.mapEntityListToDomain(cachedPosts)))
            }

            // Then fetch from network
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getPostsByAuthorId(authorId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val posts = PostMapper.mapResponseListToDomain(body.data)

                    // Cache posts from this author
                    val postEntities = PostMapper.mapResponseListToEntity(body.data, isCached = true)
                    postEntities.forEach { postDao.insertPost(it) }

                    val sortedPosts = posts.sortedByDescending { DateUtil.getTimestamp(it.createdAt) }
                    emit(Resource.Success(sortedPosts))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal memuat postingan dari author ini."))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            // Network error - return cached data if available
            val cachedPosts = postDao.getPostsByAuthorId(authorId).first()
            if (cachedPosts.isNotEmpty()) {
                emit(Resource.Success(PostMapper.mapEntityListToDomain(cachedPosts)))
            } else {
                emit(Resource.Error("Network error: ${e.localizedMessage}"))
            }
        }
    }

    override suspend fun getTotalPostsCount(): Flow<Resource<Int>> = flow {
        try {
            emit(Resource.Loading())

            // First emit cached count
            val cachedCount = postDao.getTotalPostsCount().first()
            if (cachedCount > 0) {
                emit(Resource.Success(cachedCount))
            }

            // Then fetch from network for accurate count
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.getAllPosts("${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    emit(Resource.Success(body.data.size))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_FAILED_LOAD_POST))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            // Network error - return cached count if available
            val cachedCount = postDao.getTotalPostsCount().first()
            if (cachedCount > 0) {
                emit(Resource.Success(cachedCount))
            } else {
                emit(Resource.Error("Network error: ${e.localizedMessage}"))
            }
        }
    }

    override suspend fun search(keyword: String): Flow<Resource<SearchData>> = flow {
        emit(Resource.Loading())
        try {
            // First search in local cache
            val cachedPosts = postDao.searchPosts(keyword).first()
            if (cachedPosts.isNotEmpty()) {
                val posts = PostMapper.mapEntityListToDomain(cachedPosts)
                val searchData = SearchData(
                    users = emptyList(), // Users not cached locally
                    categories = emptyList(), // Categories handled separately
                    posts = posts
                )
                emit(Resource.Success(searchData))
            }

            // Then search on network
            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.search(keyword, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    val searchResponseData = body.data
                    val domainUsers = searchResponseData.users.map { userResponse ->
                        com.virtualsblog.project.domain.model.User(
                            id = userResponse.id,
                            username = userResponse.username,
                            fullname = userResponse.fullname,
                            email = userResponse.email,
                            image = userResponse.image.ifEmpty { null },
                            createdAt = userResponse.createdAt,
                            updatedAt = userResponse.updatedAt
                        )
                    }
                    val domainCategories = CategoryMapper.mapResponseListToDomain(searchResponseData.categories)
                    val domainPosts = PostMapper.mapResponseListToDomain(searchResponseData.posts)

                    // Cache search results
                    val postEntities = PostMapper.mapResponseListToEntity(searchResponseData.posts, isCached = true)
                    postEntities.forEach { postDao.insertPost(it) }

                    emit(Resource.Success(SearchData(
                        users = domainUsers,
                        categories = domainCategories,
                        posts = domainPosts
                    )))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal melakukan pencarian."))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            // Network error - return cached search results
            val cachedPosts = postDao.searchPosts(keyword).first()
            if (cachedPosts.isNotEmpty()) {
                val posts = PostMapper.mapEntityListToDomain(cachedPosts)
                val searchData = SearchData(
                    users = emptyList(),
                    categories = emptyList(),
                    posts = posts
                )
                emit(Resource.Success(searchData))
            } else {
                emit(Resource.Error("Network error: ${e.localizedMessage}"))
            }
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

            // Validate file
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
                else -> "application/octet-stream"
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

                    // Cache the new post
                    val postEntity = PostMapper.mapDomainToEntity(post, isCached = true)
                    postDao.insertPost(postEntity)

                    emit(Resource.Success(post))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal membuat postingan"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun updatePost(
        postId: String,
        title: String,
        content: String,
        categoryId: String,
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
            val categoryIdBody = categoryId.toRequestBody("text/plain".toMediaTypeOrNull())
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
                categoryId = categoryIdBody,
                photo = photoPart
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val updatedPost = PostMapper.mapResponseToDomain(body.data)

                    // Update cached post
                    val postEntity = PostMapper.mapDomainToEntity(updatedPost, isCached = true)
                    postDao.updatePost(postEntity)

                    emit(Resource.Success(updatedPost))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_POST_UPDATE_FAILED))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
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

            val response = blogApi.deletePost(postId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val deletedPostData = PostMapper.mapResponseToDomain(body.data)

                    // Remove from cache
                    postDao.deletePostById(postId)
                    commentDao.deleteCommentsByPostId(postId)

                    emit(Resource.Success(deletedPostData))
                } else {
                    emit(Resource.Error(body?.message ?: Constants.ERROR_POST_DELETE_FAILED))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
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

                    // Cache the new comment
                    val commentEntity = CommentMapper.mapDomainToEntity(comment)
                    commentDao.insertComment(commentEntity)

                    // Update post comment count
                    val cachedPost = postDao.getPostById(postId)
                    if (cachedPost != null) {
                        postDao.updatePostComments(postId, cachedPost.comments + 1)
                    }

                    emit(Resource.Success(comment))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal membuat komentar"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun deleteComment(commentId: String): Flow<Resource<Comment>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            // Get comment before deletion to update post count
            val commentToDelete = commentDao.getCommentById(commentId)

            val response = blogApi.deleteComment(commentId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val comment = CommentMapper.mapResponseToDomain(body.data)

                    // Remove from cache
                    commentDao.deleteCommentById(commentId)

                    // Update post comment count
                    if (commentToDelete != null) {
                        val cachedPost = postDao.getPostById(commentToDelete.postId)
                        if (cachedPost != null) {
                            postDao.updatePostComments(commentToDelete.postId, maxOf(0, cachedPost.comments - 1))
                        }
                    }

                    emit(Resource.Success(comment))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal menghapus komentar"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    override suspend fun toggleLike(postId: String): Flow<Resource<Pair<Boolean, Int>>> = flow {
        try {
            emit(Resource.Loading())

            val token = authRepository.getAuthToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error(Constants.ERROR_UNAUTHORIZED))
                return@flow
            }

            val response = blogApi.toggleLike(postId, "${Constants.BEARER_PREFIX}$token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    val message = body.message.lowercase()
                    val isLiked = when {
                        message.contains("ditambahkan") ||
                                message.contains("berhasil") && !message.contains("dihapus") -> true
                        message.contains("dihapus") ||
                                message.contains("dibatalkan") ||
                                message.contains("removed") -> false
                        else -> body.data != null
                    }

                    // Update cached post
                    val cachedPost = postDao.getPostById(postId)
                    if (cachedPost != null) {
                        val newLikeCount = if (isLiked) cachedPost.likes + 1 else maxOf(0, cachedPost.likes - 1)
                        postDao.updatePostLikes(postId, newLikeCount, isLiked)
                    }

                    emit(Resource.Success(Pair(isLiked, -1)))
                } else {
                    emit(Resource.Error(body?.message ?: "Gagal toggle like"))
                }
            } else {
                emit(handleHttpError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            emit(Resource.Error("${Constants.ERROR_UNKNOWN}: ${e.localizedMessage}"))
        }
    }

    // Generic HTTP error handler
    private fun <T> handleHttpError(code: Int, errorBody: String?): Resource<T> {
        val specificMessage = when (code) {
            400 -> {
                if (!errorBody.isNullOrEmpty()) {
                    try {
                        val errorType = object : TypeToken<ApiResponse<List<ValidationError>>>() {}.type
                        val errorResponse: ApiResponse<List<ValidationError>> = gson.fromJson(errorBody, errorType)
                        errorResponse.data.firstOrNull()?.msg ?: errorResponse.message ?: "Permintaan tidak valid atau data input salah."
                    } catch (e: Exception) {
                        if (errorBody.contains("File type not allowed", ignoreCase = true)) "Tipe file tidak diizinkan. Gunakan JPG, JPEG, atau PNG"
                        else if (errorBody.contains("photo wajib diisi", ignoreCase = true)) "Gambar wajib diupload"
                        else if (errorBody.contains("Keyword pencarian wajib diisi", ignoreCase = true)) "Keyword pencarian wajib diisi"
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
                        val errorType = object : TypeToken<ApiResponse<String>>() {}.type
                        val errorResponse: ApiResponse<String> = gson.fromJson(errorBody, errorType)
                        when {
                            errorResponse.data?.contains("File type not allowed", ignoreCase = true) == true -> "Tipe file tidak diizinkan. Gunakan JPG, JPEG, atau PNG"
                            errorResponse.data?.contains("Failed to upload file", ignoreCase = true) == true -> "Gagal mengunggah file ke server."
                            else -> errorResponse.message ?: Constants.ERROR_UNKNOWN
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
