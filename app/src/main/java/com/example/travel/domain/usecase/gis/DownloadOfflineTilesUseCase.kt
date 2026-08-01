package com.example.travel.domain.usecase.gis

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.GisRepository
import com.example.travel.gis.domain.model.OfflineRegion
import javax.inject.Inject

class DownloadOfflineTilesUseCase @Inject constructor(
    private val gisRepository: GisRepository
) {
    suspend operator fun invoke(region: OfflineRegion): Resource<Boolean> {
        return gisRepository.downloadOfflineTiles(region)
    }
}
