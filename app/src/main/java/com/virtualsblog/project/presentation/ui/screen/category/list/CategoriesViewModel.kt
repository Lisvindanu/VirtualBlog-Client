package com.virtualsblog.project.presentation.ui.screen.category.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.blog.GetCategoriesUseCase
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            // 🚀 Categories are perfect for aggressive caching (rarely change)
            getCategoriesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            categories = result.data ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        // Only show error if no cached data
                        if (_uiState.value.categories.isEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.message ?: "Gagal memuat kategori."
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Only show loading if no cached data
                        if (_uiState.value.categories.isEmpty()) {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}