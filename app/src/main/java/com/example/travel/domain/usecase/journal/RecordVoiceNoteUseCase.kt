package com.example.travel.domain.usecase.journal

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.VoiceNote
import com.example.travel.domain.repository.VoiceNoteRepository
import javax.inject.Inject

class RecordVoiceNoteUseCase @Inject constructor(
    private val voiceNoteRepository: VoiceNoteRepository
) {
    suspend operator fun invoke(voiceNote: VoiceNote): Resource<Boolean> {
        return voiceNoteRepository.recordVoiceNote(voiceNote)
    }
}
