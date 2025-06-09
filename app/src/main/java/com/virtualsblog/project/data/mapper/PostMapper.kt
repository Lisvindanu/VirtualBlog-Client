// PostMapper.kt - FIXED: Always get dynamic data from API
package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.local.entities.PostEntity
import com.virtualsblog.project.data.remote.dto.response.PostDetailResponse
import com.virtualsblog.project.data.remote.dto.response.PostResponse
import com.virtualsblog.project.domain.model.Post

object PostMapper {

    // ===== API TO DOMAIN (ALWAYS COMPLETE DATA) =====

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
            // 🔥 ALWAYS FROM API - Real-time data
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
            // 🔥 ALWAYS FROM API - Real-time data
            likes = response.count?.Like ?: 0,
            comments = response.count?.Comment ?: 0,
            isLiked = response.liked,
            image = response.image,
            slug = response.slug,
            actualComments = CommentMapper.mapEmbeddedCommentResponseListToDomain(response.comments ?: emptyList())
        )
    }

    // ===== CACHE MAPPING (STATIC DATA ONLY) =====

    // From API Response to Cache Entity (NO dynamic data)
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
            image = response.image,
            slug = response.slug,
            lastUpdated = lastUpdated,
            isStale = false
            // 🚫 NO likes, comments, isLiked - these come from API only!
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
            image = response.image,
            slug = response.slug,
            lastUpdated = lastUpdated,
            isStale = false
            // 🚫 NO likes, comments, isLiked - these come from API only!
        )
    }

    // ===== CACHE TO DOMAIN (PLACEHOLDER FOR DYNAMIC DATA) =====

    // From Cache Entity to Domain - with PLACEHOLDER dynamic data
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
            image = entity.image,
            slug = entity.slug,
            // 🎯 PLACEHOLDER VALUES - Real data comes from API refresh
            likes = 0, // Will be updated from API
            comments = 0, // Will be updated from API
            isLiked = false, // Will be updated from API
            actualComments = emptyList() // Will be updated from API
        )
    }

    // ===== BATCH OPERATIONS =====

    fun mapResponseListToEntities(responses: List<PostResponse>, lastUpdated: Long = System.currentTimeMillis()): List<PostEntity> {
        return responses.map { mapResponseToEntity(it, lastUpdated) }
    }

    fun mapEntitiesToDomainList(entities: List<PostEntity>): List<Post> {
        return entities.map { mapEntityToDomain(it) }
    }

    fun mapResponseListToDomain(responseList: List<PostResponse>): List<Post> {
        return responseList.map { mapResponseToDomain(it) }
    }

    // ===== HYBRID STRATEGIES FOR COMBINING CACHE + API =====

    /**
     * Combine cached static data with fresh API dynamic data
     * Use this when you have cached post but want fresh like/comment data
     */
    fun combineEntityWithApiResponse(entity: PostEntity, apiResponse: PostResponse): Post {
        return Post(
            // Static data from cache (faster)
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
            image = entity.image,
            slug = entity.slug,
            // Dynamic data from API (always fresh)
            likes = apiResponse.count?.Like ?: 0,
            comments = apiResponse.count?.Comment ?: 0,
            isLiked = apiResponse.liked,
            actualComments = CommentMapper.mapEmbeddedCommentResponseListToDomain(apiResponse.comments ?: emptyList())
        )
    }

    /**
     * Update only dynamic data in existing Post
     * Use this for optimistic updates or real-time refreshes
     */
    fun updateDynamicData(post: Post, likes: Int, comments: Int, isLiked: Boolean): Post {
        return post.copy(
            likes = likes,
            comments = comments,
            isLiked = isLiked
        )
    }

    /**
     * Check if two posts are the same (ignoring dynamic data)
     * Use this to determine if cache needs updating
     */
    fun areStaticDataEqual(post1: Post, post2: Post): Boolean {
        return post1.id == post2.id &&
                post1.title == post2.title &&
                post1.content == post2.content &&
                post1.authorId == post2.authorId &&
                post1.categoryId == post2.categoryId &&
                post1.updatedAt == post2.updatedAt
    }
}