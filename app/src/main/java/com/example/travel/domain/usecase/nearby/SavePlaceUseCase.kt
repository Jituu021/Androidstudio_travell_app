package com.example.travel.domain.usecase.nearby

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.NearbyRepository
import com.example.travel.gis.domain.model.MapLocation
import javax.inject.Inject

class SavePlaceUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    suspend operator fun invoke(place: MapLocation): Resource<Boolean> {
        return nearbyRepository.savePlace(place)
    }
}
