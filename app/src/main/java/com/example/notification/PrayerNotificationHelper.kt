package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object PrayerNotificationHelper {

    const val CHANNEL_ID_ADHAN = "prayer_alerts_adhan_v4"
    const val CHANNEL_NAME_ADHAN = "Ezan Sesi (Özel Oynatıcı)"
    const val CHANNEL_DESC_ADHAN = "Ezan vakti girdiğinde ses çakışması olmadan sadece ezan okur"

    const val CHANNEL_ID_MELODY = "prayer_alerts_melody_v4"
    const val CHANNEL_NAME_MELODY = "Vakit Bildirimi (Standart Melodi)"
    const val CHANNEL_DESC_MELODY = "Ezan vakti girdiğinde sistem alarm melodisi çalar"

    const val CHANNEL_ID_SILENT = "prayer_alerts_silent_v4"
    const val CHANNEL_NAME_SILENT = "Sessiz Vakit Bildirimi"
    const val CHANNEL_DESC_SILENT = "Yalnızca titreşimli veya sessiz ezan vakti bildirimi"

    fun createNotificationChannel(context: Context) = createNotificationChannels(context)

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // 1. Adhan channel (Silent chime so audio doesn't overlap with Adhan playback service)
            val adhanChannel = NotificationChannel(
                CHANNEL_ID_ADHAN,
                CHANNEL_NAME_ADHAN,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_ADHAN
                enableLights(true)
                lightColor = 0xFF0F5A47.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
                setSound(null, null) // CRITICAL: no channel sound, avoids audio overlap with Adhan service!
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(adhanChannel)

            // 2. Melody channel (Plays system alarm/ringtone sound)
            val defaultAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val melodyChannel = NotificationChannel(
                CHANNEL_ID_MELODY,
                CHANNEL_NAME_MELODY,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_MELODY
                enableLights(true)
                lightColor = 0xFF0F5A47.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setSound(defaultAlarmSound, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(melodyChannel)

            // 3. Silent / Vibration channel
            val silentChannel = NotificationChannel(
                CHANNEL_ID_SILENT,
                CHANNEL_NAME_SILENT,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_SILENT
                enableLights(true)
                lightColor = 0xFF0F5A47.toInt()
                enableVibration(true)
                setSound(null, null)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(silentChannel)
        }
    }

    fun showPrayerNotification(
        context: Context,
        prayerKey: String,
        prayerName: String,
        prayerTime: String,
        cityName: String,
        alertSoundType: String = "ezan", // "ezan", "melodi", "titresim"
        isVibrationEnabled: Boolean = true,
        notificationId: Int = (System.currentTimeMillis() % 10000).toInt()
    ) {
        createNotificationChannels(context)

        // Select channel and sound based on alertSoundType
        val channelId = when (alertSoundType) {
            "melodi" -> CHANNEL_ID_MELODY
            "titresim" -> CHANNEL_ID_SILENT
            else -> CHANNEL_ID_ADHAN // "ezan" uses adhan channel with no notification chime!
        }

        // Intent to open MainActivity
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_PRAYER_KEY", prayerKey)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent to stop adhan
        val stopAdhanIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_STOP_ADHAN
        }
        val stopAdhanPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 10,
            stopAdhanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Vakit Geldi: $prayerName Ezanı ($prayerTime)"
        val messageText = "$cityName için $prayerName ezanı vakti girdi. Haydi namaza, haydi kurtuluşa!"

        val defaultAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .setFullScreenIntent(openAppPendingIntent, true) // Wakes screen and shows full-screen HUD

        if (isVibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        // Only set notification sound if user explicitly chose "melodi"
        if (alertSoundType == "melodi") {
            builder.setSound(defaultAlarmSound)
        } else {
            builder.setSound(null)
        }

        if (alertSoundType == "ezan") {
            builder.addAction(android.R.drawable.ic_media_pause, "Ezanı Durdur", stopAdhanPendingIntent)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(notificationId, builder.build())
    }

    fun showHolidayNotification(
        context: Context,
        holidayId: String,
        holidayTitle: String,
        holidayHijriDate: String,
        holidayDescription: String,
        playSoundAlert: Boolean = true,
        alertSoundType: String = "ezan",
        isVibrationEnabled: Boolean = true,
        notificationId: Int = (holidayId.hashCode() and 0x7FFFFFFF)
    ) {
        createNotificationChannels(context)

        val effectiveSoundType = if (playSoundAlert) alertSoundType else "titresim"
        val channelId = when (effectiveSoundType) {
            "melodi" -> CHANNEL_ID_MELODY
            "titresim" -> CHANNEL_ID_SILENT
            else -> CHANNEL_ID_ADHAN
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_HOLIDAY_ID", holidayId)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopAdhanIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_STOP_ADHAN
        }
        val stopAdhanPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 20,
            stopAdhanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "$holidayTitle ($holidayHijriDate)"
        val defaultAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(holidayDescription)
            .setStyle(NotificationCompat.BigTextStyle().bigText(holidayDescription))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .setFullScreenIntent(openAppPendingIntent, true)

        if (isVibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        if (effectiveSoundType == "melodi") {
            builder.setSound(defaultAlarmSound)
        } else {
            builder.setSound(null)
        }

        if (effectiveSoundType == "ezan") {
            builder.addAction(android.R.drawable.ic_media_pause, "Sesi Durdur", stopAdhanPendingIntent)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(notificationId, builder.build())
    }

    fun showImsakiyeNotification(
        context: Context,
        isSahur: Boolean,
        prayerTime: String,
        minutesBefore: Int,
        cityName: String,
        playSoundAlert: Boolean = true,
        alertSoundType: String = "ezan",
        isVibrationEnabled: Boolean = true,
        notificationId: Int = if (isSahur) 7901 else 7902
    ) {
        createNotificationChannels(context)

        val effectiveSoundType = if (playSoundAlert) alertSoundType else "titresim"
        val channelId = when (effectiveSoundType) {
            "melodi" -> CHANNEL_ID_MELODY
            "titresim" -> CHANNEL_ID_SILENT
            else -> CHANNEL_ID_ADHAN
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_IMSAKIYE_TYPE", if (isSahur) "sahur" else "iftar")
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopAdhanIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_STOP_ADHAN
        }
        val stopAdhanPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 30,
            stopAdhanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isSahur) {
            if (minutesBefore > 0) {
                "Sahur Vakti Hatırlatması ($minutesBefore dk kaldı • İmsak: $prayerTime)"
            } else {
                "İmsak Vakti Girdi ($prayerTime) • Hayırlı Sahurlar"
            }
        } else {
            if (minutesBefore > 0) {
                "İftar Vaktine $minutesBefore Dakika Kaldı (İftar: $prayerTime)"
            } else {
                "İftar Vakti Geldi ($prayerTime) • Hayırlı İftarlar!"
            }
        }

        val bodyText = if (isSahur) {
            if (minutesBefore > 0) {
                "$cityName için imsak vaktine ($prayerTime) $minutesBefore dakika kaldı. Bereketli ve hayırlı sahurlar dileriz."
            } else {
                "$cityName için imsak vakti ($prayerTime) girdi. Allah tuttuğunuz oruçları kabul eylesin."
            }
        } else {
            if (minutesBefore > 0) {
                "$cityName için akşam ezanı ve iftar vaktine ($prayerTime) $minutesBefore dakika kaldı."
            } else {
                "$cityName için akşam ezanı ve iftar vakti ($prayerTime) girdi. Allah kabul etsin, hayırlı iftarlar!"
            }
        }

        val defaultAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(bodyText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .setFullScreenIntent(openAppPendingIntent, true)

        if (isVibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        if (effectiveSoundType == "melodi") {
            builder.setSound(defaultAlarmSound)
        } else {
            builder.setSound(null)
        }

        if (effectiveSoundType == "ezan") {
            builder.addAction(android.R.drawable.ic_media_pause, "Ezanı Durdur", stopAdhanPendingIntent)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(notificationId, builder.build())
    }
}
