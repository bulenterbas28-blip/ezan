package com.example.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.model.PrayerType
import com.example.data.repository.UserPreferences

class PrayerAlarmWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_PRAYER_KEY = "key_prayer_key"
        const val KEY_PRAYER_NAME = "key_prayer_name"
        const val KEY_PRAYER_TIME = "key_prayer_time"
        const val KEY_CITY_NAME = "key_city_name"
    }

    override suspend fun doWork(): Result {
        val prayerKey = inputData.getString(KEY_PRAYER_KEY) ?: return Result.failure()
        val prayerName = inputData.getString(KEY_PRAYER_NAME) ?: "Ezan"
        val prayerTime = inputData.getString(KEY_PRAYER_TIME) ?: ""
        val cityName = inputData.getString(KEY_CITY_NAME) ?: "Bulunduğunuz Konum"

        val prefs = UserPreferences(applicationContext)

        // Check master notification preference
        if (!prefs.isNotificationsEnabled) {
            return Result.success()
        }

        // Check prayer-specific notification preference
        val prayerType = PrayerType.fromKey(prayerKey)
        val isSoundAlertEnabled = prefs.isPrayerSoundEnabled(prayerType)

        if (isSoundAlertEnabled) {
            val notificationId = prayerKey.hashCode()
            PrayerNotificationHelper.showPrayerNotification(
                context = applicationContext,
                prayerKey = prayerKey,
                prayerName = prayerName,
                prayerTime = prayerTime,
                cityName = cityName,
                notificationId = notificationId
            )
        }

        // Keep the forward schedule updated for subsequent prayers
        PrayerScheduler.scheduleUpcomingPrayers(applicationContext)

        return Result.success()
    }
}
