package com.virtualsblog.project.presentation.ui.screen.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.usecase.blog.GetPostByIdUseCase
import com.virtualsblog.project.domain.usecase.blog.ToggleLikeUseCase
import com.virtualsblog.project.domain.usecase.blog.DeletePostUseCase as ActualDeletePostUseCase
import com.virtualsblog.project.domain.usecase.comment.CreateCommentUseCase
import com.virtualsblog.project.domain.usecase.comment.DeleteCommentUseCase
import com.virtualsblog.project.preferences.UserPreferences
import com.virtualsblog.project.util.NavigationState
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val actualDeletePostUseCase: ActualDeletePostUseCase,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    private val navigationState: NavigationState
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = userPreferences.userData.map { it.userId }.first()
            _uiState.value = _uiState.value.copy(currentUserId = userId)
        }
    }

    fun getCurrentUserId(): Flow<String?> {
        return userPreferences.userData.map { it.userId }
    }

    fun getCurrentUser(): Flow<User?> {
        return authRepository.getCurrentUser()
    }

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                comments = emptyList(),
                post = null,
                deletePostSuccess = false,
                deletePostError = null
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
                            post = null
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
                            error = resource.message
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
                            add(0, newComment)
                        }
                        val updatedPost = currentPost.copy(
                            comments = currentPost.comments + 1
                        )
                        _uiState.value = _uiState.value.copy(
                            post = updatedPost,
                            comments = updatedComments,
                            isCommentLoading = false,
                            commentText = ""
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isCommentLoading = false,
                            error = resource.message
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
                            error = resource.message
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


    fun deleteCurrentPost() {
        val postIdToDelete = _uiState.value.post?.id ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeletingPost = true,
                deletePostError = null,
                deletePostSuccess = false,
                error = null
            )

            try {
                actualDeletePostUseCase(postIdToDelete).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            // FIXED: Signal navigation state about deletion
                            navigationState.postDeleted(postIdToDelete)

                            _uiState.value = _uiState.value.copy(
                                isDeletingPost = false,
                                deletePostSuccess = true,
                                post = null,
                                comments = emptyList(),
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isDeletingPost = false,
                                deletePostError = resource.message ?: "Gagal menghapus postingan.",
                                deletePostSuccess = false
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isDeletingPost = true,
                                deletePostError = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeletingPost = false,
                    deletePostError = "Terjadi kesalahan tidak terduga: ${e.localizedMessage}",
                    deletePostSuccess = false
                )
            }
        }
    }



fun resetDeleteSuccessFlag() {
        _uiState.value = _uiState.value.copy(deletePostSuccess = false)
    }
}