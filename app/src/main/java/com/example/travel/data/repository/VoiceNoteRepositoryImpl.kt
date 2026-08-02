package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.JournalDao
import com.example.travel.data.local.db.entity.VoiceNoteEntity
import com.example.travel.domain.model.VoiceNote
import com.example.travel.domain.repository.VoiceNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceNoteRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : VoiceNoteRepository {

    override fun getVoiceNotesForEntry(entryId: String): Flow<List<VoiceNote>> {
        return journalDao.getVoiceNotesForEntry(entryId).map { list ->
            list.map { e ->
                VoiceNote(
                    id = e.id,
                    entryId = e.entryId,
                    title = e.title,
                    localFilePath = e.localFilePath,
                    durationSeconds = e.durationSeconds,
                    fileSizeBytes = e.fileSizeBytes,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun recordVoiceNote(voiceNote: VoiceNote): Resource<Boolean> {
        return try {
            val entity = VoiceNoteEntity(
                id = voiceNote.id.ifEmpty { "voice_${System.currentTimeMillis()}" },
                entryId = voiceNote.entryId,
                title = voiceNote.title,
                localFilePath = voiceNote.localFilePath,
                durationSeconds = voiceNote.durationSeconds,
                fileSizeBytes = voiceNote.fileSizeBytes
            )
            journalDao.insertVoiceNote(entity)
            Timber.d("Saved voice note: ${voiceNote.title} (${voiceNote.durationSeconds}s)")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error saving voice note")
            Resource.Error(e.message ?: "Failed to save voice note", e)
        }
    }

    override suspend fun deleteVoiceNote(id: String): Resource<Boolean> {
        return try {
            journalDao.deleteVoiceNote(id)
            Timber.d("Deleted voice note: $id")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete voice note", e)
        }
    }
}
