package com.example.travel.domain.usecase.speech

import com.example.travel.domain.repository.VoiceNavigationRepository
import javax.inject.Inject

class SpeakInstructionUseCase @Inject constructor(
    private val voiceNavigationRepository: VoiceNavigationRepository
) {
    operator fun invoke(instruction: String, flush: Boolean = false) {
        voiceNavigationRepository.speakInstruction(instruction, flush)
    }
}
