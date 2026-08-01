package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, pass: String): Resource<User>
    suspend fun register(username: String, email: String, pass: String): Resource<User>
    suspend fun sendOtp(email: String): Resource<String>
    suspend fun verifyOtp(email: String, otp: String): Resource<Boolean>
    fun getSessionUser(): Flow<User?>
    suspend fun logout()
}
