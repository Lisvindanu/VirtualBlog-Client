package com.virtualsblog.project.presentation

import androidx.lifecycle.ViewModel
import com.virtualsblog.project.util.NavigationEvent
import com.virtualsblog.project.util.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

/**
 * MainViewModel acts as a central communication hub for navigation events
 * and state management across different screens in the app
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val navigationState: NavigationState
) : ViewModel() {

    // Expose navigation events as SharedFlow for screens to observe
    val navigationEvents: SharedFlow<NavigationEvent> = navigationState.navigationEvents

    // Convenience methods for triggering common navigation events

    /**
     * Trigger refresh for home screen
     */
    fun refreshHome() {
        navigationState.refreshHome()
    }

    /**
     * Trigger refresh for post list screen
     */
    fun refreshPostList() {
        navigationState.refreshPostList()
    }

    /**
     * Signal that a post has been deleted
     * This will trigger refresh on multiple screens
     */
    fun postDeleted(postId: String) {
        navigationState.postDeleted(postId)
    }

    /**
     * Signal that a new post has been created
     * This will trigger refresh on multiple screens
     */
    fun postCreated(postId: String) {
        navigationState.postCreated(postId)
    }

    /**
     * Signal that a post has been updated
     * This will trigger refresh on multiple screens
     */
    fun postUpdated(postId: String) {
        navigationState.postUpdated(postId)
    }

    /**
     * Signal that user has logged out
     * This will clear all refresh flags and states
     */
    fun userLoggedOut() {
        navigationState.userLoggedOut()
    }

    /**
     * Trigger a specific navigation event
     */
    fun triggerEvent(event: NavigationEvent) {
        navigationState.triggerEvent(event)
    }

    /**
     * Get current state flags (for polling-based approach if needed)
     */
    fun getShouldRefreshHome(): Boolean = navigationState.shouldRefreshHome
    fun getShouldRefreshPostList(): Boolean = navigationState.shouldRefreshPostList

    /**
     * Clear specific refresh flags
     */
    fun clearRefreshHome() {
        navigationState.setRefreshHome(false)
    }

    fun clearRefreshPostList() {
        navigationState.setRefreshPostList(false)
    }

    /**
     * Clear all refresh flags
     */
    fun clearAllRefreshFlags() {
        navigationState.clearAllFlags()
    }
}