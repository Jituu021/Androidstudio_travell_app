package com.example.travel.data.mapper

import com.example.travel.data.local.db.entity.UserEntity
import com.example.travel.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        email = email,
        isOtpVerified = isOtpVerified
    )
}

fun User.toEntity(passwordHash: String = "", otpCode: String? = null): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        email = email,
        passwordHash = passwordHash,
        otpCode = otpCode,
        isOtpVerified = isOtpVerified
    )
}
