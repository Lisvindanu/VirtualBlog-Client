package com.virtualsblog.project.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_metadata")
data class CacheMetadataEntity(
    @PrimaryKey
    val key: String, // e.g., "all_posts", "categories", "home_posts"
    val lastRefresh: Long,
    val expiresAt: Long,
    val isRefreshing: Boolean = false
)