package com.virtualsblog.project.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object BlogDatabaseMigrations {

    /**
     * Migration from database version 1 to 2.
     * This migration adds the initial tables for posts, categories, comments, and cache metadata.
     * It assumes version 1 only contained the `users` table or was empty.
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the 'posts' table with the original schema (including likes, comments, isLiked)
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

            // Create the 'categories' table
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

            // Create the 'comments' table with a foreign key to the 'posts' table
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

            // Create an index on the 'postId' column of the 'comments' table for better query performance
            database.execSQL("""
                CREATE INDEX IF NOT EXISTS `index_comments_postId` ON `comments` (`postId`)
            """.trimIndent())

            // Create the 'cache_metadata' table to manage cache expiration
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

    /**
     * Migration from database version 2 to 3.
     * This migration modifies the 'posts' table to remove dynamic columns (`likes`, `comments`, `isLiked`)
     * to improve caching strategy and avoid storing volatile data.
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 1. Create a new temporary table with the desired schema
            database.execSQL("""
                CREATE TABLE `posts_new` (
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
                    `image` TEXT, 
                    `slug` TEXT NOT NULL, 
                    `lastUpdated` INTEGER NOT NULL, 
                    `isStale` INTEGER NOT NULL, 
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())

            // 2. Copy the data from the old table to the new table, excluding the removed columns
            database.execSQL("""
                INSERT INTO `posts_new` (id, title, content, author, authorId, authorUsername, authorImage, category, categoryId, createdAt, updatedAt, image, slug, lastUpdated, isStale)
                SELECT id, title, content, author, authorId, authorUsername, authorImage, category, categoryId, createdAt, updatedAt, image, slug, lastUpdated, isStale FROM `posts`
            """.trimIndent())

            // 3. Drop the old 'posts' table
            database.execSQL("DROP TABLE `posts`")

            // 4. Rename the new table to the original name
            database.execSQL("ALTER TABLE `posts_new` RENAME TO `posts`")

            // 5. Recreate any necessary indices on the new 'posts' table
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_authorId` ON `posts` (`authorId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_categoryId` ON `posts` (`categoryId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_createdAt` ON `posts` (`createdAt`)")
        }
    }
}
