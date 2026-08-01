package com.example.travel.domain.usecase.route

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.RouteRepository
import com.example.travel.gis.domain.model.MapRoute
import com.example.travel.gis.domain.model.TravelMode
import javax.inject.Inject

class GetCachedRouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(
        waypoints: List<Pair<Double, Double>>,
        mode: TravelMode
    ): Resource<List<MapRoute>> {
        return routeRepository.calculateRoute(waypoints, mode)
    }
}
