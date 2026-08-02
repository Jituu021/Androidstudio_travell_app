package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.LocalGuideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalGuideDao {
    @Query("SELECT * FROM local_guides ORDER BY timestamp DESC")
    fun getAllGuides(): Flow<List<LocalGuideEntity>>

    @Query("SELECT * FROM local_guides WHERE destination = :destination LIMIT 1")
    suspend fun getGuideForDestination(destination: String): LocalGuideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuide(guide: LocalGuideEntity)

    @Query("UPDATE local_guides SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean)

    @Query("DELETE FROM local_guides WHERE id = :id")
    suspend fun deleteGuide(id: String)
}
