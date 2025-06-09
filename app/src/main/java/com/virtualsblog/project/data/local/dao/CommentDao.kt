package com.virtualsblog.project.data.local.dao

import androidx.room.*
import com.virtualsblog.project.data.local.entities.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPostFlow(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    suspend fun getCommentsForPostSync(postId: String): List<CommentEntity>

    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: String): CommentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Delete
    suspend fun deleteComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun deleteCommentsForPost(postId: String)

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    suspend fun getCommentCountForPost(postId: String): Int

    // Pending sync management (for offline support)
    @Query("SELECT * FROM comments WHERE isPendingSync = 1")
    suspend fun getPendingSyncComments(): List<CommentEntity>

    @Query("UPDATE comments SET isPendingSync = :pending WHERE id = :commentId")
    suspend fun updatePendingSyncStatus(commentId: String, pending: Boolean)
}
