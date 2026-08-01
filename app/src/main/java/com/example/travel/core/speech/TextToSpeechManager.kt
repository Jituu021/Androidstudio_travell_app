package com.example.travel.core.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class VoiceSettings(
    val isMuted: Boolean = false,
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val language: String = Locale.getDefault().language
)

@Singleton
class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener, AudioManager.OnAudioFocusChangeListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var focusRequest: AudioFocusRequest? = null

    private val _voiceSettings = MutableStateFlow(VoiceSettings())
    val voiceSettings: StateFlow<VoiceSettings> = _voiceSettings.asStateFlow()

    private var lastSpokenInstruction: String = ""

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Timber.w("TTS Default language missing or not supported")
            }
            isInitialized = true
            Timber.d("TextToSpeechManager initialized successfully")
        } else {
            Timber.e("TextToSpeech initialization failed with status: $status")
        }
    }

    fun speak(text: String, flush: Boolean = false) {
        if (!isInitialized || _voiceSettings.value.isMuted || text.isBlank()) return
        if (text == lastSpokenInstruction && !flush) return

        lastSpokenInstruction = text
        requestAudioFocus()

        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.setSpeechRate(_voiceSettings.value.speechRate)
        tts?.setPitch(_voiceSettings.value.pitch)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts?.speak(text, queueMode, null, "tts_nav_id_${System.currentTimeMillis()}")
        } else {
            @Suppress("DEPRECATION")
            tts?.speak(text, queueMode, null)
        }
        Timber.d("TTS Spoke: $text")
    }

    fun stop() {
        tts?.stop()
        abandonAudioFocus()
    }

    fun setMuted(muted: Boolean) {
        _voiceSettings.value = _voiceSettings.value.copy(isMuted = muted)
        if (muted) stop()
    }

    fun setSpeechRate(rate: Float) {
        _voiceSettings.value = _voiceSettings.value.copy(speechRate = rate.coerceIn(0.5f, 2.0f))
    }

    fun setPitch(pitch: Float) {
        _voiceSettings.value = _voiceSettings.value.copy(pitch = pitch.coerceIn(0.5f, 2.0f))
    }

    fun getAvailableLanguages(): List<Locale> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isInitialized) {
            tts?.availableLanguages?.toList() ?: listOf(Locale.getDefault())
        } else {
            listOf(Locale.getDefault())
        }
    }

    fun setLanguage(locale: Locale) {
        if (isInitialized) {
            tts?.setLanguage(locale)
            _voiceSettings.value = _voiceSettings.value.copy(language = locale.language)
        }
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(playbackAttributes)
                .setOnAudioFocusChangeListener(this)
                .build()

            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_NOTIFICATION,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(this)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                tts?.stop()
            }
        }
    }
}
