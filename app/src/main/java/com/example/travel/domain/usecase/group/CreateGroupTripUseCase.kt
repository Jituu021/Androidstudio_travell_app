package com.example.travel.domain.usecase.group

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.GroupTrip
import com.example.travel.domain.repository.GroupTripRepository
import javax.inject.Inject

class CreateGroupTripUseCase @Inject constructor(
    private val groupTripRepository: GroupTripRepository
) {
    suspend operator fun invoke(title: String, destination: String, ownerId: String): Resource<GroupTrip> {
        return groupTripRepository.createGroupTrip(title, destination, ownerId)
    }
}
