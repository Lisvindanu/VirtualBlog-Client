package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.remote.dto.response.CommentDetailResponse
import com.virtualsblog.project.domain.model.Comment

object CommentMapper {

    fun mapResponseToDomain(response: CommentDetailResponse): Comment {
        return Comment(
            id = response.id,
            content = response.comment,
            authorId = response.userId,
            authorName = response.user.fullname,
            authorUsername = response.user.username,
            authorImage = response.user.image,
            postId = response.postId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }

    fun mapResponseListToDomain(responseList: List<CommentDetailResponse>): List<Comment> {
        return responseList.map { mapResponseToDomain(it) }
    }
}