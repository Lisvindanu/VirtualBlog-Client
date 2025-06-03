package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(postId: String): Flow<Resource<Post>> {
        if (postId.isBlank()) {
            return kotlinx.coroutines.flow.flow { emit(Resource.Error("Post ID tidak boleh kosong.")) }
        }
        return repository.deletePost(postId)
    }
}