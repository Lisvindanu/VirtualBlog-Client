package com.virtualsblog.project.presentation.ui.screen.post.create

import com.virtualsblog.project.domain.model.Category
import java.io.File

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val content: String = "",
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val selectedImageFile: File? = null,
    val selectedImageUri: String? = null,
    val titleError: String? = null,
    val contentError: String? = null,
    val categoryError: String? = null,
    val imageError: String? = null,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isCategoriesLoading: Boolean = false
)