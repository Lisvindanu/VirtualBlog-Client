package com.virtualsblog.project.data.local.dao

import androidx.room.*
import com.virtualsblog.project.data.local.entities.CacheMetadataEntity

@Dao
interface CacheMetadataDao {

    @Query("SELECT * FROM cache_metadata WHERE `key` = :key")
    suspend fun getCacheMetadata(key: String): CacheMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCacheMetadata(metadata: CacheMetadataEntity)

    @Query("DELETE FROM cache_metadata WHERE `key` = :key")
    suspend fun deleteCacheMetadata(key: String)

    @Query("UPDATE cache_metadata SET isRefreshing = :isRefreshing WHERE `key` = :key")
    suspend fun setRefreshingStatus(key: String, isRefreshing: Boolean)

    @Query("DELETE FROM cache_metadata")
    suspend fun clearAllCacheMetadata()

    @Query("SELECT * FROM cache_metadata")
    suspend fun getAllCacheMetadata(): List<CacheMetadataEntity>

    // Helper methods with proper error handling
    suspend fun isCacheValid(key: String): Boolean {
        return try {
            val metadata = getCacheMetadata(key)
            metadata != null && metadata.expiresAt > System.currentTimeMillis()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isCacheStale(key: String): Boolean {
        return try {
            val metadata = getCacheMetadata(key)
            metadata != null && metadata.expiresAt <= System.currentTimeMillis()
        } catch (e: Exception) {
            true // Assume stale on error
        }
    }

    // Additional utility methods
    suspend fun getCacheAge(key: String): Long {
        return try {
            val metadata = getCacheMetadata(key)
            if (metadata != null) {
                System.currentTimeMillis() - metadata.lastRefresh
            } else {
                Long.MAX_VALUE // Very old if not found
            }
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }

    suspend fun isCurrentlyRefreshing(key: String): Boolean {
        return try {
            val metadata = getCacheMetadata(key)
            metadata?.isRefreshing == true
        } catch (e: Exception) {
            false
        }
    }
}