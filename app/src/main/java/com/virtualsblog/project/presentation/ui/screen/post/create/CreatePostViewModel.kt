package com.virtualsblog.project.presentation.ui.screen.post.create

import android.content.Context
import android.net.Uri
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

    // SIMPLIFIED: Image handling method yang lebih sederhana dan reliable
    fun updateSelectedImage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                // Validasi MIME type
                val mimeType = context.contentResolver.getType(imageUri)
                if (mimeType == null || !isValidImageType(mimeType)) {
                    _uiState.value = _uiState.value.copy(
                        selectedImageFile = null,
                        selectedImageUri = null,
                        imageError = "File harus berupa gambar JPG, JPEG, atau PNG"
                    )
                    return@launch
                }

                // Baca input stream untuk validasi
                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream == null) {
                    _uiState.value = _uiState.value.copy(
                        selectedImageFile = null,
                        selectedImageUri = null,
                        imageError = "Gagal membaca file gambar"
                    )
                    return@launch
                }

                // Read file content ke byte array untuk validasi ukuran
                val fileBytes = inputStream.use { it.readBytes() }

                if (fileBytes.size > Constants.MAX_IMAGE_SIZE) {
                    _uiState.value = _uiState.value.copy(
                        selectedImageFile = null,
                        selectedImageUri = null,
                        imageError = "Ukuran file maksimal 10MB"
                    )
                    return@launch
                }

                // Buat temporary file dengan nama yang proper
                val fileExtension = getFileExtension(mimeType)
                val fileName = "post_image_${System.currentTimeMillis()}.$fileExtension"
                val tempFile = File(context.cacheDir, fileName)

                // Write bytes ke file
                tempFile.writeBytes(fileBytes)

                // Validasi file hasil write
                if (tempFile.exists() && tempFile.length() > 0) {
                    _uiState.value = _uiState.value.copy(
                        selectedImageFile = tempFile,
                        selectedImageUri = imageUri.toString(),
                        imageError = null
                    )
                } else {
                    if (tempFile.exists()) tempFile.delete()
                    _uiState.value = _uiState.value.copy(
                        selectedImageFile = null,
                        selectedImageUri = null,
                        imageError = "Gagal memproses file gambar"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    selectedImageFile = null,
                    selectedImageUri = null,
                    imageError = "Error: ${e.message}"
                )
            }
        }
    }

    // Helper methods
    private fun isValidImageType(mimeType: String): Boolean {
        val allowedTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        return allowedTypes.contains(mimeType.lowercase())
    }

    private fun getFileExtension(mimeType: String): String {
        return when (mimeType.lowercase()) {
            "image/jpeg" -> "jpg"
            "image/jpg" -> "jpg"
            "image/png" -> "png"
            else -> "jpg"
        }
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

        // Validate title sesuai API requirement (minimal 3 karakter)
        val titleError = when {
            currentState.title.isBlank() -> {
                hasError = true
                "Judul postingan harus diisi"
            }
            currentState.title.length < 3 -> { // API requirement
                hasError = true
                "Judul postingan minimal 3 karakter"
            }
            currentState.title.length > Constants.MAX_TITLE_LENGTH -> {
                hasError = true
                "Judul postingan maksimal ${Constants.MAX_TITLE_LENGTH} karakter"
            }
            else -> null
        }

        // Validate content sesuai API requirement (minimal 10 karakter)
        val contentError = when {
            currentState.content.isBlank() -> {
                hasError = true
                "Konten postingan harus diisi"
            }
            currentState.content.length < 10 -> { // API requirement
                hasError = true
                "Konten postingan minimal 10 karakter"
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

        // Validate image
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
                "File gambar kosong"
            }
            currentState.selectedImageFile.length() > Constants.MAX_IMAGE_SIZE -> {
                hasError = true
                "Ukuran gambar maksimal 10MB"
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