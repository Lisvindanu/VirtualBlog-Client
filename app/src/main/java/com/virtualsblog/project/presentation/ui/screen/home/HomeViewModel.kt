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
    private val navigationState: NavigationState // ADDED
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
            // Simple polling approach - check every 500ms if refresh is needed
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
                        // Handle error jika diperlukan, tapi tidak perlu menampilkan error ke user
                        // karena ini hanya untuk statistik
                    }
                    is Resource.Loading -> {
                        // Loading state untuk total count tidak perlu ditampilkan
                    }
                }
            }
        }
    }

    // FIXED: Improved refresh function with better error handling
    fun refreshPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)

            try {
                // Refresh authentication status
                checkAuthStatus()

                // Refresh posts data
                loadPosts()

                // Refresh total posts count
                loadTotalPostsCount()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = "Gagal merefresh data: ${e.localizedMessage}"
                )
            }
        }
    }

    // FIXED: Function to handle post removal from home screen
    fun removePostFromList(postId: String) {
        val currentPosts = _uiState.value.posts
        val updatedPosts = currentPosts.filterNot { it.id == postId }
        val newTotalCount = maxOf(0, _uiState.value.totalPostsCount - 1)

        _uiState.value = _uiState.value.copy(
            posts = updatedPosts,
            totalPostsCount = newTotalCount
        )
    }

    // ADDED: Force refresh posts after navigation back from detail
    fun forceRefreshPosts() {
        loadPosts()
        loadTotalPostsCount()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun togglePostLike(postId: String) {
        viewModelScope.launch {
            toggleLikeUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val (isLiked, totalLikes) = resource.data!!
                        val updatedPosts = _uiState.value.posts.map { post ->
                            if (post.id == postId) {
                                post.copy(
                                    isLiked = isLiked,
                                    likes = totalLikes
                                )
                            } else {
                                post
                            }
                        }
                        _uiState.value = _uiState.value.copy(posts = updatedPosts)
                    }
                    is Resource.Error -> {
                        // Handle error - could show toast or snackbar
                        // For now, we'll silently fail to avoid disrupting UX
                    }
                    is Resource.Loading -> {
                        // Optional: Could show loading state for individual post
                        // For now, we'll keep it simple and not show loading
                    }
                }
            }
        }
    }
}