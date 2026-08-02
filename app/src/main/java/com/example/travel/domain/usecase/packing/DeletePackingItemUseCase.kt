package com.example.travel.domain.usecase.packing

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.PackingRepository
import javax.inject.Inject

class DeletePackingItemUseCase @Inject constructor(
    private val packingRepository: PackingRepository
) {
    suspend operator fun invoke(id: String): Resource<Boolean> {
        return packingRepository.deletePackingItem(id)
    }
}
