package com.virtualsblog.project.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["postId"])]
)
data class CommentEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorImage: String? = null,
    val postId: String,
    val createdAt: String,
    val updatedAt: String,

    // Cache metadata
    val lastUpdated: Long = System.currentTimeMillis(),
    val isPendingSync: Boolean = false // For offline-created comments
)