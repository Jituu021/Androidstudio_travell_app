package com.example.travel.domain.usecase.search

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.SearchRepository
import com.example.travel.gis.domain.model.MapLocation
import javax.inject.Inject

class ToggleFavoritePlaceUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(place: MapLocation): Resource<Boolean> {
        return searchRepository.toggleFavoritePlace(place)
    }
}
