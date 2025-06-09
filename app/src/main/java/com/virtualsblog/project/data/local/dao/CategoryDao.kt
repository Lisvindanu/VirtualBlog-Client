// CategoryDao.kt - Perfect for aggressive caching
package com.virtualsblog.project.data.local.dao

import androidx.room.*
import com.virtualsblog.project.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategoriesSync(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("DELETE FROM categories")
    suspend fun clearAllCategories()

    @Query("SELECT EXISTS(SELECT 1 FROM categories)")
    suspend fun hasAnyCachedCategories(): Boolean

    @Query("SELECT lastUpdated FROM categories ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestCacheTime(): Long?
}