package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.VoiceNote
import kotlinx.coroutines.flow.Flow

interface VoiceNoteRepository {
    fun getVoiceNotesForEntry(entryId: String): Flow<List<VoiceNote>>
    suspend fun recordVoiceNote(voiceNote: VoiceNote): Resource<Boolean>
    suspend fun deleteVoiceNote(id: String): Resource<Boolean>
}
