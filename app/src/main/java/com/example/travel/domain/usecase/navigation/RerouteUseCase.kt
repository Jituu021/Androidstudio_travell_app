package com.example.travel.domain.usecase.navigation

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.NavigationRepository
import com.example.travel.gis.domain.model.MapRoute
import javax.inject.Inject

class RerouteUseCase @Inject constructor(
    private val navigationRepository: NavigationRepository
) {
    suspend operator fun invoke(): Resource<MapRoute?> {
        return navigationRepository.recalculateRouteForOffRoute()
    }
}
