// app/src/main/java/com/virtualsblog/project/domain/repository/BlogRepository.kt
package com.virtualsblog.project.domain.repository

import com.virtualsblog.project.domain.model.Category
import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface BlogRepository {
    suspend fun getAllPosts(): Flow<Resource<List<Post>>>
    suspend fun getPostsForHome(): Flow<Resource<List<Post>>>
    suspend fun getTotalPostsCount(): Flow<Resource<Int>>
    suspend fun getPostById(postId: String): Flow<Resource<Post>>
    suspend fun getCategories(): Flow<Resource<List<Category>>>
    suspend fun createPost(
        title: String,
        content: String,
        categoryId: String,
        photo: File
    ): Flow<Resource<Post>>

    suspend fun updatePost(
        postId: String,
        title: String,
        content: String,
        categoryId: String,
        photo: File? // Photo is optional for update
    ): Flow<Resource<Post>>

    suspend fun deletePost(postId: String): Flow<Resource<Post>>

    // COMMENT METHODS
    suspend fun createComment(
        postId: String,
        content: String
    ): Flow<Resource<Comment>>

    suspend fun deleteComment(
        commentId: String
    ): Flow<Resource<Comment>>

    // LIKE METHODS
    suspend fun toggleLike(
        postId: String
    ): Flow<Resource<Pair<Boolean, Int>>> // Returns (isLiked, totalLikes)
}