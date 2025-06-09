package com.virtualsblog.project.data.local.dao

import androidx.room.*
import com.virtualsblog.project.data.local.entities.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    // ===== CACHE-FIRST READS =====

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPostsFlow(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    suspend fun getAllPostsSync(): List<PostEntity>

    @Query("SELECT * FROM posts ORDER BY createdAt DESC LIMIT :limit")
    fun getHomePostsFlow(limit: Int = 10): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostByIdFlow(postId: String): Flow<PostEntity?>

    @Query("SELECT * FROM posts WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getPostsByCategoryFlow(categoryId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun getPostsByAuthorFlow(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getTotalCount(): Int

    // ===== CACHE MANAGEMENT =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    // ===== QUICK UPDATES (for real-time actions) =====

    @Query("UPDATE posts SET likes = :likes, isLiked = :isLiked WHERE id = :postId")
    suspend fun updateLikeStatus(postId: String, likes: Int, isLiked: Boolean)

    @Query("UPDATE posts SET comments = :commentCount WHERE id = :postId")
    suspend fun updateCommentCount(postId: String, commentCount: Int)

    @Query("UPDATE posts SET isStale = :isStale WHERE id = :postId")
    suspend fun markPostStale(postId: String, isStale: Boolean = true)

    @Query("UPDATE posts SET isStale = 1 WHERE lastUpdated < :timestamp")
    suspend fun markOldPostsStale(timestamp: Long)

    // ===== CACHE FRESHNESS =====

    @Query("SELECT * FROM posts WHERE isStale = 1 ORDER BY createdAt DESC")
    suspend fun getStalePost(): List<PostEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM posts)")
    suspend fun hasAnyCachedPosts(): Boolean

    @Query("SELECT lastUpdated FROM posts ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestCacheTime(): Long?
}