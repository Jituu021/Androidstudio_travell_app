package com.example.travel.domain.usecase.nearby

import com.example.travel.domain.repository.NearbyRepository
import com.example.travel.gis.domain.model.MapLocation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedPlacesUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    operator fun invoke(): Flow<List<MapLocation>> {
        return nearbyRepository.getSavedPlaces()
    }
}
