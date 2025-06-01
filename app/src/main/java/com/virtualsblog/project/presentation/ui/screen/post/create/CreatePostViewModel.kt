package com.virtualsblog.project.presentation.ui.screen.post.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    // TODO: Inject CreatePostUseCase when available
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = null
        )
    }

    fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(
            content = content,
            contentError = null
        )
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            category = category,
            categoryError = null
        )
    }

    fun updateSelectedImageUri(uri: String?) {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri
        )
    }

    fun createPost() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                // TODO: Replace with actual API call
                // Simulate API call with delay
                delay(2000)

                // Simulate success response
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal membuat postingan. Silakan coba lagi."
                )
            }
        }
    }

    private fun validateForm(): Boolean {
        val currentState = _uiState.value
        var hasError = false

        // Validate title
        val titleError = when {
            currentState.title.isBlank() -> {
                hasError = true
                "Judul postingan tidak boleh kosong"
            }
            currentState.title.length < 10 -> {
                hasError = true
                "Judul postingan minimal 10 karakter"
            }
            currentState.title.length > 200 -> {
                hasError = true
                "Judul postingan maksimal 200 karakter"
            }
            else -> null
        }

        // Validate content
        val contentError = when {
            currentState.content.isBlank() -> {
                hasError = true
                "Konten postingan tidak boleh kosong"
            }
            currentState.content.length < 50 -> {
                hasError = true
                "Konten postingan minimal 50 karakter"
            }
            currentState.content.length > 5000 -> {
                hasError = true
                "Konten postingan maksimal 5000 karakter"
            }
            else -> null
        }

        // Validate category
        val categoryError = when {
            currentState.category.isBlank() -> {
                hasError = true
                "Pilih kategori postingan"
            }
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            titleError = titleError,
            contentError = contentError,
            categoryError = categoryError
        )

        return !hasError
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = CreatePostUiState()
    }
}