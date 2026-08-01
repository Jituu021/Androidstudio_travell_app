package com.example.travel.data.repository

import com.example.travel.core.speech.TextToSpeechManager
import com.example.travel.core.speech.VoiceSettings
import com.example.travel.domain.repository.VoiceNavigationRepository
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceNavigationRepositoryImpl @Inject constructor(
    private val ttsManager: TextToSpeechManager
) : VoiceNavigationRepository {

    override fun getVoiceSettings(): StateFlow<VoiceSettings> {
        return ttsManager.voiceSettings
    }

    override fun speakInstruction(instruction: String, flush: Boolean) {
        ttsManager.speak(instruction, flush)
    }

    override fun stopSpeech() {
        ttsManager.stop()
    }

    override fun setMuted(muted: Boolean) {
        ttsManager.setMuted(muted)
    }

    override fun setSpeechRate(rate: Float) {
        ttsManager.setSpeechRate(rate)
    }

    override fun setPitch(pitch: Float) {
        ttsManager.setPitch(pitch)
    }

    override fun setLanguage(locale: Locale) {
        ttsManager.setLanguage(locale)
    }
}
