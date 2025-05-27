package com.virtualsblog.project.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

// Extension functions untuk kemudahan penggunaan
inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        if (data != null) {
            action(data)
        }
    }
    return this
}

inline fun <T> Resource<T>.onError(action: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(message ?: Constants.ERROR_UNKNOWN)
    }
    return this
}

inline fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}