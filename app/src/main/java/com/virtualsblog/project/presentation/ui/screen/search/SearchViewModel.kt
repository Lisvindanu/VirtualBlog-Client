package com.virtualsblog.project.presentation.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.blog.SearchUseCase
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel() // Cancel previous job if user is still typing
        if (query.length >= 2) { // Perform search if query is 2 or more characters, or adjust as needed
            searchJob = viewModelScope.launch {
                delay(500) // Debounce: wait for 500ms after user stops typing
                performSearch(query)
            }
        } else if (query.isEmpty()) {
            // Clear results if query is empty, and reset to initial state
            _uiState.value = _uiState.value.copy(searchResults = null, error = null, isInitialState = true)
        }
    }

    fun performSearch(query: String = _uiState.value.searchQuery) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Keyword pencarian tidak boleh kosong.",
                searchResults = null,
                isInitialState = true // Back to initial state if query is blanked after a search
            )
            return
        }
        searchJob?.cancel() // Cancel any existing debounced job
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, isInitialState = false)
            searchUseCase(query).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            searchResults = result.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message ?: "Gagal melakukan pencarian.",
                            searchResults = null // Clear previous results on error
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null, isInitialState = false)
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSearch() {
        _uiState.value = SearchUiState() // Reset to initial state
        searchJob?.cancel()
    }
}