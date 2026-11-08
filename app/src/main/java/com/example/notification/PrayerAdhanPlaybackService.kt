package com.example.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class PrayerAdhanPlaybackService : Service() {

    companion object {
        const val ACTION_PLAY_ADHAN = "com.example.ACTION_PLAY_ADHAN"
        const val ACTION_STOP_ADHAN = "com.example.ACTION_STOP_ADHAN"

        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_CITY_NAME = "extra_city_name"
        const val EXTRA_PRAYER_KEY = "extra_prayer_key"
        const val EXTRA_VOLUME_PERCENT = "extra_volume_percent"

        const val NOTIFICATION_ID = 8888
    }

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "EzanVakti:AdhanPlaybackWakeLock"
        )?.apply {
            setReferenceCounted(false)
            acquire(4 * 60 * 1000L) // 4 minutes max wake lock
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == ACTION_STOP_ADHAN) {
            stopAdhan()
            return START_NOT_STICKY
        }

        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: "Ezan"
        val cityName = intent?.getStringExtra(EXTRA_CITY_NAME) ?: "Bulunduğunuz Konum"
        val prayerKey = intent?.getStringExtra(EXTRA_PRAYER_KEY) ?: "ogle"
        val volumePercent = intent?.getIntExtra(EXTRA_VOLUME_PERCENT, 100) ?: 100

        // Build Foreground Notification
        val notification = buildForegroundNotification(prayerName, cityName, prayerKey)
        startForeground(NOTIFICATION_ID, notification)

        // Start Audio Playback using bundled raw adhan (100% offline, 0ms latency)
        startPlayback(volumePercent)

        return START_NOT_STICKY
    }

    private fun buildForegroundNotification(
        prayerName: String,
        cityName: String,
        prayerKey: String
    ): android.app.Notification {
        PrayerNotificationHelper.createNotificationChannels(this)

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_PRAYER_KEY", prayerKey)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            101,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, PrayerAdhanPlaybackService::class.java).apply {
            action = ACTION_STOP_ADHAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            102,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "$prayerName Ezanı Okunuyor"
        val content = "$cityName için vakit girdi. Haydi namaza!"

        return NotificationCompat.Builder(this, PrayerNotificationHelper.CHANNEL_ID_ADHAN)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Ezanı Durdur", stopPendingIntent)
            .build()
    }

    private fun startPlayback(volumePercent: Int) {
        stopAdhanInternal(keepService = true)

        try {
            val vol = (volumePercent.coerceIn(10, 100)) / 100f

            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.adhan)?.apply {
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
                )
                setVolume(vol, vol)
                setOnCompletionListener {
                    stopAdhan()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AdhanPlayback", "Error playing adhan: what=$what extra=$extra")
                    stopAdhan()
                    true
                }
                start()
            }

            if (mediaPlayer == null) {
                Log.e("AdhanPlayback", "MediaPlayer.create returned null for R.raw.adhan")
                stopAdhan()
            }
        } catch (e: Exception) {
            Log.e("AdhanPlayback", "Exception starting adhan playback", e)
            stopAdhan()
        }
    }

    private fun stopAdhanInternal(keepService: Boolean = false) {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {
        }
        if (!keepService) {
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun stopAdhan() {
        stopAdhanInternal(keepService = false)
    }

    override fun onDestroy() {
        stopAdhan()
        super.onDestroy()
    }
}
