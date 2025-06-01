package com.virtualsblog.project.presentation.ui.screen.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.blog.GetPostByIdUseCase
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getPostByIdUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            post = resource.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.message ?: "Gagal memuat detail postingan"
                        )
                    }
                }
            }
        }
    }

    fun toggleLike() {
        // TODO: Implementasi like/unlike functionality
        val currentPost = _uiState.value.post ?: return
        
        // For now, just toggle locally
        val updatedPost = currentPost.copy(
            isLiked = !currentPost.isLiked,
            likes = if (currentPost.isLiked) currentPost.likes - 1 else currentPost.likes + 1
        )
        
        _uiState.value = _uiState.value.copy(post = updatedPost)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}