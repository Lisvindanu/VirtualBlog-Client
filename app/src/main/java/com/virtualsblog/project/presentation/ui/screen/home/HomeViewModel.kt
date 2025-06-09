// HomeViewModel.kt - FIXED: Simplified for API-First Dynamic Data
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

    // ===== 🚀 SIMPLIFIED: POSTS LOADING =====
    private fun loadPosts() {
        viewModelScope.launch {
            getPostsForHomeUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Only show loading if no posts yet
                        if (_uiState.value.posts.isEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        // ✅ SUCCESS: Repository gives us complete data (cache + fresh API)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            posts = resource.data ?: emptyList(),
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        // Only show error if no cached data
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

    // ===== TOTAL POSTS COUNT =====
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
                        // Ignore count errors - not critical
                    }
                    is Resource.Loading -> {
                        // Ignore loading state for count
                    }
                }
            }
        }
    }

    // ===== 🔄 PULL-TO-REFRESH =====
    fun refreshPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                error = null
            )

            try {
                // Refresh auth status
                checkAuthStatus()

                // Repository will handle cache + fresh API automatically
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

    fun forceRefreshPosts() {
        // Just trigger the load - repository handles the caching strategy
        loadPosts()
        loadTotalPostsCount()
    }

    // ===== 💓 SIMPLIFIED LIKE SYSTEM =====
    fun togglePostLike(postId: String, onConfirmDislike: () -> Unit) {
        val currentPosts = _uiState.value.posts
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return

        val currentPost = currentPosts[postIndex]

        // Confirmation for dislike
        if (currentPost.isLiked) {
            onConfirmDislike()
            return
        }

        performLike(postId)
    }

    fun performDislike(postId: String) {
        performLike(postId)
    }

    private fun performLike(postId: String) {
        viewModelScope.launch {
            // Show loading state
            _uiState.value = _uiState.value.copy(
                likingPostIds = _uiState.value.likingPostIds + postId
            )

            toggleLikeUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // ✅ SUCCESS: Hide loading, then auto-refresh from API
                        _uiState.value = _uiState.value.copy(
                            likingPostIds = _uiState.value.likingPostIds - postId
                        )

                        // 🔄 AUTO REFRESH: Get fresh data to sync like counts
                        delay(300) // Small delay for better UX
                        loadPosts() // This will get fresh data from repository
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            likingPostIds = _uiState.value.likingPostIds - postId,
                            error = resource.message
                        )
                    }

                    is Resource.Loading -> {
                        // Already handled by likingPostIds
                    }
                }
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

    // ===== ERROR HANDLING =====
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // ===== LIFECYCLE MANAGEMENT =====
    override fun onCleared() {
        super.onCleared()
        // ViewModel cleanup if needed
    }

    // ===== 📊 DEBUG HELPERS =====
    fun debugCacheState() {
        viewModelScope.launch {
            val currentPosts = _uiState.value.posts
            android.util.Log.d("HomeViewModel", """
                🏠 Home State Debug:
                - Posts count: ${currentPosts.size}
                - Is loading: ${_uiState.value.isLoading}
                - Is refreshing: ${_uiState.value.isRefreshing}
                - Error: ${_uiState.value.error}
                - Total count: ${_uiState.value.totalPostsCount}
                - Currently liking: ${_uiState.value.likingPostIds}
                
                🎯 Strategy: Cache-first static data + API-first dynamic data
                📱 Likes/Comments always fresh from API
                ⚡ Static content (title, author) cached for speed
            """.trimIndent())
        }
    }

    /**
     * 🎯 KEY INSIGHT:
     *
     * With the new hybrid strategy:
     * 1. Static data (title, content, author) = CACHED for speed
     * 2. Dynamic data (likes, comments, isLiked) = ALWAYS from API for accuracy
     * 3. Repository handles this automatically
     * 4. ViewModel just triggers refreshes and handles loading states
     *
     * This gives us:
     * ✅ Fast initial load (cached static data)
     * ✅ Accurate counts (fresh API dynamic data)
     * ✅ Simple ViewModel logic
     * ✅ Automatic cache management
     */
}