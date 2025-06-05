// HomeViewModel.kt - Permanent Like System
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
        checkAuthStatus()
        loadPosts()
        loadTotalPostsCount()
        observeNavigationState()
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
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
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
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = resource.message ?: "Gagal memuat postingan"
                        )
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
                        // Handle error jika diperlukan
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

    // NEW: Permanent Like System with Confirmation
    fun togglePostLike(postId: String, onConfirmDislike: () -> Unit) {
        val currentPosts = _uiState.value.posts
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return

        val currentPost = currentPosts[postIndex]
        val wasLiked = currentPost.isLiked

        // PERMANENT LIKE SYSTEM: Jika sudah liked, minta konfirmasi untuk dislike
        if (wasLiked) {
            onConfirmDislike()
            return
        }

        // Jika belum liked, langsung like
        performLike(postId)
    }

    fun performDislike(postId: String) {
        performLike(postId) // Same API call, server handles toggle
    }

    private fun performLike(postId: String) {
        val currentPosts = _uiState.value.posts
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return

        val currentPost = currentPosts[postIndex]

        viewModelScope.launch {
            // Show loading state
            _uiState.value = _uiState.value.copy(
                likingPostIds = _uiState.value.likingPostIds + postId
            )

            toggleLikeUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // SUCCESS: Auto refresh untuk avoid bugs
                        _uiState.value = _uiState.value.copy(
                            likingPostIds = _uiState.value.likingPostIds - postId
                        )

                        // SILENT REFRESH: Reload data dari server
                        silentRefreshAfterLike()
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
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

    // SILENT REFRESH: Refresh tanpa loading indicator yang mengganggu
    private fun silentRefreshAfterLike() {
        viewModelScope.launch {
            // Small delay untuk user experience
            delay(300)

            try {
                getPostsForHomeUseCase().collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            // Update posts tanpa loading indicator
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
}