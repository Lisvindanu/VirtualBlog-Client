package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.local.entities.PostEntity
import com.virtualsblog.project.data.remote.dto.response.PostDetailResponse
import com.virtualsblog.project.data.remote.dto.response.PostResponse
import com.virtualsblog.project.domain.model.Post

object PostMapper {

    // From API Response to Domain
    fun mapResponseToDomain(response: PostResponse): Post {
        return Post(
            id = response.id,
            title = response.title,
            content = response.content,
            author = response.author.fullname,
            authorId = response.authorId,
            authorUsername = response.author.username,
            authorImage = response.author.image,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            category = response.category.name,
            categoryId = response.categoryId,
            likes = response.count?.Like ?: 0,
            comments = response.count?.Comment ?: 0,
            isLiked = response.liked,
            image = response.image,
            slug = response.slug,
            actualComments = CommentMapper.mapEmbeddedCommentResponseListToDomain(response.comments ?: emptyList())
        )
    }

    fun mapDetailResponseToDomain(response: PostDetailResponse): Post {
        return Post(
            id = response.id,
            title = response.title,
            content = response.content,
            author = response.author.fullname,
            authorId = response.authorId,
            authorUsername = response.author.username,
            authorImage = response.author.image,
            category = response.category.name,
            categoryId = response.categoryId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            likes = response.count?.Like ?: 0,
            comments = response.count?.Comment ?: 0,
            isLiked = response.liked,
            image = response.image,
            slug = response.slug,
            actualComments = CommentMapper.mapEmbeddedCommentResponseListToDomain(response.comments ?: emptyList())
        )
    }

    // ===== CACHE MAPPING =====

    // From API Response to Cache Entity
    fun mapResponseToEntity(response: PostResponse, lastUpdated: Long = System.currentTimeMillis()): PostEntity {
        return PostEntity(
            id = response.id,
            title = response.title,
            content = response.content,
            author = response.author.fullname,
            authorId = response.authorId,
            authorUsername = response.author.username,
            authorImage = response.author.image,
            category = response.category.name,
            categoryId = response.categoryId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            likes = response.count?.Like ?: 0,
            comments = response.count?.Comment ?: 0,
            isLiked = response.liked,
            image = response.image,
            slug = response.slug,
            lastUpdated = lastUpdated,
            isStale = false
        )
    }

    fun mapDetailResponseToEntity(response: PostDetailResponse, lastUpdated: Long = System.currentTimeMillis()): PostEntity {
        return PostEntity(
            id = response.id,
            title = response.title,
            content = response.content,
            author = response.author.fullname,
            authorId = response.authorId,
            authorUsername = response.author.username,
            authorImage = response.author.image,
            category = response.category.name,
            categoryId = response.categoryId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            likes = response.count?.Like ?: 0,
            comments = response.count?.Comment ?: 0,
            isLiked = response.liked,
            image = response.image,
            slug = response.slug,
            lastUpdated = lastUpdated,
            isStale = false
        )
    }

    // From Cache Entity to Domain
    fun mapEntityToDomain(entity: PostEntity): Post {
        return Post(
            id = entity.id,
            title = entity.title,
            content = entity.content,
            author = entity.author,
            authorId = entity.authorId,
            authorUsername = entity.authorUsername,
            authorImage = entity.authorImage,
            category = entity.category,
            categoryId = entity.categoryId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            likes = entity.likes,
            comments = entity.comments,
            isLiked = entity.isLiked,
            image = entity.image,
            slug = entity.slug,
            actualComments = emptyList() // Comments loaded separately
        )
    }

    // Batch operations
    fun mapResponseListToEntities(responses: List<PostResponse>, lastUpdated: Long = System.currentTimeMillis()): List<PostEntity> {
        return responses.map { mapResponseToEntity(it, lastUpdated) }
    }

    fun mapEntitiesToDomainList(entities: List<PostEntity>): List<Post> {
        return entities.map { mapEntityToDomain(it) }
    }

    fun mapResponseListToDomain(responseList: List<PostResponse>): List<Post> {
        return responseList.map { mapResponseToDomain(it) }
    }

    // ===== QUICK UPDATES (for real-time actions) =====

    // Update like status in domain model
    fun updateLikeStatus(post: Post, isLiked: Boolean, newLikeCount: Int): Post {
        return post.copy(
            isLiked = isLiked,
            likes = newLikeCount
        )
    }

    // Update comment count in domain model
    fun updateCommentCount(post: Post, newCommentCount: Int): Post {
        return post.copy(comments = newCommentCount)
    }
}