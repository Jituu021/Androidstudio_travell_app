package com.example.travel.domain.usecase.speech

import com.example.travel.domain.repository.VoiceNavigationRepository
import javax.inject.Inject

class StopSpeechUseCase @Inject constructor(
    private val voiceNavigationRepository: VoiceNavigationRepository
) {
    operator fun invoke() {
        voiceNavigationRepository.stopSpeech()
    }
}
