package com.example.travel.domain.usecase.auth

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.AuthRepository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<String> {
        if (email.isBlank()) return Resource.Error("Email address cannot be empty")
        return authRepository.sendOtp(email)
    }
}
