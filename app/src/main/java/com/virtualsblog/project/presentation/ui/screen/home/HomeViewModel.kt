package com.virtualsblog.project.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.data.local.CacheManager
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
    private val navigationState: NavigationState,
    private val cacheManager: CacheManager // Added cache manager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
        loadPosts()
        loadTotalPostsCount()
        observeNavigationState()

        // Clear expired cache on init
        viewModelScope.launch {
            try {
                cacheManager.clearExpiredCache()
            } catch (e: Exception) {
                // Ignore cache clear errors
            }
        }
    }

    private fun observeNavigationState() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(500)
                if (navigationState.shouldRefreshHome) {
                    navigationState.setRefreshHome(false)
                    forceRefreshPosts()
                }
            }
        }
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            combine(
                authRepository.getCurrentUser(),
                userDao.getCurrentUser()
            ) { authUser, roomUser ->
                val currentUser = roomUser?.let { UserMapper.mapEntityToDomain(it) } ?: authUser

                _uiState.value = _uiState.value.copy(
                    isLoggedIn = currentUser != null,
                    username = currentUser?.username ?: "",
                    userImageUrl = currentUser?.image
                )
            }.collect { /* Data sudah diproses di atas */ }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getPostsForHomeUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (_uiState.value.posts.isEmpty()) {
                            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                        }
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            posts = resource.data ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        // Only show error if we don't have cached data
                        if (_uiState.value.posts.isEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message ?: "Gagal memuat postingan"
                            )
                        } else {
                            // We have cached data, just stop loading/refreshing
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

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
                        // Handle error jika diperlukan - keep existing count
                    }
                    is Resource.Loading -> {
                        // Loading state tidak perlu ditampilkan
                    }
                }
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)

            try {
                checkAuthStatus()
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
        loadPosts()
        loadTotalPostsCount()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Permanent Like System with offline support
    fun togglePostLike(postId: String, onConfirmDislike: () -> Unit) {
        val currentPosts = _uiState.value.posts
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return

        val currentPost = currentPosts[postIndex]
        val wasLiked = currentPost.isLiked

        if (wasLiked) {
            onConfirmDislike()
            return
        }

        performLike(postId)
    }

    fun performDislike(postId: String) {
        performLike(postId)
    }

    private fun performLike(postId: String) {
        val currentPost = _uiState.value.posts.find { it.id == postId } ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                likingPostIds = _uiState.value.likingPostIds + postId
            )

            // Optimistic UI update for better UX
            val updatedPosts = _uiState.value.posts.map { post ->
                if (post.id == postId) {
                    post.copy(
                        isLiked = !post.isLiked,
                        likes = if (!post.isLiked) post.likes + 1 else maxOf(0, post.likes - 1)
                    )
                } else post
            }
            _uiState.value = _uiState.value.copy(posts = updatedPosts)

            toggleLikeUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            likingPostIds = _uiState.value.likingPostIds - postId
                        )
                        // The repository handles cache updates
                        silentRefreshAfterLike()
                    }
                    is Resource.Error -> {
                        // Revert optimistic update on error
                        val revertedPosts = _uiState.value.posts.map { post ->
                            if (post.id == postId) {
                                currentPost
                            } else post
                        }
                        _uiState.value = _uiState.value.copy(
                            posts = revertedPosts,
                            likingPostIds = _uiState.value.likingPostIds - postId,
                            error = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        // Already handled by adding to likingPostIds
                    }
                }
            }
        }
    }

    private fun silentRefreshAfterLike() {
        viewModelScope.launch {
            delay(300)
            try {
                getPostsForHomeUseCase().collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                posts = resource.data ?: emptyList(),
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            // Ignore error untuk silent refresh
                        }
                        is Resource.Loading -> {
                            // No loading indicator untuk silent refresh
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore errors for silent refresh
            }
        }
    }

    // Cache management functions
    fun clearCache() {
        viewModelScope.launch {
            try {
                cacheManager.clearAllCache()
                // Reload data after clearing cache
                loadPosts()
                loadTotalPostsCount()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Gagal membersihkan cache: ${e.localizedMessage}"
                )
            }
        }
    }

    fun getCacheInfo() {
        viewModelScope.launch {
            try {
                val cacheInfo = cacheManager.getCacheInfo()
                // You can emit this info if needed
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}