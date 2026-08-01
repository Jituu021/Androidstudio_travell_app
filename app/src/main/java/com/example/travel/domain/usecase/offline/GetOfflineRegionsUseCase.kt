package com.example.travel.domain.usecase.offline

import com.example.travel.domain.repository.OfflineMapRepository
import com.example.travel.gis.domain.model.OfflineRegion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOfflineRegionsUseCase @Inject constructor(
    private val offlineMapRepository: OfflineMapRepository
) {
    operator fun invoke(): Flow<List<OfflineRegion>> {
        return offlineMapRepository.getAllOfflineRegions()
    }
}
