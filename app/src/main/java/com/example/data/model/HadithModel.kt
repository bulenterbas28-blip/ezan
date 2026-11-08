package com.example.data.model

data class HadithItem(
    val id: String,
    val categoryTr: String,
    val categoryEn: String,
    val categoryAr: String,
    val arabicText: String,
    val turkishText: String,
    val englishText: String,
    val sourceTr: String,
    val sourceEn: String,
    val sourceAr: String
) {
    fun getLocalizedCategory(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> categoryTr
        AppLanguage.ENGLISH -> categoryEn
        AppLanguage.ARABIC -> categoryAr
    }

    fun getLocalizedText(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> turkishText
        AppLanguage.ENGLISH -> englishText
        AppLanguage.ARABIC -> arabicText
    }

    fun getLocalizedSource(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> sourceTr
        AppLanguage.ENGLISH -> sourceEn
        AppLanguage.ARABIC -> sourceAr
    }
}
