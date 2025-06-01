package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(postId: String): Flow<Resource<Post>> {
        return repository.getPostById(postId)
    }
}