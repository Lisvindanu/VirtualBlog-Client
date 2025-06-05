package com.virtualsblog.project.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object BlogDatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add posts table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `posts` (
                    `id` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `content` TEXT NOT NULL,
                    `author` TEXT NOT NULL,
                    `authorId` TEXT NOT NULL,
                    `authorUsername` TEXT NOT NULL,
                    `authorImage` TEXT,
                    `category` TEXT NOT NULL,
                    `categoryId` TEXT NOT NULL,
                    `createdAt` TEXT NOT NULL,
                    `updatedAt` TEXT NOT NULL,
                    `likes` INTEGER NOT NULL DEFAULT 0,
                    `comments` INTEGER NOT NULL DEFAULT 0,
                    `isLiked` INTEGER NOT NULL DEFAULT 0,
                    `image` TEXT,
                    `slug` TEXT NOT NULL,
                    `isCached` INTEGER NOT NULL DEFAULT 0,
                    `lastSyncTime` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add categories table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `categories` (
                    `id` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `createdAt` TEXT NOT NULL,
                    `updatedAt` TEXT NOT NULL,
                    `lastSyncTime` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add comments table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `comments` (
                    `id` TEXT NOT NULL,
                    `content` TEXT NOT NULL,
                    `authorId` TEXT NOT NULL,
                    `authorName` TEXT NOT NULL,
                    `authorUsername` TEXT NOT NULL,
                    `authorImage` TEXT,
                    `postId` TEXT NOT NULL,
                    `createdAt` TEXT NOT NULL,
                    `updatedAt` TEXT NOT NULL,
                    `lastSyncTime` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`),
                    FOREIGN KEY(`postId`) REFERENCES `posts`(`id`) ON DELETE CASCADE
                )
            """.trimIndent())

            // Create indexes for better performance
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_postId` ON `comments` (`postId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_categoryId` ON `posts` (`categoryId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_authorId` ON `posts` (`authorId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_createdAt` ON `posts` (`createdAt`)")
        }
    }
}