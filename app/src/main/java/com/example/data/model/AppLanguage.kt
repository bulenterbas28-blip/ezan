package com.example.data.model

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(
    val code: String,
    val title: String,
    val nativeTitle: String,
    val flag: String,
    val layoutDirection: LayoutDirection
) {
    TURKISH("tr", "Türkçe", "Türkçe", "🇹🇷", LayoutDirection.Ltr),
    ENGLISH("en", "English", "English", "🇬🇧", LayoutDirection.Ltr),
    ARABIC("ar", "Arapça", "العربية", "🇸🇦", LayoutDirection.Rtl);

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: TURKISH
        }
    }
}
