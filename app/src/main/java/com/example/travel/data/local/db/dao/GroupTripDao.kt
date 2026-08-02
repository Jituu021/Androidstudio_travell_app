package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.GroupTripEntity
import com.example.travel.data.local.db.entity.TripMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupTripDao {
    @Query("SELECT * FROM group_trips ORDER BY lastSyncedTimestamp DESC")
    fun getAllGroupTrips(): Flow<List<GroupTripEntity>>

    @Query("SELECT * FROM group_trips WHERE id = :id")
    suspend fun getGroupTripById(id: String): GroupTripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupTrip(trip: GroupTripEntity)

    @Query("UPDATE group_trips SET isArchived = :isArchived WHERE id = :id")
    suspend fun updateArchivedStatus(id: String, isArchived: Boolean)

    @Query("SELECT * FROM trip_members WHERE tripId = :tripId")
    fun getMembersForTrip(tripId: String): Flow<List<TripMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripMember(member: TripMemberEntity)

    @Query("DELETE FROM trip_members WHERE tripId = :tripId AND userId = :userId")
    suspend fun removeTripMember(tripId: String, userId: String)
}
