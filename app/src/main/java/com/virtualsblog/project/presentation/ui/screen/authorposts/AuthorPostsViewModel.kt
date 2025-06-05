package com.virtualsblog.project.presentation.ui.screen.authorposts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.blog.GetPostsByAuthorIdUseCase
import com.virtualsblog.project.domain.usecase.blog.ToggleLikeUseCase
import com.virtualsblog.project.presentation.ui.navigation.BlogDestinations
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@HiltViewModel
class AuthorPostsViewModel @Inject constructor(
    private val getPostsByAuthorIdUseCase: GetPostsByAuthorIdUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val authorId: String = savedStateHandle.get<String>(BlogDestinations.Args.AUTHOR_ID) ?: ""
    private val encodedAuthorName: String = savedStateHandle.get<String>(BlogDestinations.Args.AUTHOR_NAME) ?: "Pengguna"
    private val authorNameArg: String = try {
        URLDecoder.decode(encodedAuthorName, StandardCharsets.UTF_8.toString())
    } catch (e: Exception) {
        "Pengguna" // Fallback if decoding fails
    }


    private val _uiState = MutableStateFlow(AuthorPostsUiState(authorName = authorNameArg))
    val uiState: StateFlow<AuthorPostsUiState> = _uiState.asStateFlow()

    init {
        if (authorId.isNotBlank()) {
            loadPostsForAuthor(authorId)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = false, error = "ID Author tidak valid.")
        }
    }

    fun loadPostsForAuthor(authId: String = authorId) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getPostsByAuthorIdUseCase(authId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            posts = result.data ?: emptyList(),
                            error = null
                            // authorName can also be updated here if posts have consistent author name
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message ?: "Gagal memuat postingan dari author ini."
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
                                        likes = if (isLiked) it.likes + 1 else kotlin.math.max(0, it.likes - 1)
                                    )
                                } else it
                            },
                            likingPostIds = _uiState.value.likingPostIds - postId
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = resource.message ?: "Gagal mengubah status like.",
                            likingPostIds = _uiState.value.likingPostIds - postId
                        )
                    }
                    is Resource.Loading -> {
                        // Handled
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}