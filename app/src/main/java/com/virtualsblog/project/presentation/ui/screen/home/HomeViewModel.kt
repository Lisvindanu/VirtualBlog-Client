package com.virtualsblog.project.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.Post // Pastikan model Post diimport jika belum
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

    // Gunakan HomeUiState yang sudah diperbarui dengan userImageUrl
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
        loadMockPosts() // Atau metode untuk memuat post dari repository Anda
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = user != null,
                    username = user?.username ?: "",
                    userImageUrl = user?.image // <-- Perbarui userImageUrl di sini
                )
            }
        }
    }

    private fun loadMockPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                kotlinx.coroutines.delay(1000) // Simulasi delay jaringan

                val mockPosts = listOf(
                    Post(id = "1", title = "Memulai Perjalanan Android Development", content = "Android development adalah skill yang sangat berharga...", author = "Anaphygon", createdAt = "2024-01-15T10:30:00Z", updatedAt = "2024-01-15T10:30:00Z", category = "Technology"),
                    Post(id = "2", title = "Tips Produktif Bekerja dari Rumah", content = "Work from home sudah menjadi tren baru...", author = "Sari Indah", createdAt = "2024-01-14T15:20:00Z", updatedAt = "2024-01-14T15:20:00Z", category = "Lifestyle"),
                    Post(id = "3", title = "Mengenal Jetpack Compose", content = "Jetpack Compose adalah toolkit UI modern...", author = "Developer Pro", createdAt = "2024-01-13T09:15:00Z", updatedAt = "2024-01-13T09:15:00Z", category = "Technology"),
                    Post(id = "4", title = "Resep Kopi yang Sempurna", content = "Sebagai coffee lover sejati...", author = "Coffee Enthusiast", createdAt = "2024-01-12T07:45:00Z", updatedAt = "2024-01-12T07:45:00Z", category = "Food & Drink"),
                    Post(id = "5", title = "Investasi untuk Pemula", content = "Investasi adalah salah satu cara terbaik...", author = "Financial Advisor", createdAt = "2024-01-11T14:30:00Z", updatedAt = "2024-01-11T14:30:00Z", category = "Finance")
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
        // Idealnya, panggil checkAuthStatus lagi jika ada kemungkinan data user berubah
        // dan panggil metode untuk memuat ulang postingan dari sumber data.
        // checkAuthStatus() // Bisa jadi tidak perlu jika Flow sudah otomatis update
        loadMockPosts() // Untuk contoh ini, kita muat ulang mock posts
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}