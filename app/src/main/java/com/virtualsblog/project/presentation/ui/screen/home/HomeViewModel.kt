// HomeViewModel.kt - Complete Cache-First Implementation
package com.virtualsblog.project.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.data.local.dao.UserDao
import com.virtualsblog.project.data.mapper.UserMapper
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.usecase.blog.GetPostsForHomeUseCase
import com.virtualsblog.project.domain.usecase.blog.GetTotalPostsCountUseCase
import com.virtualsblog.project.domain.usecase.blog.ToggleLikeUseCase
import com.virtualsblog.project.util.NavigationState
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDao: UserDao,
    private val getPostsForHomeUseCase: GetPostsForHomeUseCase,
    private val getTotalPostsCountUseCase: GetTotalPostsCountUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val navigationState: NavigationState
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        initializeHome()
    }

    private fun initializeHome() {
        checkAuthStatus()
        loadPosts()
        loadTotalPostsCount()
        observeNavigationState()
    }

    // ===== NAVIGATION STATE OBSERVER =====
    private fun observeNavigationState() {
        viewModelScope.launch {
            while (true) {
                delay(500) // Check every 500ms
                if (navigationState.shouldRefreshHome) {
                    navigationState.setRefreshHome(false)
                    forceRefreshPosts()
                }
            }
        }
    }

    // ===== AUTHENTICATION STATUS =====
    private fun checkAuthStatus() {
        viewModelScope.launch {
            combine(
                authRepository.getCurrentUser(),
                userDao.getCurrentUser()
            ) { authUser, roomUser ->
                // Prioritize Room cache, fallback to auth flow
                val currentUser = roomUser?.let { UserMapper.mapEntityToDomain(it) } ?: authUser

                _uiState.value = _uiState.value.copy(
                    isLoggedIn = currentUser != null,
                    username = currentUser?.username ?: "",
                    userImageUrl = currentUser?.image
                )
            }.collect { }
        }
    }

    // ===== CACHE-FIRST: POSTS FOR HOME =====
    private fun loadPosts() {
        viewModelScope.launch {
            getPostsForHomeUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // 🚀 SMART LOADING: Only show spinner if no cached data
                        if (_uiState.value.posts.isEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                        // If we have cached data, keep showing it while loading fresh data
                    }

                    is Resource.Success -> {
                        // ✅ SUCCESS: Update UI with fresh data (from cache or network)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            posts = resource.data ?: emptyList(),
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        // 🛡️ GRACEFUL ERROR: Only show error if no cached data
                        if (_uiState.value.posts.isEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message ?: "Gagal memuat postingan"
                            )
                        } else {
                            // Keep showing cached data, just stop refreshing
                            _uiState.value = _uiState.value.copy(
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

    // ===== CACHE-FIRST: TOTAL POSTS COUNT =====
    private fun loadTotalPostsCount() {
        viewModelScope.launch {
            getTotalPostsCountUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            totalPostsCount = resource.data ?: 0
                        )
                    }
                    is Resource.Error -> {
                        // Ignore count errors - not critical for user experience
                    }
                    is Resource.Loading -> {
                        // Ignore loading state for count - background operation
                    }
                }
            }
        }
    }

    // ===== PULL-TO-REFRESH =====
    fun refreshPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                error = null
            )

            try {
                // Refresh auth status
                checkAuthStatus()

                // Cache-first repository will handle the refresh automatically
                // No need to manually call anything - just trigger the flows
                loadPosts()
                loadTotalPostsCount()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = "Gagal merefresh data: ${e.localizedMessage}"
                )
            }
        }
    }

    // ===== POST MANAGEMENT =====
    fun removePostFromList(postId: String) {
        val currentPosts = _uiState.value.posts
        val updatedPosts = currentPosts.filterNot { it.id == postId }
        val newTotalCount = maxOf(0, _uiState.value.totalPostsCount - 1)

        _uiState.value = _uiState.value.copy(
            posts = updatedPosts,
            totalPostsCount = newTotalCount
        )
    }

    fun forceRefreshPosts() {
        // Force refresh will trigger cache refresh in repository
        // Repository will emit cached data first, then fresh data
        loadPosts()
        loadTotalPostsCount()
    }

    // ===== ERROR HANDLING =====
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // ===== LIKE SYSTEM (Real-time + Cache Update) =====
    fun togglePostLike(postId: String, onConfirmDislike: () -> Unit) {
        val currentPosts = _uiState.value.posts
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return

        val currentPost = currentPosts[postIndex]
        val wasLiked = currentPost.isLiked

        // Permanent Like System: Ask confirmation for dislike
        if (wasLiked) {
            onConfirmDislike()
            return
        }

        performLike(postId)
    }

    fun performDislike(postId: String) {
        performLike(postId) // Same API call, server handles toggle
    }

    private fun performLike(postId: String) {
        val currentPosts = _uiState.value.posts
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return

        viewModelScope.launch {
            // 🎯 OPTIMISTIC UPDATE: Show loading state immediately
            _uiState.value = _uiState.value.copy(
                likingPostIds = _uiState.value.likingPostIds + postId
            )

            toggleLikeUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // ✅ SUCCESS: Cache updated automatically by repository
                        _uiState.value = _uiState.value.copy(
                            likingPostIds = _uiState.value.likingPostIds - postId
                        )

                        // 🔄 BACKGROUND REFRESH: Get fresh data from cache
                        // Repository already updated cache, fresh data will flow automatically
                        // No manual state updates needed!
                    }

                    is Resource.Error -> {
                        // ❌ ERROR: Remove loading state, show error
                        _uiState.value = _uiState.value.copy(
                            likingPostIds = _uiState.value.likingPostIds - postId,
                            error = resource.message
                        )
                    }

                    is Resource.Loading -> {
                        // Loading state already handled by likingPostIds
                    }
                }
            }
        }
    }

    // ===== LIFECYCLE MANAGEMENT =====
    override fun onCleared() {
        super.onCleared()
        // ViewModel cleanup if needed
    }

    // ===== DEBUG/DEVELOPMENT HELPERS =====
    fun debugCacheState() {
        viewModelScope.launch {
            val currentPosts = _uiState.value.posts
            android.util.Log.d("HomeViewModel", """
                Cache State Debug:
                - Cached posts: ${currentPosts.size}
                - Is loading: ${_uiState.value.isLoading}
                - Is refreshing: ${_uiState.value.isRefreshing}
                - Error: ${_uiState.value.error}
                - Total count: ${_uiState.value.totalPostsCount}
            """.trimIndent())
        }
    }

    // ===== PERFORMANCE OPTIMIZATION =====
    private fun shouldSkipUpdate(newPosts: List<com.virtualsblog.project.domain.model.Post>): Boolean {
        val currentPosts = _uiState.value.posts

        // Skip update if posts are identical (performance optimization)
        if (currentPosts.size == newPosts.size) {
            val areIdentical = currentPosts.zip(newPosts).all { (current, new) ->
                current.id == new.id &&
                        current.likes == new.likes &&
                        current.isLiked == new.isLiked &&
                        current.comments == new.comments
            }
            return areIdentical
        }

        return false
    }
}