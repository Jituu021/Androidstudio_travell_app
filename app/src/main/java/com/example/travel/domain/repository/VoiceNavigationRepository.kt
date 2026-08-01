package com.example.travel.domain.repository

import com.example.travel.core.speech.VoiceSettings
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

interface VoiceNavigationRepository {
    fun getVoiceSettings(): StateFlow<VoiceSettings>
    fun speakInstruction(instruction: String, flush: Boolean = false)
    fun stopSpeech()
    fun setMuted(muted: Boolean)
    fun setSpeechRate(rate: Float)
    fun setPitch(pitch: Float)
    fun setLanguage(locale: Locale)
}
