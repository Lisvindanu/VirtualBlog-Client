// BlogDatabaseMigrations.kt - Updated for v2
package com.virtualsblog.project.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object BlogDatabaseMigrations {

    // Migration from v1 (only users) to v2 (full cache system)
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create posts table
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
                    `lastUpdated` INTEGER NOT NULL DEFAULT 0,
                    `isStale` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())

            // Create categories table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `categories` (
                    `id` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `createdAt` TEXT NOT NULL,
                    `updatedAt` TEXT NOT NULL,
                    `lastUpdated` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())

            // Create comments table
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
                    `lastUpdated` INTEGER NOT NULL DEFAULT 0,
                    `isPendingSync` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`id`),
                    FOREIGN KEY(`postId`) REFERENCES `posts`(`id`) ON DELETE CASCADE
                )
            """.trimIndent())

            // Create index for comments
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS `index_comments_postId` ON `comments` (`postId`)
            """.trimIndent())

            // Create cache metadata table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `cache_metadata` (
                    `key` TEXT NOT NULL,
                    `lastRefresh` INTEGER NOT NULL,
                    `expiresAt` INTEGER NOT NULL,
                    `isRefreshing` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`key`)
                )
            """.trimIndent())
        }
    }
}