package com.example.travel.domain.usecase.journal

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.VoiceNoteRepository
import javax.inject.Inject

class DeleteVoiceNoteUseCase @Inject constructor(
    private val voiceNoteRepository: VoiceNoteRepository
) {
    suspend operator fun invoke(id: String): Resource<Boolean> {
        return voiceNoteRepository.deleteVoiceNote(id)
    }
}
