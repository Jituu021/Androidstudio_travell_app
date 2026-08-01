package com.example.travel.domain.usecase.auth

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, otp: String): Resource<Boolean> {
        if (email.isBlank()) return Resource.Error("Email address cannot be empty")
        if (otp.isBlank()) return Resource.Error("OTP code cannot be empty")
        return authRepository.verifyOtp(email, otp)
    }
}
