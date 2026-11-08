package com.example.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppLanguage
import com.example.data.model.City
import com.example.data.model.DailyInspiration
import com.example.data.model.DayPrayerTimes
import com.example.data.model.DhikrItem
import com.example.data.model.HolidayAlarmConfig
import com.example.data.model.HolidayAlertMode
import com.example.data.model.HolidayReminderTiming
import com.example.data.model.MosqueItem
import com.example.data.model.NextPrayerCountdown
import com.example.data.model.PrayerStatus
import com.example.data.model.PrayerType
import com.example.data.model.ReligiousHoliday
import com.example.data.model.SinglePrayerInfo
import com.example.data.model.UserLocationHierarchy
import com.example.data.repository.DailyInspirations
import com.example.data.repository.DhikrRepository
import com.example.data.repository.MosqueRepository
import com.example.data.repository.PrayerCalculator
import com.example.data.repository.TurkishCities
import com.example.data.repository.UserPreferences
import com.example.sensor.CompassManager
import com.example.service.AdhanPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    val adhanPlayer = AdhanPlayer(application)
    val compassManager = CompassManager(application)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // City state
    private val _currentCity = MutableStateFlow(
        if (userPreferences.isGpsLocationEnabled && userPreferences.customLat != 0f && userPreferences.customLon != 0f) {
            val lat = userPreferences.customLat.toDouble()
            val lon = userPreferences.customLon.toDouble()
            val nearest = TurkishCities.findNearestCity(lat, lon)
            val shortName = formatCompactLocationLabel(
                primary = userPreferences.customCityName,
                fallbackCity = nearest.name
            )
            City(nearest.plateCode, shortName, lat, lon)
        } else {
            TurkishCities.allCities.find { it.plateCode == userPreferences.selectedCityPlate }
                ?: TurkishCities.defaultCity
        }
    )
    val currentCity: StateFlow<City> = _currentCity.asStateFlow()

    private val _isGpsActive = MutableStateFlow(userPreferences.isGpsLocationEnabled)
    val isGpsActive: StateFlow<Boolean> = _isGpsActive.asStateFlow()

    // Preferences states
    private val _temkinOffsetMinutes = MutableStateFlow(userPreferences.temkinOffsetMinutes)
    val temkinOffsetMinutes: StateFlow<Int> = _temkinOffsetMinutes.asStateFlow()

    private val _soundPreferences = MutableStateFlow(userPreferences.getSoundPreferencesMap())
    val soundPreferences: StateFlow<Map<String, Boolean>> = _soundPreferences.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(userPreferences.isDarkTheme)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isNotificationsEnabled = MutableStateFlow(userPreferences.isNotificationsEnabled)
    val isNotificationsEnabled: StateFlow<Boolean> = _isNotificationsEnabled.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(userPreferences.isVibrationEnabled)
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    private val _alertSoundType = MutableStateFlow(userPreferences.alertSoundType)
    val alertSoundType: StateFlow<String> = _alertSoundType.asStateFlow()

    private val _alertVolumePercent = MutableStateFlow(userPreferences.alertVolumePercent)
    val alertVolumePercent: StateFlow<Int> = _alertVolumePercent.asStateFlow()

    private val _playWhenScreenOff = MutableStateFlow(userPreferences.playWhenScreenOff)
    val playWhenScreenOff: StateFlow<Boolean> = _playWhenScreenOff.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.fromCode(userPreferences.appLanguage))
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _holidayAlarmConfigs = MutableStateFlow(userPreferences.getAllHolidayAlarmConfigs())
    val holidayAlarmConfigs: StateFlow<Map<String, HolidayAlarmConfig>> = _holidayAlarmConfigs.asStateFlow()

    // İmsakiye: Sahur & İftar Alarm States
    private val _isSahurAlarmEnabled = MutableStateFlow(userPreferences.isSahurAlarmEnabled)
    val isSahurAlarmEnabled: StateFlow<Boolean> = _isSahurAlarmEnabled.asStateFlow()

    private val _sahurMinutesBeforeImsak = MutableStateFlow(userPreferences.sahurMinutesBeforeImsak)
    val sahurMinutesBeforeImsak: StateFlow<Int> = _sahurMinutesBeforeImsak.asStateFlow()

    private val _sahurPlaySound = MutableStateFlow(userPreferences.sahurPlaySound)
    val sahurPlaySound: StateFlow<Boolean> = _sahurPlaySound.asStateFlow()

    private val _isIftarAlarmEnabled = MutableStateFlow(userPreferences.isIftarAlarmEnabled)
    val isIftarAlarmEnabled: StateFlow<Boolean> = _isIftarAlarmEnabled.asStateFlow()

    private val _iftarMinutesBeforeAksam = MutableStateFlow(userPreferences.iftarMinutesBeforeAksam)
    val iftarMinutesBeforeAksam: StateFlow<Int> = _iftarMinutesBeforeAksam.asStateFlow()

    private val _iftarPlaySound = MutableStateFlow(userPreferences.iftarPlaySound)
    val iftarPlaySound: StateFlow<Boolean> = _iftarPlaySound.asStateFlow()

    // Prayer Times state
    private val _todayPrayerTimes = MutableStateFlow<DayPrayerTimes?>(null)
    val todayPrayerTimes: StateFlow<DayPrayerTimes?> = _todayPrayerTimes.asStateFlow()

    private val _nextPrayerCountdown = MutableStateFlow<NextPrayerCountdown?>(null)
    val nextPrayerCountdown: StateFlow<NextPrayerCountdown?> = _nextPrayerCountdown.asStateFlow()

    private val _monthlyPrayerTimes = MutableStateFlow<List<DayPrayerTimes>>(emptyList())
    val monthlyPrayerTimes: StateFlow<List<DayPrayerTimes>> = _monthlyPrayerTimes.asStateFlow()

    // Qibla & Nearby Mosques state (hierarchical Mahalle -> İlçe -> İl, max 10)
    private val _qiblaBearing = MutableStateFlow(0.0)
    val qiblaBearing: StateFlow<Double> = _qiblaBearing.asStateFlow()

    private val _distanceToKaabaKm = MutableStateFlow(0)
    val distanceToKaabaKm: StateFlow<Int> = _distanceToKaabaKm.asStateFlow()

    private val _userLocationHierarchy = MutableStateFlow(
        UserLocationHierarchy(
            mahalle = "",
            ilce = "Merkez",
            il = _currentCity.value.name.substringBefore(" ("),
            isLiveGps = _isGpsActive.value
        )
    )
    val userLocationHierarchy: StateFlow<UserLocationHierarchy> = _userLocationHierarchy.asStateFlow()

    private val _nearbyMosques = MutableStateFlow<List<Pair<MosqueItem, Double>>>(emptyList())
    val nearbyMosques: StateFlow<List<Pair<MosqueItem, Double>>> = _nearbyMosques.asStateFlow()

    private val _isLoadingMosques = MutableStateFlow(false)
    val isLoadingMosques: StateFlow<Boolean> = _isLoadingMosques.asStateFlow()

    val compassAzimuth = compassManager.azimuth
    val hasCompassSensor = compassManager.hasSensor

    // Dhikr state
    private val _dhikrItems = MutableStateFlow(DhikrRepository.defaultDhikrs)
    val dhikrItems: StateFlow<List<DhikrItem>> = _dhikrItems.asStateFlow()

    private val _selectedDhikr = MutableStateFlow(DhikrRepository.defaultDhikrs[0])
    val selectedDhikr: StateFlow<DhikrItem> = _selectedDhikr.asStateFlow()

    private val _totalDhikrCount = MutableStateFlow(userPreferences.totalDhikrCount)
    val totalDhikrCount: StateFlow<Int> = _totalDhikrCount.asStateFlow()

    // Daily Inspiration
    private val _todayInspiration = MutableStateFlow(DailyInspirations.getTodayInspiration())
    val todayInspiration: StateFlow<DailyInspiration> = _todayInspiration.asStateFlow()

    // Auto Adhan trigger guard: key = "dateKey_prayerKey"
    private var lastTriggeredPrayerKey: String? = null

    init {
        compassManager.startListening()
        refreshAllPrayerData()
        startClockAndCountdownLoop()
        if (!userPreferences.hasUserManuallySelectedCity) {
            val gotDeviceLocation = refreshFromInstantDeviceLocation()
            if (!gotDeviceLocation) {
                autoDetectNetworkIpLocationFallback()
            }
        }
    }

    fun shouldAutoDetectLocationOnStartup(): Boolean {
        return !userPreferences.hasUserManuallySelectedCity
    }

    fun selectCity(city: City) {
        _currentCity.value = city
        _isGpsActive.value = false
        userPreferences.hasUserManuallySelectedCity = true
        userPreferences.selectedCityPlate = city.plateCode
        userPreferences.isGpsLocationEnabled = false
        refreshAllPrayerData()
        scheduleBackgroundAlarms()
    }

    fun setGpsLocation(lat: Double, lon: Double, customName: String = "") {
        val nearest = TurkishCities.findNearestCity(lat, lon)
        val displayTitle = formatCompactLocationLabel(
            primary = customName,
            fallbackCity = nearest.name
        )
        val city = City(nearest.plateCode, displayTitle, lat, lon)
        _currentCity.value = city
        _isGpsActive.value = true
        userPreferences.hasUserManuallySelectedCity = false
        userPreferences.isGpsLocationEnabled = true
        userPreferences.customLat = lat.toFloat()
        userPreferences.customLon = lon.toFloat()
        userPreferences.customCityName = displayTitle
        refreshAllPrayerData()
        scheduleBackgroundAlarms()
    }

    /**
     * Fallback network location detection on first launch when GPS permission is pending
     * or device GPS has no last-known fix yet. Never overrides a user's manual city selection.
     */
    private fun autoDetectNetworkIpLocationFallback() {
        viewModelScope.launch {
            val detected = withContext(Dispatchers.IO) {
                try {
                    val conn = (URL("https://ipwho.is/").openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 4000
                        readTimeout = 4000
                        setRequestProperty("Accept", "application/json")
                    }
                    if (conn.responseCode in 200..299) {
                        val body = conn.inputStream.bufferedReader().use { it.readText() }
                        val json = JSONObject(body)
                        if (json.optBoolean("success", true)) {
                            val lat = json.optDouble("latitude", Double.NaN)
                            val lon = json.optDouble("longitude", Double.NaN)
                            val city = json.optString("city", "")
                            if (!lat.isNaN() && !lon.isNaN() && lat in 35.0..43.0 && lon in 25.0..45.0) {
                                Triple(lat, lon, city)
                            } else null
                        } else null
                    } else null
                } catch (_: Exception) {
                    null
                }
            }
            if (detected != null && !userPreferences.hasUserManuallySelectedCity) {
                setGpsLocation(detected.first, detected.second, detected.third)
            }
        }
    }

    private fun formatCompactLocationLabel(
        primary: String,
        fallbackCity: String,
        maxLength: Int = 14
    ): String {
        val cleanedPrimary = primary
            .substringBefore("(")
            .substringAfterLast(",")
            .replace("Mahallesi", "", ignoreCase = true)
            .replace("Mah.", "", ignoreCase = true)
            .trim()

        val genericPlaceholders = setOf(
            "",
            "Konumunuz",
            "Anlık Konum",
            "Mevcut Konum"
        )

        val candidate = if (cleanedPrimary in genericPlaceholders) {
            fallbackCity.trim()
        } else {
            cleanedPrimary
        }

        val base = candidate.ifBlank { fallbackCity.trim() }
        return if (base.length <= maxLength) {
            base
        } else {
            base.take(maxLength - 1).trimEnd() + "…"
        }
    }

    /**
     * Reads the device's instantaneous GPS/Network/Fused location if permission is granted
     * and updates both the current location (Mahalle / İlçe / İl) and the top-10 closest mosques list.
     */
    @SuppressLint("MissingPermission")
    fun refreshFromInstantDeviceLocation(): Boolean {
        val app = getApplication<Application>()
        val fineGranted = ContextCompat.checkSelfPermission(
            app,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            app,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            refreshNearbyMosques()
            return false
        }

        return try {
            val locationManager = app.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (locationManager == null) {
                refreshNearbyMosques()
                return false
            }

            var bestLocation: Location? = null
            val providers = buildList {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(LocationManager.FUSED_PROVIDER)
                }
                add(LocationManager.GPS_PROVIDER)
                add(LocationManager.NETWORK_PROVIDER)
                add(LocationManager.PASSIVE_PROVIDER)
            }
            for (provider in providers) {
                try {
                    if (locationManager.isProviderEnabled(provider)) {
                        val loc = locationManager.getLastKnownLocation(provider)
                        if (loc != null) {
                            if (bestLocation == null ||
                                loc.time > bestLocation!!.time + 120_000L ||
                                (abs(loc.time - bestLocation!!.time) <= 120_000L && loc.accuracy < bestLocation!!.accuracy)
                            ) {
                                bestLocation = loc
                            }
                        }
                    }
                } catch (_: Exception) {
                }
            }

            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    setGpsLocation(location.latitude, location.longitude, "Anlık Konum")
                    try {
                        locationManager.removeUpdates(this)
                    } catch (_: Exception) {
                    }
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            // Always request a fresh single update so moving between neighborhoods updates immediately
            try {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    @Suppress("DEPRECATION")
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null)
                }
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    @Suppress("DEPRECATION")
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null)
                }
            } catch (_: Exception) {
            }

            if (bestLocation != null) {
                setGpsLocation(bestLocation.latitude, bestLocation.longitude, "Anlık Konum")
                true
            } else {
                refreshNearbyMosques()
                true
            }
        } catch (_: Exception) {
            refreshNearbyMosques()
            false
        }
    }

    fun refreshNearbyMosques() {
        val app = getApplication<Application>()
        val city = _currentCity.value
        val userLat = city.lat
        val userLon = city.lon
        val liveGps = _isGpsActive.value

        // Immediately show local mosques ordered by Mahalle -> İlçe -> İl, max 10
        _nearbyMosques.value = MosqueRepository.getNearbyMosques(
            userLat = userLat,
            userLon = userLon,
            limit = MosqueRepository.MAX_NEARBY_MOSQUES,
            hierarchy = _userLocationHierarchy.value
        )

        // Asynchronously resolve exact Mahalle / İlçe / İl and fetch real-time neighborhood mosques
        viewModelScope.launch {
            _isLoadingMosques.value = true
            try {
                val (resolvedHierarchy, liveSorted) = MosqueRepository.fetchLiveNearbyMosques(
                    context = app,
                    userLat = userLat,
                    userLon = userLon,
                    cityNameHint = city.name.substringBefore(" ("),
                    isLiveGps = liveGps,
                    limit = MosqueRepository.MAX_NEARBY_MOSQUES
                )
                _userLocationHierarchy.value = resolvedHierarchy

                if (liveGps && (resolvedHierarchy.ilce.isNotBlank() || resolvedHierarchy.il.isNotBlank())) {
                    val rawShortArea = when {
                        resolvedHierarchy.ilce.isNotBlank() &&
                            !resolvedHierarchy.ilce.equals("Merkez", ignoreCase = true) -> resolvedHierarchy.ilce
                        resolvedHierarchy.il.isNotBlank() -> resolvedHierarchy.il
                        else -> city.name
                    }
                    val compactTitle = formatCompactLocationLabel(
                        primary = rawShortArea,
                        fallbackCity = resolvedHierarchy.il.ifBlank { city.name }
                    )
                    userPreferences.customCityName = compactTitle
                    _currentCity.value = City(
                        plateCode = city.plateCode,
                        name = compactTitle,
                        lat = userLat,
                        lon = userLon
                    )
                }

                if (liveSorted.isNotEmpty()) {
                    // Preserve hierarchical Mahalle -> İlçe -> İl sort, then distance ascending
                    _nearbyMosques.value = liveSorted
                        .sortedWith(
                            compareBy<Pair<MosqueItem, Double>> { it.first.scopeTier.rankOrder }
                                .thenBy { it.second }
                        )
                        .take(MosqueRepository.MAX_NEARBY_MOSQUES)
                }
            } finally {
                _isLoadingMosques.value = false
            }
        }
    }

    fun updateTemkinOffset(offsetMinutes: Int) {
        val clamped = offsetMinutes.coerceIn(-15, 15)
        _temkinOffsetMinutes.value = clamped
        userPreferences.temkinOffsetMinutes = clamped
        refreshAllPrayerData()
    }

    fun togglePrayerSound(prayerType: PrayerType) {
        val current = _soundPreferences.value[prayerType.key] ?: true
        val updated = !current
        userPreferences.setPrayerSoundEnabled(prayerType, updated)
        _soundPreferences.value = userPreferences.getSoundPreferencesMap()
        refreshTodayTimes()
        scheduleBackgroundAlarms()
    }

    fun toggleNotifications() {
        val updated = !_isNotificationsEnabled.value
        _isNotificationsEnabled.value = updated
        userPreferences.isNotificationsEnabled = updated
        if (updated) {
            scheduleBackgroundAlarms()
        } else {
            com.example.notification.PrayerScheduler.cancelAllAlerts(getApplication())
        }
    }

    fun scheduleBackgroundAlarms() {
        com.example.notification.PrayerScheduler.scheduleUpcomingPrayers(getApplication())
    }

    fun toggleDarkTheme() {
        val updated = !_isDarkTheme.value
        _isDarkTheme.value = updated
        userPreferences.isDarkTheme = updated
    }

    fun toggleVibration() {
        val updated = !_isVibrationEnabled.value
        _isVibrationEnabled.value = updated
        userPreferences.isVibrationEnabled = updated
    }

    fun setAlertSoundType(type: String) {
        _alertSoundType.value = type
        userPreferences.alertSoundType = type
        scheduleBackgroundAlarms()
    }

    fun setAlertVolumePercent(percent: Int) {
        val clamped = percent.coerceIn(10, 100)
        _alertVolumePercent.value = clamped
        userPreferences.alertVolumePercent = clamped
    }

    fun togglePlayWhenScreenOff() {
        val updated = !_playWhenScreenOff.value
        _playWhenScreenOff.value = updated
        userPreferences.playWhenScreenOff = updated
    }

    fun testCurrentAlertSound() {
        adhanPlayer.toggleSound(_alertSoundType.value, _alertVolumePercent.value)
    }

    // İmsakiye: Sahur & İftar Alarm Controls
    fun toggleSahurAlarm(enabled: Boolean) {
        _isSahurAlarmEnabled.value = enabled
        userPreferences.isSahurAlarmEnabled = enabled
        com.example.notification.PrayerScheduler.scheduleImsakiyeAlarms(getApplication())
    }

    fun setSahurMinutesBefore(minutes: Int) {
        val valid = minutes.coerceIn(0, 120)
        _sahurMinutesBeforeImsak.value = valid
        userPreferences.sahurMinutesBeforeImsak = valid
        if (!_isSahurAlarmEnabled.value) {
            _isSahurAlarmEnabled.value = true
            userPreferences.isSahurAlarmEnabled = true
        }
        com.example.notification.PrayerScheduler.scheduleImsakiyeAlarms(getApplication())
    }

    fun setSahurPlaySound(playSound: Boolean) {
        _sahurPlaySound.value = playSound
        userPreferences.sahurPlaySound = playSound
        com.example.notification.PrayerScheduler.scheduleImsakiyeAlarms(getApplication())
    }

    fun toggleIftarAlarm(enabled: Boolean) {
        _isIftarAlarmEnabled.value = enabled
        userPreferences.isIftarAlarmEnabled = enabled
        com.example.notification.PrayerScheduler.scheduleImsakiyeAlarms(getApplication())
    }

    fun setIftarMinutesBefore(minutes: Int) {
        val valid = minutes.coerceIn(0, 120)
        _iftarMinutesBeforeAksam.value = valid
        userPreferences.iftarMinutesBeforeAksam = valid
        if (!_isIftarAlarmEnabled.value) {
            _isIftarAlarmEnabled.value = true
            userPreferences.isIftarAlarmEnabled = true
        }
        com.example.notification.PrayerScheduler.scheduleImsakiyeAlarms(getApplication())
    }

    fun setIftarPlaySound(playSound: Boolean) {
        _iftarPlaySound.value = playSound
        userPreferences.iftarPlaySound = playSound
        com.example.notification.PrayerScheduler.scheduleImsakiyeAlarms(getApplication())
    }

    fun testImsakiyeAlert(isSahur: Boolean) {
        val today = _todayPrayerTimes.value
        val timeFormatted = if (isSahur) {
            today?.imsak ?: "05:30"
        } else {
            today?.aksam ?: "18:15"
        }
        com.example.notification.PrayerScheduler.triggerImmediateImsakiyeTestAlert(
            context = getApplication(),
            isSahur = isSahur,
            prayerTimeFormatted = timeFormatted
        )
    }

    /**
     * Computes the full Ramadan İmsakiye table for the given year (2026 or 2027)
     * according to Diyanet Ramadan dates for the user's current city/coordinates.
     * - 2026 Ramadan (1447 H): 19 Şubat 2026 – 19 Mart 2026 (29 days)
     * - 2027 Ramadan (1448 H): 8 Şubat 2027 – 8 Mart 2027 (29 days)
     */
    fun getRamadanImsakiyeDays(year: Int): List<DayPrayerTimes> {
        val city = _currentCity.value
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR"))
        val totalDays = 29
        if (year == 2027) {
            cal.set(2027, Calendar.FEBRUARY, 8, 12, 0, 0)
        } else {
            cal.set(2026, Calendar.FEBRUARY, 19, 12, 0, 0)
        }

        val list = mutableListOf<DayPrayerTimes>()
        for (i in 0 until totalDays) {
            val c = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
            val dayTimes = PrayerCalculator.calculatePrayerTimes(
                year = c.get(Calendar.YEAR),
                month = c.get(Calendar.MONTH) + 1,
                day = c.get(Calendar.DAY_OF_MONTH),
                lat = city.lat,
                lon = city.lon,
                temkinOffsetMinutes = _temkinOffsetMinutes.value,
                soundPreferences = _soundPreferences.value
            )
            list.add(dayTimes)
        }
        return list
    }

    fun saveHolidayAlarm(
        holiday: ReligiousHoliday,
        isEnabled: Boolean,
        timing: HolidayReminderTiming,
        alertMode: HolidayAlertMode
    ) {
        val config = HolidayAlarmConfig(
            holidayId = holiday.id,
            isEnabled = isEnabled,
            timing = timing,
            alertMode = alertMode
        )
        userPreferences.saveHolidayAlarmConfig(config)
        _holidayAlarmConfigs.value = userPreferences.getAllHolidayAlarmConfigs()
        com.example.notification.PrayerScheduler.scheduleReligiousDayAlarms(getApplication())
    }

    fun toggleHolidayAlarmQuick(holiday: ReligiousHoliday): Boolean {
        val current = _holidayAlarmConfigs.value[holiday.id]
            ?: userPreferences.getHolidayAlarmConfig(holiday)
        val newEnabled = !current.isEnabled
        val updated = current.copy(isEnabled = newEnabled)
        userPreferences.saveHolidayAlarmConfig(updated)
        _holidayAlarmConfigs.value = userPreferences.getAllHolidayAlarmConfigs()
        com.example.notification.PrayerScheduler.scheduleReligiousDayAlarms(getApplication())
        return newEnabled
    }

    fun testHolidayAlert(holiday: ReligiousHoliday, alertMode: HolidayAlertMode) {
        com.example.notification.PrayerScheduler.triggerImmediateHolidayTestAlert(
            context = getApplication(),
            holiday = holiday,
            alertMode = alertMode,
            lang = _currentLanguage.value
        )
        if (alertMode == HolidayAlertMode.NOTIFICATION_AND_SOUND) {
            adhanPlayer.toggleSound(_alertSoundType.value, _alertVolumePercent.value)
        }
    }

    private fun refreshAllPrayerData() {
        val city = _currentCity.value
        _qiblaBearing.value = PrayerCalculator.calculateQiblaDirection(city.lat, city.lon)
        _distanceToKaabaKm.value = PrayerCalculator.calculateDistanceToKaabaKm(city.lat, city.lon)

        refreshTodayTimes()
        calculateMonthlyTimes()
        refreshNearbyMosques()
        scheduleBackgroundAlarms()
    }

    private fun refreshTodayTimes() {
        val city = _currentCity.value
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR"))
        val times = PrayerCalculator.calculatePrayerTimes(
            year = cal.get(Calendar.YEAR),
            month = cal.get(Calendar.MONTH) + 1,
            day = cal.get(Calendar.DAY_OF_MONTH),
            lat = city.lat,
            lon = city.lon,
            temkinOffsetMinutes = _temkinOffsetMinutes.value,
            soundPreferences = _soundPreferences.value
        )
        _todayPrayerTimes.value = times
        updateCountdown()
    }

    private fun calculateMonthlyTimes() {
        val city = _currentCity.value
        val list = mutableListOf<DayPrayerTimes>()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR"))

        for (i in 0 until 30) {
            val c = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
            val dayTimes = PrayerCalculator.calculatePrayerTimes(
                year = c.get(Calendar.YEAR),
                month = c.get(Calendar.MONTH) + 1,
                day = c.get(Calendar.DAY_OF_MONTH),
                lat = city.lat,
                lon = city.lon,
                temkinOffsetMinutes = _temkinOffsetMinutes.value,
                soundPreferences = _soundPreferences.value
            )
            list.add(dayTimes)
        }
        _monthlyPrayerTimes.value = list
    }

    private fun startClockAndCountdownLoop() {
        viewModelScope.launch {
            while (isActive) {
                updateCountdown()
                delay(1000)
            }
        }
    }

    private fun updateCountdown() {
        val todayTimes = _todayPrayerTimes.value ?: return
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR"))
        val dateKey = "${cal.get(Calendar.YEAR)}_${cal.get(Calendar.DAY_OF_YEAR)}"

        // Find next prayer today
        var nextPrayer: SinglePrayerInfo? = null
        var prevPrayerTimeMillis = todayTimes.prayersList.first().timeMillis - (6 * 3600 * 1000)

        val updatedPrayersList = todayTimes.prayersList.mapIndexed { index, prayer ->
            val isPassed = prayer.timeMillis <= now
            val isNext = !isPassed && (nextPrayer == null)

            val status = when {
                isNext -> {
                    nextPrayer = prayer
                    if (index > 0) {
                        prevPrayerTimeMillis = todayTimes.prayersList[index - 1].timeMillis
                    }
                    PrayerStatus.ACTIVE_NEXT
                }
                isPassed -> PrayerStatus.PAST
                else -> PrayerStatus.UPCOMING
            }

            prayer.copy(
                status = status,
                isSoundAlertEnabled = _soundPreferences.value[prayer.type.key] ?: true
            )
        }

        // If all prayers today passed, next prayer is tomorrow's Imsak
        val effectiveNextPrayer: SinglePrayerInfo
        val effectivePrevMillis: Long
        if (nextPrayer != null) {
            effectiveNextPrayer = nextPrayer!!
            effectivePrevMillis = prevPrayerTimeMillis
        } else {
            // Tomorrow's Imsak
            val tomorrowCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
            val city = _currentCity.value
            val tomorrowTimes = PrayerCalculator.calculatePrayerTimes(
                year = tomorrowCal.get(Calendar.YEAR),
                month = tomorrowCal.get(Calendar.MONTH) + 1,
                day = tomorrowCal.get(Calendar.DAY_OF_MONTH),
                lat = city.lat,
                lon = city.lon,
                temkinOffsetMinutes = _temkinOffsetMinutes.value,
                soundPreferences = _soundPreferences.value
            )
            val tomorrowImsak = tomorrowTimes.prayersList.first()
            effectiveNextPrayer = tomorrowImsak.copy(status = PrayerStatus.ACTIVE_NEXT)
            effectivePrevMillis = todayTimes.prayersList.last().timeMillis // today's Yatsı
        }

        _todayPrayerTimes.value = todayTimes.copy(prayersList = updatedPrayersList)

        // Calculate countdown values
        val diffMs = effectiveNextPrayer.timeMillis - now
        val remainingSeconds = (diffMs / 1000).coerceAtLeast(0)

        val intervalDuration = (effectiveNextPrayer.timeMillis - effectivePrevMillis).coerceAtLeast(1)
        val elapsed = (now - effectivePrevMillis).coerceAtLeast(0)
        val progressFraction = (elapsed.toFloat() / intervalDuration.toFloat()).coerceIn(0f, 1f)

        val hours = remainingSeconds / 3600
        val minutes = (remainingSeconds % 3600) / 60
        val seconds = remainingSeconds % 60

        _nextPrayerCountdown.value = NextPrayerCountdown(
            nextPrayer = effectiveNextPrayer,
            remainingSeconds = remainingSeconds,
            progressFraction = progressFraction,
            formattedHours = "%02d".format(Locale.US, hours),
            formattedMinutes = "%02d".format(Locale.US, minutes),
            formattedSeconds = "%02d".format(Locale.US, seconds)
        )

        // Automatic Adhan triggering check
        // If remaining diff is within 0 to -3 seconds tolerance and hasn't triggered yet for this prayer
        val prayerTriggerKey = "${dateKey}_${effectiveNextPrayer.type.key}"
        if (diffMs in -3000..1000) {
            if (lastTriggeredPrayerKey != prayerTriggerKey) {
                lastTriggeredPrayerKey = prayerTriggerKey
                val soundEnabled = _soundPreferences.value[effectiveNextPrayer.type.key] ?: true
                if (soundEnabled && effectiveNextPrayer.type.isFardhPrayer) {
                    adhanPlayer.playAdhan("${effectiveNextPrayer.type.turkishName} Ezanı")
                }
            }
        }
    }

    // Dhikr methods
    fun selectDhikr(dhikr: DhikrItem) {
        _selectedDhikr.value = dhikr
    }

    fun incrementDhikr() {
        val current = _selectedDhikr.value
        val updatedCount = current.currentCount + 1
        val updatedItem = current.copy(currentCount = updatedCount)
        _selectedDhikr.value = updatedItem

        // Update list
        _dhikrItems.value = _dhikrItems.value.map {
            if (it.id == current.id) updatedItem else it
        }

        // Increment total
        val newTotal = _totalDhikrCount.value + 1
        _totalDhikrCount.value = newTotal
        userPreferences.totalDhikrCount = newTotal

        // Haptic feedback
        if (_isVibrationEnabled.value) {
            triggerHapticFeedback(isTargetHit = updatedCount % current.targetCount == 0)
        }
    }

    fun resetDhikr() {
        val current = _selectedDhikr.value
        val updatedItem = current.copy(currentCount = 0)
        _selectedDhikr.value = updatedItem
        _dhikrItems.value = _dhikrItems.value.map {
            if (it.id == current.id) updatedItem else it
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
        userPreferences.appLanguage = lang.code
    }

    private fun triggerHapticFeedback(isTargetHit: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (isTargetHit) {
                    VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
                } else {
                    VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                if (isTargetHit) {
                    vibrator?.vibrate(longArrayOf(0, 100, 50, 100), -1)
                } else {
                    vibrator?.vibrate(35)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        compassManager.stopListening()
        adhanPlayer.release()
    }
}
