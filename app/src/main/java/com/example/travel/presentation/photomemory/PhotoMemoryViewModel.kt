package com.example.travel.presentation.photomemory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.domain.model.PhotoMemory
import com.example.travel.domain.model.PhotoMetadata
import com.example.travel.domain.repository.PhotoMemoryRepository
import com.example.travel.domain.usecase.photomemory.ExtractPhotoMetadataUseCase
import com.example.travel.domain.usecase.photomemory.GroupPhotosUseCase
import com.example.travel.domain.usecase.photomemory.ScanTripPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoMemoryUiState(
    val photos: List<PhotoMemory> = emptyList(),
    val groupedTimeline: Map<String, List<PhotoMemory>> = emptyMap(),
    val selectedPhotoMetadata: PhotoMetadata? = null,
    val isLoading: Boolean = false,
    val currentTripId: String = "default_trip"
)

@HiltViewModel
class PhotoMemoryViewModel @Inject constructor(
    private val photoMemoryRepository: PhotoMemoryRepository,
    private val scanTripPhotosUseCase: ScanTripPhotosUseCase,
    private val extractPhotoMetadataUseCase: ExtractPhotoMetadataUseCase,
    private val groupPhotosUseCase: GroupPhotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoMemoryUiState())
    val uiState: StateFlow<PhotoMemoryUiState> = _uiState.asStateFlow()

    fun loadPhotosForTrip(tripId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, currentTripId = tripId)
            photoMemoryRepository.getPhotosForTrip(tripId).collect { list ->
                val grouped = groupPhotosUseCase(list)
                _uiState.value = _uiState.value.copy(
                    photos = list,
                    groupedTimeline = grouped,
                    isLoading = false
                )
            }
        }
    }

    fun addPhoto(localPath: String, hash: String, lat: Double, lon: Double, locationName: String) {
        viewModelScope.launch {
            val photo = PhotoMemory(
                id = "photo_${System.currentTimeMillis()}",
                tripId = _uiState.value.currentTripId,
                localFilePath = localPath,
                fileHash = hash,
                latitude = lat,
                longitude = lon,
                locationName = locationName
            )
            scanTripPhotosUseCase(photo)
        }
    }

    fun inspectPhotoMetadata(photoId: String) {
        viewModelScope.launch {
            val result = extractPhotoMetadataUseCase(photoId)
            if (result is com.example.travel.core.common.result.Resource.Success) {
                _uiState.value = _uiState.value.copy(selectedPhotoMetadata = result.data)
            }
        }
    }
}
