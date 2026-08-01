package com.example.travel.domain.usecase.nearby

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.NearbyRepository
import com.example.travel.gis.domain.model.MapLocation
import javax.inject.Inject

class SearchNearbyPlacesUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    suspend operator fun invoke(
        category: String,
        userLat: Double,
        userLon: Double,
        radiusMeters: Int = 3000,
        sortBy: String = "distance"
    ): Resource<List<MapLocation>> {
        return nearbyRepository.getNearbyPlaces(category, userLat, userLon, radiusMeters, sortBy)
    }
}
