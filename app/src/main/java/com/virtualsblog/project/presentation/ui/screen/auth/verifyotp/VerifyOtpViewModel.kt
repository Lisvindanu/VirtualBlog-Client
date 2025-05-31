package com.virtualsblog.project.presentation.ui.screen.auth.verifyotp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.auth.VerifyOtpUseCase
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyOtpUiState())
    val uiState: StateFlow<VerifyOtpUiState> = _uiState.asStateFlow()

    fun verifyOtp(email: String, otp: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Email tidak boleh kosong",
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

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = verifyOtpUseCase(email, otp)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null,
                        tokenId = result.data
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: "Gagal memverifikasi OTP",
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