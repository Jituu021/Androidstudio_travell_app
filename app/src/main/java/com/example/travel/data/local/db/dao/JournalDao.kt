package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.JournalEntity
import com.example.travel.data.local.db.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journals ORDER BY timestamp DESC")
    fun getAllJournals(): Flow<List<JournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity)

    @Query("SELECT * FROM journal_entries WHERE journalId = :journalId ORDER BY timestamp DESC")
    fun getEntriesForJournal(journalId: String): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntry(id: String)

    @Query("DELETE FROM journals WHERE id = :id")
    suspend fun deleteJournal(id: String)

    @Query("SELECT * FROM journal_media WHERE entryId = :entryId")
    fun getMediaForEntry(entryId: String): Flow<List<com.example.travel.data.local.db.entity.JournalMediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: com.example.travel.data.local.db.entity.JournalMediaEntity)

    @Query("DELETE FROM journal_media WHERE id = :id")
    suspend fun deleteMedia(id: String)

    @Query("SELECT * FROM journal_locations WHERE entryId = :entryId LIMIT 1")
    fun getLocationForEntry(entryId: String): Flow<com.example.travel.data.local.db.entity.JournalLocationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: com.example.travel.data.local.db.entity.JournalLocationEntity)

    @Query("SELECT * FROM voice_notes WHERE entryId = :entryId ORDER BY timestamp DESC")
    fun getVoiceNotesForEntry(entryId: String): Flow<List<com.example.travel.data.local.db.entity.VoiceNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceNote(voiceNote: com.example.travel.data.local.db.entity.VoiceNoteEntity)

    @Query("DELETE FROM voice_notes WHERE id = :id")
    suspend fun deleteVoiceNote(id: String)

    @Query("SELECT * FROM ai_journal_summaries WHERE journalId = :journalId AND date = :date LIMIT 1")
    fun getAISummary(journalId: String, date: String): Flow<com.example.travel.data.local.db.entity.AIJournalSummaryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAISummary(summary: com.example.travel.data.local.db.entity.AIJournalSummaryEntity)

    @Query("SELECT * FROM ai_trip_stories WHERE journalId = :journalId LIMIT 1")
    fun getAITripStory(journalId: String): Flow<com.example.travel.data.local.db.entity.AITripStoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAITripStory(story: com.example.travel.data.local.db.entity.AITripStoryEntity)
}
