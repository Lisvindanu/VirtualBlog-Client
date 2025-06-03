// DeleteCommentUseCase.kt
package com.virtualsblog.project.domain.usecase.comment

import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(
        commentId: String
    ): Flow<Resource<Comment>> {
        return repository.deleteComment(commentId)
    }
}