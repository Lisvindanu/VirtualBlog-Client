package com.virtualsblog.project.presentation.ui.screen.auth.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.usecase.user.GetProfileUseCase
import com.virtualsblog.project.domain.usecase.user.UpdateProfileUseCase
import com.virtualsblog.project.domain.usecase.user.UploadProfilePictureUseCase
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
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = getProfileUseCase()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        user = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    authRepository.getCurrentUser().collect { localUser ->
                        _uiState.value = _uiState.value.copy(
                            user = localUser,
                            isLoading = false,
                            error = result.message ?: Constants.ERROR_FAILED_LOAD_PROFILE
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun updateProfile(fullname: String, email: String, username: String) {
        if (fullname.isBlank() || email.isBlank() || username.isBlank()) {
            _uiState.value = _uiState.value.copy(error = Constants.ERROR_REQUIRED_FIELDS, isSuccess = false)
            return
        }
        if (fullname.length < Constants.MIN_FULLNAME_LENGTH) {
            _uiState.value = _uiState.value.copy(
                error = Constants.VALIDATION_FULLNAME_MIN_LENGTH,
                isSuccess = false
            )
            return
        }
        if (username.length < Constants.MIN_USERNAME_LENGTH) {
            _uiState.value = _uiState.value.copy(
                error = Constants.VALIDATION_USERNAME_MIN_LENGTH,
                isSuccess = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, isSuccess = false)
            when (val result = updateProfileUseCase(fullname, email, username)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        user = result.data,
                        isLoading = false,
                        error = null,
                        isSuccess = true
                    )
                    kotlinx.coroutines.delay(3000)
                    _uiState.value = _uiState.value.copy(isSuccess = false)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: Constants.ERROR_PROFILE_UPDATE_FAILED,
                        isSuccess = false
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun uploadProfilePicture(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingImage = true,
                error = null,
                isSuccess = false
            )

            when (val result = uploadProfilePictureUseCase(context, imageUri)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        user = result.data,
                        isUploadingImage = false,
                        error = null,
                        isSuccess = true
                    )
                    kotlinx.coroutines.delay(3000)
                    _uiState.value = _uiState.value.copy(isSuccess = false)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        error = result.message ?: "Gagal mengunggah foto profil",
                        isSuccess = false
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isUploadingImage = true)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = ProfileUiState(isLoggedOut = true)
        }
    }

    fun refreshProfile() {
        loadProfile()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, isSuccess = false)
    }

    fun clearUpdateSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}