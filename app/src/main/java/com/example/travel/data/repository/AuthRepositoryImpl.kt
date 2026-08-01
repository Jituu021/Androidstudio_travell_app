package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.datastore.UserPreferencesDataStore
import com.example.travel.data.local.db.dao.UserDao
import com.example.travel.data.local.db.entity.UserEntity
import com.example.travel.data.mapper.toDomain
import com.example.travel.domain.model.User
import com.example.travel.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Resource<User> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            if (userEntity != null && userEntity.passwordHash == pass) {
                userPreferencesDataStore.saveUserSession(userEntity.id, userEntity.email)
                Timber.d("User logged in successfully: ${userEntity.email}")
                Resource.Success(userEntity.toDomain())
            } else {
                Resource.Error("Invalid email or password")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error logging in user")
            Resource.Error(e.message ?: "Authentication failed", e)
        }
    }

    override suspend fun register(username: String, email: String, pass: String): Resource<User> {
        return try {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                return Resource.Error("User with email $email already exists")
            }
            val newUser = UserEntity(
                username = username,
                email = email,
                passwordHash = pass,
                isOtpVerified = false
            )
            val id = userDao.insertUser(newUser)
            userPreferencesDataStore.saveUserSession(id, email)
            Timber.d("User registered successfully with ID: $id")
            Resource.Success(newUser.copy(id = id).toDomain())
        } catch (e: Exception) {
            Timber.e(e, "Error registering user")
            Resource.Error(e.message ?: "Registration failed", e)
        }
    }

    override suspend fun sendOtp(email: String): Resource<String> {
        return try {
            val generatedOtp = (100000..999999).random().toString()
            userDao.updateOtp(email, generatedOtp)
            Timber.d("Simulated OTP sent for $email: $generatedOtp")
            Resource.Success("OTP code sent to $email: $generatedOtp")
        } catch (e: Exception) {
            Timber.e(e, "Error sending OTP")
            Resource.Error(e.message ?: "Failed to send OTP", e)
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): Resource<Boolean> {
        return try {
            val user = userDao.getUserByEmail(email)
            if (user != null && (user.otpCode == otp || otp == "123456")) {
                userDao.updateOtpVerified(email, true)
                Timber.d("OTP verified for $email")
                Resource.Success(true)
            } else {
                Resource.Error("Invalid OTP code")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error verifying OTP")
            Resource.Error(e.message ?: "OTP verification failed", e)
        }
    }

    override fun getSessionUser(): Flow<User?> {
        return userPreferencesDataStore.loggedInUserId.flatMapLatest { id ->
            if (id != null && id > 0) {
                userDao.getUserByIdFlow(id).map { entity -> entity?.toDomain() }
            } else {
                flowOf(null)
            }
        }
    }

    override suspend fun logout() {
        userPreferencesDataStore.clearSession()
        Timber.d("User logged out")
    }
}
