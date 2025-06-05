package com.virtualsblog.project.presentation.ui.screen.category.posts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.blog.GetPostsByCategoryIdUseCase
import com.virtualsblog.project.domain.usecase.blog.ToggleLikeUseCase
import com.virtualsblog.project.presentation.ui.navigation.BlogDestinations
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryPostsViewModel @Inject constructor(
    private val getPostsByCategoryIdUseCase: GetPostsByCategoryIdUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: String = savedStateHandle.get<String>(BlogDestinations.Args.CATEGORY_ID) ?: ""
    private val categoryNameArg: String = savedStateHandle.get<String>(BlogDestinations.Args.CATEGORY_NAME) ?: "Kategori"

    private val _uiState = MutableStateFlow(CategoryPostsUiState(categoryName = categoryNameArg))
    val uiState: StateFlow<CategoryPostsUiState> = _uiState.asStateFlow()

    init {
        if (categoryId.isNotBlank()) {
            loadPostsForCategory(categoryId)
        } else {
            _uiState.value = _uiState.value.copy(error = "ID Kategori tidak valid.")
        }
    }

    fun loadPostsForCategory(catId: String = categoryId) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getPostsByCategoryIdUseCase(catId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            posts = result.data ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message ?: "Gagal memuat post untuk kategori ini."
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun togglePostLike(postId: String, onConfirmDislike: () -> Unit) {
        val currentPosts = _uiState.value.posts
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return

        val currentPost = currentPosts[postIndex]
        if (currentPost.isLiked) {
            onConfirmDislike()
            return
        }
        performLikeToggle(postId)
    }

    fun performDislike(postId: String) {
        performLikeToggle(postId)
    }

    private fun performLikeToggle(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(likingPostIds = _uiState.value.likingPostIds + postId)
            toggleLikeUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val isLiked = resource.data?.first ?: false
                        _uiState.value = _uiState.value.copy(
                            posts = _uiState.value.posts.map {
                                if (it.id == postId) {
                                    it.copy(
                                        isLiked = isLiked,
                                        likes = if (isLiked) it.likes + 1 else maxOf(0, it.likes -1)
                                    )
                                } else it
                            },
                            likingPostIds = _uiState.value.likingPostIds - postId
                        )
                        // Optionally, reload all posts for the category to get the most accurate like count from server
                        // loadPostsForCategory(categoryId)
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = resource.message ?: "Gagal mengubah status like.",
                            likingPostIds = _uiState.value.likingPostIds - postId
                        )
                    }
                    is Resource.Loading -> {
                        // Handled by adding to likingPostIds
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}