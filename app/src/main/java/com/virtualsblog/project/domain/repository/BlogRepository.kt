package com.virtualsblog.project.domain.repository

import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.util.Resource
import kotlinx.coroutines.flow.Flow

interface BlogRepository {
    suspend fun getAllPosts(): Flow<Resource<List<Post>>>
}