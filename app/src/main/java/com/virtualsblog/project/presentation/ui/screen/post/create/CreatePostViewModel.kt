package com.virtualsblog.project.presentation.ui.screen.post.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun createPost(title: String, content: String, category: String) {
        // Input validation
        when {
            title.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    error = "Judul postingan tidak boleh kosong"
                )
                return
            }
            title.length < 5 -> {
                _uiState.value = _uiState.value.copy(
                    error = "Judul postingan minimal 5 karakter"
                )
                return
            }
            content.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    error = "Konten postingan tidak boleh kosong"
                )
                return
            }
            content.length < 20 -> {
                _uiState.value = _uiState.value.copy(
                    error = "Konten postingan minimal 20 karakter"
                )
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            try {
                // Simulate API call delay
                kotlinx.coroutines.delay(2000)

                // Mock success response
                // TODO: Replace with actual API call when backend is ready

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )

                // Auto-hide success message after 2 seconds
                kotlinx.coroutines.delay(2000)
                _uiState.value = _uiState.value.copy(isSuccess = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Gagal membuat postingan: ${e.message}",
                    isSuccess = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}