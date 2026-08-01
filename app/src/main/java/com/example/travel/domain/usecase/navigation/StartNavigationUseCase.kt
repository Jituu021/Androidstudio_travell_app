package com.example.travel.domain.usecase.navigation

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.NavigationRepository
import com.example.travel.gis.domain.model.MapRoute
import javax.inject.Inject

class StartNavigationUseCase @Inject constructor(
    private val navigationRepository: NavigationRepository
) {
    operator fun invoke(route: MapRoute): Resource<Boolean> {
        return navigationRepository.startNavigation(route)
    }
}
