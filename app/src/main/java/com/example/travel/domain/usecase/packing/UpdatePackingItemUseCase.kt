package com.example.travel.domain.usecase.packing

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.PackingRepository
import javax.inject.Inject

class UpdatePackingItemUseCase @Inject constructor(
    private val packingRepository: PackingRepository
) {
    suspend operator fun invoke(id: String, isPacked: Boolean): Resource<Boolean> {
        return packingRepository.updatePackedStatus(id, isPacked)
    }
}
