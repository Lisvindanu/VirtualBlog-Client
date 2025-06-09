// CommentMapper.kt - Enhanced for real-time comments
package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.local.entities.CommentEntity
import com.virtualsblog.project.data.remote.dto.response.CommentDetailResponse
import com.virtualsblog.project.data.remote.dto.response.CommentResponse
import com.virtualsblog.project.domain.model.Comment

object CommentMapper {

    // API Detail Response to Domain
    fun mapResponseToDomain(response: CommentDetailResponse): Comment {
        return Comment(
            id = response.id,
            content = response.content ?: "",
            authorId = response.userId ?: "unknown_user_id_in_detail",
            authorName = response.user.fullname,
            authorUsername = response.user.username,
            authorImage = response.user.image,
            postId = response.postId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }

    // API Embedded Response to Domain
    fun mapEmbeddedCommentResponseToDomain(response: CommentResponse): Comment {
        return Comment(
            id = response.id,
            content = response.content ?: "",
            authorId = response.authorId ?: "unknown_author_id_embedded",
            authorName = response.author?.fullname ?: "Unknown Author",
            authorUsername = response.author?.username ?: "unknown",
            authorImage = response.author?.image,
            postId = response.postId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }

    // ===== CACHE MAPPING =====

    // API Detail Response to Cache Entity
    fun mapResponseToEntity(response: CommentDetailResponse, lastUpdated: Long = System.currentTimeMillis()): CommentEntity {
        return CommentEntity(
            id = response.id,
            content = response.content ?: "",
            authorId = response.userId ?: "unknown_user_id",
            authorName = response.user.fullname,
            authorUsername = response.user.username,
            authorImage = response.user.image,
            postId = response.postId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            lastUpdated = lastUpdated,
            isPendingSync = false
        )
    }

    // Cache Entity to Domain
    fun mapEntityToDomain(entity: CommentEntity): Comment {
        return Comment(
            id = entity.id,
            content = entity.content,
            authorId = entity.authorId,
            authorName = entity.authorName,
            authorUsername = entity.authorUsername,
            authorImage = entity.authorImage,
            postId = entity.postId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    // Domain to Cache Entity (for offline comments)
    fun mapDomainToEntity(comment: Comment, lastUpdated: Long = System.currentTimeMillis(), isPendingSync: Boolean = false): CommentEntity {
        return CommentEntity(
            id = comment.id,
            content = comment.content,
            authorId = comment.authorId,
            authorName = comment.authorName,
            authorUsername = comment.authorUsername,
            authorImage = comment.authorImage,
            postId = comment.postId,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt,
            lastUpdated = lastUpdated,
            isPendingSync = isPendingSync
        )
    }

    // Batch operations
    fun mapDetailResponseListToDomain(responseList: List<CommentDetailResponse>): List<Comment> {
        return responseList.map { mapResponseToDomain(it) }
    }

    fun mapEmbeddedCommentResponseListToDomain(responseList: List<CommentResponse>): List<Comment> {
        return responseList.map { mapEmbeddedCommentResponseToDomain(it) }
    }

    fun mapEntitiesToDomainList(entities: List<CommentEntity>): List<Comment> {
        return entities.map { mapEntityToDomain(it) }
    }

    fun mapResponseListToEntities(responses: List<CommentDetailResponse>, lastUpdated: Long = System.currentTimeMillis()): List<CommentEntity> {
        return responses.map { mapResponseToEntity(it, lastUpdated) }
    }
}