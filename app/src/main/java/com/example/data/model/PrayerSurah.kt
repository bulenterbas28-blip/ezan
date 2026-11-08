package com.example.data.model

enum class SurahCategory(val displayName: String) {
    SURELER("Kısa Sureler"),
    DUALAR("Namaz Duaları");

    fun getLocalizedName(lang: AppLanguage): String = when (this) {
        SURELER -> when (lang) {
            AppLanguage.TURKISH -> "Kısa Sûreler"
            AppLanguage.ENGLISH -> "Short Surahs"
            AppLanguage.ARABIC -> "قصار السور"
        }
        DUALAR -> when (lang) {
            AppLanguage.TURKISH -> "Namaz Duaları"
            AppLanguage.ENGLISH -> "Prayer Supplications"
            AppLanguage.ARABIC -> "أدعية الصلاة"
        }
    }
}

data class PrayerSurah(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val category: SurahCategory,
    val whereRecited: String, // Namazdaki yeri
    val arabicText: String,
    val turkishPronunciation: String,
    val turkishMeaning: String,
    val benefits: String? = null,
    val audioUrl: String? = null,
    val englishTitle: String = title,
    val englishMeaning: String = turkishMeaning,
    val englishWhereRecited: String = whereRecited,
    val arabicWhereRecited: String = whereRecited,
    val englishBenefits: String? = benefits,
    val arabicBenefits: String? = benefits
) {
    fun getLocalizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> title
        AppLanguage.ENGLISH -> englishTitle
        AppLanguage.ARABIC -> arabicTitle
    }

    fun getLocalizedMeaning(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> turkishMeaning
        AppLanguage.ENGLISH -> englishMeaning
        AppLanguage.ARABIC -> arabicText
    }

    fun getLocalizedWhereRecited(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> whereRecited
        AppLanguage.ENGLISH -> englishWhereRecited
        AppLanguage.ARABIC -> arabicWhereRecited
    }

    fun getLocalizedBenefits(lang: AppLanguage): String? = when (lang) {
        AppLanguage.TURKISH -> benefits
        AppLanguage.ENGLISH -> englishBenefits
        AppLanguage.ARABIC -> arabicBenefits
    }
}
