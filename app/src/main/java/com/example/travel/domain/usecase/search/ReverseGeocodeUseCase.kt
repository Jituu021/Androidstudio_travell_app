package com.example.travel.domain.usecase.search

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.SearchRepository
import javax.inject.Inject

class ReverseGeocodeUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(userLat: Double, userLon: Double): Resource<String> {
        return searchRepository.reverseGeocode(userLat, userLon)
    }
}
