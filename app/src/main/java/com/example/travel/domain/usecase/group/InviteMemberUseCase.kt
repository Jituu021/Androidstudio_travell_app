package com.example.travel.domain.usecase.group

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.GroupTripRepository
import javax.inject.Inject

class InviteMemberUseCase @Inject constructor(
    private val groupTripRepository: GroupTripRepository
) {
    suspend operator fun invoke(tripId: String, name: String, role: String): Resource<Boolean> {
        return groupTripRepository.inviteMember(tripId, name, role)
    }
}
