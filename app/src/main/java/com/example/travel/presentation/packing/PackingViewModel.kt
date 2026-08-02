package com.example.travel.presentation.packing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.domain.model.PackingItem
import com.example.travel.domain.repository.PackingRepository
import com.example.travel.domain.usecase.packing.DeletePackingItemUseCase
import com.example.travel.domain.usecase.packing.GeneratePackingListUseCase
import com.example.travel.domain.usecase.packing.SavePackingListUseCase
import com.example.travel.domain.usecase.packing.UpdatePackingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PackingUiState(
    val items: List<PackingItem> = emptyList(),
    val totalCount: Int = 0,
    val packedCount: Int = 0,
    val progressPercent: Int = 0,
    val isLoading: Boolean = false,
    val currentTripId: String = "default_trip"
)

@HiltViewModel
class PackingViewModel @Inject constructor(
    private val packingRepository: PackingRepository,
    private val generatePackingListUseCase: GeneratePackingListUseCase,
    private val savePackingListUseCase: SavePackingListUseCase,
    private val updatePackingItemUseCase: UpdatePackingItemUseCase,
    private val deletePackingItemUseCase: DeletePackingItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PackingUiState())
    val uiState: StateFlow<PackingUiState> = _uiState.asStateFlow()

    init {
        loadPackingItems("default_trip")
    }

    fun loadPackingItems(tripId: String) {
        viewModelScope.launch {
            packingRepository.getPackingItems(tripId).collect { list ->
                val packed = list.count { it.isPacked }
                val total = list.size
                val pct = if (total > 0) (packed * 100) / total else 0
                _uiState.value = _uiState.value.copy(
                    items = list,
                    totalCount = total,
                    packedCount = packed,
                    progressPercent = pct,
                    currentTripId = tripId
                )
            }
        }
    }

    fun generateChecklist(destination: String, travelType: String, durationDays: Int, weather: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            generatePackingListUseCase(destination, travelType, durationDays, weather)
            val tripId = "trip_${destination.lowercase().replace(" ", "_")}"
            loadPackingItems(tripId)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun togglePacked(id: String, currentPacked: Boolean) {
        viewModelScope.launch {
            updatePackingItemUseCase(id, !currentPacked)
        }
    }

    fun addCustomItem(category: String, itemName: String, isEssential: Boolean) {
        viewModelScope.launch {
            val item = PackingItem(
                id = "custom_${System.currentTimeMillis()}",
                tripId = _uiState.value.currentTripId,
                category = category,
                itemName = itemName,
                isPacked = false,
                isEssential = isEssential
            )
            savePackingListUseCase(item)
        }
    }

    fun removeItem(id: String) {
        viewModelScope.launch {
            deletePackingItemUseCase(id)
        }
    }
}
