package com.example.travel.domain.usecase.packing

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.PackingItem
import com.example.travel.domain.repository.PackingRepository
import javax.inject.Inject

class GeneratePackingListUseCase @Inject constructor(
    private val packingRepository: PackingRepository
) {
    suspend operator fun invoke(
        destination: String,
        travelType: String,
        durationDays: Int,
        weatherCondition: String
    ): Resource<List<PackingItem>> {
        return packingRepository.generatePackingList(destination, travelType, durationDays, weatherCondition)
    }
}
