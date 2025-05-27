package com.virtualsblog.project.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// String Extensions
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidUsername(): Boolean {
    return this.length >= Constants.MIN_USERNAME_LENGTH && this.matches(Regex("^[a-zA-Z0-9_]+$"))
}

// Compose Extensions
@Composable
fun <T> Flow<T>.collectAsEffect(
    block: suspend (T) -> Unit
) {
    LaunchedEffect(Unit) {
        this@collectAsEffect.onEach(block).collect()
    }
}

// Resource Extensions
fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading
fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success
fun <T> Resource<T>.isError(): Boolean = this is Resource.Error

fun <T> Resource<T>.getDataOrNull(): T? {
    return if (this is Resource.Success) data else null
}

fun <T> Resource<T>.getErrorMessage(): String? {
    return if (this is Resource.Error) message else null
}