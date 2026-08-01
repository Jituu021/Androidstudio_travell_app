package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.gis.domain.model.MapRoute
import com.example.travel.gis.domain.model.TravelMode

interface RouteRepository {
    suspend fun calculateRoute(
        waypoints: List<Pair<Double, Double>>,
        mode: TravelMode
    ): Resource<List<MapRoute>>

    fun optimizeWaypointOrder(
        waypoints: List<Pair<Double, Double>>
    ): List<Pair<Double, Double>>
}
