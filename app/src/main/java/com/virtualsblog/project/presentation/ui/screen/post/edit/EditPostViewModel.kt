package com.virtualsblog.project.presentation.ui.screen.post.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.blog.GetPostByIdUseCase
import com.virtualsblog.project.domain.usecase.blog.UpdatePostUseCase
import com.virtualsblog.project.presentation.ui.navigation.BlogDestinations
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
class EditPostViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPostUiState())
    val uiState: StateFlow<EditPostUiState> = _uiState.asStateFlow()

    private val postId: String = savedStateHandle.get<String>(BlogDestinations.Args.POST_ID) ?: ""

    init {
        if (postId.isNotBlank()) {
            loadPostDetails(postId)
        } else {
            _uiState.value = _uiState.value.copy(generalError = "ID Postingan tidak valid.")
        }
    }

    private fun loadPostDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPost = true, generalError = null)
            getPostByIdUseCase(id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { post ->
                            _uiState.value = _uiState.value.copy(
                                isLoadingPost = false,
                                post = post,
                                title = post.title,
                                content = post.content,
                                currentImageUrl = post.image,
                                currentCategoryId = post.categoryId // Store original category ID
                            )
                        } ?: run {
                            _uiState.value = _uiState.value.copy(isLoadingPost = false, generalError = "Gagal memuat data postingan.")
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(isLoadingPost = false, generalError = resource.message ?: "Error memuat postingan.")
                    }
                    is Resource.Loading -> {
                        // Handled by isLoadingPost
                    }
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title, titleError = null)
    }

    fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content, contentError = null)
    }

    fun updateSelectedImage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val mimeType = context.contentResolver.getType(imageUri)
                if (mimeType == null || !isValidImageType(mimeType)) {
                    _uiState.value = _uiState.value.copy(selectedImageFile = null, selectedImageUri = null, imageError = "Format gambar tidak didukung (JPG/PNG).")
                    return@launch
                }

                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream == null) {
                    _uiState.value = _uiState.value.copy(selectedImageFile = null, selectedImageUri = null, imageError = "Gagal membaca file gambar.")
                    return@launch
                }

                val fileBytes = inputStream.use { it.readBytes() }
                if (fileBytes.size > Constants.MAX_IMAGE_SIZE) {
                    _uiState.value = _uiState.value.copy(selectedImageFile = null, selectedImageUri = null, imageError = "Ukuran gambar maksimal 10MB.")
                    return@launch
                }

                val fileExtension = getFileExtension(mimeType)
                val fileName = "edited_post_image_${System.currentTimeMillis()}.$fileExtension"
                val tempFile = File(context.cacheDir, fileName).apply { writeBytes(fileBytes) }

                if (tempFile.exists() && tempFile.length() > 0) {
                    _uiState.value = _uiState.value.copy(selectedImageFile = tempFile, selectedImageUri = imageUri.toString(), imageError = null)
                } else {
                    if(tempFile.exists()) tempFile.delete() // Clean up if file creation failed partially
                    _uiState.value = _uiState.value.copy(selectedImageFile = null, selectedImageUri = null, imageError = "Gagal memproses file gambar.")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(selectedImageFile = null, selectedImageUri = null, imageError = "Error: ${e.message}")
            }
        }
    }

    private fun isValidImageType(mimeType: String): Boolean {
        val allowedTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        return allowedTypes.contains(mimeType.lowercase())
    }

    private fun getFileExtension(mimeType: String): String {
        return when (mimeType.lowercase()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            else -> "tmp" // Fallback, though validation should prevent this
        }
    }

    fun updatePost(context: Context) {
        if (!validateForm(context)) return

        val currentState = _uiState.value
        val originalCategoryId = currentState.currentCategoryId // Use stored original category ID

        if (originalCategoryId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(generalError = "ID Kategori asli tidak ditemukan. Tidak dapat mengedit.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingPost = true, generalError = null, updateSuccess = false)
            updatePostUseCase(
                postId = postId,
                title = currentState.title.trim(),
                content = currentState.content.trim(),
                categoryId = originalCategoryId, // Pass the original category ID
                photo = currentState.selectedImageFile
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdatingPost = false,
                            updateSuccess = true,
                            post = resource.data, // Update with new post data
                            // Optionally update currentImageUrl and currentCategoryId if they can change
                            currentImageUrl = resource.data?.image,
                            currentCategoryId = resource.data?.categoryId
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(isUpdatingPost = false, generalError = resource.message ?: "Gagal memperbarui post.")
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isUpdatingPost = true)
                    }
                }
            }
        }
    }

    private fun validateForm(context: Context): Boolean {
        var isValid = true
        val current = _uiState.value

        if (current.title.trim().length < 3) {
            _uiState.value = _uiState.value.copy(titleError = "Judul minimal 3 karakter.")
            isValid = false
        } else {
            _uiState.value = _uiState.value.copy(titleError = null)
        }

        if (current.content.trim().length < 10) {
            _uiState.value = _uiState.value.copy(contentError = "Konten minimal 10 karakter.")
            isValid = false
        } else {
            _uiState.value = _uiState.value.copy(contentError = null)
        }

        // Photo validation (optional for edit, only validate if new one is selected)
        current.selectedImageFile?.let {
            if (it.length() > Constants.MAX_IMAGE_SIZE) {
                _uiState.value = _uiState.value.copy(imageError = "Ukuran gambar maksimal 10MB.")
                isValid = false
            } else {
                // Get the mime type for validation
                current.selectedImageUri?.let { uriString ->
                    val uri = Uri.parse(uriString)
                    val mimeType = context.contentResolver.getType(uri) ?: ""
                    if (!isValidImageType(mimeType)) {
                        _uiState.value = _uiState.value.copy(imageError = "Format gambar tidak didukung (JPG/PNG).")
                        isValid = false
                    } else {
                        _uiState.value = _uiState.value.copy(imageError = null)
                    }
                }
            }
        }
        return isValid
    }

    fun clearGeneralError() {
        _uiState.value = _uiState.value.copy(generalError = null)
    }

    fun resetUpdateSuccessFlag() {
        _uiState.value = _uiState.value.copy(updateSuccess = false)
    }
}