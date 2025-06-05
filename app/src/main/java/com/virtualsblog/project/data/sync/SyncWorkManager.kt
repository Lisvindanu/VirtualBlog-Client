package com.virtualsblog.project.data.sync

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncWorkManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val SYNC_WORK_NAME = "blog_sync_work"
        private const val SYNC_INTERVAL_HOURS = 6L
    }

    fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            SYNC_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
    }

    fun cancelSyncWork() {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }
}