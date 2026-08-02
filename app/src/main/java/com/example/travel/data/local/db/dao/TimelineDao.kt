package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.MemoryRouteEntity
import com.example.travel.data.local.db.entity.TimelineEventEntity
import com.example.travel.data.local.db.entity.TripStatisticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineDao {
    @Query("SELECT * FROM timeline_events WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getTimelineEventsForTrip(tripId: String): Flow<List<TimelineEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineEvent(event: TimelineEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEvents(events: List<TimelineEventEntity>)

    @Query("SELECT * FROM memory_routes WHERE tripId = :tripId LIMIT 1")
    fun getRouteForTrip(tripId: String): Flow<MemoryRouteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: MemoryRouteEntity)

    @Query("SELECT * FROM trip_statistics WHERE tripId = :tripId LIMIT 1")
    fun getTripStatistics(tripId: String): Flow<TripStatisticsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripStatistics(stats: TripStatisticsEntity)
}
