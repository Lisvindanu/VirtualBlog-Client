package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.remote.dto.response.CategoryResponse
import com.virtualsblog.project.domain.model.Category

object CategoryMapper {
    
    fun mapResponseToDomain(response: CategoryResponse): Category {
        return Category(
            id = response.id,
            name = response.name,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }
    
    fun mapResponseListToDomain(responseList: List<CategoryResponse>): List<Category> {
        return responseList.map { mapResponseToDomain(it) }
    }
}