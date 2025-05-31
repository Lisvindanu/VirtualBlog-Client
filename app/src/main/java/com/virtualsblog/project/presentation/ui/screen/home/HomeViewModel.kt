package com.virtualsblog.project.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.data.local.dao.UserDao
import com.virtualsblog.project.data.mapper.UserMapper
import com.virtualsblog.project.domain.model.Post
import com.virtualsblog.project.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDao: UserDao // Inject UserDao untuk akses Room database
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
        loadMockPosts()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Combine data dari AuthRepository dan Room database
            combine(
                authRepository.getCurrentUser(),
                userDao.getCurrentUser()
            ) { authUser, roomUser ->
                // Prioritaskan data dari Room database karena lebih up-to-date
                val currentUser = roomUser?.let { UserMapper.mapEntityToDomain(it) } ?: authUser

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = currentUser != null,
                    username = currentUser?.username ?: "",
                    userImageUrl = currentUser?.image
                )
            }.collect { /* Data sudah diproses di atas */ }
        }
    }

    private fun loadMockPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                kotlinx.coroutines.delay(1000) // Simulasi delay jaringan

                val mockPosts = listOf(
                    Post(
                        id = "1",
                        title = "Memulai Perjalanan Android Development",
                        content = "Android development adalah skill yang sangat berharga di era digital ini. Dalam artikel ini, saya akan berbagi pengalaman bagaimana memulai belajar Android development dari nol hingga bisa membuat aplikasi pertama...",
                        author = "Anaphygon",
                        createdAt = "2024-01-15T10:30:00Z",
                        updatedAt = "2024-01-15T10:30:00Z",
                        category = "Technology",
                        likes = 24,
                        comments = 8,
                        isLiked = false
                    ),
                    Post(
                        id = "2",
                        title = "Tips Produktif Bekerja dari Rumah",
                        content = "Work from home sudah menjadi tren baru sejak pandemi. Banyak perusahaan yang memutuskan untuk tetap menerapkan sistem kerja hybrid atau full remote. Berikut adalah beberapa tips yang saya terapkan untuk tetap produktif ketika bekerja dari rumah...",
                        author = "Sari Indah",
                        createdAt = "2024-01-14T15:20:00Z",
                        updatedAt = "2024-01-14T15:20:00Z",
                        category = "Lifestyle",
                        likes = 42,
                        comments = 15,
                        isLiked = true
                    ),
                    Post(
                        id = "3",
                        title = "Mengenal Jetpack Compose",
                        content = "Jetpack Compose adalah toolkit UI modern untuk Android yang memungkinkan kita membuat UI dengan pendekatan deklaratif. Berbeda dengan sistem View tradisional, Compose menggunakan fungsi Composable untuk membangun UI...",
                        author = "Developer Pro",
                        createdAt = "2024-01-13T09:15:00Z",
                        updatedAt = "2024-01-13T09:15:00Z",
                        category = "Technology",
                        likes = 67,
                        comments = 23,
                        isLiked = false
                    ),
                    Post(
                        id = "4",
                        title = "Resep Kopi yang Sempurna",
                        content = "Sebagai coffee lover sejati, saya ingin berbagi resep kopi yang selalu saya buat setiap pagi. Dengan bahan-bahan sederhana dan teknik yang tepat, kita bisa membuat kopi yang rasanya seperti di cafe favorit...",
                        author = "Coffee Enthusiast",
                        createdAt = "2024-01-12T07:45:00Z",
                        updatedAt = "2024-01-12T07:45:00Z",
                        category = "Food & Drink",
                        likes = 89,
                        comments = 31,
                        isLiked = true
                    ),
                    Post(
                        id = "5",
                        title = "Investasi untuk Pemula",
                        content = "Investasi adalah salah satu cara terbaik untuk membangun kekayaan jangka panjang. Namun, banyak pemula yang merasa bingung harus mulai dari mana. Artikel ini akan membahas dasar-dasar investasi yang perlu diketahui sebelum memulai...",
                        author = "Financial Advisor",
                        createdAt = "2024-01-11T14:30:00Z",
                        updatedAt = "2024-01-11T14:30:00Z",
                        category = "Finance",
                        likes = 156,
                        comments = 47,
                        isLiked = false
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
        // Refresh auth status dan posts
        checkAuthStatus()
        loadMockPosts()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}