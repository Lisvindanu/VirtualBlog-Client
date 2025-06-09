package com.virtualsblog.project.presentation.ui.screen.post.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.blog.GetPostsUseCase
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState: StateFlow<PostListUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            // 🚀 Cache-First: Show cached posts instantly, refresh in background
            getPostsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Only show loading spinner if no cached data
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
                        // Only show error if no cached data available
                        if (_uiState.value.posts.isEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message ?: "Gagal memuat postingan"
                            )
                        } else {
                            // Keep showing cached data
                            _uiState.value = _uiState.value.copy(isRefreshing = false)
                        }
                    }
                }
            }
        }
    }

    fun refreshPosts() {
        // Set refreshing state and reload posts
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        loadPosts() // Cache-first repository will handle the refresh
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}