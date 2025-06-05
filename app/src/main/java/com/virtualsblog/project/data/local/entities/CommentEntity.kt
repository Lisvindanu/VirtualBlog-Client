package com.virtualsblog.project.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorImage: String?,
    val postId: String,
    val createdAt: String,
    val updatedAt: String,
    val lastSyncTime: Long = System.currentTimeMillis()
)
