package com.example.travel.domain.usecase.gis

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.GisRepository
import com.example.travel.gis.domain.model.MapRoute
import javax.inject.Inject

class CalculateRouteUseCase @Inject constructor(
    private val gisRepository: GisRepository
) {
    suspend operator fun invoke(originLat: Double, originLon: Double, destLat: Double, destLon: Double, profile: String = "driving"): Resource<MapRoute> {
        return gisRepository.calculateRoute(originLat, originLon, destLat, destLon, profile)
    }
}
