package com.example.travel.domain.usecase.offline

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.OfflineMapRepository
import javax.inject.Inject

class DeleteOfflineRegionUseCase @Inject constructor(
    private val offlineMapRepository: OfflineMapRepository
) {
    suspend operator fun invoke(id: String): Resource<Boolean> {
        return offlineMapRepository.deleteOfflineRegion(id)
    }
}
