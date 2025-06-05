package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsByAuthorIdUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(authorId: String): Flow<Resource<List<Post>>> {
        if (authorId.isBlank()) {
            return kotlinx.coroutines.flow.flow { emit(Resource.Error("ID Author tidak boleh kosong.")) }
        }
        return repository.getPostsByAuthorId(authorId)
    }
}