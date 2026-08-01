package com.example.travel.domain.usecase.route

import com.example.travel.domain.repository.RouteRepository
import javax.inject.Inject

class OptimizeWaypointsUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    operator fun invoke(waypoints: List<Pair<Double, Double>>): List<Pair<Double, Double>> {
        return routeRepository.optimizeWaypointOrder(waypoints)
    }
}
