package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.util.Log
import com.example.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdhanPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingTitle = MutableStateFlow<String?>(null)
    val currentPlayingTitle: StateFlow<String?> = _currentPlayingTitle.asStateFlow()

    fun playAdhan(title: String = "Ezan-ı Şerif", volumePercent: Int = 100) {
        try {
            stop()
            val vol = (volumePercent.coerceIn(10, 100)) / 100f
            mediaPlayer = MediaPlayer.create(context, R.raw.adhan)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setVolume(vol, vol)
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPlayingTitle.value = null
                    release()
                    mediaPlayer = null
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AdhanPlayer", "MediaPlayer error: what=$what, extra=$extra")
                    _isPlaying.value = false
                    _currentPlayingTitle.value = null
                    true
                }
                start()
            }
            _isPlaying.value = true
            _currentPlayingTitle.value = title
        } catch (e: Exception) {
            Log.e("AdhanPlayer", "Failed to play adhan", e)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    fun playMelody(title: String = "Standart Melodi", volumePercent: Int = 100) {
        try {
            stop()
            val vol = (volumePercent.coerceIn(10, 100)) / 100f
            val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(context, defaultSound)
                prepare()
                setVolume(vol, vol)
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPlayingTitle.value = null
                    release()
                    mediaPlayer = null
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AdhanPlayer", "Melody error: what=$what, extra=$extra")
                    _isPlaying.value = false
                    _currentPlayingTitle.value = null
                    true
                }
                start()
            }
            _isPlaying.value = true
            _currentPlayingTitle.value = title
        } catch (e: Exception) {
            Log.e("AdhanPlayer", "Failed to play melody", e)
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    fun playSound(type: String, volumePercent: Int = 100) {
        if (type == "melodi") {
            playMelody(volumePercent = volumePercent)
        } else {
            playAdhan(volumePercent = volumePercent)
        }
    }

    fun toggleSound(type: String, volumePercent: Int = 100) {
        if (_isPlaying.value) {
            stop()
        } else {
            playSound(type, volumePercent)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AdhanPlayer", "Error stopping player", e)
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _currentPlayingTitle.value = null
        }
    }

    fun toggle(title: String = "Ezan-ı Şerif") {
        if (_isPlaying.value) {
            stop()
        } else {
            playAdhan(title)
        }
    }

    fun release() {
        stop()
    }
}
