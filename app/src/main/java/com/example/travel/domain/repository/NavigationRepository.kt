package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.core.navigation.NavigationState
import com.example.travel.gis.domain.model.MapRoute
import kotlinx.coroutines.flow.StateFlow

interface NavigationRepository {
    fun getNavigationState(): StateFlow<NavigationState>
    fun startNavigation(route: MapRoute): Resource<Boolean>
    fun pauseNavigation()
    fun resumeNavigation()
    fun stopNavigation()
    suspend fun recalculateRouteForOffRoute(): Resource<MapRoute?>
}
