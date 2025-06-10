package com.virtualsblog.project.presentation.ui.screen.auth.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.user.GetProfileUseCase
import com.virtualsblog.project.domain.usecase.user.UpdateProfileUseCase
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.Resource
import com.virtualsblog.project.util.ValidationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadInitialProfile()
    }

    private fun loadInitialProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = getProfileUseCase()) {
                is Resource.Success -> {
                    val user = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        fullname = user?.fullname ?: "",
                        username = user?.username ?: "",
                        email = user?.email ?: "",
                        error = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: Constants.ERROR_FAILED_LOAD_PROFILE
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun updateProfile(fullname: String, email: String, username: String) {
        // Clear error sebelum validasi
        _uiState.value = _uiState.value.copy(error = null)

        // Validasi input
        if (fullname.isBlank() || email.isBlank() || username.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = Constants.ERROR_REQUIRED_FIELDS,
                isLoading = false
            )
            return
        }

        // Validasi nama lengkap
        val fullnameValidation = ValidationUtil.validateFullname(fullname.trim())
        if (!fullnameValidation.isValid) {
            _uiState.value = _uiState.value.copy(
                error = fullnameValidation.errorMessage ?: Constants.VALIDATION_FULLNAME_MIN_LENGTH,
                isLoading = false
            )
            return
        }

        // Validasi email
        val emailValidation = ValidationUtil.validateEmail(email.trim())
        if (!emailValidation.isValid) {
            _uiState.value = _uiState.value.copy(
                error = emailValidation.errorMessage ?: Constants.VALIDATION_EMAIL_INVALID,
                isLoading = false
            )
            return
        }

        // Validasi username
        val usernameValidation = ValidationUtil.validateUsername(username.trim())
        if (!usernameValidation.isValid) {
            _uiState.value = _uiState.value.copy(
                error = usernameValidation.errorMessage ?: Constants.VALIDATION_USERNAME_MIN_LENGTH,
                isLoading = false
            )
            return
        }

        // Cek apakah ada perubahan
        val currentState = _uiState.value
        if (fullname.trim() == currentState.fullname &&
            email.trim() == currentState.email &&
            username.trim() == currentState.username) {
            _uiState.value = _uiState.value.copy(
                error = "Tidak ada perubahan yang perlu disimpan",
                isLoading = false
            )
            return
        }

        // Proses update
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            when (val result = updateProfileUseCase(fullname.trim(), email.trim(), username.trim())) {
                is Resource.Success -> {
                    val updatedUser = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null,
                        fullname = updatedUser?.fullname ?: fullname.trim(),
                        username = updatedUser?.username ?: username.trim(),
                        email = updatedUser?.email ?: email.trim()
                    )
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

    fun onFullNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(
            fullname = newName,
            error = null // Clear error saat user mulai edit
        )
    }

    fun onUserNameChange(newUsername: String) {
        _uiState.value = _uiState.value.copy(
            username = newUsername,
            error = null // Clear error saat user mulai edit
        )
    }

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            error = null // Clear error saat user mulai edit
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    fun refreshProfile() {
        loadInitialProfile()
    }
}