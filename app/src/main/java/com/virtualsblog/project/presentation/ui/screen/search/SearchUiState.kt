package com.virtualsblog.project.presentation.ui.screen.search

import com.virtualsblog.project.domain.model.SearchData

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: SearchData? = null,
    val error: String? = null,
    val isInitialState: Boolean = true // To show a prompt before first search
)