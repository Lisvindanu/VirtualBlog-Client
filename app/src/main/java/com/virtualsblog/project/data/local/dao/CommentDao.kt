package com.virtualsblog.project.data.local.dao

import androidx.room.*
import com.virtualsblog.project.data.local.entities.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt DESC")
    fun getCommentsByPostId(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE id = :commentId LIMIT 1")
    suspend fun getCommentById(commentId: String): CommentEntity?

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    suspend fun getCommentCountByPostId(postId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Update
    suspend fun updateComment(comment: CommentEntity)

    @Delete
    suspend fun deleteComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun deleteCommentsByPostId(postId: String)

    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()
}