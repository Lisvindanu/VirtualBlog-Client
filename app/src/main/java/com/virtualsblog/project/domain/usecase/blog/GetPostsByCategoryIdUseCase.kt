package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsByCategoryIdUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(categoryId: String): Flow<Resource<List<Post>>> {
        if (categoryId.isBlank()) {
            return kotlinx.coroutines.flow.flow { emit(Resource.Error("ID Kategori tidak boleh kosong.")) }
        }
        return repository.getPostsByCategoryId(categoryId)
    }
}