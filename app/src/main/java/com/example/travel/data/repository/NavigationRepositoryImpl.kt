package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.core.navigation.NavigationEngine
import com.example.travel.core.navigation.NavigationState
import com.example.travel.domain.repository.LocationRepository
import com.example.travel.domain.repository.NavigationRepository
import com.example.travel.domain.repository.RouteRepository
import com.example.travel.gis.domain.model.MapRoute
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepositoryImpl @Inject constructor(
    private val navigationEngine: NavigationEngine,
    private val locationRepository: LocationRepository,
    private val routeRepository: RouteRepository
) : NavigationRepository {

    override fun getNavigationState(): StateFlow<NavigationState> {
        return navigationEngine.navState
    }

    override fun startNavigation(route: MapRoute): Resource<Boolean> {
        return try {
            navigationEngine.startNavigation(route)
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error starting navigation")
            Resource.Error(e.message ?: "Failed to start navigation", e)
        }
    }

    override fun pauseNavigation() {
        navigationEngine.pauseNavigation()
    }

    override fun resumeNavigation() {
        navigationEngine.resumeNavigation()
    }

    override fun stopNavigation() {
        navigationEngine.stopNavigation()
    }

    override suspend fun recalculateRouteForOffRoute(): Resource<MapRoute?> {
        return try {
            val currentState = navigationEngine.navState.value
            val activeRoute = currentState.activeRoute ?: return Resource.Success(null)
            val currentLoc = locationRepository.getTelemetryState().value

            val newWaypoints = listOf(
                Pair(currentLoc.latitude, currentLoc.longitude),
                Pair(activeRoute.destination.latitude, activeRoute.destination.longitude)
            )

            val result = routeRepository.calculateRoute(newWaypoints, activeRoute.travelMode)
            if (result is Resource.Success && result.data.isNotEmpty()) {
                val newRoute = result.data.first()
                navigationEngine.startNavigation(newRoute)
                Timber.d("Successfully recalculated route off-route: ${newRoute.id}")
                Resource.Success(newRoute)
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error recalculating route off-route")
            Resource.Error(e.message ?: "Failed to recalculate route", e)
        }
    }
}
