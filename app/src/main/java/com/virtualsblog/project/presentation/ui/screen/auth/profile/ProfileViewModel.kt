package com.virtualsblog.project.presentation.ui.screen.auth.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.usecase.auth.UpdateProfileUseCase
import com.virtualsblog.project.domain.usecase.auth.UploadProfilePictureUseCase // Pastikan diimport
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
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase // Ditambahkan
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, updateSuccess = false)

            try {
                when (val result = authRepository.getProfile()) {
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
                                error = if (localUser == null) result.message else null // Tampilkan error API jika localUser juga null
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                authRepository.getCurrentUser().collect { localUser ->
                    _uiState.value = _uiState.value.copy(
                        user = localUser,
                        isLoading = false,
                        error = if (localUser == null) "Gagal memuat profil: ${e.message}" else null
                    )
                }
            }
        }
    }

    fun updateProfile(fullname: String, email: String, username: String) {
        if (fullname.isBlank() || email.isBlank() || username.isBlank()) {
            _uiState.value = _uiState.value.copy( error = "Semua field harus diisi", updateSuccess = false )
            return
        }
        // Validasi lainnya bisa ditambahkan di sini atau di UseCase

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, updateSuccess = false)
            when (val result = updateProfileUseCase(fullname, email, username)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        user = result.data,
                        isLoading = false,
                        updateSuccess = true,
                        error = null
                    )
                    kotlinx.coroutines.delay(2000) // Tampilkan pesan sukses sejenak
                    _uiState.value = _uiState.value.copy(updateSuccess = false)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: Constants.ERROR_PROFILE_UPDATE_FAILED,
                        updateSuccess = false
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, updateSuccess = false)
            when (val result = uploadProfilePictureUseCase(context, imageUri)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        user = result.data, // API mengembalikan user yang terupdate
                        isLoading = false,
                        updateSuccess = true, // Tandai sukses untuk menampilkan pesan
                        error = null
                    )
                    kotlinx.coroutines.delay(2000) // Sembunyikan pesan sukses setelah beberapa detik
                    _uiState.value = _uiState.value.copy(updateSuccess = false)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: "Gagal mengunggah foto profil",
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
                user = null // Kosongkan data user setelah logout
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