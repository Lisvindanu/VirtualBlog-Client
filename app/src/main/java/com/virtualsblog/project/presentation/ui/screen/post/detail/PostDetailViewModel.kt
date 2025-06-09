// PostDetailViewModel.kt - Complete Cache-First + Real-time Implementation
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
import kotlinx.coroutines.delay
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
        initializePostDetail()
    }

    // ===== INITIALIZATION =====
    private fun initializePostDetail() {
        viewModelScope.launch {
            try {
                val userId = userPreferences.userData.map { it.userId }.first()
                _uiState.value = _uiState.value.copy(currentUserId = userId)
            } catch (e: Exception) {
                android.util.Log.w("PostDetailViewModel", "Failed to get current user ID", e)
            }
        }
    }

    // ===== USER INFORMATION =====
    fun getCurrentUserId(): Flow<String?> {
        return userPreferences.userData.map { it.userId }
    }

    fun getCurrentUser(): Flow<User?> {
        return authRepository.getCurrentUser()
    }

    // ===== HYBRID STRATEGY: POST LOADING (Cache → Network) =====
    fun loadPost(postId: String) {
        if (postId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "ID postingan tidak valid"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                comments = emptyList(),
                post = null,
                deletePostSuccess = false,
                deletePostError = null,
                postJustDeleted = false
            )

            // 🔄 HYBRID STRATEGY: Quick cache load → Fresh network data
            getPostByIdUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Only show loading if we don't have cached data
                        if (_uiState.value.post == null) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        val postData = resource.data
                        if (postData != null) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                post = postData,
                                comments = postData.actualComments,
                                error = null
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Data postingan tidak ditemukan",
                                post = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        // Only show error if no cached data
                        if (_uiState.value.post == null) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = resource.message ?: "Gagal memuat detail postingan",
                                post = null
                            )
                        }
                        // If we have cached data, keep showing it
                    }
                }
            }
        }
    }

    // ===== PERMANENT LIKE SYSTEM =====
    fun toggleLike(onConfirmDislike: () -> Unit) {
        val currentPost = _uiState.value.post ?: return
        val wasLiked = currentPost.isLiked

        // PERMANENT LIKE SYSTEM: Ask confirmation for dislike
        if (wasLiked) {
            onConfirmDislike()
            return
        }

        // If not liked, perform like immediately
        performLike()
    }

    fun performDislike() {
        performLike() // Same API call, server handles toggle
    }

    private fun performLike() {
        val currentPost = _uiState.value.post ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLikeLoading = true)

            toggleLikeUseCase(currentPost.id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(isLikeLoading = false)

                        // 🔄 SILENT REFRESH: Auto refresh after like
                        silentRefreshAfterLike(currentPost.id)
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

    // ===== SILENT REFRESH AFTER LIKE =====
    private fun silentRefreshAfterLike(postId: String) {
        viewModelScope.launch {
            // Small delay for better UX
            delay(300)

            try {
                getPostByIdUseCase(postId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val postData = resource.data
                            if (postData != null) {
                                _uiState.value = _uiState.value.copy(
                                    post = postData,
                                    comments = postData.actualComments,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            // Ignore errors for silent refresh
                        }
                        is Resource.Loading -> {
                            // No loading indicator for silent refresh
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore errors for silent refresh
                android.util.Log.w("PostDetailViewModel", "Silent refresh failed", e)
            }
        }
    }

    // ===== REAL-TIME COMMENTS =====
    fun createComment(content: String) {
        val currentPost = _uiState.value.post ?: return
        val trimmedContent = content.trim()

        if (trimmedContent.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Komentar tidak boleh kosong"
            )
            return
        }

        if (trimmedContent.length < 3) {
            _uiState.value = _uiState.value.copy(
                error = "Komentar minimal 3 karakter"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCommentLoading = true)

            createCommentUseCase(currentPost.id, trimmedContent).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val newComment = resource.data!!

                        // 🚀 OPTIMISTIC UPDATE: Add comment immediately
                        val updatedComments = _uiState.value.comments.toMutableList().apply {
                            add(0, newComment) // Add to top
                        }

                        val updatedPost = currentPost.copy(
                            comments = currentPost.comments + 1
                        )

                        _uiState.value = _uiState.value.copy(
                            post = updatedPost,
                            comments = updatedComments,
                            isCommentLoading = false,
                            commentText = "", // Clear input
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isCommentLoading = false,
                            error = resource.message ?: "Gagal menambahkan komentar"
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
        val commentToDelete = _uiState.value.comments.find { it.id == commentId } ?: return

        viewModelScope.launch {
            deleteCommentUseCase(commentId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // 🚀 OPTIMISTIC UPDATE: Remove comment immediately
                        val updatedComments = _uiState.value.comments.filterNot { it.id == commentId }
                        val updatedPost = currentPost.copy(
                            comments = maxOf(0, currentPost.comments - 1)
                        )

                        _uiState.value = _uiState.value.copy(
                            post = updatedPost,
                            comments = updatedComments,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = resource.message ?: "Gagal menghapus komentar"
                        )
                    }

                    is Resource.Loading -> {
                        // Optional: Add loading state for specific comment deletion
                    }
                }
            }
        }
    }

    // ===== COMMENT TEXT MANAGEMENT =====
    fun updateCommentText(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text)
    }

    // ===== POST DELETION =====
    fun deleteCurrentPost() {
        val postIdToDelete = _uiState.value.post?.id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeletingPost = true,
                deletePostError = null,
                deletePostSuccess = false,
                postJustDeleted = false,
                error = null
            )

            try {
                actualDeletePostUseCase(postIdToDelete).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            // 🎯 NAVIGATION TRIGGER: Signal deletion to other screens
                            navigationState.postDeleted(postIdToDelete)

                            _uiState.value = _uiState.value.copy(
                                isDeletingPost = false,
                                deletePostSuccess = true,
                                postJustDeleted = true,
                                post = null, // Clear post data
                                comments = emptyList(),
                                error = null
                            )
                        }

                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isDeletingPost = false,
                                deletePostError = resource.message ?: "Gagal menghapus postingan.",
                                deletePostSuccess = false,
                                postJustDeleted = false
                            )
                        }

                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isDeletingPost = true,
                                deletePostError = null,
                                postJustDeleted = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeletingPost = false,
                    deletePostError = "Terjadi kesalahan tidak terduga: ${e.localizedMessage}",
                    deletePostSuccess = false,
                    postJustDeleted = false
                )
            }
        }
    }

    // ===== ERROR HANDLING =====
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null,
            deletePostError = null
        )
    }

    // ===== STATE MANAGEMENT =====
    fun resetDeleteSuccessFlag() {
        _uiState.value = _uiState.value.copy(
            deletePostSuccess = false,
            postJustDeleted = false
        )
    }

    fun acknowledgePostDeletionHandled() {
        _uiState.value = _uiState.value.copy(postJustDeleted = false)
    }

    // ===== VALIDATION HELPERS =====
    private fun isValidComment(comment: String): Boolean {
        val trimmed = comment.trim()
        return trimmed.isNotBlank() && trimmed.length >= 3
    }

    private fun canDeleteComment(comment: Comment): Boolean {
        val currentUserId = _uiState.value.currentUserId
        return currentUserId != null && currentUserId == comment.authorId
    }

    private fun canDeletePost(): Boolean {
        val currentUserId = _uiState.value.currentUserId
        val post = _uiState.value.post
        return currentUserId != null && post != null && currentUserId == post.authorId
    }

    // ===== PERFORMANCE OPTIMIZATION =====
    private fun shouldUpdateComments(newComments: List<Comment>): Boolean {
        val currentComments = _uiState.value.comments

        if (currentComments.size != newComments.size) return true

        return !currentComments.zip(newComments).all { (current, new) ->
            current.id == new.id && current.content == new.content
        }
    }

    // ===== LIFECYCLE MANAGEMENT =====
    override fun onCleared() {
        super.onCleared()
        // Cleanup if needed
    }

    // ===== DEBUG HELPERS =====
    fun debugPostState() {
        val state = _uiState.value
        android.util.Log.d("PostDetailViewModel", """
            Post Detail State:
            - Post ID: ${state.post?.id}
            - Post Title: ${state.post?.title}
            - Comments: ${state.comments.size}
            - Is Loading: ${state.isLoading}
            - Is Like Loading: ${state.isLikeLoading}
            - Is Comment Loading: ${state.isCommentLoading}
            - Error: ${state.error}
            - Current User: ${state.currentUserId}
        """.trimIndent())
    }

    // ===== REFRESH HELPERS =====
    fun refreshPostData() {
        val postId = _uiState.value.post?.id ?: return
        loadPost(postId)
    }

    fun forceRefreshPost() {
        val postId = _uiState.value.post?.id ?: return

        // Clear current data and reload
        _uiState.value = _uiState.value.copy(
            post = null,
            comments = emptyList(),
            error = null
        )

        loadPost(postId)
    }
}