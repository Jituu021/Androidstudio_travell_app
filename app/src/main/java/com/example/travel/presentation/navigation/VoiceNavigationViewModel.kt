package com.example.travel.presentation.navigation

import androidx.lifecycle.ViewModel
import com.example.travel.core.speech.VoiceSettings
import com.example.travel.domain.repository.VoiceNavigationRepository
import com.example.travel.domain.usecase.speech.SpeakInstructionUseCase
import com.example.travel.domain.usecase.speech.StopSpeechUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class VoiceNavigationViewModel @Inject constructor(
    private val voiceNavigationRepository: VoiceNavigationRepository,
    private val speakInstructionUseCase: SpeakInstructionUseCase,
    private val stopSpeechUseCase: StopSpeechUseCase
) : ViewModel() {

    val voiceSettings: StateFlow<VoiceSettings> = voiceNavigationRepository.getVoiceSettings()

    fun speak(text: String, flush: Boolean = false) {
        speakInstructionUseCase(text, flush)
    }

    fun stop() {
        stopSpeechUseCase()
    }

    fun toggleMute() {
        val current = voiceSettings.value.isMuted
        voiceNavigationRepository.setMuted(!current)
    }

    fun setSpeechRate(rate: Float) {
        voiceNavigationRepository.setSpeechRate(rate)
    }

    fun setPitch(pitch: Float) {
        voiceNavigationRepository.setPitch(pitch)
    }

    fun setLanguage(locale: Locale) {
        voiceNavigationRepository.setLanguage(locale)
    }
}
