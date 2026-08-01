package com.example.travel.domain.usecase.search

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.SearchRepository
import com.example.travel.gis.domain.model.MapLocation
import javax.inject.Inject

class SearchPlaceUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String, userLat: Double, userLon: Double): Resource<List<MapLocation>> {
        if (query.isBlank()) return Resource.Success(emptyList())
        return searchRepository.searchPlaces(query, userLat, userLon)
    }
}
