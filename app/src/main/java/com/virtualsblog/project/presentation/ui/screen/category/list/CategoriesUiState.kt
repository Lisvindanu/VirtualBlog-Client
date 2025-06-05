package com.virtualsblog.project.presentation.ui.screen.category.list

import com.virtualsblog.project.domain.model.Category

data class CategoriesUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null
)