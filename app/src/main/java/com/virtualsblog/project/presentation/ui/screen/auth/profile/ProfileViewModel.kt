package com.virtualsblog.project.presentation.ui.screen.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.usecase.auth.UpdateProfileUseCase
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // First try to get profile from server
                when (val result = authRepository.getProfile()) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            user = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        // If server fails, try to get from local storage
                        authRepository.getCurrentUser().collect { localUser ->
                            _uiState.value = _uiState.value.copy(
                                user = localUser,
                                isLoading = false,
                                error = if (localUser == null) result.message else null
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                // Fallback to local user data
                authRepository.getCurrentUser().collect { localUser ->
                    _uiState.value = _uiState.value.copy(
                        user = localUser,
                        isLoading = false,
                        error = if (localUser == null) "Gagal memuat profil" else null
                    )
                }
            }
        }
    }

    fun updateProfile(fullname: String, email: String, username: String) {
        if (fullname.isBlank() || email.isBlank() || username.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Semua field harus diisi"
            )
            return
        }

        if (fullname.length < Constants.MIN_FULLNAME_LENGTH) {
            _uiState.value = _uiState.value.copy(
                error = "Nama lengkap minimal ${Constants.MIN_FULLNAME_LENGTH} karakter"
            )
            return
        }

        if (username.length < Constants.MIN_USERNAME_LENGTH) {
            _uiState.value = _uiState.value.copy(
                error = "Username minimal ${Constants.MIN_USERNAME_LENGTH} karakter"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                updateSuccess = false
            )

            when (val result = updateProfileUseCase(fullname, email, username)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        user = result.data,
                        isLoading = false,
                        error = null,
                        updateSuccess = true
                    )

                    // Hide success message after 3 seconds
                    kotlinx.coroutines.delay(3000)
                    _uiState.value = _uiState.value.copy(updateSuccess = false)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: Constants.ERROR_UNKNOWN,
                        updateSuccess = false
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(
                isLoggedOut = true,
                user = null
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearUpdateSuccess() {
        _uiState.value = _uiState.value.copy(updateSuccess = false)
    }

    fun refreshProfile() {
        loadProfile()
    }
}