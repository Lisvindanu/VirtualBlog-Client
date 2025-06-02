package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.remote.dto.response.PostDetailResponse
import com.virtualsblog.project.data.remote.dto.response.PostResponse
import com.virtualsblog.project.domain.model.Post

object PostMapper {

    fun mapResponseToDomain(response: PostResponse): Post {
        return Post(
            id = response.id,
            title = response.title,
            content = response.content,
            author = response.author.fullname,
            authorUsername = response.author.username,
            authorImage = response.author.image,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            category = response.category.name,
            // FIXED: Handle null count safely - post baru biasanya belum ada like/comment
            likes = response.count?.Like ?: 0,
            comments = response.count?.Comment ?: 0,
            isLiked = response.liked,
            image = response.image,
            slug = response.slug
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
            authorUsername = response.author.username,
            authorImage = response.author.image,
            category = response.category.name,
            createdAt = response.createdAt,
            updatedAt = response.updatedAt,
            // FIXED: Handle null count safely - detail post juga bisa null pada kasus tertentu
            likes = response.count?.Like ?: 0,
            comments = response.count?.Comment ?: 0,
            isLiked = response.liked,
            image = response.image,
            slug = response.slug
        )
    }
}