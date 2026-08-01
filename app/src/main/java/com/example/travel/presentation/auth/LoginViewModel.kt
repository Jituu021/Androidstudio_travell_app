package com.example.travel.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.core.common.result.Resource
import com.example.travel.domain.usecase.auth.LoginUseCase
import com.example.travel.domain.usecase.auth.RegisterUseCase
import com.example.travel.domain.usecase.auth.SendOtpUseCase
import com.example.travel.domain.usecase.auth.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val username: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val isOtpSent: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) { _uiState.value = _uiState.value.copy(email = email, errorMessage = null) }
    fun onPasswordChanged(pass: String) { _uiState.value = _uiState.value.copy(pass = pass, errorMessage = null) }
    fun onUsernameChanged(name: String) { _uiState.value = _uiState.value.copy(username = name, errorMessage = null) }
    fun onOtpChanged(otp: String) { _uiState.value = _uiState.value.copy(otp = otp, errorMessage = null) }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = loginUseCase(_uiState.value.email, _uiState.value.pass)) {
                is Resource.Success -> {
                    Timber.d("Login successful for ${_uiState.value.email}")
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                }
                is Resource.Error -> {
                    Timber.e("Login failed: ${res.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = registerUseCase(_uiState.value.username, _uiState.value.email, _uiState.value.pass)) {
                is Resource.Success -> {
                    Timber.d("Registration successful")
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                }
                is Resource.Error -> {
                    Timber.e("Registration failed: ${res.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun sendOtp() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = sendOtpUseCase(_uiState.value.email)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isOtpSent = true, infoMessage = res.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = verifyOtpUseCase(_uiState.value.email, _uiState.value.otp)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
                is Resource.Loading -> {}
            }
        }
    }
}
