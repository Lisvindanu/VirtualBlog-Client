package com.virtualsblog.project.util

sealed class UiEvent {
    object Success : UiEvent()
    data class ShowToast(val message: String) : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
    object NavigateUp : UiEvent()
    data class Navigate(val route: String) : UiEvent()
}

sealed class AuthUiEvent : UiEvent() {
    object LoginSuccess : AuthUiEvent()
    object RegisterSuccess : AuthUiEvent()
    object LogoutSuccess : AuthUiEvent()
    data class AuthError(val message: String) : AuthUiEvent()
}