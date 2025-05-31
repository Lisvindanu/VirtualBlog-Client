package com.virtualsblog.project.data.local.dao

import androidx.room.*
import com.virtualsblog.project.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentUserSync(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isCurrent = 0")
    suspend fun clearCurrentUser()

    @Query("UPDATE users SET isCurrent = 1 WHERE id = :userId")
    suspend fun setCurrentUser(userId: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("UPDATE users SET fullname = :fullname, email = :email, username = :username, image = :image, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateUserProfile(
        userId: String,
        fullname: String,
        email: String,
        username: String,
        image: String?,
        updatedAt: String
    )

    @Query("UPDATE users SET image = :imageUrl, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateUserImage(userId: String, imageUrl: String?, updatedAt: String)
}