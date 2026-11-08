package com.example.data.model

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil

enum class ReligiousEventType {
    KANDIL,
    BAYRAM,
    SPECIAL_DAY;

    fun getLocalizedBadge(lang: AppLanguage): String = when (this) {
        KANDIL -> when (lang) {
            AppLanguage.TURKISH -> "Kandil Gecesi"
            AppLanguage.ENGLISH -> "Holy Night"
            AppLanguage.ARABIC -> "ليلة مباركة"
        }
        BAYRAM -> when (lang) {
            AppLanguage.TURKISH -> "Dini Bayram"
            AppLanguage.ENGLISH -> "Eid Holiday"
            AppLanguage.ARABIC -> "عيد مبارك"
        }
        SPECIAL_DAY -> when (lang) {
            AppLanguage.TURKISH -> "Mübarek Gün"
            AppLanguage.ENGLISH -> "Sacred Day"
            AppLanguage.ARABIC -> "يوم مبارك"
        }
    }
}

enum class HolidayReminderTiming(val key: String) {
    EVENING_KANDIL("evening_1930"),
    MORNING_OF_DAY("morning_09"),
    ONE_DAY_BEFORE("day_before_12");

    fun getLocalizedLabel(lang: AppLanguage): String = when (this) {
        EVENING_KANDIL -> when (lang) {
            AppLanguage.TURKISH -> "Gününde Akşam / Kandil Vakti (19:30)"
            AppLanguage.ENGLISH -> "On the Evening / Holy Night (19:30)"
            AppLanguage.ARABIC -> "مساء اليوم / ليلة المناسبة (١٩:٣٠)"
        }
        MORNING_OF_DAY -> when (lang) {
            AppLanguage.TURKISH -> "Tam Gününde Sabah (09:00)"
            AppLanguage.ENGLISH -> "On the Day Morning (09:00)"
            AppLanguage.ARABIC -> "صباح نفس اليوم (٠٩:٠٠)"
        }
        ONE_DAY_BEFORE -> when (lang) {
            AppLanguage.TURKISH -> "1 Gün Önce Öğlen (12:00)"
            AppLanguage.ENGLISH -> "1 Day Before at Noon (12:00)"
            AppLanguage.ARABIC -> "قبل يوم واحد ظهراً (١٢:٠٠)"
        }
    }

    fun getShortLabel(lang: AppLanguage): String = when (this) {
        EVENING_KANDIL -> when (lang) {
            AppLanguage.TURKISH -> "19:30 (Akşam)"
            AppLanguage.ENGLISH -> "19:30 (Evening)"
            AppLanguage.ARABIC -> "١٩:٣٠ (مساءً)"
        }
        MORNING_OF_DAY -> when (lang) {
            AppLanguage.TURKISH -> "09:00 (Sabah)"
            AppLanguage.ENGLISH -> "09:00 (Morning)"
            AppLanguage.ARABIC -> "٠٩:٠٠ (صباحاً)"
        }
        ONE_DAY_BEFORE -> when (lang) {
            AppLanguage.TURKISH -> "1 Gün Önce 12:00"
            AppLanguage.ENGLISH -> "1 Day Before 12:00"
            AppLanguage.ARABIC -> "قبل يوم ١٢:٠٠"
        }
    }

    companion object {
        fun fromKey(key: String): HolidayReminderTiming =
            entries.find { it.key == key } ?: MORNING_OF_DAY
    }
}

enum class HolidayAlertMode(val key: String) {
    NOTIFICATION_AND_SOUND("alarm_sound"),
    NOTIFICATION_ONLY("notification_only");

    fun getLocalizedLabel(lang: AppLanguage): String = when (this) {
        NOTIFICATION_AND_SOUND -> when (lang) {
            AppLanguage.TURKISH -> "Sesli Alarm ve Bildirim (Ezan / Melodi)"
            AppLanguage.ENGLISH -> "Sound Alarm & Notification (Adhan / Melody)"
            AppLanguage.ARABIC -> "منبه صوتي وإشعار (أذان / نغمة)"
        }
        NOTIFICATION_ONLY -> when (lang) {
            AppLanguage.TURKISH -> "Yalnızca Bildirim ve Titreşim"
            AppLanguage.ENGLISH -> "Notification & Vibration Only"
            AppLanguage.ARABIC -> "إشعار واهتزاز فقط"
        }
    }

    companion object {
        fun fromKey(key: String): HolidayAlertMode =
            entries.find { it.key == key } ?: NOTIFICATION_AND_SOUND
    }
}

data class HolidayAlarmConfig(
    val holidayId: String,
    val isEnabled: Boolean = false,
    val timing: HolidayReminderTiming = HolidayReminderTiming.MORNING_OF_DAY,
    val alertMode: HolidayAlertMode = HolidayAlertMode.NOTIFICATION_AND_SOUND
)

data class ReligiousHoliday(
    val id: String,
    val year: Int,
    val month: Int, // 1..12
    val day: Int,   // 1..31
    val weekdayTr: String,
    val type: ReligiousEventType,
    val titleTr: String,
    val titleEn: String,
    val titleAr: String,
    val hijriDateTr: String,
    val hijriDateEn: String,
    val hijriDateAr: String,
    val descriptionTr: String,
    val descriptionEn: String,
    val descriptionAr: String
) {
    fun getLocalizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> titleTr
        AppLanguage.ENGLISH -> titleEn
        AppLanguage.ARABIC -> titleAr
    }

    fun getLocalizedHijriDate(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> hijriDateTr
        AppLanguage.ENGLISH -> hijriDateEn
        AppLanguage.ARABIC -> hijriDateAr
    }

    fun getLocalizedDescription(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> descriptionTr
        AppLanguage.ENGLISH -> descriptionEn
        AppLanguage.ARABIC -> descriptionAr
    }

    fun getLocalizedGregorianDate(lang: AppLanguage): String {
        val monthNamesTr = arrayOf(
            "", "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        )
        val monthNamesEn = arrayOf(
            "", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val monthNamesAr = arrayOf(
            "", "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
        )
        val weekdaysEn = mapOf(
            "Pazartesi" to "Monday", "Salı" to "Tuesday", "Çarşamba" to "Wednesday",
            "Perşembe" to "Thursday", "Cuma" to "Friday", "Cumartesi" to "Saturday", "Pazar" to "Sunday"
        )
        val weekdaysAr = mapOf(
            "Pazartesi" to "الإثنين", "Salı" to "الثلاثاء", "Çarşamba" to "الأربعاء",
            "Perşembe" to "الخميس", "Cuma" to "الجمعة", "Cumartesi" to "السبت", "Pazar" to "الأحد"
        )

        return when (lang) {
            AppLanguage.TURKISH -> "$day ${monthNamesTr[month]} $year $weekdayTr"
            AppLanguage.ENGLISH -> "$day ${monthNamesEn[month]} $year, ${weekdaysEn[weekdayTr] ?: weekdayTr}"
            AppLanguage.ARABIC -> "$day ${monthNamesAr[month]} $year - ${weekdaysAr[weekdayTr] ?: weekdayTr}"
        }
    }

    fun getShortDateBadge(lang: AppLanguage): String {
        val monthShortTr = arrayOf("", "Oca", "Şub", "Mar", "Nis", "May", "Haz", "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara")
        val monthShortEn = arrayOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthShortAr = arrayOf("", "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر")
        val m = when (lang) {
            AppLanguage.TURKISH -> monthShortTr[month]
            AppLanguage.ENGLISH -> monthShortEn[month]
            AppLanguage.ARABIC -> monthShortAr[month]
        }
        return "$day $m"
    }

    fun getStartOfDayMillis(): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR")).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun daysRemainingFrom(nowMillis: Long = System.currentTimeMillis()): Int {
        val todayCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR")).apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetMillis = getStartOfDayMillis()
        val diffMillis = targetMillis - todayCal.timeInMillis
        return ceil(diffMillis.toDouble() / (24 * 60 * 60 * 1000.0)).toInt()
    }

    fun calculateTriggerMillis(timing: HolidayReminderTiming): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR")).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        when (timing) {
            HolidayReminderTiming.MORNING_OF_DAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 9)
                cal.set(Calendar.MINUTE, 0)
            }
            HolidayReminderTiming.EVENING_KANDIL -> {
                cal.set(Calendar.HOUR_OF_DAY, 19)
                cal.set(Calendar.MINUTE, 30)
            }
            HolidayReminderTiming.ONE_DAY_BEFORE -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                cal.set(Calendar.HOUR_OF_DAY, 12)
                cal.set(Calendar.MINUTE, 0)
            }
        }
        return cal.timeInMillis
    }
}
