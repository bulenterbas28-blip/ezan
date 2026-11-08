package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.City
import com.example.data.model.PrayerType

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ezan_vakti_prefs", Context.MODE_PRIVATE)

    var selectedCityPlate: Int
        get() = prefs.getInt("selected_city_plate", 34) // 34 Istanbul fallback
        set(value) = prefs.edit().putInt("selected_city_plate", value).apply()

    var hasUserManuallySelectedCity: Boolean
        get() = prefs.getBoolean("user_manually_selected_city", false)
        set(value) = prefs.edit().putBoolean("user_manually_selected_city", value).apply()

    var isGpsLocationEnabled: Boolean
        get() = prefs.getBoolean("gps_location_enabled", !hasUserManuallySelectedCity)
        set(value) = prefs.edit().putBoolean("gps_location_enabled", value).apply()

    var customLat: Float
        get() = prefs.getFloat("custom_lat", 41.0082f)
        set(value) = prefs.edit().putFloat("custom_lat", value).apply()

    var customLon: Float
        get() = prefs.getFloat("custom_lon", 28.9784f)
        set(value) = prefs.edit().putFloat("custom_lon", value).apply()

    var customCityName: String
        get() = prefs.getString("custom_city_name", "İstanbul") ?: "İstanbul"
        set(value) = prefs.edit().putString("custom_city_name", value).apply()

    var temkinOffsetMinutes: Int
        get() = prefs.getInt("temkin_offset_minutes", 0)
        set(value) = prefs.edit().putInt("temkin_offset_minutes", value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean("is_dark_theme", true)
        set(value) = prefs.edit().putBoolean("is_dark_theme", value).apply()

    var appLanguage: String
        get() = prefs.getString("app_language", "tr") ?: "tr"
        set(value) = prefs.edit().putString("app_language", value).apply()

    var alertSoundType: String
        get() = prefs.getString("alert_sound_type", "ezan") ?: "ezan" // "ezan", "melodi", "titresim"
        set(value) = prefs.edit().putString("alert_sound_type", value).apply()

    var alertVolumePercent: Int
        get() = prefs.getInt("alert_volume_percent", 100) // 10..100
        set(value) = prefs.edit().putInt("alert_volume_percent", value).apply()

    var playWhenScreenOff: Boolean
        get() = prefs.getBoolean("play_when_screen_off", true)
        set(value) = prefs.edit().putBoolean("play_when_screen_off", value).apply()

    var totalDhikrCount: Int
        get() = prefs.getInt("total_dhikr_count", 0)
        set(value) = prefs.edit().putInt("total_dhikr_count", value).apply()

    var isNotificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications_enabled", true)
        set(value) = prefs.edit().putBoolean("notifications_enabled", value).apply()

    fun isPrayerSoundEnabled(prayerType: PrayerType): Boolean {
        // By default, Gunest has no adhan alert, all other 5 have adhan sound enabled
        val defaultVal = prayerType != PrayerType.GUNES
        return prefs.getBoolean("sound_alert_${prayerType.key}", defaultVal)
    }

    fun setPrayerSoundEnabled(prayerType: PrayerType, enabled: Boolean) {
        prefs.edit().putBoolean("sound_alert_${prayerType.key}", enabled).apply()
    }

    fun getSoundPreferencesMap(): Map<String, Boolean> {
        return PrayerType.entries.associate { it.key to isPrayerSoundEnabled(it) }
    }

    var bookmarkSurahNumber: Int
        get() = prefs.getInt("quran_bookmark_surah_num", 1) // default Fatiha
        set(value) = prefs.edit().putInt("quran_bookmark_surah_num", value).apply()

    var bookmarkSurahName: String
        get() = prefs.getString("quran_bookmark_surah_name", "Fâtiha") ?: "Fâtiha"
        set(value) = prefs.edit().putString("quran_bookmark_surah_name", value).apply()

    var bookmarkAyahNumber: Int
        get() = prefs.getInt("quran_bookmark_ayah_num", 1)
        set(value) = prefs.edit().putInt("quran_bookmark_ayah_num", value).apply()

    var bookmarkTimestamp: Long
        get() = prefs.getLong("quran_bookmark_timestamp", 0L)
        set(value) = prefs.edit().putLong("quran_bookmark_timestamp", value).apply()

    fun saveQuranBookmark(surahNumber: Int, surahName: String, ayahNumber: Int) {
        prefs.edit()
            .putInt("quran_bookmark_surah_num", surahNumber)
            .putString("quran_bookmark_surah_name", surahName)
            .putInt("quran_bookmark_ayah_num", ayahNumber)
            .putLong("quran_bookmark_timestamp", System.currentTimeMillis())
            .apply()
    }

    fun getHolidayAlarmConfig(holiday: com.example.data.model.ReligiousHoliday): com.example.data.model.HolidayAlarmConfig {
        val defaultTiming = if (holiday.type == com.example.data.model.ReligiousEventType.KANDIL) {
            com.example.data.model.HolidayReminderTiming.EVENING_KANDIL.key
        } else {
            com.example.data.model.HolidayReminderTiming.MORNING_OF_DAY.key
        }
        val isEnabled = prefs.getBoolean("holiday_alarm_enabled_${holiday.id}", false)
        val timingKey = prefs.getString("holiday_alarm_timing_${holiday.id}", defaultTiming) ?: defaultTiming
        val modeKey = prefs.getString(
            "holiday_alarm_mode_${holiday.id}",
            com.example.data.model.HolidayAlertMode.NOTIFICATION_AND_SOUND.key
        ) ?: com.example.data.model.HolidayAlertMode.NOTIFICATION_AND_SOUND.key

        return com.example.data.model.HolidayAlarmConfig(
            holidayId = holiday.id,
            isEnabled = isEnabled,
            timing = com.example.data.model.HolidayReminderTiming.fromKey(timingKey),
            alertMode = com.example.data.model.HolidayAlertMode.fromKey(modeKey)
        )
    }

    fun saveHolidayAlarmConfig(config: com.example.data.model.HolidayAlarmConfig) {
        prefs.edit()
            .putBoolean("holiday_alarm_enabled_${config.holidayId}", config.isEnabled)
            .putString("holiday_alarm_timing_${config.holidayId}", config.timing.key)
            .putString("holiday_alarm_mode_${config.holidayId}", config.alertMode.key)
            .apply()
    }

    fun getAllHolidayAlarmConfigs(): Map<String, com.example.data.model.HolidayAlarmConfig> {
        return ReligiousDaysRepository.allHolidays.associate { h ->
            h.id to getHolidayAlarmConfig(h)
        }
    }

    // İmsakiye: Sahur (İmsak) & İftar (Akşam) Özel Bildirim / Alarm Ayarları
    var isSahurAlarmEnabled: Boolean
        get() = prefs.getBoolean("imsakiye_sahur_alarm_enabled", false)
        set(value) = prefs.edit().putBoolean("imsakiye_sahur_alarm_enabled", value).apply()

    var sahurMinutesBeforeImsak: Int
        get() = prefs.getInt("imsakiye_sahur_minutes_before", 45) // 0, 15, 30, 45, 60 dk önce
        set(value) = prefs.edit().putInt("imsakiye_sahur_minutes_before", value).apply()

    var sahurPlaySound: Boolean
        get() = prefs.getBoolean("imsakiye_sahur_play_sound", true)
        set(value) = prefs.edit().putBoolean("imsakiye_sahur_play_sound", value).apply()

    var isIftarAlarmEnabled: Boolean
        get() = prefs.getBoolean("imsakiye_iftar_alarm_enabled", false)
        set(value) = prefs.edit().putBoolean("imsakiye_iftar_alarm_enabled", value).apply()

    var iftarMinutesBeforeAksam: Int
        get() = prefs.getInt("imsakiye_iftar_minutes_before", 0) // 0 (Tam İftar), 5, 10, 15, 30 dk önce
        set(value) = prefs.edit().putInt("imsakiye_iftar_minutes_before", value).apply()

    var iftarPlaySound: Boolean
        get() = prefs.getBoolean("imsakiye_iftar_play_sound", true)
        set(value) = prefs.edit().putBoolean("imsakiye_iftar_play_sound", value).apply()
}
