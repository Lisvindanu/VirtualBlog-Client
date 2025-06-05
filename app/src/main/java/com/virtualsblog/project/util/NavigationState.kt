package com.virtualsblog.project.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton class that manages navigation state and events across the application
 * This provides a centralized way to coordinate refreshes and state changes between screens
 */
@Singleton
class NavigationState @Inject constructor() {

    // Internal mutable shared flow for navigation events
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )

    // Public read-only shared flow
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // State flags for tracking refresh requirements
    var shouldRefreshHome: Boolean = false
        private set

    var shouldRefreshPostList: Boolean = false
        private set

    /**
     * Trigger a navigation event
     */
    fun triggerEvent(event: NavigationEvent) {
        _navigationEvents.tryEmit(event)

        // Update state flags based on event type
        when (event) {
            is NavigationEvent.RefreshHome -> shouldRefreshHome = true
            is NavigationEvent.RefreshPostList -> shouldRefreshPostList = true
            is NavigationEvent.PostCreated,
            is NavigationEvent.PostUpdated,
            is NavigationEvent.PostDeleted -> {
                shouldRefreshHome = true
                shouldRefreshPostList = true
            }
            is NavigationEvent.UserLoggedOut -> clearAllFlags()
            else -> { /* No state change needed */ }
        }
    }

    /**
     * Convenience methods for common events
     */
    fun refreshHome() {
        triggerEvent(NavigationEvent.RefreshHome)
    }

    fun refreshPostList() {
        triggerEvent(NavigationEvent.RefreshPostList)
    }

    fun postCreated(postId: String) {
        triggerEvent(NavigationEvent.PostCreated(postId))
    }

    fun postUpdated(postId: String) {
        triggerEvent(NavigationEvent.PostUpdated(postId))
    }

    fun postDeleted(postId: String) {
        triggerEvent(NavigationEvent.PostDeleted(postId))
    }

    fun userLoggedOut() {
        triggerEvent(NavigationEvent.UserLoggedOut)
    }

    /**
     * State flag management
     */
    fun setRefreshHome(shouldRefresh: Boolean) {
        shouldRefreshHome = shouldRefresh
    }

    fun setRefreshPostList(shouldRefresh: Boolean) {
        shouldRefreshPostList = shouldRefresh
    }

    fun clearAllFlags() {
        shouldRefreshHome = false
        shouldRefreshPostList = false
    }
}