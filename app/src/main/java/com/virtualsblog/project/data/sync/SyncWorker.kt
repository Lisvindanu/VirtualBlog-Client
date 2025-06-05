package com.virtualsblog.project.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.virtualsblog.project.data.local.CacheManager
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.util.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val blogRepository: BlogRepository,
    private val cacheManager: CacheManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Clear expired cache
            cacheManager.clearExpiredCache()

            // Sync latest posts
            val postsResult = blogRepository.getAllPosts().first()
            if (postsResult is Resource.Error) {
                return Result.retry()
            }

            // Sync categories
            val categoriesResult = blogRepository.getCategories().first()
            if (categoriesResult is Resource.Error) {
                return Result.retry()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}