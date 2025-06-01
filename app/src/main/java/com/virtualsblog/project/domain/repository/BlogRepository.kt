package com.virtualsblog.project.domain.repository

import com.virtualsblog.project.domain.model.Category
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
}