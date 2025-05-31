package com.virtualsblog.project.presentation.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.data.local.dao.UserDao
import com.virtualsblog.project.domain.usecase.auth.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userDao: UserDao // Inject UserDao untuk cek status login dari Room
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkAuthenticationStatus() {
        viewModelScope.launch {
            try {
                // Check user dari UseCase (DataStore)
                val currentUserFromUseCase = getCurrentUserUseCase().first()

                // Check user dari Room database
                val currentUserFromRoom = userDao.getCurrentUserSync()

                // User dianggap login jika ada di salah satu tempat
                val isLoggedIn = currentUserFromUseCase != null || currentUserFromRoom != null

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = isLoggedIn,
                    shouldNavigate = true
                )

            } catch (e: Exception) {
                // Jika ada error, coba check hanya dari Room database
                try {
                    val currentUserFromRoom = userDao.getCurrentUserSync()
                    val isLoggedIn = currentUserFromRoom != null

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = isLoggedIn,
                        shouldNavigate = true
                    )
                } catch (roomError: Exception) {
                    // Jika semua gagal, assume user tidak login
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        shouldNavigate = true
                    )
                }
            }
        }
    }
}