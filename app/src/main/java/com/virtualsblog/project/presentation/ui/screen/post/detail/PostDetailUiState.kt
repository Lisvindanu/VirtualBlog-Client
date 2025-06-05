package com.virtualsblog.project.presentation.ui.screen.post.detail

import com.virtualsblog.project.domain.model.Comment
import com.virtualsblog.project.domain.model.Post

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val commentText: String = "",
    val isCommentLoading: Boolean = false,
    val isLikeLoading: Boolean = false,
    val error: String? = null,
    // Delete states
    val isDeletingPost: Boolean = false,
    val deletePostError: String? = null,
    val deletePostSuccess: Boolean = false,
    val postJustDeleted: Boolean = false,
    val currentUserId: String? = null // To check authorship
)