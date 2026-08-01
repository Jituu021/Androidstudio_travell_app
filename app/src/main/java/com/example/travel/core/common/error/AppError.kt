package com.example.travel.core.common.error

sealed class AppError(override val message: String, val causeThrowable: Throwable? = null) : Exception(message, causeThrowable) {
    data class NetworkError(val code: Int? = null, override val message: String = "Network connection failed") : AppError(message)
    data class DatabaseError(override val message: String = "Database operation failed", val throwable: Throwable? = null) : AppError(message, throwable)
    data class AuthError(override val message: String = "Authentication failed") : AppError(message)
    data class ValidationError(override val message: String) : AppError(message)
    data class UnknownError(override val message: String = "An unexpected error occurred", val throwable: Throwable? = null) : AppError(message, throwable)
}
