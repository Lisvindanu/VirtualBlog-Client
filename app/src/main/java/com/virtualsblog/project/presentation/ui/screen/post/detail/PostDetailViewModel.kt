// PostDetailViewModel.kt - FIXED: API-First Dynamic Data Strategy
package com.virtualsblog.project.presentation.ui.screen.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // ===== 🎯 SIMPLIFIED: POST LOADING =====
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

            // 🚀 REPOSITORY HANDLES EVERYTHING: Cache + Fresh API
            getPostByIdUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Only show loading if we don't have data yet
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
                                post = postData, // Complete data with fresh likes/comments
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
                    }
                }
            }
        }
    }

    // ===== 💓 SIMPLIFIED LIKE SYSTEM =====
    fun toggleLike(onConfirmDislike: () -> Unit) {
        val currentPost = _uiState.value.post ?: return

        // Confirmation for dislike
        if (currentPost.isLiked) {
            onConfirmDislike()
            return
        }

        performLike()
    }

    fun performDislike() {
        performLike()
    }

    private fun performLike() {
        val currentPost = _uiState.value.post ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLikeLoading = true)

            toggleLikeUseCase(currentPost.id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(isLikeLoading = false)

                        // 🔄 AUTO REFRESH: Get fresh data after like
                        delay(300) // Small delay for better UX
                        refreshPostData()
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

    // ===== 💬 SIMPLIFIED COMMENTS =====
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
                        _uiState.value = _uiState.value.copy(
                            isCommentLoading = false,
                            commentText = "", // Clear input
                            error = null
                        )

                        // 🔄 AUTO REFRESH: Get fresh data with new comment
                        delay(200)
                        refreshPostData()
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
        viewModelScope.launch {
            deleteCommentUseCase(commentId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(error = null)

                        // 🔄 AUTO REFRESH: Get fresh data after delete
                        delay(200)
                        refreshPostData()
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = resource.message ?: "Gagal menghapus komentar"
                        )
                    }

                    is Resource.Loading -> {
                        // Optional: Add loading state for specific comment
                    }
                }
            }
        }
    }

    // ===== COMMENT TEXT MANAGEMENT =====
    fun updateCommentText(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text)
    }

    // ===== 🗑️ POST DELETION =====
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
                            // Signal deletion to other screens
                            navigationState.postDeleted(postIdToDelete)

                            _uiState.value = _uiState.value.copy(
                                isDeletingPost = false,
                                deletePostSuccess = true,
                                postJustDeleted = true,
                                post = null,
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

    // ===== 🔄 REFRESH HELPERS =====
    fun refreshPostData() {
        val postId = _uiState.value.post?.id ?: return

        // Don't show loading for silent refresh
        viewModelScope.launch {
            getPostByIdUseCase(postId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val postData = resource.data
                        if (postData != null) {
                            _uiState.value = _uiState.value.copy(
                                post = postData, // Fresh data with updated counts
                                comments = postData.actualComments,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        // Ignore errors for silent refresh
                    }
                    is Resource.Loading -> {
                        // Don't show loading for silent refresh
                    }
                }
            }
        }
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

    // ===== LIFECYCLE MANAGEMENT =====
    override fun onCleared() {
        super.onCleared()
        // ViewModel cleanup if needed
    }

    // ===== 📊 DEBUG HELPERS =====
    fun debugPostState() {
        val state = _uiState.value
        android.util.Log.d("PostDetailViewModel", """
            📖 Post Detail State Debug:
            - Post ID: ${state.post?.id}
            - Post Title: ${state.post?.title}
            - Likes: ${state.post?.likes} (isLiked: ${state.post?.isLiked})
            - Comments: ${state.comments.size} (count from post: ${state.post?.comments})
            - Is Loading: ${state.isLoading}
            - Is Like Loading: ${state.isLikeLoading}
            - Is Comment Loading: ${state.isCommentLoading}
            - Error: ${state.error}
            - Current User: ${state.currentUserId}
            
            🎯 Strategy: Repository handles cache + fresh API
            💓 Likes/Comments always from API for real-time accuracy
            ⚡ Static content cached, dynamic content fresh
        """.trimIndent())
    }

    /**
     * 🎯 KEY CHANGES SUMMARY:
     *
     * BEFORE (Complex):
     * ❌ Manual optimistic updates in ViewModel
     * ❌ Manual cache management
     * ❌ Complex state synchronization
     * ❌ Like/comment counts stored in local DB
     *
     * AFTER (Simple):
     * ✅ Repository handles everything automatically
     * ✅ Like/comment counts always from API
     * ✅ Auto-refresh after actions for fresh data
     * ✅ ViewModel just triggers actions & handles UI state
     * ✅ No manual cache manipulation
     *
     * BENEFITS:
     * 🚀 Faster initial load (cached static data)
     * 📊 Accurate counts (fresh API dynamic data)
     * 🐛 Fewer bugs (less complex state management)
     * 🔄 Automatic sync between screens
     * 💾 Better cache strategy (static vs dynamic separation)
     */
}