package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.local.entities.CategoryEntity
import com.virtualsblog.project.data.remote.dto.response.CategoryResponse
import com.virtualsblog.project.domain.model.Category

object CategoryMapper {

    // Remote to Domain
    fun mapResponseToDomain(response: CategoryResponse): Category {
        return Category(
            id = response.id,
            name = response.name,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }

    // Entity to Domain
    fun mapEntityToDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    // Domain to Entity
    fun mapDomainToEntity(category: Category): CategoryEntity {
        return CategoryEntity(
            id = category.id,
            name = category.name,
            createdAt = category.createdAt,
            updatedAt = category.updatedAt,
            lastSyncTime = System.currentTimeMillis()
        )
    }

    // Remote to Entity
    fun mapResponseToEntity(response: CategoryResponse): CategoryEntity {
        return CategoryEntity(
            id = response.id,
            name = response.name,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            lastSyncTime = System.currentTimeMillis()
        )
    }

    // List conversions
    fun mapResponseListToDomain(responseList: List<CategoryResponse>): List<Category> {
        return responseList.map { mapResponseToDomain(it) }
    }

    fun mapEntityListToDomain(entityList: List<CategoryEntity>): List<Category> {
        return entityList.map { mapEntityToDomain(it) }
    }

    fun mapDomainListToEntity(categoryList: List<Category>): List<CategoryEntity> {
        return categoryList.map { mapDomainToEntity(it) }
    }

    fun mapResponseListToEntity(responseList: List<CategoryResponse>): List<CategoryEntity> {
        return responseList.map { mapResponseToEntity(it) }
    }
}