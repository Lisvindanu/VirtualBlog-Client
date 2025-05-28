package com.virtualsblog.project.presentation.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkAuthenticationStatus() {
        viewModelScope.launch {
            try {
                // Check if user is logged in
                val currentUser = getCurrentUserUseCase().first()
                val isLoggedIn = currentUser != null

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = isLoggedIn,
                    shouldNavigate = true
                )
            } catch (e: Exception) {
                // If there's an error, assume user is not logged in
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    shouldNavigate = true
                )
            }
        }
    }
}