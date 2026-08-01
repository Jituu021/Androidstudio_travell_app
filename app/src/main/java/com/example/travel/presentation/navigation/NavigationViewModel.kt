package com.example.travel.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.core.navigation.NavigationEngine
import com.example.travel.core.navigation.NavigationState
import com.example.travel.domain.usecase.navigation.RerouteUseCase
import com.example.travel.domain.usecase.navigation.StartNavigationUseCase
import com.example.travel.domain.usecase.navigation.StopNavigationUseCase
import com.example.travel.gis.domain.model.MapRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationEngine: NavigationEngine,
    private val startNavigationUseCase: StartNavigationUseCase,
    private val stopNavigationUseCase: StopNavigationUseCase,
    private val rerouteUseCase: RerouteUseCase
) : ViewModel() {

    val navState: StateFlow<NavigationState> = navigationEngine.navState

    fun startNav(route: MapRoute) {
        startNavigationUseCase(route)
    }

    fun pauseNav() {
        navigationEngine.pauseNavigation()
    }

    fun resumeNav() {
        navigationEngine.resumeNavigation()
    }

    fun stopNav() {
        stopNavigationUseCase()
    }

    fun toggleNightMode() {
        navigationEngine.toggleNightMode()
    }

    fun triggerReroute() {
        viewModelScope.launch {
            rerouteUseCase()
        }
    }
}
