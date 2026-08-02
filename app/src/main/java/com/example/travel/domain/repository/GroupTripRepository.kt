package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.GroupTrip
import com.example.travel.domain.model.TripMember
import kotlinx.coroutines.flow.Flow

interface GroupTripRepository {
    fun getAllGroupTrips(): Flow<List<GroupTrip>>
    suspend fun createGroupTrip(title: String, destination: String, ownerId: String): Resource<GroupTrip>
    suspend fun inviteMember(tripId: String, name: String, role: String): Resource<Boolean>
    suspend fun removeMember(tripId: String, userId: String): Resource<Boolean>
    suspend fun archiveTrip(tripId: String, isArchived: Boolean): Resource<Boolean>
    fun getMembersForTrip(tripId: String): Flow<List<TripMember>>
}
