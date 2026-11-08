package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.example.data.model.PrayerType
import com.example.data.repository.UserPreferences

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PRAYER_ALARM = "com.example.ACTION_PRAYER_ALARM"
        const val ACTION_HOLIDAY_ALARM = "com.example.ACTION_HOLIDAY_ALARM"
        const val ACTION_IMSAKIYE_ALARM = "com.example.ACTION_IMSAKIYE_ALARM"
        const val ACTION_STOP_ADHAN = "com.example.ACTION_STOP_ADHAN"

        const val EXTRA_PRAYER_KEY = "extra_prayer_key"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
        const val EXTRA_CITY_NAME = "extra_city_name"

        const val EXTRA_HOLIDAY_ID = "extra_holiday_id"
        const val EXTRA_HOLIDAY_TITLE = "extra_holiday_title"
        const val EXTRA_HOLIDAY_HIJRI = "extra_holiday_hijri"
        const val EXTRA_HOLIDAY_DESC = "extra_holiday_desc"
        const val EXTRA_HOLIDAY_PLAY_SOUND = "extra_holiday_play_sound"

        const val EXTRA_IMSAKIYE_IS_SAHUR = "extra_imsakiye_is_sahur"
        const val EXTRA_IMSAKIYE_MINUTES_BEFORE = "extra_imsakiye_minutes_before"
        const val EXTRA_IMSAKIYE_PLAY_SOUND = "extra_imsakiye_play_sound"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == ACTION_STOP_ADHAN) {
            val stopIntent = Intent(context, PrayerAdhanPlaybackService::class.java).apply {
                this.action = PrayerAdhanPlaybackService.ACTION_STOP_ADHAN
            }
            context.startService(stopIntent)
            return
        }

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"
        ) {
            PrayerScheduler.scheduleUpcomingPrayers(context)
            PrayerScheduler.scheduleReligiousDayAlarms(context)
            PrayerScheduler.scheduleImsakiyeAlarms(context)
            return
        }

        val prefs = UserPreferences(context)
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isScreenOn = powerManager?.isInteractive ?: true
        val canPlaySound = if (!isScreenOn) prefs.playWhenScreenOff else true

        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "EzanVakti:PrayerAlarmWakeLock"
        )?.apply {
            setReferenceCounted(false)
            acquire(30000L)
        }

        try {
            if (action == ACTION_IMSAKIYE_ALARM) {
                val isSahur = intent.getBooleanExtra(EXTRA_IMSAKIYE_IS_SAHUR, true)
                val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""
                val cityName = intent.getStringExtra(EXTRA_CITY_NAME) ?: prefs.customCityName
                val minutesBefore = intent.getIntExtra(EXTRA_IMSAKIYE_MINUTES_BEFORE, 0)
                val playSound = intent.getBooleanExtra(EXTRA_IMSAKIYE_PLAY_SOUND, true) && canPlaySound

                val effectiveSoundType = if (playSound) {
                    if (prefs.alertSoundType == "titresim") "ezan" else prefs.alertSoundType
                } else {
                    "titresim"
                }

                PrayerNotificationHelper.showImsakiyeNotification(
                    context = context,
                    isSahur = isSahur,
                    prayerTime = prayerTime,
                    minutesBefore = minutesBefore,
                    cityName = cityName,
                    playSoundAlert = playSound,
                    alertSoundType = effectiveSoundType,
                    isVibrationEnabled = prefs.isVibrationEnabled
                )

                if (playSound && effectiveSoundType == "ezan") {
                    val label = if (isSahur) "Sahur / İmsak" else "İftar / Akşam"
                    val serviceIntent = Intent(context, PrayerAdhanPlaybackService::class.java).apply {
                        this.action = PrayerAdhanPlaybackService.ACTION_PLAY_ADHAN
                        putExtra(PrayerAdhanPlaybackService.EXTRA_PRAYER_KEY, if (isSahur) "imsak" else "aksam")
                        putExtra(PrayerAdhanPlaybackService.EXTRA_PRAYER_NAME, label)
                        putExtra(PrayerAdhanPlaybackService.EXTRA_CITY_NAME, cityName)
                        putExtra(PrayerAdhanPlaybackService.EXTRA_VOLUME_PERCENT, prefs.alertVolumePercent)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
                PrayerScheduler.scheduleImsakiyeAlarms(context)
                return
            }

            if (action == ACTION_HOLIDAY_ALARM) {
                val holidayId = intent.getStringExtra(EXTRA_HOLIDAY_ID) ?: return
                val holidayTitle = intent.getStringExtra(EXTRA_HOLIDAY_TITLE) ?: "Mübarek Gün"
                val holidayHijri = intent.getStringExtra(EXTRA_HOLIDAY_HIJRI) ?: ""
                val holidayDesc = intent.getStringExtra(EXTRA_HOLIDAY_DESC) ?: ""
                val playSound = intent.getBooleanExtra(EXTRA_HOLIDAY_PLAY_SOUND, true) && canPlaySound

                val effectiveSoundType = if (playSound) {
                    if (prefs.alertSoundType == "titresim") "melodi" else prefs.alertSoundType
                } else {
                    "titresim"
                }

                PrayerNotificationHelper.showHolidayNotification(
                    context = context,
                    holidayId = holidayId,
                    holidayTitle = holidayTitle,
                    holidayHijriDate = holidayHijri,
                    holidayDescription = holidayDesc,
                    playSoundAlert = playSound,
                    alertSoundType = effectiveSoundType,
                    isVibrationEnabled = prefs.isVibrationEnabled
                )

                if (playSound && effectiveSoundType == "ezan") {
                    val serviceIntent = Intent(context, PrayerAdhanPlaybackService::class.java).apply {
                        this.action = PrayerAdhanPlaybackService.ACTION_PLAY_ADHAN
                        putExtra(PrayerAdhanPlaybackService.EXTRA_PRAYER_KEY, holidayId)
                        putExtra(PrayerAdhanPlaybackService.EXTRA_PRAYER_NAME, holidayTitle)
                        putExtra(PrayerAdhanPlaybackService.EXTRA_CITY_NAME, holidayHijri)
                        putExtra(PrayerAdhanPlaybackService.EXTRA_VOLUME_PERCENT, prefs.alertVolumePercent)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
                return
            }

            // Handle Standard Prayer Alarm Trigger
            val prayerKey = intent.getStringExtra(EXTRA_PRAYER_KEY) ?: return
            val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Ezan"
            val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""
            val cityName = intent.getStringExtra(EXTRA_CITY_NAME) ?: "Bulunduğunuz Konum"

            if (!prefs.isNotificationsEnabled) {
                PrayerScheduler.scheduleUpcomingPrayers(context)
                return
            }

            val prayerType = PrayerType.fromKey(prayerKey)
            val isSoundAlertEnabled = prefs.isPrayerSoundEnabled(prayerType)

            val notificationId = prayerKey.hashCode()
            val effectiveSoundType = if (isSoundAlertEnabled && canPlaySound) {
                prefs.alertSoundType
            } else {
                "titresim"
            }

            PrayerNotificationHelper.showPrayerNotification(
                context = context,
                prayerKey = prayerKey,
                prayerName = prayerName,
                prayerTime = prayerTime,
                cityName = cityName,
                alertSoundType = effectiveSoundType,
                isVibrationEnabled = prefs.isVibrationEnabled,
                notificationId = notificationId
            )

            if (isSoundAlertEnabled && canPlaySound && effectiveSoundType == "ezan") {
                val serviceIntent = Intent(context, PrayerAdhanPlaybackService::class.java).apply {
                    this.action = PrayerAdhanPlaybackService.ACTION_PLAY_ADHAN
                    putExtra(PrayerAdhanPlaybackService.EXTRA_PRAYER_KEY, prayerKey)
                    putExtra(PrayerAdhanPlaybackService.EXTRA_PRAYER_NAME, prayerName)
                    putExtra(PrayerAdhanPlaybackService.EXTRA_CITY_NAME, cityName)
                    putExtra(PrayerAdhanPlaybackService.EXTRA_VOLUME_PERCENT, prefs.alertVolumePercent)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        } finally {
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
            PrayerScheduler.scheduleUpcomingPrayers(context)
        }
    }
}
