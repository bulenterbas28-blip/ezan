package com.example.data.model

data class DhikrItem(
    val id: String,
    val arabic: String,
    val turkishTitle: String,
    val turkishPronunciation: String,
    val meaning: String,
    val targetCount: Int,
    var currentCount: Int = 0,
    val englishTitle: String = turkishTitle,
    val englishMeaning: String = meaning,
    val arabicMeaning: String = arabic
) {
    fun getLocalizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> turkishTitle
        AppLanguage.ENGLISH -> englishTitle
        AppLanguage.ARABIC -> arabic
    }

    fun getLocalizedMeaning(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> meaning
        AppLanguage.ENGLISH -> englishMeaning
        AppLanguage.ARABIC -> arabicMeaning
    }
}

data class DailyInspiration(
    val id: String,
    val type: InspirationType,
    val arabicText: String? = null,
    val turkishText: String,
    val englishText: String = turkishText,
    val source: String,
    val englishSource: String = source,
    val arabicSource: String = source
) {
    fun getLocalizedText(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> turkishText
        AppLanguage.ENGLISH -> englishText
        AppLanguage.ARABIC -> arabicText ?: turkishText
    }

    fun getLocalizedSource(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> source
        AppLanguage.ENGLISH -> englishSource
        AppLanguage.ARABIC -> arabicSource
    }
}

enum class InspirationType {
    AYAT,
    HADITH,
    DUA
}
