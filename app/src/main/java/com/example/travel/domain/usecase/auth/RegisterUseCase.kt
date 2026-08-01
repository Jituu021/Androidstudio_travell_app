package com.example.travel.domain.usecase.auth

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.User
import com.example.travel.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, pass: String): Resource<User> {
        if (username.isBlank()) return Resource.Error("Username cannot be empty")
        if (email.isBlank()) return Resource.Error("Email address cannot be empty")
        if (pass.isBlank()) return Resource.Error("Password cannot be empty")
        return authRepository.register(username, email, pass)
    }
}
