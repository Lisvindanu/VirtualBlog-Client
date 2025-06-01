package com.virtualsblog.project.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.data.local.dao.UserDao
import com.virtualsblog.project.data.mapper.UserMapper
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.usecase.blog.GetPostsForHomeUseCase
import com.virtualsblog.project.domain.usecase.blog.GetTotalPostsCountUseCase
import com.virtualsblog.project.util.Resource
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
    private val userDao: UserDao,
    private val getPostsForHomeUseCase: GetPostsForHomeUseCase,
    private val getTotalPostsCountUseCase: GetTotalPostsCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
        loadPosts()
        loadTotalPostsCount()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            combine(
                authRepository.getCurrentUser(),
                userDao.getCurrentUser()
            ) { authUser, roomUser ->
                val currentUser = roomUser?.let { UserMapper.mapEntityToDomain(it) } ?: authUser

                _uiState.value = _uiState.value.copy(
                    isLoggedIn = currentUser != null,
                    username = currentUser?.username ?: "",
                    userImageUrl = currentUser?.image
                )
            }.collect { /* Data sudah diproses di atas */ }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getPostsForHomeUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false, // Stop refreshing
                            posts = resource.data ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false, // Stop refreshing
                            error = resource.message ?: "Gagal memuat postingan"
                        )
                    }
                }
            }
        }
    }

    private fun loadTotalPostsCount() {
        viewModelScope.launch {
            getTotalPostsCountUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            totalPostsCount = resource.data ?: 0
                        )
                    }
                    is Resource.Error -> {
                        // Handle error jika diperlukan, tapi tidak perlu menampilkan error ke user
                        // karena ini hanya untuk statistik
                    }
                    is Resource.Loading -> {
                        // Loading state untuk total count tidak perlu ditampilkan
                    }
                }
            }
        }
    }

    fun refreshPosts() {
        // Set refreshing state
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        checkAuthStatus()
        loadPosts()
        loadTotalPostsCount()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}