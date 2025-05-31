package com.virtualsblog.project.presentation.ui.screen.auth.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.auth.ChangePasswordUseCase
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        // Validasi input
        if (oldPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Kata sandi lama harus diisi"
            )
            return
        }

        if (newPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Kata sandi baru harus diisi"
            )
            return
        }

        if (newPassword.length < 6) {
            _uiState.value = _uiState.value.copy(
                error = "Kata sandi baru minimal 6 karakter"
            )
            return
        }

        if (newPassword != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                error = "Konfirmasi kata sandi tidak sama"
            )
            return
        }

        if (oldPassword == newPassword) {
            _uiState.value = _uiState.value.copy(
                error = "Kata sandi baru harus berbeda dari kata sandi lama"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            when (val result = changePasswordUseCase(oldPassword, newPassword, confirmPassword)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )

                    // Auto-hide success message setelah beberapa detik
                    kotlinx.coroutines.delay(3000)
                    _uiState.value = _uiState.value.copy(isSuccess = false)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: "Gagal mengubah kata sandi",
                        isSuccess = false
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}