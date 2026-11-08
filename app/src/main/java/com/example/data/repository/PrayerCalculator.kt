package com.example.data.repository

import com.example.data.model.DayPrayerTimes
import com.example.data.model.PrayerStatus
import com.example.data.model.PrayerType
import com.example.data.model.SinglePrayerInfo
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

object PrayerCalculator {

    // Kaaba Coordinates (Mecca)
    const val KAABA_LAT = 21.4225241
    const val KAABA_LON = 39.8261818

    // Calculation Method Constants
    private const val FAJR_ANGLE = -18.0 // Diyanet standard Fajr angle
    private const val ISHA_ANGLE = -17.0 // Diyanet standard Isha angle
    private const val SUN_ALTITUDE = -0.8333 // Atmospheric refraction & disc

    /**
     * Calculates prayer times for a specific date, latitude, and longitude
     * with optional user-configured temkin offsets.
     */
    fun calculatePrayerTimes(
        year: Int,
        month: Int, // 1 to 12
        day: Int,
        lat: Double,
        lon: Double,
        temkinOffsetMinutes: Int = 0,
        soundPreferences: Map<String, Boolean> = emptyMap()
    ): DayPrayerTimes {
        val timezone = 3.0 // Turkey is permanently UTC+3 (TRT)

        // Julian Day calculation
        val jd = computeJulianDay(year, month, day)
        val d = jd - 2451545.0

        // Sun's mean anomaly & mean longitude
        val m = fixAngle(357.529 + 0.98560028 * d)
        val l0 = fixAngle(280.459 + 0.98564736 * d)

        // Sun's ecliptic longitude
        val lambda = fixAngle(l0 + 1.915 * sin(Math.toRadians(m)) + 0.020 * sin(Math.toRadians(2 * m)))

        // Obliquity of the ecliptic
        val epsilon = 23.439 - 0.00000036 * d

        // Sun's declination & right ascension
        val sinDelta = sin(Math.toRadians(epsilon)) * sin(Math.toRadians(lambda))
        val delta = Math.toDegrees(asin(sinDelta))

        val cosAlpha = cos(Math.toRadians(lambda))
        val sinAlpha = cos(Math.toRadians(epsilon)) * sin(Math.toRadians(lambda))
        var alpha = Math.toDegrees(atan2(sinAlpha, cosAlpha))
        alpha = fixAngle(alpha) / 15.0

        // Equation of Time (in hours)
        val equationOfTime = (l0 / 15.0) - alpha

        // Solar Noon (Öğle) in hours
        val noonTime = 12.0 + timezone - (lon / 15.0) - equationOfTime

        // Hour angle for Fajr (-18 deg)
        val fajrHa = computeHourAngle(FAJR_ANGLE, lat, delta)
        // Hour angle for Sunrise (-0.833 deg)
        val sunHa = computeHourAngle(SUN_ALTITUDE, lat, delta)
        // Hour angle for Isha (-17 deg)
        val ishaHa = computeHourAngle(ISHA_ANGLE, lat, delta)

        // Asr (Shafi'i: shadow length factor = 1, used by Diyanet in Turkey)
        val latRad = Math.toRadians(lat)
        val deltaRad = Math.toRadians(delta)
        val noonZenith = abs(latRad - deltaRad)
        val asrAltitude = Math.toDegrees(atan(1.0 / (1.0 + tan(noonZenith))))
        val asrHa = computeHourAngle(asrAltitude, lat, delta)

        // Times in decimal hours
        var imsakHours = noonTime - (fajrHa / 15.0)
        var gunesHours = noonTime - (sunHa / 15.0)
        var ogleHours = noonTime
        var ikindiHours = noonTime + (asrHa / 15.0)
        var aksamHours = noonTime + (sunHa / 15.0)
        var yatsiHours = noonTime + (ishaHa / 15.0)

        // Diyanet standard temkin minutes + user custom offset
        // In Turkey, Diyanet uses standard temkin:
        // İmsak: -2m, Güneş: -5m, Öğle: +6m, İkindi: +4m, Akşam: +7m, Yatsı: +2m
        imsakHours += (-2.0 + temkinOffsetMinutes) / 60.0
        gunesHours += (-5.0 + temkinOffsetMinutes) / 60.0
        ogleHours += (6.0 + temkinOffsetMinutes) / 60.0
        ikindiHours += (4.0 + temkinOffsetMinutes) / 60.0
        aksamHours += (7.0 + temkinOffsetMinutes) / 60.0
        yatsiHours += (2.0 + temkinOffsetMinutes) / 60.0

        val imsakStr = formatDecimalHours(imsakHours)
        val gunesStr = formatDecimalHours(gunesHours)
        val ogleStr = formatDecimalHours(ogleHours)
        val ikindiStr = formatDecimalHours(ikindiHours)
        val aksamStr = formatDecimalHours(aksamHours)
        val yatsiStr = formatDecimalHours(yatsiHours)

        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR")).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val nowMillis = System.currentTimeMillis()

        fun makeTimeMillis(timeStr: String): Long {
            val parts = timeStr.split(":").map { it.toInt() }
            val c = (cal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, parts[0])
                set(Calendar.MINUTE, parts[1])
            }
            return c.timeInMillis
        }

        val imsakMillis = makeTimeMillis(imsakStr)
        val gunesMillis = makeTimeMillis(gunesStr)
        val ogleMillis = makeTimeMillis(ogleStr)
        val ikindiMillis = makeTimeMillis(ikindiStr)
        val aksamMillis = makeTimeMillis(aksamStr)
        val yatsiMillis = makeTimeMillis(yatsiStr)

        val prayerTimesList = listOf(
            SinglePrayerInfo(
                type = PrayerType.IMSAK,
                timeFormatted = imsakStr,
                timeMillis = imsakMillis,
                status = if (imsakMillis < nowMillis) PrayerStatus.PAST else PrayerStatus.UPCOMING,
                isSoundAlertEnabled = soundPreferences[PrayerType.IMSAK.key] ?: true
            ),
            SinglePrayerInfo(
                type = PrayerType.GUNES,
                timeFormatted = gunesStr,
                timeMillis = gunesMillis,
                status = if (gunesMillis < nowMillis) PrayerStatus.PAST else PrayerStatus.UPCOMING,
                isSoundAlertEnabled = soundPreferences[PrayerType.GUNES.key] ?: false
            ),
            SinglePrayerInfo(
                type = PrayerType.OGLE,
                timeFormatted = ogleStr,
                timeMillis = ogleMillis,
                status = if (ogleMillis < nowMillis) PrayerStatus.PAST else PrayerStatus.UPCOMING,
                isSoundAlertEnabled = soundPreferences[PrayerType.OGLE.key] ?: true
            ),
            SinglePrayerInfo(
                type = PrayerType.IKINDI,
                timeFormatted = ikindiStr,
                timeMillis = ikindiMillis,
                status = if (ikindiMillis < nowMillis) PrayerStatus.PAST else PrayerStatus.UPCOMING,
                isSoundAlertEnabled = soundPreferences[PrayerType.IKINDI.key] ?: true
            ),
            SinglePrayerInfo(
                type = PrayerType.AKSAM,
                timeFormatted = aksamStr,
                timeMillis = aksamMillis,
                status = if (aksamMillis < nowMillis) PrayerStatus.PAST else PrayerStatus.UPCOMING,
                isSoundAlertEnabled = soundPreferences[PrayerType.AKSAM.key] ?: true
            ),
            SinglePrayerInfo(
                type = PrayerType.YATSI,
                timeFormatted = yatsiStr,
                timeMillis = yatsiMillis,
                status = if (yatsiMillis < nowMillis) PrayerStatus.PAST else PrayerStatus.UPCOMING,
                isSoundAlertEnabled = soundPreferences[PrayerType.YATSI.key] ?: true
            )
        )

        val gregorianText = formatGregorianDate(cal)
        val hijriText = computeHijriDate(jd)

        return DayPrayerTimes(
            dateEpochDay = cal.timeInMillis / (24 * 3600 * 1000),
            gregorianDateText = gregorianText,
            hijriDateText = hijriText,
            imsak = imsakStr,
            gunes = gunesStr,
            ogle = ogleStr,
            ikindi = ikindiStr,
            aksam = aksamStr,
            yatsi = yatsiStr,
            prayersList = prayerTimesList
        )
    }

    private fun computeHourAngle(alt: Double, lat: Double, delta: Double): Double {
        val latR = Math.toRadians(lat)
        val deltaR = Math.toRadians(delta)
        val altR = Math.toRadians(alt)
        val cosHa = (sin(altR) - sin(latR) * sin(deltaR)) / (cos(latR) * cos(deltaR))
        val clampedCos = cosHa.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clampedCos))
    }

    private fun fixAngle(deg: Double): Double {
        var a = deg % 360.0
        if (a < 0) a += 360.0
        return a
    }

    private fun computeJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + (a / 4)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun formatDecimalHours(hours: Double): String {
        var normalized = hours % 24.0
        if (normalized < 0) normalized += 24.0
        val totalMinutes = (normalized * 60).roundToInt()
        val h = (totalMinutes / 60) % 24
        val m = totalMinutes % 60
        return "%02d:%02d".format(Locale.US, h, m)
    }

    private fun formatGregorianDate(calendar: Calendar): String {
        val monthsTr = arrayOf(
            "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        )
        val daysTr = arrayOf(
            "", "Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi"
        )
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = monthsTr[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR)
        val dayOfWeek = daysTr[calendar.get(Calendar.DAY_OF_WEEK)]
        return "$day $month $year $dayOfWeek"
    }

    /**
     * Computes Hijri date from Julian Day with Turkish Islamic month names.
     */
    fun computeHijriDate(jd: Double): String {
        val hijriMonths = arrayOf(
            "Muharrem", "Safer", "Rebiülevvel", "Rebiülahir",
            "Cemaziyelevvel", "Cemaziyelahir", "Recep", "Şaban",
            "Ramazan", "Şevval", "Zilkade", "Zilhicce"
        )

        // Islamic calendar approximation algorithm
        val jdInt = jd.toInt()
        val l = jdInt - 1948440 + 10632
        val n = (l - 1) / 10631
        val l2 = l - 10631 * n + 354
        val j = ((10985 - l2) / 5316) * ((50 * l2) / 17719) + (l2 / 5670) * ((43 * l2) / 15238)
        val l3 = l2 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val m = (24 * l3) / 709
        val day = l3 - (709 * m) / 24
        val year = 30 * n + j - 30

        val safeMonth = (m - 1).coerceIn(0, 11)
        val monthName = hijriMonths[safeMonth]
        return "$day $monthName $year H"
    }

    /**
     * Calculates Qibla direction (true azimuth bearing towards Kaaba) from given coordinates.
     */
    fun calculateQiblaDirection(lat: Double, lon: Double): Double {
        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)
        val kaabaLatRad = Math.toRadians(KAABA_LAT)
        val kaabaLonRad = Math.toRadians(KAABA_LON)

        val dLon = kaabaLonRad - lonRad
        val y = sin(dLon) * cos(kaabaLatRad)
        val x = cos(latRad) * sin(kaabaLatRad) - sin(latRad) * cos(kaabaLatRad) * cos(dLon)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0
        return bearing
    }

    /**
     * Calculates great-circle distance in kilometers to Kaaba.
     */
    fun calculateDistanceToKaabaKm(lat: Double, lon: Double): Int {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(KAABA_LAT - lat)
        val dLon = Math.toRadians(KAABA_LON - lon)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(KAABA_LAT)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c).roundToInt()
    }
}
