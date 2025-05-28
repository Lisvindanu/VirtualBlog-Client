package com.virtualsblog.project.presentation.ui.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtualsblog.project.domain.usecase.auth.RegisterUseCase
import com.virtualsblog.project.util.Constants
import com.virtualsblog.project.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Ubah parameter: hilangkan 'name', 'email' diganti 'usernameFromUi'
    fun register(usernameFromUi: String, password: String, confirmPassword: String) {
        if (usernameFromUi.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Username, password, dan konfirmasi password harus diisi",
                isLoading = false
            )
            return
        }

        // Validasi kesamaan password bisa juga dilakukan di sini atau di UI
        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                error = "Password dan konfirmasi password tidak sama",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // Kirim usernameFromUi sebagai argumen pertama ke use case
            when (val result = registerUseCase(usernameFromUi, password, confirmPassword)) {
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
                        error = result.message ?: Constants.ERROR_UNKNOWN,
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