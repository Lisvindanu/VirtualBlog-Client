package com.virtualsblog.project.presentation.ui.screen.post.edit

import com.virtualsblog.project.domain.model.Post
import java.io.File

data class EditPostUiState(
    val isLoadingPost: Boolean = false,
    val isUpdatingPost: Boolean = false,
    val post: Post? = null,
    val title: String = "",
    val content: String = "",
    val selectedImageFile: File? = null,
    val selectedImageUri: String? = null,
    val currentImageUrl: String? = null,
    val titleError: String? = null,
    val contentError: String? = null,
    val imageError: String? = null,
    val generalError: String? = null,
    val updateSuccess: Boolean = false
)