package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.TripNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripNoteDao {
    @Query("SELECT * FROM trip_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<TripNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: TripNoteEntity): Long

    @Query("DELETE FROM trip_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}
