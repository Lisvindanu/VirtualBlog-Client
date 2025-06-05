package com.virtualsblog.project.domain.usecase.blog

import com.virtualsblog.project.domain.model.SearchData
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(keyword: String): Flow<Resource<SearchData>> {
        if (keyword.isBlank()) {
            return kotlinx.coroutines.flow.flow { emit(Resource.Error("Keyword tidak boleh kosong.")) }
        }
        // Minimum keyword length check if needed by API (docs say min 1 char for query param)
        if (keyword.length < 1) {
            return kotlinx.coroutines.flow.flow { emit(Resource.Error("Keyword minimal 1 karakter.")) }
        }
        return repository.search(keyword)
    }
}