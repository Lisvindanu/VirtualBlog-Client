package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.local.entities.CategoryEntity
import com.virtualsblog.project.data.remote.dto.response.CategoryResponse
import com.virtualsblog.project.domain.model.Category

object CategoryMapper {

    // API to Domain
    fun mapResponseToDomain(response: CategoryResponse): Category {
        return Category(
            id = response.id,
            name = response.name,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }

    // API to Cache Entity
    fun mapResponseToEntity(response: CategoryResponse, lastUpdated: Long = System.currentTimeMillis()): CategoryEntity {
        return CategoryEntity(
            id = response.id,
            name = response.name,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            lastUpdated = lastUpdated
        )
    }

    // Cache Entity to Domain
    fun mapEntityToDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    // Batch operations
    fun mapResponseListToDomain(responseList: List<CategoryResponse>): List<Category> {
        return responseList.map { mapResponseToDomain(it) }
    }

    fun mapResponseListToEntities(responses: List<CategoryResponse>, lastUpdated: Long = System.currentTimeMillis()): List<CategoryEntity> {
        return responses.map { mapResponseToEntity(it, lastUpdated) }
    }

    fun mapEntitiesToDomainList(entities: List<CategoryEntity>): List<Category> {
        return entities.map { mapEntityToDomain(it) }
    }
}
