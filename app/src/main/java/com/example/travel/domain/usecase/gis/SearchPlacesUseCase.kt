package com.example.travel.domain.usecase.gis

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.GisRepository
import com.example.travel.gis.domain.model.MapLocation
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(
    private val gisRepository: GisRepository
) {
    suspend operator fun invoke(query: String, userLat: Double, userLon: Double): Resource<List<MapLocation>> {
        return gisRepository.searchPlaces(query, userLat, userLon)
    }
}
