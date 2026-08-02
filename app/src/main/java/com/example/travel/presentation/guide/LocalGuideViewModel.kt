package com.example.travel.presentation.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.LocalGuide
import com.example.travel.domain.repository.LocalGuideRepository
import com.example.travel.domain.usecase.guide.GenerateLocalGuideUseCase
import com.example.travel.domain.usecase.guide.GetSavedGuidesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocalGuideUiState(
    val activeGuide: LocalGuide? = null,
    val savedGuides: List<LocalGuide> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LocalGuideViewModel @Inject constructor(
    private val localGuideRepository: LocalGuideRepository,
    private val generateLocalGuideUseCase: GenerateLocalGuideUseCase,
    private val getSavedGuidesUseCase: GetSavedGuidesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocalGuideUiState())
    val uiState: StateFlow<LocalGuideUiState> = _uiState.asStateFlow()

    init {
        loadSavedGuides()
        loadGuideForDestination("Tokyo")
    }

    private fun loadSavedGuides() {
        viewModelScope.launch {
            getSavedGuidesUseCase().collect { list ->
                _uiState.value = _uiState.value.copy(savedGuides = list)
            }
        }
    }

    fun loadGuideForDestination(destination: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = generateLocalGuideUseCase(destination)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, activeGuide = result.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun toggleBookmark() {
        val guide = _uiState.value.activeGuide ?: return
        viewModelScope.launch {
            val newStatus = !guide.isBookmarked
            localGuideRepository.updateBookmarkStatus(guide.id, newStatus)
            _uiState.value = _uiState.value.copy(
                activeGuide = guide.copy(isBookmarked = newStatus)
            )
        }
    }
}
