package com.example.travel.domain.model

data class User(
    val id: Long = 0,
    val username: String,
    val email: String,
    val isOtpVerified: Boolean = false
)
