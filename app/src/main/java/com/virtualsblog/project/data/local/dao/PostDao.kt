package com.virtualsblog.project.data.local.dao

import androidx.room.*
import com.virtualsblog.project.data.local.entities.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts ORDER BY createdAt DESC LIMIT :limit")
    fun getPostsForHome(limit: Int = 10): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM posts")
    fun getTotalPostsCount(): Flow<Int>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    fun getPostByIdFlow(postId: String): Flow<PostEntity?>

    @Query("SELECT * FROM posts WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getPostsByCategoryId(categoryId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun getPostsByAuthorId(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%' ORDER BY createdAt DESC")
    fun searchPosts(keyword: String): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)


    @Query("UPDATE posts SET comments = :comments WHERE id = :postId")
    suspend fun updatePostComments(postId: String, comments: Int)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Query("DELETE FROM posts WHERE lastSyncTime < :threshold")
    suspend fun deleteOldCachedPosts(threshold: Long)

    @Query("SELECT * FROM posts WHERE isCached = 1 AND lastSyncTime < :threshold")
    suspend fun getExpiredCachedPosts(threshold: Long): List<PostEntity>
}