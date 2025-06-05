package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.local.entities.CommentEntity
import com.virtualsblog.project.data.remote.dto.response.CommentDetailResponse
import com.virtualsblog.project.data.remote.dto.response.CommentResponse
import com.virtualsblog.project.domain.model.Comment

object CommentMapper {

    // Remote to Domain
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

    // Entity to Domain
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

    // Domain to Entity
    fun mapDomainToEntity(comment: Comment): CommentEntity {
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
            lastSyncTime = System.currentTimeMillis()
        )
    }

    // Remote to Entity
    fun mapResponseToEntity(response: CommentDetailResponse): CommentEntity {
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
            lastSyncTime = System.currentTimeMillis()
        )
    }

    // List conversions
    fun mapDetailResponseListToDomain(responseList: List<CommentDetailResponse>): List<Comment> {
        return responseList.map { mapResponseToDomain(it) }
    }

    fun mapEmbeddedCommentResponseListToDomain(responseList: List<CommentResponse>): List<Comment> {
        return responseList.map { mapEmbeddedCommentResponseToDomain(it) }
    }

    fun mapEntityListToDomain(entityList: List<CommentEntity>): List<Comment> {
        return entityList.map { mapEntityToDomain(it) }
    }

    fun mapDomainListToEntity(commentList: List<Comment>): List<CommentEntity> {
        return commentList.map { mapDomainToEntity(it) }
    }
}