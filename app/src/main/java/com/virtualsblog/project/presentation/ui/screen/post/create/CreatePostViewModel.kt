package com.virtualsblog.project.presentation.ui.screen.post.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.Category
import com.virtualsblog.project.domain.usecase.blog.CreatePostUseCase
import com.virtualsblog.project.domain.usecase.blog.GetCategoriesUseCase
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import com.virtualsblog.project.util.FileUtils

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCategoriesLoading = true)
            
            try {
                getCategoriesUseCase().collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                categories = resource.data ?: emptyList(),
                                isCategoriesLoading = false,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isCategoriesLoading = false,
                                error = resource.message
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isCategoriesLoading = true)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCategoriesLoading = false,
                    error = e.message ?: Constants.ERROR_UNKNOWN
                )
            }
        }
    }

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

    fun updateSelectedCategory(category: Category) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            categoryError = null
        )
    }

    fun updateSelectedImage(file: File?, uriString: String?) {
        val imageError = when {
            file == null -> "Pilih gambar untuk postingan"
            !file.exists() -> "File gambar tidak ditemukan"
            file.length() == 0L -> "File gambar kosong atau rusak"
            file.length() > Constants.MAX_IMAGE_SIZE -> "Ukuran gambar maksimal 10MB"
            !FileUtils.isValidImageFile(file) -> "File harus berupa gambar JPG, JPEG, atau PNG yang valid"
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            selectedImageFile = if (imageError == null) file else null,
            selectedImageUri = if (imageError == null) uriString else null,
            imageError = imageError
        )
    }

    fun createPost() {
        if (!validateForm()) {
            return
        }

        val currentState = _uiState.value
        val imageFile = currentState.selectedImageFile
        val category = currentState.selectedCategory

        if (imageFile == null || category == null) {
            _uiState.value = _uiState.value.copy(
                error = "Semua field harus diisi dengan benar"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                createPostUseCase(
                    title = currentState.title.trim(),
                    content = currentState.content.trim(),
                    categoryId = category.id,
                    photo = imageFile
                ).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                error = null
                            )
                            // Auto hide success message after delay seperti ProfileViewModel
                            kotlinx.coroutines.delay(3000)
                            _uiState.value = _uiState.value.copy(isSuccess = false)
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = resource.message ?: "Gagal membuat postingan"
                            )
                        }
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
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
                Constants.VALIDATION_POST_TITLE_REQUIRED
            }
            currentState.title.length < Constants.MIN_TITLE_LENGTH -> {
                hasError = true
                "Judul postingan minimal ${Constants.MIN_TITLE_LENGTH} karakter"
            }
            currentState.title.length > Constants.MAX_TITLE_LENGTH -> {
                hasError = true
                "Judul postingan maksimal ${Constants.MAX_TITLE_LENGTH} karakter"
            }
            else -> null
        }

        // Validate content
        val contentError = when {
            currentState.content.isBlank() -> {
                hasError = true
                Constants.VALIDATION_POST_CONTENT_REQUIRED
            }
            currentState.content.length < Constants.MIN_CONTENT_LENGTH -> {
                hasError = true
                "Konten postingan minimal ${Constants.MIN_CONTENT_LENGTH} karakter"
            }
            currentState.content.length > Constants.MAX_CONTENT_LENGTH -> {
                hasError = true
                "Konten postingan maksimal ${Constants.MAX_CONTENT_LENGTH} karakter"
            }
            else -> null
        }

        // Validate category
        val categoryError = when {
            currentState.selectedCategory == null -> {
                hasError = true
                "Pilih kategori postingan"
            }
            else -> null
        }

        // Validate image dengan pengecekan yang lebih ketat
        val imageError = when {
            currentState.selectedImageFile == null -> {
                hasError = true
                "Pilih gambar untuk postingan"
            }
            !currentState.selectedImageFile.exists() -> {
                hasError = true
                "File gambar tidak ditemukan"
            }
            currentState.selectedImageFile.length() == 0L -> {
                hasError = true
                "File gambar kosong atau rusak"
            }
            currentState.selectedImageFile.length() > Constants.MAX_IMAGE_SIZE -> {
                hasError = true
                "File terlalu besar (maksimal 10MB)"
            }
            !FileUtils.isValidImageFile(currentState.selectedImageFile) -> {
                hasError = true
                "File harus berupa gambar JPG, JPEG, atau PNG yang valid"
            }
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            titleError = titleError,
            contentError = contentError,
            categoryError = categoryError,
            imageError = imageError
        )

        return !hasError
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = CreatePostUiState()
        loadCategories()
    }

    fun refreshCategories() {
        loadCategories()
    }
}