package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.AppLanguage
import com.example.data.model.Ayah
import com.example.data.model.PrayerSurah
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class AudioPlaybackType {
    NONE,
    ARABIC,
    MEANING
}

class SurahAudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val audioCacheDir: File by lazy {
        File(context.cacheDir, "quran_dua_audio_cache").apply { mkdirs() }
    }

    private val _playbackType = MutableStateFlow(AudioPlaybackType.NONE)
    val playbackType: StateFlow<AudioPlaybackType> = _playbackType.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId: StateFlow<String?> = _currentPlayingId.asStateFlow()

    private val _currentPlayingAyahNumber = MutableStateFlow<Int?>(null)
    val currentPlayingAyahNumber: StateFlow<Int?> = _currentPlayingAyahNumber.asStateFlow()

    private val _isLooping = MutableStateFlow(false)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _currentDurationText = MutableStateFlow("00:00")
    val currentDurationText: StateFlow<String> = _currentDurationText.asStateFlow()

    private var playbackJob: Job? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("SurahAudioPlayer", "Failed to initialize TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                    _isLoading.value = false
                }

                override fun onDone(utteranceId: String?) {
                    scope.launch {
                        _isPlaying.value = false
                        _playbackType.value = AudioPlaybackType.NONE
                        _currentPlayingAyahNumber.value = null
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    scope.launch {
                        _isPlaying.value = false
                        _isLoading.value = false
                        _playbackType.value = AudioPlaybackType.NONE
                        _currentPlayingAyahNumber.value = null
                    }
                }
            })
            isTtsReady = true
        }
    }

    /**
     * Plays a full Surah Ayah-by-Ayah in Arabic (Mishary Rashid Al-Afasy via EveryAyah CDN + fallbacks)
     * so playback begins in <200ms and highlights each Ayah as it plays.
     */
    fun playSurahAyahs(surahNumber: Int, ayahs: List<Ayah>) {
        val surahId = "surah_ar_$surahNumber"
        if (_currentPlayingId.value == surahId && (_isPlaying.value || _isLoading.value)) {
            stop()
            return
        }

        stop()
        if (ayahs.isEmpty()) return

        _currentPlayingId.value = surahId
        _playbackType.value = AudioPlaybackType.ARABIC
        _isLoading.value = true

        playbackJob = scope.launch {
            do {
                for ((index, ayah) in ayahs.withIndex()) {
                    if (!isActive) break
                    _currentPlayingAyahNumber.value = ayah.numberInSurah
                    _progress.value = index.toFloat() / ayahs.size.coerceAtLeast(1).toFloat()

                    // Prefetch next ayah in background
                    if (index + 1 < ayahs.size) {
                        val nextAyah = ayahs[index + 1]
                        launch(Dispatchers.IO) {
                            val nextUrl = buildEveryAyahUrl(surahNumber, nextAyah.numberInSurah)
                            downloadToCache(nextUrl)
                        }
                    }

                    val primaryUrl = ayah.audioUrl ?: buildEveryAyahUrl(surahNumber, ayah.numberInSurah)
                    val backupUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${ayah.numberInQuran}.mp3"

                    val played = playSingleUrlOrFallbackSuspend(
                        primaryUrl = primaryUrl,
                        backupUrl = backupUrl,
                        fallbackText = ayah.arabicText,
                        fallbackLangCode = "ar",
                        offlinePronunciation = ayah.getEffectivePronunciation()
                    )
                    if (!played && !isActive) break
                }
            } while (isActive && _isLooping.value)

            if (isActive) {
                stop()
            }
        }
    }

    /**
     * Plays the localized meaning (Meal / Translation) of a full Surah Ayah-by-Ayah.
     */
    fun playSurahMeaning(surahNumber: Int, ayahs: List<Ayah>, lang: AppLanguage) {
        val surahId = "surah_meaning_$surahNumber"
        if (_currentPlayingId.value == surahId && (_isPlaying.value || _isLoading.value)) {
            stop()
            return
        }

        stop()
        if (ayahs.isEmpty()) return

        _currentPlayingId.value = surahId
        _playbackType.value = AudioPlaybackType.MEANING
        _isLoading.value = true

        val langCode = when (lang) {
            AppLanguage.TURKISH -> "tr"
            AppLanguage.ENGLISH -> "en"
            AppLanguage.ARABIC -> "ar"
        }

        playbackJob = scope.launch {
            do {
                for ((index, ayah) in ayahs.withIndex()) {
                    if (!isActive) break
                    _currentPlayingAyahNumber.value = ayah.numberInSurah
                    _progress.value = index.toFloat() / ayahs.size.coerceAtLeast(1).toFloat()

                    val meaningText = ayah.getLocalizedMeaning(lang)
                    playSpeechChunksSuspend(
                        text = meaningText,
                        langCode = langCode,
                        offlineFallbackText = meaningText
                    )
                }
            } while (isActive && _isLooping.value)

            if (isActive) {
                stop()
            }
        }
    }

    /**
     * Plays a single Ayah (Arabic recitation or localized Meaning).
     */
    fun playSingleAyah(
        surahNumber: Int,
        ayah: Ayah,
        playMeaning: Boolean = false,
        lang: AppLanguage = AppLanguage.TURKISH
    ) {
        val itemId = "ayah_${surahNumber}_${ayah.numberInSurah}_${if (playMeaning) "meaning" else "ar"}"
        if (_currentPlayingId.value == itemId && (_isPlaying.value || _isLoading.value)) {
            stop()
            return
        }

        stop()
        _currentPlayingId.value = itemId
        _currentPlayingAyahNumber.value = ayah.numberInSurah
        _playbackType.value = if (playMeaning) AudioPlaybackType.MEANING else AudioPlaybackType.ARABIC
        _isLoading.value = true

        playbackJob = scope.launch {
            if (playMeaning) {
                val langCode = when (lang) {
                    AppLanguage.TURKISH -> "tr"
                    AppLanguage.ENGLISH -> "en"
                    AppLanguage.ARABIC -> "ar"
                }
                val text = ayah.getLocalizedMeaning(lang)
                playSpeechChunksSuspend(text = text, langCode = langCode, offlineFallbackText = text)
            } else {
                val primaryUrl = ayah.audioUrl ?: buildEveryAyahUrl(surahNumber, ayah.numberInSurah)
                val backupUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${ayah.numberInQuran}.mp3"
                playSingleUrlOrFallbackSuspend(
                    primaryUrl = primaryUrl,
                    backupUrl = backupUrl,
                    fallbackText = ayah.arabicText,
                    fallbackLangCode = "ar",
                    offlinePronunciation = ayah.getEffectivePronunciation()
                )
            }
            if (isActive) {
                stop()
            }
        }
    }

    /**
     * Plays the Arabic recitation of a Dua or Surah (MP3 URL or natural Arabic voice stream + offline TTS fallback).
     */
    fun playArabic(surah: PrayerSurah) {
        if (_currentPlayingId.value == surah.id && (_isPlaying.value || _isLoading.value) && _playbackType.value == AudioPlaybackType.ARABIC) {
            stop()
            return
        }

        stop()
        _currentPlayingId.value = surah.id
        _playbackType.value = AudioPlaybackType.ARABIC
        _isLoading.value = true

        playbackJob = scope.launch {
            do {
                if (!surah.audioUrl.isNullOrBlank()) {
                    playSingleUrlOrFallbackSuspend(
                        primaryUrl = surah.audioUrl,
                        backupUrl = null,
                        fallbackText = surah.arabicText,
                        fallbackLangCode = "ar",
                        offlinePronunciation = surah.turkishPronunciation
                    )
                } else {
                    playSpeechChunksSuspend(
                        text = surah.arabicText,
                        langCode = "ar",
                        offlineFallbackText = surah.turkishPronunciation.ifBlank { surah.arabicText }
                    )
                }
            } while (isActive && _isLooping.value)

            if (isActive) {
                stop()
            }
        }
    }

    /**
     * Reads the Meaning / Translation of a Dua or Surah in the selected language (TR, EN, or AR).
     */
    fun playTurkishMeaning(surah: PrayerSurah, lang: AppLanguage = AppLanguage.TURKISH) {
        if (_currentPlayingId.value == surah.id && (_isPlaying.value || _isLoading.value) && _playbackType.value == AudioPlaybackType.MEANING) {
            stop()
            return
        }

        stop()
        _currentPlayingId.value = surah.id
        _playbackType.value = AudioPlaybackType.MEANING
        _isLoading.value = true

        val langCode = when (lang) {
            AppLanguage.TURKISH -> "tr"
            AppLanguage.ENGLISH -> "en"
            AppLanguage.ARABIC -> "ar"
        }
        val textToSpeak = surah.getLocalizedMeaning(lang)

        playbackJob = scope.launch {
            playSpeechChunksSuspend(
                text = textToSpeak,
                langCode = langCode,
                offlineFallbackText = textToSpeak
            )
            if (isActive) {
                stop()
            }
        }
    }

    /**
     * Generic helper to read any Hadith or text aloud in Arabic or localized language.
     */
    fun playCustomText(
        id: String,
        text: String,
        langCode: String,
        isArabicRecitation: Boolean = false
    ) {
        val targetType = if (isArabicRecitation) AudioPlaybackType.ARABIC else AudioPlaybackType.MEANING
        if (_currentPlayingId.value == id && (_isPlaying.value || _isLoading.value) && _playbackType.value == targetType) {
            stop()
            return
        }

        stop()
        _currentPlayingId.value = id
        _playbackType.value = targetType
        _isLoading.value = true

        playbackJob = scope.launch {
            playSpeechChunksSuspend(
                text = text,
                langCode = langCode,
                offlineFallbackText = text
            )
            if (isActive) {
                stop()
            }
        }
    }

    private fun buildEveryAyahUrl(surahNumber: Int, ayahNumber: Int): String {
        val s = surahNumber.toString().padStart(3, '0')
        val a = ayahNumber.toString().padStart(3, '0')
        return "https://everyayah.com/data/Alafasy_128kbps/$s$a.mp3"
    }

    private suspend fun playSingleUrlOrFallbackSuspend(
        primaryUrl: String,
        backupUrl: String?,
        fallbackText: String,
        fallbackLangCode: String,
        offlinePronunciation: String
    ): Boolean {
        val primaryFile = withContext(Dispatchers.IO) { downloadToCache(primaryUrl) }
        if (primaryFile != null && primaryFile.exists() && primaryFile.length() > 500) {
            val ok = playAudioFileSuspend(primaryFile)
            if (ok) return true
        }

        if (!backupUrl.isNullOrBlank()) {
            val backupFile = withContext(Dispatchers.IO) { downloadToCache(backupUrl) }
            if (backupFile != null && backupFile.exists() && backupFile.length() > 500) {
                val ok = playAudioFileSuspend(backupFile)
                if (ok) return true
            }
        }

        // Fallback to natural speech synthesis
        return playSpeechChunksSuspend(
            text = fallbackText,
            langCode = fallbackLangCode,
            offlineFallbackText = offlinePronunciation.ifBlank { fallbackText }
        )
    }

    private suspend fun playSpeechChunksSuspend(
        text: String,
        langCode: String,
        offlineFallbackText: String
    ): Boolean {
        val cleaned = text.replace("۝", ".").trim()
        if (cleaned.isEmpty()) return false

        val chunks = splitTextIntoChunks(cleaned, maxLen = 175)
        var allChunksPlayedOnline = true

        for (chunk in chunks) {
            if (!currentCoroutineContext().isActive) return false
            val encoded = withContext(Dispatchers.IO) {
                URLEncoder.encode(chunk, "UTF-8")
            }
            val ttsUrl = "https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=$langCode&q=$encoded"
            val audioFile = withContext(Dispatchers.IO) { downloadToCache(ttsUrl) }
            if (audioFile != null && audioFile.exists() && audioFile.length() > 200) {
                val ok = playAudioFileSuspend(audioFile)
                if (!ok) {
                    allChunksPlayedOnline = false
                    break
                }
            } else {
                allChunksPlayedOnline = false
                break
            }
        }

        if (allChunksPlayedOnline) return true

        // Offline on-device TTS fallback
        return playOnDeviceTtsSuspend(text = cleaned, langCode = langCode, fallbackLatinText = offlineFallbackText)
    }

    private fun splitTextIntoChunks(text: String, maxLen: Int): List<String> {
        if (text.length <= maxLen) return listOf(text)
        val result = mutableListOf<String>()
        val sentences = text.split(Regex("(?<=[.!?؛،,])\\s+"))
        var current = StringBuilder()

        for (sentence in sentences) {
            if (current.length + sentence.length + 1 <= maxLen) {
                if (current.isNotEmpty()) current.append(" ")
                current.append(sentence)
            } else {
                if (current.isNotEmpty()) {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                if (sentence.length <= maxLen) {
                    current.append(sentence)
                } else {
                    // Split by words
                    val words = sentence.split(" ")
                    for (w in words) {
                        if (current.length + w.length + 1 <= maxLen) {
                            if (current.isNotEmpty()) current.append(" ")
                            current.append(w)
                        } else {
                            if (current.isNotEmpty()) result.add(current.toString())
                            current = StringBuilder(w)
                        }
                    }
                }
            }
        }
        if (current.isNotEmpty()) {
            result.add(current.toString())
        }
        return result
    }

    private fun downloadToCache(url: String): File? {
        return try {
            val hash = sha256(url)
            val targetFile = File(audioCacheDir, "$hash.mp3")
            if (targetFile.exists() && targetFile.length() > 400) {
                return targetFile
            }

            val req = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36")
                .build()

            httpClient.newCall(req).execute().use { response ->
                if (!response.isSuccessful) return null
                val bytes = response.body?.bytes() ?: return null
                if (bytes.size < 200) return null
                targetFile.writeBytes(bytes)
                targetFile
            }
        } catch (e: Exception) {
            Log.w("SurahAudioPlayer", "Download failed for $url: ${e.message}")
            null
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(32)
    }

    private suspend fun playAudioFileSuspend(file: File): Boolean = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { cont ->
            try {
                releaseMediaPlayerOnly()
                val fis = FileInputStream(file)
                val mp = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(fis.fd)
                    prepare()
                }
                try {
                    fis.close()
                } catch (_: Exception) {}

                mediaPlayer = mp
                _isLoading.value = false
                _isPlaying.value = true

                mp.setOnCompletionListener {
                    if (cont.isActive) cont.resume(true) {}
                }
                mp.setOnErrorListener { _, what, extra ->
                    Log.e("SurahAudioPlayer", "MediaPlayer error what=$what extra=$extra")
                    if (cont.isActive) cont.resume(false) {}
                    true
                }

                startProgressTracker()
                mp.start()

                cont.invokeOnCancellation {
                    releaseMediaPlayerOnly()
                }
            } catch (e: Exception) {
                Log.e("SurahAudioPlayer", "Error playing cached file", e)
                if (cont.isActive) cont.resume(false) {}
            }
        }
    }

    private suspend fun playOnDeviceTtsSuspend(
        text: String,
        langCode: String,
        fallbackLatinText: String
    ): Boolean = withContext(Dispatchers.Main) {
        if (!isTtsReady || tts == null) {
            _isLoading.value = false
            return@withContext false
        }

        suspendCancellableCoroutine { cont ->
            try {
                val targetLocale = when (langCode) {
                    "ar" -> Locale("ar")
                    "en" -> Locale.US
                    else -> Locale("tr", "TR")
                }
                val langRes = tts?.setLanguage(targetLocale)
                val actualTextToSpeak = if (
                    langCode == "ar" &&
                    (langRes == TextToSpeech.LANG_MISSING_DATA || langRes == TextToSpeech.LANG_NOT_SUPPORTED)
                ) {
                    // Arabic TTS pack not installed on device: use Turkish voice reading Turkish pronunciation!
                    tts?.language = Locale("tr", "TR")
                    fallbackLatinText
                } else {
                    text
                }

                val utteranceId = "utt_${System.currentTimeMillis()}"
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(id: String?) {
                        _isLoading.value = false
                        _isPlaying.value = true
                    }

                    override fun onDone(id: String?) {
                        if (cont.isActive) cont.resume(true) {}
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(id: String?) {
                        if (cont.isActive) cont.resume(false) {}
                    }
                })

                _isLoading.value = false
                _isPlaying.value = true
                tts?.speak(actualTextToSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

                cont.invokeOnCancellation {
                    tts?.stop()
                }
            } catch (e: Exception) {
                if (cont.isActive) cont.resume(false) {}
            }
        }
    }

    private fun releaseMediaPlayerOnly() {
        progressJob?.cancel()
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {
        } finally {
            mediaPlayer = null
        }
    }

    fun pause() {
        stop()
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        releaseMediaPlayerOnly()
        try {
            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
        } catch (_: Exception) {
        }
        _isPlaying.value = false
        _isLoading.value = false
        _playbackType.value = AudioPlaybackType.NONE
        _currentPlayingId.value = null
        _currentPlayingAyahNumber.value = null
        _progress.value = 0f
        _currentDurationText.value = "00:00"
    }

    fun toggleLoop() {
        val updated = !_isLooping.value
        _isLooping.value = updated
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (_isPlaying.value && mediaPlayer != null) {
                try {
                    val current = mediaPlayer?.currentPosition ?: 0
                    val total = mediaPlayer?.duration ?: 1
                    if (total > 0) {
                        _progress.value = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                        val curSec = current / 1000
                        val min = curSec / 60
                        val sec = curSec % 60
                        _currentDurationText.value = "%02d:%02d".format(Locale.US, min, sec)
                    }
                } catch (_: Exception) {}
                delay(250)
            }
        }
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
        scope.cancel()
    }
}
