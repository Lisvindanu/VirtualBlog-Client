package com.virtualsblog.project.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object BlogDatabaseMigrations {

    // Migration dari versi 1 ke 2 (untuk masa depan ketika menambah tabel baru)
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Contoh: Menambah tabel posts
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `posts` (
                    `id` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `content` TEXT NOT NULL,
                    `author` TEXT NOT NULL,
                    `authorId` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `likes` INTEGER NOT NULL DEFAULT 0,
                    `comments` INTEGER NOT NULL DEFAULT 0,
                    `isLiked` INTEGER NOT NULL DEFAULT 0,
                    `imageUrl` TEXT,
                    `excerpt` TEXT NOT NULL DEFAULT '',
                    `readTime` INTEGER NOT NULL DEFAULT 0,
                    `isPublished` INTEGER NOT NULL DEFAULT 1,
                    `viewCount` INTEGER NOT NULL DEFAULT 0,
                    `createdAt` TEXT NOT NULL,
                    `updatedAt` TEXT NOT NULL,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())
        }
    }

    // Migration dari versi 2 ke 3 (untuk masa depan ketika menambah tabel categories)
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Contoh: Menambah tabel categories
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `categories` (
                    `id` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `slug` TEXT NOT NULL,
                    `description` TEXT,
                    `createdAt` TEXT NOT NULL,
                    `updatedAt` TEXT NOT NULL,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())
        }
    }

    // Migration dari versi 3 ke 4 (untuk masa depan ketika menambah tabel comments)
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Contoh: Menambah tabel comments
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `comments` (
                    `id` TEXT NOT NULL,
                    `postId` TEXT NOT NULL,
                    `authorId` TEXT NOT NULL,
                    `authorName` TEXT NOT NULL,
                    `content` TEXT NOT NULL,
                    `likes` INTEGER NOT NULL DEFAULT 0,
                    `isLiked` INTEGER NOT NULL DEFAULT 0,
                    `isEdited` INTEGER NOT NULL DEFAULT 0,
                    `createdAt` TEXT NOT NULL,
                    `updatedAt` TEXT NOT NULL,
                    PRIMARY KEY(`id`),
                    FOREIGN KEY(`postId`) REFERENCES `posts`(`id`) ON DELETE CASCADE,
                    FOREIGN KEY(`authorId`) REFERENCES `users`(`id`) ON DELETE CASCADE
                )
            """.trimIndent())
        }
    }
}