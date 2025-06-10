
package com.virtualsblog.project.presentation.ui.screen.auth.editprofile

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fullname: String = "",
    val username: String = "",
    val email: String = ""
)