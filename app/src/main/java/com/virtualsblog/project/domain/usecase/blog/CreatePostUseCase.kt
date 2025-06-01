package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        categoryId: String,
        photo: File
    ): Flow<Resource<Post>> {
        return repository.createPost(title, content, categoryId, photo)
    }
}