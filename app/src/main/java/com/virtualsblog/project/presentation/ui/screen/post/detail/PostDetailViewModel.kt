package com.virtualsblog.project.presentation.ui.screen.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.usecase.blog.GetPostByIdUseCase
import com.virtualsblog.project.domain.usecase.blog.ToggleLikeUseCase
// Import for deleting the post itself
import com.virtualsblog.project.domain.usecase.blog.DeletePostUseCase as ActualDeletePostUseCase
import com.virtualsblog.project.domain.usecase.comment.CreateCommentUseCase
import com.virtualsblog.project.domain.usecase.comment.DeleteCommentUseCase // This is for comments
import com.virtualsblog.project.preferences.UserPreferences
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first // For getting the first emission
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase, // For deleting comments
    private val actualDeletePostUseCase: ActualDeletePostUseCase, // For deleting the entire post
    private val authRepository: AuthRepository, // Kept for getCurrentUser if still needed elsewhere
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        // Fetch and set currentUserId in the state when ViewModel is created
        viewModelScope.launch {
            val userId = userPreferences.userData.map { it.userId }.first() // Get initial value
            _uiState.value = _uiState.value.copy(currentUserId = userId)
        }
    }

    // This function can still be used by the screen if it needs a direct Flow
    fun getCurrentUserId(): Flow<String?> {
        return userPreferences.userData.map { it.userId }
    }

    // Kept if the screen directly observes this, though currentUserId is now in UiState
    fun getCurrentUser(): Flow<User?> {
        return authRepository.getCurrentUser()
    }

    fun loadPost(postId: String) {
        viewModelScope.launch {
            // Reset relevant states before loading
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                comments = emptyList(),
                post = null, // Clear previous post data
                deletePostSuccess = false, // Reset delete success flag
                deletePostError = null // Reset delete error
            )

            getPostByIdUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        val postData = resource.data
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            post = postData,
                            comments = postData?.actualComments ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.message ?: "Gagal memuat detail postingan",
                            post = null // Ensure post is null on error
                        )
                    }
                }
            }
        }
    }

    fun toggleLike() {
        val currentPost = _uiState.value.post ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLikeLoading = true)
            toggleLikeUseCase(currentPost.id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val (isLiked, totalLikes) = resource.data!!
                        val updatedPost = currentPost.copy(
                            isLiked = isLiked,
                            likes = totalLikes
                        )
                        _uiState.value = _uiState.value.copy(
                            post = updatedPost,
                            isLikeLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLikeLoading = false,
                            error = resource.message // Can show this error in a Snackbar/Toast
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLikeLoading = true)
                    }
                }
            }
        }
    }

    fun createComment(content: String) {
        val currentPost = _uiState.value.post ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCommentLoading = true)
            createCommentUseCase(currentPost.id, content).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val newComment = resource.data!!
                        val updatedComments = _uiState.value.comments.toMutableList().apply {
                            add(0, newComment) // Add new comment to the top
                        }
                        val updatedPost = currentPost.copy(
                            comments = currentPost.comments + 1
                        )
                        _uiState.value = _uiState.value.copy(
                            post = updatedPost,
                            comments = updatedComments,
                            isCommentLoading = false,
                            commentText = "" // Clear comment input
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isCommentLoading = false,
                            error = resource.message // Show this error
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isCommentLoading = true)
                    }
                }
            }
        }
    }

    fun deleteComment(commentId: String) {
        val currentPost = _uiState.value.post ?: return
        viewModelScope.launch {
            // Optionally, add a loading state for deleting a specific comment if needed
            deleteCommentUseCase(commentId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val updatedComments = _uiState.value.comments.filterNot { it.id == commentId }
                        val updatedPost = currentPost.copy(
                            comments = maxOf(0, currentPost.comments - 1)
                        )
                        _uiState.value = _uiState.value.copy(
                            post = updatedPost,
                            comments = updatedComments
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = resource.message // Show this error
                        )
                    }
                    is Resource.Loading -> {
                        // Optional: loading state for specific comment deletion
                    }
                }
            }
        }
    }

    fun updateCommentText(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, deletePostError = null)
    }

    // --- ADDED FUNCTIONS for Post Deletion ---
    fun deleteCurrentPost() {
        val postIdToDelete = _uiState.value.post?.id ?: return // Ensure post ID is available
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeletingPost = true,
                deletePostError = null,
                deletePostSuccess = false
            )
            actualDeletePostUseCase(postIdToDelete).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isDeletingPost = false,
                            deletePostSuccess = true,
                            post = null // Clear the post from state upon successful deletion
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isDeletingPost = false,
                            deletePostError = resource.message ?: "Gagal menghapus postingan."
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isDeletingPost = true) // Already set, but good for clarity
                    }
                }
            }
        }
    }

    fun resetDeleteSuccessFlag() {
        _uiState.value = _uiState.value.copy(deletePostSuccess = false)
    }
    // --- END OF ADDED FUNCTIONS ---
}