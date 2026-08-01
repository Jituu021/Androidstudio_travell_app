package com.example.travel.domain.usecase.navigation

import com.example.travel.domain.repository.NavigationRepository
import javax.inject.Inject

class StopNavigationUseCase @Inject constructor(
    private val navigationRepository: NavigationRepository
) {
    operator fun invoke() {
        navigationRepository.stopNavigation()
    }
}
