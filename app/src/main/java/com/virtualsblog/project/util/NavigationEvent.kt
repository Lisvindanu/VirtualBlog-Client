package com.virtualsblog.project.util

/**
 * Sealed class representing different navigation events that can occur across the app
 * These events are used to coordinate state changes between different screens
 */
sealed class NavigationEvent {
    // Home screen events
    object RefreshHome : NavigationEvent()

    // Post list events
    object RefreshPostList : NavigationEvent()

    // Post lifecycle events
    data class PostCreated(val postId: String) : NavigationEvent()
    data class PostUpdated(val postId: String) : NavigationEvent()
    data class PostDeleted(val postId: String) : NavigationEvent()

    // User events
    object UserLoggedOut : NavigationEvent()

    // Generic refresh event for any screen
    data class RefreshScreen(val screenId: String) : NavigationEvent()
}