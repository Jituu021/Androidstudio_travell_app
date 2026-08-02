package com.example.travel.presentation.safety

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.SafetyReport
import com.example.travel.domain.repository.SafetyRepository
import com.example.travel.domain.usecase.safety.CalculateSafetyScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SafetyUiState(
    val report: SafetyReport? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SafetyViewModel @Inject constructor(
    private val safetyRepository: SafetyRepository,
    private val calculateSafetyScoreUseCase: CalculateSafetyScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SafetyUiState())
    val uiState: StateFlow<SafetyUiState> = _uiState.asStateFlow()

    init {
        loadLatestReport()
        refreshSafetyAnalysis(34.0522, -118.2437)
    }

    private fun loadLatestReport() {
        viewModelScope.launch {
            safetyRepository.getLatestSafetyReport().collect { cached ->
                if (cached != null && _uiState.value.report == null) {
                    _uiState.value = _uiState.value.copy(report = cached)
                }
            }
        }
    }

    fun refreshSafetyAnalysis(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = calculateSafetyScoreUseCase(lat, lon)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, report = result.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }
}
