package com.virtualsblog.project.presentation.ui.screen.auth.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.auth.ResetPasswordUseCase
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun resetPassword(
        tokenId: String,
        otp: String,
        password: String,
        confirmPassword: String
    ) {
        if (tokenId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Token tidak valid",
                isLoading = false
            )
            return
        }

        if (otp.isBlank() || otp.length != 6) {
            _uiState.value = _uiState.value.copy(
                error = "Kode OTP harus 6 digit",
                isLoading = false
            )
            return
        }

        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Kata sandi baru harus diisi",
                isLoading = false
            )
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(
                error = "Kata sandi minimal 6 karakter",
                isLoading = false
            )
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                error = "Konfirmasi kata sandi tidak sama",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = resetPasswordUseCase(tokenId, otp, password, confirmPassword)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
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
}