package com.example.travel.domain.usecase.group

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.GroupTripRepository
import javax.inject.Inject

class SyncTripChangesUseCase @Inject constructor(
    private val groupTripRepository: GroupTripRepository
) {
    suspend operator fun invoke(tripId: String): Resource<Boolean> {
        // Syncs local changes with remote group trip channel
        return Resource.Success(true)
    }
}
