// CacheConstants.kt - Cache strategy configuration
package com.virtualsblog.project.data.local

object CacheConstants {
    // Cache keys
    const val CACHE_KEY_ALL_POSTS = "all_posts"
    const val CACHE_KEY_HOME_POSTS = "home_posts"
    const val CACHE_KEY_CATEGORIES = "categories"
    const val CACHE_KEY_POST_DETAIL = "post_detail_"
    const val CACHE_KEY_AUTHOR_POSTS = "author_posts_"
    const val CACHE_KEY_CATEGORY_POSTS = "category_posts_"

    // Cache durations (in milliseconds)
    const val CACHE_DURATION_POSTS = 5 * 60 * 1000L // 5 minutes
    const val CACHE_DURATION_CATEGORIES = 60 * 60 * 1000L // 1 hour (categories rarely change)
    const val CACHE_DURATION_POST_DETAIL = 2 * 60 * 1000L // 2 minutes (comments update frequently)
    const val CACHE_DURATION_USER_POSTS = 10 * 60 * 1000L // 10 minutes

    // Stale threshold - when to prefer network over cache
    const val STALE_THRESHOLD = 30 * 1000L // 30 seconds

    // Max cache size (number of posts to keep)
    const val MAX_CACHED_POSTS = 200
    const val MAX_CACHED_COMMENTS_PER_POST = 100
}