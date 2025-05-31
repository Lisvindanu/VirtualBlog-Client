package com.virtualsblog.project.di

import android.content.Context
import androidx.room.Room
import com.virtualsblog.project.data.local.dao.UserDao
import com.virtualsblog.project.data.local.database.BlogDatabase
import com.virtualsblog.project.data.local.database.BlogDatabaseMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBlogDatabase(@ApplicationContext context: Context): BlogDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            BlogDatabase::class.java,
            "blog_database"
        )
            // Untuk production, gunakan migration instead of fallbackToDestructiveMigration
            .addMigrations(
                BlogDatabaseMigrations.MIGRATION_1_2,
                BlogDatabaseMigrations.MIGRATION_2_3,
                BlogDatabaseMigrations.MIGRATION_3_4
            )
            // Hanya gunakan ini untuk development/testing
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: BlogDatabase): UserDao {
        return database.userDao()
    }

    // Uncomment ketika Post, Category, Comment entities sudah ready
    /*
    @Provides
    @Singleton
    fun providePostDao(database: BlogDatabase): PostDao {
        return database.postDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: BlogDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideCommentDao(database: BlogDatabase): CommentDao {
        return database.commentDao()
    }
    */
}