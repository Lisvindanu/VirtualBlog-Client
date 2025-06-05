// PostMapper.kt - Updated untuk memastikan like status tersimpan
package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.remote.dto.response.PostDetailResponse
import com.virtualsblog.project.data.remote.dto.response.PostResponse
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.data.mapper.CommentMapper

object PostMapper {

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
            // FIXED: Pastikan like status dari server digunakan
            isLiked = response.liked, // Ini sangat penting untuk persistent like state
            image = response.image,
            slug = response.slug,
            actualComments = CommentMapper.mapEmbeddedCommentResponseListToDomain(response.comments ?: emptyList())
        )
    }

    fun mapResponseListToDomain(responseList: List<PostResponse>): List<Post> {
        return responseList.map { mapResponseToDomain(it) }
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
            // FIXED: Pastikan like status dari server digunakan
            isLiked = response.liked, // Ini sangat penting untuk persistent like state
            image = response.image,
            slug = response.slug,
            actualComments = CommentMapper.mapEmbeddedCommentResponseListToDomain(response.comments ?: emptyList())
        )
    }
}