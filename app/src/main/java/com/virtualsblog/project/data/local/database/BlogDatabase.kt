package com.virtualsblog.project.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.virtualsblog.project.data.local.dao.UserDao
import com.virtualsblog.project.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class
        // PostEntity::class,        // Untuk masa depan
        // CategoryEntity::class,    // Untuk masa depan
        // CommentEntity::class      // Untuk masa depan
    ],
    version = 1,
    exportSchema = false
)
abstract class BlogDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    // abstract fun postDao(): PostDao          // Untuk masa depan
    // abstract fun categoryDao(): CategoryDao  // Untuk masa depan
    // abstract fun commentDao(): CommentDao    // Untuk masa depan

    companion object {
        @Volatile
        private var INSTANCE: BlogDatabase? = null

        fun getDatabase(context: Context): BlogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BlogDatabase::class.java,
                    "blog_database"
                )
                    .fallbackToDestructiveMigration() // Hanya untuk development
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}