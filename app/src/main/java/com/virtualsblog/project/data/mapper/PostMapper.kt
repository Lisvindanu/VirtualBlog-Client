package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.local.entities.PostEntity
import com.virtualsblog.project.data.remote.dto.response.PostDetailResponse
import com.virtualsblog.project.data.remote.dto.response.PostResponse
import com.virtualsblog.project.domain.model.Post

object PostMapper {

    // Remote to Domain
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

    // Entity to Domain
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

    // Domain to Entity
    fun mapDomainToEntity(post: Post, isCached: Boolean = false): PostEntity {
        return PostEntity(
            id = post.id,
            title = post.title,
            content = post.content,
            author = post.author,
            authorId = post.authorId,
            authorUsername = post.authorUsername,
            authorImage = post.authorImage,
            category = post.category,
            categoryId = post.categoryId,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
            likes = post.likes,
            comments = post.comments,
            isLiked = post.isLiked,
            image = post.image,
            slug = post.slug,
            isCached = isCached,
            lastSyncTime = System.currentTimeMillis()
        )
    }

    // Remote to Entity (for caching)
    fun mapResponseToEntity(response: PostResponse, isCached: Boolean = true): PostEntity {
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
            isCached = isCached,
            lastSyncTime = System.currentTimeMillis()
        )
    }

    // List conversions
    fun mapResponseListToDomain(responseList: List<PostResponse>): List<Post> {
        return responseList.map { mapResponseToDomain(it) }
    }

    fun mapEntityListToDomain(entityList: List<PostEntity>): List<Post> {
        return entityList.map { mapEntityToDomain(it) }
    }

    fun mapDomainListToEntity(postList: List<Post>, isCached: Boolean = false): List<PostEntity> {
        return postList.map { mapDomainToEntity(it, isCached) }
    }

    fun mapResponseListToEntity(responseList: List<PostResponse>, isCached: Boolean = true): List<PostEntity> {
        return responseList.map { mapResponseToEntity(it, isCached) }
    }
}
