package com.example.data.model

enum class MosqueScopeTier(val rankOrder: Int) {
    MAHALLE(0), // Önce bulunulan mahalle içi
    ILCE(1),    // Sonra bulunulan ilçe içi
    IL(2);      // Yoksa bulunulan il içi

    fun getLocalizedBadge(lang: AppLanguage): String = when (this) {
        MAHALLE -> when (lang) {
            AppLanguage.TURKISH -> "MAHALLE İÇİ"
            AppLanguage.ENGLISH -> "NEIGHBORHOOD"
            AppLanguage.ARABIC -> "في الحي"
        }
        ILCE -> when (lang) {
            AppLanguage.TURKISH -> "İLÇE İÇİ"
            AppLanguage.ENGLISH -> "DISTRICT"
            AppLanguage.ARABIC -> "في المنطقة"
        }
        IL -> when (lang) {
            AppLanguage.TURKISH -> "İL İÇİ"
            AppLanguage.ENGLISH -> "PROVINCE"
            AppLanguage.ARABIC -> "في المحافظة"
        }
    }
}

data class UserLocationHierarchy(
    val mahalle: String = "",
    val ilce: String = "",
    val il: String = "",
    val isLiveGps: Boolean = false
) {
    fun formatSummary(lang: AppLanguage): String {
        val parts = listOf(mahalle, ilce, il).map { it.trim() }.filter { it.isNotBlank() }.distinct()
        return if (parts.isNotEmpty()) {
            parts.joinToString(" • ")
        } else {
            when (lang) {
                AppLanguage.TURKISH -> "Mevcut Konum"
                AppLanguage.ENGLISH -> "Current Location"
                AppLanguage.ARABIC -> "الموقع الحالي"
            }
        }
    }
}

data class MosqueItem(
    val id: String,
    val name: String,
    val district: String,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val descriptionTr: String = "",
    val descriptionEn: String = "",
    val descriptionAr: String = "",
    val neighborhood: String = "",
    val scopeTier: MosqueScopeTier = MosqueScopeTier.IL
) {
    fun getFormattedAreaLabel(): String {
        val cleanNeighborhood = neighborhood.trim()
        val cleanDistrict = district.trim()
        val cleanCity = cityName.trim()
        val parts = mutableListOf<String>()
        if (cleanNeighborhood.isNotBlank()) parts.add(cleanNeighborhood)
        if (cleanDistrict.isNotBlank() && !cleanDistrict.equals(cleanNeighborhood, ignoreCase = true)) {
            parts.add(cleanDistrict)
        }
        if (cleanCity.isNotBlank() &&
            !cleanCity.equals(cleanDistrict, ignoreCase = true) &&
            parts.size < 2
        ) {
            parts.add(cleanCity)
        }
        return parts.joinToString(" / ").ifBlank { cleanCity }
    }

    fun calculateDistanceKm(userLat: Double, userLon: Double): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(lat - userLat)
        val dLon = Math.toRadians(lon - userLon)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(lat)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    fun calculateBearing(userLat: Double, userLon: Double): Double {
        val lat1 = Math.toRadians(userLat)
        val lon1 = Math.toRadians(userLon)
        val lat2 = Math.toRadians(lat)
        val lon2 = Math.toRadians(lon)

        val dLon = lon2 - lon1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)
        var brng = Math.toDegrees(Math.atan2(y, x))
        if (brng < 0) brng += 360.0
        return brng
    }
}
