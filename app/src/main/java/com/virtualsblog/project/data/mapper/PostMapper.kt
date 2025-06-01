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
            likes = response.count.Like,
            comments = response.count.Comment,
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
            likes = response.count.Like,
            comments = response.count.Comment,
            isLiked = response.liked,
            image = response.image,
            slug = response.slug
        )
    }
}