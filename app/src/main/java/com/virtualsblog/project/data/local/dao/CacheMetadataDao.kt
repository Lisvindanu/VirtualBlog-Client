package com.virtualsblog.project.data.local.dao

import androidx.room.*
import com.virtualsblog.project.data.local.entities.CacheMetadataEntity

@Dao
interface CacheMetadataDao {

    @Query("SELECT * FROM cache_metadata WHERE key = :key")
    suspend fun getCacheMetadata(key: String): CacheMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCacheMetadata(metadata: CacheMetadataEntity)

    @Query("DELETE FROM cache_metadata WHERE key = :key")
    suspend fun deleteCacheMetadata(key: String)

    @Query("UPDATE cache_metadata SET isRefreshing = :isRefreshing WHERE key = :key")
    suspend fun setRefreshingStatus(key: String, isRefreshing: Boolean)

    // Check if cache is valid
    suspend fun isCacheValid(key: String): Boolean {
        val metadata = getCacheMetadata(key)
        return metadata != null && metadata.expiresAt > System.currentTimeMillis()
    }

    // Check if cache exists but stale
    suspend fun isCacheStale(key: String): Boolean {
        val metadata = getCacheMetadata(key)
        return metadata != null && metadata.expiresAt <= System.currentTimeMillis()
    }
}