package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.MainActivity
import com.example.data.model.AppLanguage
import com.example.data.model.HolidayAlertMode
import com.example.data.model.PrayerType
import com.example.data.model.ReligiousHoliday
import com.example.data.repository.PrayerCalculator
import com.example.data.repository.ReligiousDaysRepository
import com.example.data.repository.TurkishCities
import com.example.data.repository.UserPreferences
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object PrayerScheduler {

    const val UNIQUE_DAILY_REFRESH = "unique_prayer_daily_refresh"

    fun scheduleUpcomingPrayers(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val prefs = UserPreferences(context)

        val city = if (prefs.isGpsLocationEnabled) {
            val nearest = TurkishCities.findNearestCity(prefs.customLat.toDouble(), prefs.customLon.toDouble())
            nearest.copy(name = prefs.customCityName, lat = prefs.customLat.toDouble(), lon = prefs.customLon.toDouble())
        } else {
            TurkishCities.allCities.find { it.plateCode == prefs.selectedCityPlate } ?: TurkishCities.defaultCity
        }

        val temkin = prefs.temkinOffsetMinutes
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR"))

        val schedulesToRegister = mutableListOf<com.example.data.model.SinglePrayerInfo>()

        // Today
        val todayTimes = PrayerCalculator.calculatePrayerTimes(
            year = cal.get(Calendar.YEAR),
            month = cal.get(Calendar.MONTH) + 1,
            day = cal.get(Calendar.DAY_OF_MONTH),
            lat = city.lat,
            lon = city.lon,
            temkinOffsetMinutes = temkin,
            soundPreferences = prefs.getSoundPreferencesMap()
        )
        schedulesToRegister.addAll(todayTimes.prayersList)

        // Tomorrow
        val tomorrowCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        val tomorrowTimes = PrayerCalculator.calculatePrayerTimes(
            year = tomorrowCal.get(Calendar.YEAR),
            month = tomorrowCal.get(Calendar.MONTH) + 1,
            day = tomorrowCal.get(Calendar.DAY_OF_MONTH),
            lat = city.lat,
            lon = city.lon,
            temkinOffsetMinutes = temkin,
            soundPreferences = prefs.getSoundPreferencesMap()
        )
        schedulesToRegister.addAll(tomorrowTimes.prayersList)

        for (prayer in schedulesToRegister) {
            val isEnabled = prefs.isPrayerSoundEnabled(prayer.type)
            val requestCode = (prayer.type.key + "_" + (prayer.timeMillis / 86400000)).hashCode()

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_KEY, prayer.type.key)
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayer.type.turkishName)
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME, prayer.timeFormatted)
                putExtra(PrayerAlarmReceiver.EXTRA_CITY_NAME, city.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!prefs.isNotificationsEnabled || !isEnabled || prayer.timeMillis <= now) {
                alarmManager.cancel(pendingIntent)
            } else {
                val showAppIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val showPendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode + 1,
                    showAppIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    val clockInfo = AlarmManager.AlarmClockInfo(prayer.timeMillis, showPendingIntent)
                    alarmManager.setAlarmClock(clockInfo, pendingIntent)
                } catch (_: SecurityException) {
                    try {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            prayer.timeMillis,
                            pendingIntent
                        )
                    } catch (_: Exception) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, prayer.timeMillis, pendingIntent)
                    }
                }
            }
        }

        scheduleReligiousDayAlarms(context)
        scheduleImsakiyeAlarms(context)

        try {
            val workManager = WorkManager.getInstance(context)
            val periodicRequest = PeriodicWorkRequestBuilder<PrayerDailyRefreshWorker>(
                8, TimeUnit.HOURS
            ).build()

            workManager.enqueueUniquePeriodicWork(
                UNIQUE_DAILY_REFRESH,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        } catch (_: Exception) {
        }
    }

    /**
     * Schedules or cancels exact alarms for Sahur (İmsak) and İftar (Akşam) based on user's
     * İmsakiye notification preferences (today and tomorrow).
     */
    fun scheduleImsakiyeAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val prefs = UserPreferences(context)
        val city = if (prefs.isGpsLocationEnabled && prefs.customLat != 0f && prefs.customLon != 0f) {
            val nearest = TurkishCities.findNearestCity(prefs.customLat.toDouble(), prefs.customLon.toDouble())
            com.example.data.model.City(nearest.plateCode, prefs.customCityName, prefs.customLat.toDouble(), prefs.customLon.toDouble())
        } else {
            TurkishCities.allCities.find { it.plateCode == prefs.selectedCityPlate }
                ?: TurkishCities.defaultCity
        }
        val temkin = prefs.temkinOffsetMinutes
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR"))
        val now = System.currentTimeMillis()

        val daysList = listOf(
            cal,
            (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        )

        daysList.forEachIndexed { dayIndex, dayCal ->
            val dayTimes = PrayerCalculator.calculatePrayerTimes(
                year = dayCal.get(Calendar.YEAR),
                month = dayCal.get(Calendar.MONTH) + 1,
                day = dayCal.get(Calendar.DAY_OF_MONTH),
                lat = city.lat,
                lon = city.lon,
                temkinOffsetMinutes = temkin,
                soundPreferences = prefs.getSoundPreferencesMap()
            )

            // 1. Sahur (İmsak) Alarm
            val imsakPrayer = dayTimes.prayersList.find { it.type == PrayerType.IMSAK }
            if (imsakPrayer != null) {
                val sahurTriggerMillis = imsakPrayer.timeMillis - (prefs.sahurMinutesBeforeImsak * 60_000L)
                val sahurReqCode = ("imsakiye_sahur_$dayIndex").hashCode()
                val sahurIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    action = PrayerAlarmReceiver.ACTION_IMSAKIYE_ALARM
                    putExtra(PrayerAlarmReceiver.EXTRA_IMSAKIYE_IS_SAHUR, true)
                    putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME, imsakPrayer.timeFormatted)
                    putExtra(PrayerAlarmReceiver.EXTRA_CITY_NAME, city.name)
                    putExtra(PrayerAlarmReceiver.EXTRA_IMSAKIYE_MINUTES_BEFORE, prefs.sahurMinutesBeforeImsak)
                    putExtra(PrayerAlarmReceiver.EXTRA_IMSAKIYE_PLAY_SOUND, prefs.sahurPlaySound)
                }
                val sahurPi = PendingIntent.getBroadcast(
                    context,
                    sahurReqCode,
                    sahurIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                if (!prefs.isSahurAlarmEnabled || sahurTriggerMillis <= now) {
                    alarmManager.cancel(sahurPi)
                } else {
                    scheduleExactPendingIntent(context, alarmManager, sahurTriggerMillis, sahurPi, sahurReqCode)
                }
            }

            // 2. İftar (Akşam) Alarm
            val aksamPrayer = dayTimes.prayersList.find { it.type == PrayerType.AKSAM }
            if (aksamPrayer != null) {
                val iftarTriggerMillis = aksamPrayer.timeMillis - (prefs.iftarMinutesBeforeAksam * 60_000L)
                val iftarReqCode = ("imsakiye_iftar_$dayIndex").hashCode()
                val iftarIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    action = PrayerAlarmReceiver.ACTION_IMSAKIYE_ALARM
                    putExtra(PrayerAlarmReceiver.EXTRA_IMSAKIYE_IS_SAHUR, false)
                    putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME, aksamPrayer.timeFormatted)
                    putExtra(PrayerAlarmReceiver.EXTRA_CITY_NAME, city.name)
                    putExtra(PrayerAlarmReceiver.EXTRA_IMSAKIYE_MINUTES_BEFORE, prefs.iftarMinutesBeforeAksam)
                    putExtra(PrayerAlarmReceiver.EXTRA_IMSAKIYE_PLAY_SOUND, prefs.iftarPlaySound)
                }
                val iftarPi = PendingIntent.getBroadcast(
                    context,
                    iftarReqCode,
                    iftarIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                if (!prefs.isIftarAlarmEnabled || iftarTriggerMillis <= now) {
                    alarmManager.cancel(iftarPi)
                } else {
                    scheduleExactPendingIntent(context, alarmManager, iftarTriggerMillis, iftarPi, iftarReqCode)
                }
            }
        }
    }

    private fun scheduleExactPendingIntent(
        context: Context,
        alarmManager: AlarmManager,
        triggerMillis: Long,
        pendingIntent: PendingIntent,
        requestCode: Int
    ) {
        val showAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            requestCode + 1,
            showAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            val clockInfo = AlarmManager.AlarmClockInfo(triggerMillis, showPendingIntent)
            alarmManager.setAlarmClock(clockInfo, pendingIntent)
        } catch (_: SecurityException) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } catch (_: Exception) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        }
    }

    /**
     * Triggers an immediate test notification & optional Adhan sound for Sahur or İftar.
     */
    fun triggerImmediateImsakiyeTestAlert(
        context: Context,
        isSahur: Boolean,
        prayerTimeFormatted: String
    ) {
        val prefs = UserPreferences(context)
        val cityName = prefs.customCityName
        val minutesBefore = if (isSahur) prefs.sahurMinutesBeforeImsak else prefs.iftarMinutesBeforeAksam
        val playSound = if (isSahur) prefs.sahurPlaySound else prefs.iftarPlaySound
        val effectiveSoundType = if (playSound) {
            if (prefs.alertSoundType == "titresim") "ezan" else prefs.alertSoundType
        } else {
            "titresim"
        }

        PrayerNotificationHelper.showImsakiyeNotification(
            context = context,
            isSahur = isSahur,
            prayerTime = prayerTimeFormatted,
            minutesBefore = minutesBefore,
            cityName = cityName,
            playSoundAlert = playSound,
            alertSoundType = effectiveSoundType,
            isVibrationEnabled = prefs.isVibrationEnabled
        )

        if (playSound && effectiveSoundType == "ezan") {
            val label = if (isSahur) "Sahur / İmsak" else "İftar / Akşam"
            val serviceIntent = Intent(context, PrayerAdhanPlaybackService::class.java).apply {
                action = PrayerAdhanPlaybackService.ACTION_PLAY_ADHAN
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
    }

    /**
     * Schedules or cancels exact alarms for all Religious Days & Holidays configured by the user.
     */
    fun scheduleReligiousDayAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val prefs = UserPreferences(context)
        val lang = AppLanguage.fromCode(prefs.appLanguage)
        val now = System.currentTimeMillis()

        for (holiday in ReligiousDaysRepository.allHolidays) {
            val config = prefs.getHolidayAlarmConfig(holiday)
            val triggerMillis = holiday.calculateTriggerMillis(config.timing)
            val requestCode = ("holiday_alarm_${holiday.id}").hashCode()

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = PrayerAlarmReceiver.ACTION_HOLIDAY_ALARM
                putExtra(PrayerAlarmReceiver.EXTRA_HOLIDAY_ID, holiday.id)
                putExtra(PrayerAlarmReceiver.EXTRA_HOLIDAY_TITLE, holiday.getLocalizedTitle(lang))
                putExtra(PrayerAlarmReceiver.EXTRA_HOLIDAY_HIJRI, holiday.getLocalizedHijriDate(lang))
                putExtra(PrayerAlarmReceiver.EXTRA_HOLIDAY_DESC, holiday.getLocalizedDescription(lang))
                putExtra(
                    PrayerAlarmReceiver.EXTRA_HOLIDAY_PLAY_SOUND,
                    config.alertMode == HolidayAlertMode.NOTIFICATION_AND_SOUND
                )
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!config.isEnabled || triggerMillis <= now) {
                alarmManager.cancel(pendingIntent)
            } else {
                val showAppIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val showPendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode + 1,
                    showAppIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    val clockInfo = AlarmManager.AlarmClockInfo(triggerMillis, showPendingIntent)
                    alarmManager.setAlarmClock(clockInfo, pendingIntent)
                } catch (_: SecurityException) {
                    try {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerMillis,
                            pendingIntent
                        )
                    } catch (_: Exception) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                    }
                }
            }
        }
    }

    fun triggerImmediateTestNotification(context: Context) {
        val prefs = UserPreferences(context)
        val cityName = prefs.customCityName
        val cal = Calendar.getInstance()
        val timeFormatted = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        val soundType = prefs.alertSoundType // "ezan", "melodi", "titresim"

        PrayerNotificationHelper.showPrayerNotification(
            context = context,
            prayerKey = "ogle",
            prayerName = "Öğle",
            prayerTime = timeFormatted,
            cityName = cityName,
            alertSoundType = soundType,
            isVibrationEnabled = prefs.isVibrationEnabled
        )

        // If user selected "ezan", launch the Adhan playback foreground service
        // just like the real prayer alarm receiver does!
        if (soundType == "ezan") {
            val serviceIntent = Intent(context, PrayerAdhanPlaybackService::class.java).apply {
                action = PrayerAdhanPlaybackService.ACTION_PLAY_ADHAN
                putExtra(PrayerAdhanPlaybackService.EXTRA_PRAYER_KEY, "ogle")
                putExtra(PrayerAdhanPlaybackService.EXTRA_PRAYER_NAME, "Öğle")
                putExtra(PrayerAdhanPlaybackService.EXTRA_CITY_NAME, cityName)
                putExtra(PrayerAdhanPlaybackService.EXTRA_VOLUME_PERCENT, prefs.alertVolumePercent)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }

    fun triggerImmediateHolidayTestAlert(
        context: Context,
        holiday: ReligiousHoliday,
        alertMode: HolidayAlertMode,
        lang: AppLanguage
    ) {
        val prefs = UserPreferences(context)
        val playSound = alertMode == HolidayAlertMode.NOTIFICATION_AND_SOUND
        val effectiveSoundType = if (playSound) {
            if (prefs.alertSoundType == "titresim") "melodi" else prefs.alertSoundType
        } else {
            "titresim"
        }

        PrayerNotificationHelper.showHolidayNotification(
            context = context,
            holidayId = holiday.id,
            holidayTitle = holiday.getLocalizedTitle(lang),
            holidayHijriDate = holiday.getLocalizedHijriDate(lang),
            holidayDescription = holiday.getLocalizedDescription(lang),
            playSoundAlert = playSound,
            alertSoundType = effectiveSoundType,
            isVibrationEnabled = prefs.isVibrationEnabled
        )
    }

    fun cancelAllAlerts(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        for (prayerType in PrayerType.entries) {
            for (offsetDay in 0..2) {
                val dummyTimeMillis = System.currentTimeMillis() + (offsetDay * 86400000)
                val requestCode = (prayerType.key + "_" + (dummyTimeMillis / 86400000)).hashCode()
                val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            }
        }
        try {
            WorkManager.getInstance(context).cancelAllWorkByTag(UNIQUE_DAILY_REFRESH)
        } catch (_: Exception) {
        }
    }
}
