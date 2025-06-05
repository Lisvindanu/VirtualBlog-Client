package com.virtualsblog.project.data.local

import com.virtualsblog.project.data.local.dao.CategoryDao
import com.virtualsblog.project.data.local.dao.CommentDao
import com.virtualsblog.project.data.local.dao.PostDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    private val postDao: PostDao,
    private val categoryDao: CategoryDao,
    private val commentDao: CommentDao
) {
    companion object {
        private const val CACHE_EXPIRY_TIME = 24 * 60 * 60 * 1000L // 24 hours
    }

    suspend fun clearExpiredCache() = withContext(Dispatchers.IO) {
        val threshold = System.currentTimeMillis() - CACHE_EXPIRY_TIME

        postDao.deleteOldCachedPosts(threshold)
        categoryDao.deleteOldCachedCategories(threshold)
    }

    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        postDao.deleteAllPosts()
        categoryDao.deleteAllCategories()
        commentDao.deleteAllComments()
    }

    suspend fun getCacheInfo(): CacheInfo = withContext(Dispatchers.IO) {
        val totalPosts = postDao.getTotalPostsCount().first()
        val allCategories = categoryDao.getAllCategories().first()

        CacheInfo(
            totalPosts = totalPosts,
            totalCategories = allCategories.size,
            lastClearTime = System.currentTimeMillis()
        )
    }
}

data class CacheInfo(
    val totalPosts: Int,
    val totalCategories: Int,
    val lastClearTime: Long
)