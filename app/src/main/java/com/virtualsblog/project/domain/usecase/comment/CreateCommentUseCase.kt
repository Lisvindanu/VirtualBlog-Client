package com.virtualsblog.project.domain.usecase.comment

import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(
        postId: String,
        content: String
    ): Flow<Resource<Comment>> {
        if (content.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error("Komentar tidak boleh kosong"))
            }
        }
        if (content.length < 3) {
            return kotlinx.coroutines.flow.flow {
                emit(Resource.Error("Komentar minimal 3 karakter"))
            }
        }
        return repository.createComment(postId, content.trim())
    }
}