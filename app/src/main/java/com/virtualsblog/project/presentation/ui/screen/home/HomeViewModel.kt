package com.virtualsblog.project.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
        loadMockPosts()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = user != null,
                    username = user?.username ?: ""
                )
            }
        }
    }

    private fun loadMockPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Simulate network delay
                kotlinx.coroutines.delay(1000)

                // Mock posts data
                val mockPosts = listOf(
                    Post(
                        id = "1",
                        title = "Memulai Perjalanan Android Development",
                        content = "Android development adalah skill yang sangat berharga di era digital ini. Dalam artikel ini, saya akan berbagi pengalaman bagaimana memulai belajar Android development dari nol hingga bisa membuat aplikasi pertama.",
                        author = "Anaphygon",
                        createdAt = "2024-01-15T10:30:00Z",
                        updatedAt = "2024-01-15T10:30:00Z",
                        category = "Technology"
                    ),
                    Post(
                        id = "2",
                        title = "Tips Produktif Bekerja dari Rumah",
                        content = "Work from home sudah menjadi tren baru. Berikut adalah beberapa tips yang saya terapkan untuk tetap produktif ketika bekerja dari rumah. Mulai dari mengatur workspace hingga time management yang efektif.",
                        author = "Sari Indah",
                        createdAt = "2024-01-14T15:20:00Z",
                        updatedAt = "2024-01-14T15:20:00Z",
                        category = "Lifestyle"
                    ),
                    Post(
                        id = "3",
                        title = "Mengenal Jetpack Compose",
                        content = "Jetpack Compose adalah toolkit UI modern untuk Android. Dengan Compose, kita bisa membuat UI yang responsive dan beautiful dengan kode yang lebih sederhana. Mari kita pelajari dasar-dasarnya!",
                        author = "Developer Pro",
                        createdAt = "2024-01-13T09:15:00Z",
                        updatedAt = "2024-01-13T09:15:00Z",
                        category = "Technology"
                    ),
                    Post(
                        id = "4",
                        title = "Resep Kopi yang Sempurna",
                        content = "Sebagai coffee lover, saya ingin berbagi resep kopi yang selalu saya buat setiap pagi. Dengan bahan-bahan sederhana, kita bisa membuat kopi yang rasanya seperti di cafe favorit.",
                        author = "Coffee Enthusiast",
                        createdAt = "2024-01-12T07:45:00Z",
                        updatedAt = "2024-01-12T07:45:00Z",
                        category = "Food & Drink"
                    ),
                    Post(
                        id = "5",
                        title = "Investasi untuk Pemula",
                        content = "Investasi tidak sesulit yang dibayangkan. Artikel ini akan membahas dasar-dasar investasi yang perlu diketahui oleh pemula, termasuk jenis-jenis investasi dan tips memulai dengan modal kecil.",
                        author = "Financial Advisor",
                        createdAt = "2024-01-11T14:30:00Z",
                        updatedAt = "2024-01-11T14:30:00Z",
                        category = "Finance"
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    posts = mockPosts,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Gagal memuat postingan: ${e.message}"
                )
            }
        }
    }

    fun refreshPosts() {
        loadMockPosts()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}