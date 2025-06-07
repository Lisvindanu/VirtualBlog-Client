package com.virtualsblog.project.presentation.ui.screen.auth.profile

import com.virtualsblog.project.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val updateSuccess: Boolean = false,
    val isLoggedOut: Boolean = false
) {
    // Computed properties for easy access
    val fullname: String get() = user?.fullname ?: ""
    val username: String get() = user?.username ?: ""
    val email: String get() = user?.email ?: ""
    val userId: String get() = user?.id ?: ""
    val imageUrl: String? get() = user?.image
}