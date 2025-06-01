package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.model.Category
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<Category>>> {
        return repository.getCategories()
    }
}
