package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalPostsCountUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(): Flow<Resource<Int>> {
        return repository.getTotalPostsCount()
    }
}
