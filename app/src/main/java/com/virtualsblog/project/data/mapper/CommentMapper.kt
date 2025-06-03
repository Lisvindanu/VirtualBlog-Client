package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.remote.dto.response.CommentDetailResponse
import com.virtualsblog.project.data.remote.dto.response.CommentResponse
import com.virtualsblog.project.domain.model.Comment

object CommentMapper {

    // Untuk memetakan respons setelah membuat/menghapus komentar (menggunakan CommentDetailResponse)
    fun mapResponseToDomain(response: CommentDetailResponse): Comment {
        return Comment(
            id = response.id,
            content = response.content ?: "", // Menggunakan response.content (sesuai DTO yang diperbarui)
            authorId = response.userId ?: "unknown_user_id_in_detail", // response.userId dari DTO
            authorName = response.user.fullname, // response.user adalah CommentUserResponse
            authorUsername = response.user.username,
            authorImage = response.user.image,
            postId = response.postId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }

    // Untuk memetakan komentar yang ter-embed di dalam list post (menggunakan CommentResponse)
    fun mapEmbeddedCommentResponseToDomain(response: CommentResponse): Comment {
        return Comment(
            id = response.id,
            content = response.content ?: "", // Menggunakan response.content (sesuai DTO yang diperbarui)
            authorId = response.authorId ?: "unknown_author_id_embedded", // response.authorId (yang sebenarnya adalah userId dari JSON)
            authorName = response.author?.fullname ?: "Unknown Author", // response.author adalah AuthorResponse
            authorUsername = response.author?.username ?: "unknown",
            authorImage = response.author?.image,
            postId = response.postId,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt
        )
    }

    fun mapDetailResponseListToDomain(responseList: List<CommentDetailResponse>): List<Comment> {
        return responseList.map { mapResponseToDomain(it) }
    }

    fun mapEmbeddedCommentResponseListToDomain(responseList: List<CommentResponse>): List<Comment> {
        return responseList.map { mapEmbeddedCommentResponseToDomain(it) }
    }
}