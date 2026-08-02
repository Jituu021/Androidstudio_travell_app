package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.GroupTripDao
import com.example.travel.data.local.db.entity.GroupTripEntity
import com.example.travel.data.local.db.entity.TripMemberEntity
import com.example.travel.domain.model.GroupTrip
import com.example.travel.domain.model.TripMember
import com.example.travel.domain.repository.GroupTripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupTripRepositoryImpl @Inject constructor(
    private val groupTripDao: GroupTripDao
) : GroupTripRepository {

    override fun getAllGroupTrips(): Flow<List<GroupTrip>> {
        return groupTripDao.getAllGroupTrips().map { list ->
            list.map { e ->
                GroupTrip(
                    id = e.id,
                    title = e.title,
                    destination = e.destination,
                    ownerId = e.ownerId,
                    inviteCode = e.inviteCode,
                    isArchived = e.isArchived,
                    lastSyncedTimestamp = e.lastSyncedTimestamp
                )
            }
        }
    }

    override suspend fun createGroupTrip(title: String, destination: String, ownerId: String): Resource<GroupTrip> {
        return try {
            val tripId = "gtrip_${System.currentTimeMillis()}"
            val inviteCode = "TRIP-${UUID.randomUUID().toString().take(4).uppercase()}"
            val entity = GroupTripEntity(
                id = tripId,
                title = title,
                destination = destination,
                ownerId = ownerId,
                inviteCode = inviteCode
            )
            groupTripDao.insertGroupTrip(entity)

            // Owner member
            val ownerMember = TripMemberEntity(
                id = "mem_${System.currentTimeMillis()}",
                tripId = tripId,
                userId = ownerId,
                name = "Trip Owner",
                role = "Owner"
            )
            groupTripDao.insertTripMember(ownerMember)

            val trip = GroupTrip(
                id = tripId,
                title = title,
                destination = destination,
                ownerId = ownerId,
                inviteCode = inviteCode,
                members = listOf(TripMember(ownerMember.id, tripId, ownerId, "Trip Owner", "Owner"))
            )
            Timber.d("Created Group Trip: $title (Invite Code: $inviteCode)")
            Resource.Success(trip)
        } catch (e: Exception) {
            Timber.e(e, "Error creating group trip")
            Resource.Error(e.message ?: "Failed to create group trip", e)
        }
    }

    override suspend fun inviteMember(tripId: String, name: String, role: String): Resource<Boolean> {
        return try {
            val memberId = "mem_${System.currentTimeMillis()}"
            val entity = TripMemberEntity(
                id = memberId,
                tripId = tripId,
                userId = "user_${UUID.randomUUID().toString().take(6)}",
                name = name,
                role = role
            )
            groupTripDao.insertTripMember(entity)
            Timber.d("Invited member $name ($role) to trip $tripId")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to invite member", e)
        }
    }

    override suspend fun removeMember(tripId: String, userId: String): Resource<Boolean> {
        return try {
            groupTripDao.removeTripMember(tripId, userId)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove member", e)
        }
    }

    override suspend fun archiveTrip(tripId: String, isArchived: Boolean): Resource<Boolean> {
        return try {
            groupTripDao.updateArchivedStatus(tripId, isArchived)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update trip archive status", e)
        }
    }

    override fun getMembersForTrip(tripId: String): Flow<List<TripMember>> {
        return groupTripDao.getMembersForTrip(tripId).map { list ->
            list.map { e ->
                TripMember(
                    id = e.id,
                    tripId = e.tripId,
                    userId = e.userId,
                    name = e.name,
                    role = e.role,
                    isOnline = e.isOnline,
                    avatarUrl = e.avatarUrl
                )
            }
        }
    }
}
