package com.example.data.model

enum class PrayerType(
    val key: String,
    val turkishName: String,
    val arabicName: String,
    val description: String,
    val isFardhPrayer: Boolean
) {
    IMSAK("imsak", "İmsak", "الفجر", "Sabah namazı ve oruç başlama vakti", true),
    GUNES("gunes", "Güneş", "الشروق", "Güneş doğuşu, kerahat vakti başlangıcı", false),
    OGLE("ogle", "Öğle", "الظهر", "Öğle namazı vakti", true),
    IKINDI("ikindi", "İkindi", "العصر", "İkindi namazı vakti", true),
    AKSAM("aksam", "Akşam", "المغرب", "Akşam namazı ve iftar vakti", true),
    YATSI("yatsi", "Yatsı", "العشاء", "Yatsı ve vitir namazı vakti", true);

    companion object {
        fun fromKey(key: String): PrayerType = entries.find { it.key == key } ?: OGLE
    }
}

enum class PrayerStatus {
    PAST,
    ACTIVE_NEXT,
    UPCOMING
}

data class SinglePrayerInfo(
    val type: PrayerType,
    val timeFormatted: String, // "05:42"
    val timeMillis: Long,
    val status: PrayerStatus,
    val isSoundAlertEnabled: Boolean = true
)

data class DayPrayerTimes(
    val dateEpochDay: Long,
    val gregorianDateText: String, // "7 Kasım 2026 Cumartesi"
    val hijriDateText: String, // "27 Cemâziyelevvel 1448"
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String,
    val prayersList: List<SinglePrayerInfo>
) {
    fun getLocalizedGregorianDate(lang: AppLanguage): String {
        if (lang == AppLanguage.TURKISH) return gregorianDateText
        var result = gregorianDateText
        val monthsEn = mapOf(
            "Ocak" to "January", "Şubat" to "February", "Mart" to "March", "Nisan" to "April",
            "Mayıs" to "May", "Haziran" to "June", "Temmuz" to "July", "Ağustos" to "August",
            "Eylül" to "September", "Ekim" to "October", "Kasım" to "November", "Aralık" to "December"
        )
        val monthsAr = mapOf(
            "Ocak" to "يناير", "Şubat" to "فبراير", "Mart" to "مارس", "Nisan" to "أبريل",
            "Mayıs" to "مايو", "Haziran" to "يونيو", "Temmuz" to "يوليو", "Ağustos" to "أغسطس",
            "Eylül" to "سبتمبر", "Ekim" to "أكتوبر", "Kasım" to "نوفمبر", "Aralık" to "ديسمبر"
        )
        val daysEn = mapOf(
            "Pazartesi" to "Monday", "Salı" to "Tuesday", "Çarşamba" to "Wednesday",
            "Perşembe" to "Thursday", "Cuma" to "Friday", "Cumartesi" to "Saturday", "Pazar" to "Sunday"
        )
        val daysAr = mapOf(
            "Pazartesi" to "الإثنين", "Salı" to "الثلاثاء", "Çarşamba" to "الأربعاء",
            "Perşembe" to "الخميس", "Cuma" to "الجمعة", "Cumartesi" to "السبت", "Pazar" to "الأحد"
        )
        val mMap = if (lang == AppLanguage.ENGLISH) monthsEn else monthsAr
        val dMap = if (lang == AppLanguage.ENGLISH) daysEn else daysAr
        // Replace longer weekday names first (e.g. "Cumartesi" before "Cuma")
        dMap.entries.sortedByDescending { it.key.length }.forEach { (tr, loc) ->
            result = result.replace(tr, loc)
        }
        mMap.forEach { (tr, loc) ->
            result = result.replace(tr, loc)
        }
        return result
    }

    fun getLocalizedHijriDate(lang: AppLanguage): String {
        if (lang == AppLanguage.TURKISH) return hijriDateText
        var result = hijriDateText
        val hijriEn = mapOf(
            "Muharrem" to "Muharram",
            "Safer" to "Safar",
            "Rebîülevvel" to "Rabi' al-Awwal",
            "Rebîülâhir" to "Rabi' al-Thani",
            "Cemâziyelevvel" to "Jumada al-Awwal",
            "Cemâziyelâhir" to "Jumada al-Thani",
            "Recep" to "Rajab",
            "Şaban" to "Sha'ban",
            "Ramazan" to "Ramadan",
            "Şevval" to "Shawwal",
            "Zilkade" to "Dhu al-Qi'dah",
            "Zilhicce" to "Dhu al-Hijjah"
        )
        val hijriAr = mapOf(
            "Muharrem" to "محرم",
            "Safer" to "صفر",
            "Rebîülevvel" to "ربيع الأول",
            "Rebîülâhir" to "ربيع الآخر",
            "Cemâziyelevvel" to "جمادى الأولى",
            "Cemâziyelâhir" to "جمادى الآخرة",
            "Recep" to "رجب",
            "Şaban" to "شعبان",
            "Ramazan" to "رمضان",
            "Şevval" to "شوال",
            "Zilkade" to "ذو القعدة",
            "Zilhicce" to "ذو الحجة"
        )
        val hMap = if (lang == AppLanguage.ENGLISH) hijriEn else hijriAr
        hMap.forEach { (tr, loc) ->
            result = result.replace(tr, loc)
        }
        return result
    }
}

data class NextPrayerCountdown(
    val nextPrayer: SinglePrayerInfo,
    val remainingSeconds: Long,
    val progressFraction: Float, // 0.0f to 1.0f progress of current interval
    val formattedHours: String,
    val formattedMinutes: String,
    val formattedSeconds: String
)
