package com.virtualsblog.project.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.virtualsblog.project.data.local.dao.*
import com.virtualsblog.project.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CategoryEntity::class,
        CommentEntity::class,
        CacheMetadataEntity::class
    ],
    version = 3, // Versi database dinaikkan menjadi 3
    exportSchema = false
)
abstract class BlogDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun categoryDao(): CategoryDao
    abstract fun commentDao(): CommentDao
    abstract fun cacheMetadataDao(): CacheMetadataDao

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
                    // Daftarkan semua migrasi di sini
                    .addMigrations(
                        BlogDatabaseMigrations.MIGRATION_1_2,
                        BlogDatabaseMigrations.MIGRATION_2_3 // <-- Migrasi baru ditambahkan
                    )
                    .fallbackToDestructiveMigration(false) // Untuk development
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}