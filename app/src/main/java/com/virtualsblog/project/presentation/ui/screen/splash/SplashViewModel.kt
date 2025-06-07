package com.virtualsblog.project.presentation.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.data.remote.api.AuthApi
import com.virtualsblog.project.preferences.UserPreferences
import com.virtualsblog.project.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkAuthenticationStatus() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Ambil token dari preferences
                val token = userPreferences.getAccessToken()

                if (token.isNullOrEmpty()) {
                    // Tidak ada token, langsung ke login
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        shouldNavigate = true
                    )
                    return@launch
                }

                // Check login status dengan API
                val response = authApi.checkLogin("${Constants.BEARER_PREFIX}$token")

                if (response.isSuccessful && response.body()?.success == true) {
                    // Token valid, user masih login
                    val userData = response.body()!!.data

                    // Update user preferences dengan data terbaru
                    userPreferences.saveUserSession(
                        accessToken = token,
                        userId = userData.id,
                        username = userData.username,
                        fullname = userData.fullname,
                        email = userData.email,
                        image = userData.image.ifEmpty { null }
                    )

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        shouldNavigate = true
                    )
                } else {
                    // Token tidak valid, clear session dan redirect ke login
                    userPreferences.clearUserSession()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        shouldNavigate = true
                    )
                }
            } catch (e: Exception) {
                // Error terjadi, clear session dan redirect ke login
                userPreferences.clearUserSession()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    shouldNavigate = true
                )
            }
        }
    }
}