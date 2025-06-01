package com.virtualsblog.project.data.repository

import com.virtualsblog.project.data.mapper.PostMapper
import com.virtualsblog.project.data.remote.api.BlogApi
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.DateUtil
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
            
            // Get token untuk authorization header
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
                    // Sort posts by timestamp descending (terbaru pertama)
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
}