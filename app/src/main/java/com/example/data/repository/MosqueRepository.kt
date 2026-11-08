package com.example.data.repository

import android.content.Context
import android.location.Geocoder
import com.example.data.model.MosqueItem
import com.example.data.model.MosqueScopeTier
import com.example.data.model.UserLocationHierarchy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.abs

object MosqueRepository {

    const val MAX_NEARBY_MOSQUES = 10

    private val specificMosques: List<MosqueItem> = listOf(
        // İstanbul
        MosqueItem("ist_1", "Ayasofya-i Kebîr Câmi-i Şerîfi", "Fatih", "İstanbul", 41.0086, 28.9802, "Tarihi ve kadim ulu cami", "Historic Grand Mosque", "جامع آيا صوفيا الكبير", neighborhood = "Sultanahmet Mah."),
        MosqueItem("ist_2", "Sultanahmet Camii (Mavi Cami)", "Fatih", "İstanbul", 41.0054, 28.9768, "6 minareli tarihi şaheser cami", "The Blue Mosque", "جامع السلطان أحمد", neighborhood = "Sultanahmet Mah."),
        MosqueItem("ist_3", "Süleymaniye Camii", "Fatih", "İstanbul", 41.0161, 28.9644, "Mimar Sinan'ın kalfalık eseri", "Masterpiece of Mimar Sinan", "جامع السليمانية", neighborhood = "Süleymaniye Mah."),
        MosqueItem("ist_4", "Eyüp Sultan Camii", "Eyüpsultan", "İstanbul", 41.0478, 28.9342, "Ebû Eyyûb el-Ensârî külliyesi", "Historic spiritual sanctuary", "جامع أيوب سلطان", neighborhood = "Merkez Mah."),
        MosqueItem("ist_5", "Fatih Camii ve Külliyesi", "Fatih", "İstanbul", 41.0197, 28.9497, "Fatih Sultan Mehmed külliyesi", "Fatih Sultan Mehmed Mosque", "جامع الفاتح", neighborhood = "Ali Kuşçu Mah."),
        MosqueItem("ist_6", "Büyük Çamlıca Camii", "Üsküdar", "İstanbul", 41.0345, 29.0689, "Türkiye'nin en büyük camii", "Grandest modern mosque of Turkey", "جامع تشامليجا الكبير", neighborhood = "Ferah Mah."),
        MosqueItem("ist_7", "Ortaköy (Büyük Mecidiye) Camii", "Beşiktaş", "İstanbul", 41.0472, 29.0270, "Boğaz kıyısındaki zarif cami", "Historic Bosphorus mosque", "جامع أورتاكوي", neighborhood = "Mecidiye Mah."),
        MosqueItem("ist_8", "Şehzade Camii", "Fatih", "İstanbul", 41.0139, 28.9575, "Mimar Sinan'ın çıraklık eseri", "Mimar Sinan early masterpiece", "جامع شاهزاده", neighborhood = "Kalenderhane Mah."),
        MosqueItem("ist_9", "Yeni Cami", "Fatih", "İstanbul", 41.0169, 28.9714, "Eminönü meydanı tarihi camii", "New Mosque at Eminonu harbor", "الجامع الجديد", neighborhood = "Rüstem Paşa Mah."),
        MosqueItem("ist_10", "Mihrimah Sultan Camii", "Üsküdar", "İstanbul", 41.0267, 29.0156, "Üsküdar sahilindeki Mimar Sinan eseri", "Historic Sinan mosque by the sea", "جامع مهرماه سلطان", neighborhood = "Mimar Sinan Mah."),
        MosqueItem("ist_11", "Nuruosmaniye Camii", "Fatih", "İstanbul", 41.0103, 28.9704, "Kapalıçarşı girişindeki barok cami", "Ottoman Baroque mosque at Grand Bazaar", "جامع نور عثمانية", neighborhood = "Molla Fenari Mah."),
        MosqueItem("ist_12", "Beyazıt Camii", "Fatih", "İstanbul", 41.0103, 28.9652, "İstanbul'un en eski selatin camilerinden", "Historic Imperial Mosque at Beyazit", "جامع بايزيد", neighborhood = "Beyazıt Mah."),
        MosqueItem("ist_13", "Taksim Camii", "Beyoğlu", "İstanbul", 41.0363, 28.9844, "Taksim Meydanı merkez camii", "Central mosque at Taksim Square", "جامع تقسيم", neighborhood = "Kocatepe Mah."),
        MosqueItem("ist_14", "Marmara İlahiyat Camii", "Üsküdar", "İstanbul", 41.0235, 29.0419, "Modern mimarili merkez cami", "Modern architectural mosque in Uskudar", "جامع كلية الإلهيات بمرمرة", neighborhood = "Altunizade Mah."),
        MosqueItem("ist_15", "Osmanağa Camii", "Kadıköy", "İstanbul", 40.9912, 29.0241, "Kadıköy merkez tarihi camii", "Central historic mosque in Kadikoy", "جامع عثمان آغا", neighborhood = "Osmanağa Mah."),

        // Ankara
        MosqueItem("ank_1", "Kocatepe Camii", "Çankaya", "Ankara", 39.9167, 32.8608, "Başkentin simge merkez camii", "Landmark grand mosque of capital", "جامع كوجاتبه", neighborhood = "Kültür Mah."),
        MosqueItem("ank_2", "Hacı Bayram-ı Velî Camii", "Altındağ", "Ankara", 39.9444, 32.8580, "Tarihi manevi merkez", "Historic spiritual landmark of Ankara", "جامع حاجي بايرام ولي", neighborhood = "Hacı Bayram Mah."),
        MosqueItem("ank_3", "Melike Hatun Camii", "Altındağ", "Ankara", 39.9388, 32.8572, "Osmanlı ve Selçuklu mimarisi", "Grand central mosque in Ulus", "جامع مليكة خاتون", neighborhood = "Anafartalar Mah."),
        MosqueItem("ank_4", "Ahmet Hamdi Akseki Camii", "Çankaya", "Ankara", 39.9056, 32.7667, "Diyanet İşleri Başkanlığı yanı", "Modern architectural masterpiece", "جامع أحمد حمدي أكسكي", neighborhood = "Üniversiteler Mah."),
        MosqueItem("ank_5", "Beştepe Millet Camii", "Yenimahalle", "Ankara", 39.9312, 32.7989, "Külliyedeki merkez cami", "Beştepe Nation's Mosque", "جامع الملة في بشتبه", neighborhood = "Beştepe Mah."),
        MosqueItem("ank_6", "Aslanhane (Ahi Şerafettin) Camii", "Altındağ", "Ankara", 39.9372, 32.8653, "Selçuklu ahşap direkli tarihi cami (UNESCO)", "UNESCO World Heritage Seljuk wooden mosque", "جامع أصلانهانه التاريخي", neighborhood = "Atpazarı Mah."),
        MosqueItem("ank_7", "Maltepe Camii", "Çankaya", "Ankara", 39.9278, 32.8453, "Ankara'nın merkezi camilerinden", "Central mosque in Maltepe", "جامع مالتبه", neighborhood = "Maltepe Mah."),

        // İzmir
        MosqueItem("izm_1", "Konak Yalı Camii", "Konak", "İzmir", 38.4190, 27.1287, "Tarihi çinili meydan camii", "Historic tiled mosque at Konak square", "جامع كوناك يالي", neighborhood = "Konak Mah."),
        MosqueItem("izm_2", "Hisar Camii", "Konak", "İzmir", 38.4214, 27.1353, "İzmir'in en eski ve büyük camii", "Oldest and grandest mosque of Izmir", "جامع حصار", neighborhood = "Konak Mah."),
        MosqueItem("izm_3", "Kestanepazarı Camii", "Konak", "İzmir", 38.4197, 27.1350, "Tarihi çarşı camii", "Historic market mosque", "جامع كستانه بازاري", neighborhood = "Yenice Mah."),
        MosqueItem("izm_4", "Salepçioğlu Camii", "Konak", "İzmir", 38.4181, 27.1332, "Zarafetiyle ünlü tarihi cami", "Historic mosque in Kemeralti", "جامع سالبتشي أوغلو", neighborhood = "Uğur Mah."),
        MosqueItem("izm_5", "Bilal Saygılı Camii", "Bornova", "İzmir", 38.4581, 27.2134, "Ege Üniversitesi yanındaki külliye camii", "Grand campus mosque in Bornova", "جامع بلال صايغلي", neighborhood = "Kazımdirik Mah."),

        // Bursa
        MosqueItem("bur_1", "Bursa Ulu Cami", "Osmangazi", "Bursa", 40.1833, 29.0625, "20 kubbeli şaheser, ilk ulu mabet", "Iconic 20-dome early Ottoman masterpiece", "جامع بورصة الكبير", neighborhood = "Nalbantoğlu Mah."),
        MosqueItem("bur_2", "Yeşil Cami", "Yıldırım", "Bursa", 40.1814, 29.0750, "Muazzam çinileriyle ünlü cami", "Famous Green Mosque with exquisite tiles", "الجامع الأخضر", neighborhood = "Yeşil Mah."),
        MosqueItem("bur_3", "Emir Sultan Camii", "Yıldırım", "Bursa", 40.1822, 29.0833, "Manevi ziyaretgâh ve külliye", "Spiritual sanctuary of Emir Sultan", "جامع أمير سلطان", neighborhood = "Emirsultan Mah."),
        MosqueItem("bur_4", "Muradiye Külliyesi Camii", "Osmangazi", "Bursa", 40.1906, 29.0461, "Sultan II. Murad külliyesi", "Sultan Murad II Imperial Complex", "جامع المرادية", neighborhood = "Muradiye Mah."),

        // Konya
        MosqueItem("kon_1", "Sultan Selim Camii", "Karatay", "Konya", 37.8711, 32.5056, "Mevlana Müzesi yanı", "Ottoman imperial mosque beside Mevlana", "جامع السلطان سليم", neighborhood = "Aziziye Mah."),
        MosqueItem("kon_2", "Alâeddin Camii", "Selçuklu", "Konya", 37.8731, 32.4933, "Selçuklu sultanlarının türbeleri", "Historic Seljuk palace mosque on hill", "جامع علاء الدين", neighborhood = "Hamidiye Mah."),
        MosqueItem("kon_3", "Aziziye Camii", "Karatay", "Konya", 37.8697, 32.5019, "Barok ve Rokoko mimarisi", "Distinctive Ottoman-Baroque stone mosque", "جامع العزيزية", neighborhood = "Aziziye Mah."),
        MosqueItem("kon_4", "Kapu Camii", "Meram", "Konya", 37.8685, 32.5002, "Konya'nın en büyük Osmanlı camisi", "Largest historic Ottoman mosque in Konya", "جامع قابو", neighborhood = "Şükran Mah."),

        // Edirne
        MosqueItem("edr_1", "Selimiye Camii", "Merkez", "Edirne", 41.6781, 26.5594, "Mimar Sinan'ın ustalık eseri (UNESCO)", "Sinan masterwork & UNESCO World Heritage", "جامع السليمية", neighborhood = "Meydan Mah."),
        MosqueItem("edr_2", "Üç Şerefeli Cami", "Merkez", "Edirne", 41.6775, 26.5542, "Erken dönem Osmanlı mimarisi", "Historic mosque with distinct minarets", "جامع الشرفات الثلاث", neighborhood = "Sabuni Mah."),
        MosqueItem("edr_3", "Eski Cami (Ulu Cami)", "Merkez", "Edirne", 41.6761, 26.5567, "Hat yazılarıyla ünlü ulu cami", "Grand calligraphy mosque of Edirne", "الجامع العتيق", neighborhood = "Sabuni Mah."),

        // Adana, Antalya, Gaziantep, Şanlıurfa, Trabzon, Diyarbakır, Kayseri, Samsun, Erzurum
        MosqueItem("adn_1", "Sabancı Merkez Camii", "Seyhan", "Adana", 36.9917, 35.3292, "Seyhan Nehri kıyısında 6 minareli", "Giant 6-minaret mosque by Seyhan river", "جامع صابانجي المركزي", neighborhood = "Reşatbey Mah."),
        MosqueItem("adn_2", "Adana Ulu Cami", "Seyhan", "Adana", 36.9850, 35.3308, "Ramazanoğulları tarihi ulu camii", "Historic Ramazanoglu Grand Mosque", "جامع أضنة الكبير", neighborhood = "Ulucami Mah."),
        MosqueItem("ant_1", "Yivli Minare (Ulu) Camii", "Muratpaşa", "Antalya", 36.8865, 30.7042, "Antalya'nın Selçuklu simgesi", "Iconic Seljuk fluted minaret mosque", "جامع المنارة المضلعة", neighborhood = "Selçuk Mah."),
        MosqueItem("ant_2", "Murat Paşa Camii", "Muratpaşa", "Antalya", 36.8904, 30.7031, "Tarihi Osmanlı merkez camii", "Central Ottoman mosque of Antalya", "جامع مراد باشا", neighborhood = "Kızılsaray Mah."),
        MosqueItem("gaz_1", "Kurtuluş Camii", "Şahinbey", "Gaziantep", 37.0589, 37.3761, "Gaziantep'in en büyük tarihi camilerinden", "Historic grand mosque in Gaziantep", "جامع قورتولوش", neighborhood = "Tepebaşı Mah."),
        MosqueItem("urf_1", "Balıklıgöl (Dergâh) Camii", "Eyyübiye", "Şanlıurfa", 37.1472, 38.7850, "Hz. İbrahim makamı ve Balıklıgöl", "Sacred sanctuary at Balikligol", "جامع بحيرة الأسماك", neighborhood = "Göl Mah."),
        MosqueItem("trb_1", "Gülbahar Hatun Camii", "Ortahisar", "Trabzon", 41.0028, 39.7139, "Yavuz Sultan Selim'in annesi adına", "Historic imperial Ottoman mosque", "جامع غولبهار خاتون", neighborhood = "Gülbaharhatun Mah."),
        MosqueItem("trb_2", "Trabzon Ayasofya Camii", "Ortahisar", "Trabzon", 41.0033, 39.6961, "Tarihi sahil camii", "Historic seaside mosque of Trabzon", "جامع آيا صوفيا طرابزون", neighborhood = "Fatih Mah."),
        MosqueItem("dyr_1", "Diyarbakır Ulu Cami", "Sur", "Diyarbakır", 37.9139, 40.2378, "İslamiyet'in 5. Harem-i Şerifi", "5th Holy Sanctuary of Islam", "جامع ديار بكر الكبير", neighborhood = "Cami Kebir Mah."),
        MosqueItem("kay_1", "Hunat Hatun Camii ve Külliyesi", "Melikgazi", "Kayseri", 38.7208, 35.4897, "Anadolu Selçuklu şaheseri", "Seljuk architectural complex", "جامع خونات خاتون", neighborhood = "Hunat Mah."),
        MosqueItem("sam_1", "Samsun Büyük Cami", "İlkadım", "Samsun", 41.2889, 36.3344, "Samsun merkez tarihi ulu camii", "Central Grand Mosque of Samsun", "الجامع الكبير في سامسون", neighborhood = "Kale Mah."),
        MosqueItem("erz_1", "Erzurum Ulu Cami", "Yakutiye", "Erzurum", 39.9061, 41.2764, "Saltuklu dönemi tarihi ulu cami", "Historic Saltukid Grand Mosque", "جامع أرضروم الكبير", neighborhood = "Cumhuriyet Mah.")
    )

    // Combine specific famous mosques with central mosques for all 81 provinces
    val allMosques: List<MosqueItem> by lazy {
        val list = specificMosques.toMutableList()
        TurkishCities.allCities.forEach { city ->
            list.add(
                MosqueItem(
                    id = "city_merkez_${city.plateCode}",
                    name = "${city.name} Merkez Ulu Camii",
                    district = "Merkez",
                    cityName = city.name,
                    lat = city.lat + 0.0012,
                    lon = city.lon + 0.0010,
                    descriptionTr = "${city.name} merkez mahalle ve çarşı camii",
                    descriptionEn = "${city.name} Central Grand Mosque",
                    descriptionAr = "الجامع الكبير المركزي في ${city.name}",
                    neighborhood = "Merkez Mah."
                )
            )
            list.add(
                MosqueItem(
                    id = "city_yeni_${city.plateCode}",
                    name = "${city.name} Yeni Merkez Camii",
                    district = "Merkez",
                    cityName = city.name,
                    lat = city.lat - 0.0021,
                    lon = city.lon + 0.0024,
                    descriptionTr = "${city.name} çarşı ve meydan camii",
                    descriptionEn = "${city.name} Square Mosque",
                    descriptionAr = "جامع الميدان في ${city.name}",
                    neighborhood = "Cumhuriyet Mah."
                )
            )
            list.add(
                MosqueItem(
                    id = "city_fatih_${city.plateCode}",
                    name = "${city.name} Fatih Camii",
                    district = "Merkez",
                    cityName = city.name,
                    lat = city.lat + 0.0038,
                    lon = city.lon - 0.0031,
                    descriptionTr = "${city.name} merkez ilçe camii",
                    descriptionEn = "${city.name} Fatih Mosque",
                    descriptionAr = "جامع الفاتح في ${city.name}",
                    neighborhood = "Fatih Mah."
                )
            )
        }
        list
    }

    /**
     * Resolves the user's exact Mahalle (Neighborhood), İlçe (District), and İl (Province)
     * from coordinates using Android Geocoder and OpenStreetMap Nominatim Reverse Geocoding.
     */
    suspend fun resolveUserLocationHierarchy(
        context: Context?,
        userLat: Double,
        userLon: Double,
        fallbackCityName: String = "",
        isLiveGps: Boolean = false
    ): UserLocationHierarchy = withContext(Dispatchers.IO) {
        val nearestCity = TurkishCities.findNearestCity(userLat, userLon)
        var mahalle = ""
        var ilce = ""
        var il = fallbackCityName.substringBefore(" (").trim().let {
            if (it.isBlank() || it == "Anlık Konum" || it == "Mevcut Konum") nearestCity.name else it
        }

        // 1. Try Android Built-in Geocoder first
        if (context != null && Geocoder.isPresent()) {
            try {
                val geocoder = Geocoder(context, Locale("tr", "TR"))
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(userLat, userLon, 3)
                if (!addresses.isNullOrEmpty()) {
                    for (addr in addresses) {
                        if (mahalle.isBlank()) {
                            val subLoc = addr.subLocality?.trim().orEmpty()
                            val thorough = addr.thoroughfare?.trim().orEmpty()
                            mahalle = when {
                                subLoc.isNotBlank() -> subLoc
                                thorough.contains("Mah", ignoreCase = true) -> thorough
                                else -> ""
                            }
                        }
                        if (ilce.isBlank()) {
                            ilce = addr.subAdminArea?.trim()
                                ?.ifBlank { addr.locality?.trim() }
                                .orEmpty()
                        }
                        if (!addr.adminArea.isNullOrBlank()) {
                            il = addr.adminArea.trim()
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }

        // 2. Query OpenStreetMap Nominatim Reverse Geocoding API for exact Turkish Mahalle / İlçe / İl
        if (mahalle.isBlank() || ilce.isBlank()) {
            try {
                val urlStr = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$userLat&lon=$userLon&zoom=18&addressdetails=1&accept-language=tr"
                val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 4000
                    readTimeout = 4000
                    setRequestProperty("User-Agent", "EzanVaktiAndroidApp/1.0")
                }
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val root = JSONObject(body)
                    val addr = root.optJSONObject("address")
                    if (addr != null) {
                        val nomMahalle = addr.optString("neighbourhood")
                            .ifBlank { addr.optString("suburb") }
                            .ifBlank { addr.optString("quarter") }
                            .ifBlank { addr.optString("village") }
                            .ifBlank { addr.optString("hamlet") }
                            .trim()
                        val nomIlce = addr.optString("town")
                            .ifBlank { addr.optString("city_district") }
                            .ifBlank { addr.optString("county") }
                            .ifBlank { addr.optString("municipality") }
                            .ifBlank { addr.optString("district") }
                            .trim()
                        val nomIl = addr.optString("province")
                            .ifBlank { addr.optString("state") }
                            .ifBlank { addr.optString("city") }
                            .trim()

                        if (nomMahalle.isNotBlank()) mahalle = nomMahalle
                        if (nomIlce.isNotBlank()) ilce = nomIlce
                        if (nomIl.isNotBlank()) il = nomIl
                    }
                }
                conn.disconnect()
            } catch (_: Exception) {
            }
        }

        val normalizedMahalle = normalizeMahalleLabel(mahalle)
        val normalizedIlce = ilce.ifBlank { "Merkez" }
        val normalizedIl = il.ifBlank { nearestCity.name }

        UserLocationHierarchy(
            mahalle = normalizedMahalle,
            ilce = normalizedIlce,
            il = normalizedIl,
            isLiveGps = isLiveGps
        )
    }

    private fun normalizeMahalleLabel(raw: String): String {
        val clean = raw.trim()
        if (clean.isBlank()) return ""
        return if (clean.contains("Mah", ignoreCase = true)) {
            clean
        } else {
            "$clean Mah."
        }
    }

    /**
     * Classifies a mosque into MAHALLE (0), ILCE (1), or IL (2) relative to the user's current
     * location and resolved address hierarchy.
     */
    private fun classifyMosqueScopeTier(
        mosque: MosqueItem,
        distKm: Double,
        hierarchy: UserLocationHierarchy
    ): MosqueItem {
        val userMahalleCore = hierarchy.mahalle
            .replace("Mahallesi", "", ignoreCase = true)
            .replace("Mah.", "", ignoreCase = true)
            .trim()
        val mosqueMahalleCore = mosque.neighborhood
            .replace("Mahallesi", "", ignoreCase = true)
            .replace("Mah.", "", ignoreCase = true)
            .trim()

        val isSameMahalleByName = userMahalleCore.length >= 3 &&
            (mosqueMahalleCore.contains(userMahalleCore, ignoreCase = true) ||
                mosque.district.contains(userMahalleCore, ignoreCase = true))

        val isSameIlceByName = hierarchy.ilce.length >= 3 &&
            (mosque.district.contains(hierarchy.ilce, ignoreCase = true) ||
                hierarchy.ilce.contains(mosque.district, ignoreCase = true))

        val tier = when {
            // 1. Önce bulunulan mahalle içindeki camiler (aynı mahalle adı veya <= 1.35 km yürüme mesafesi)
            isSameMahalleByName || distKm <= 1.35 -> MosqueScopeTier.MAHALLE
            // 2. Sonra bulunulan ilçe içindeki camiler (aynı ilçe adı veya <= 12 km ilçe mesafesi)
            isSameIlceByName || distKm <= 12.0 -> MosqueScopeTier.ILCE
            // 3. Yoksa bulunulan il içindeki en yakın camiler
            else -> MosqueScopeTier.IL
        }

        val resolvedNeighborhood = when {
            mosque.neighborhood.isNotBlank() -> normalizeMahalleLabel(mosque.neighborhood)
            tier == MosqueScopeTier.MAHALLE && hierarchy.mahalle.isNotBlank() -> hierarchy.mahalle
            else -> ""
        }
        val resolvedDistrict = when {
            mosque.district.isNotBlank() &&
                mosque.district != "Yakın Çevre" &&
                !mosque.district.equals(hierarchy.il, ignoreCase = true) -> mosque.district
            tier != MosqueScopeTier.IL && hierarchy.ilce.isNotBlank() -> hierarchy.ilce
            else -> mosque.district.ifBlank { hierarchy.ilce.ifBlank { "Merkez" } }
        }
        val resolvedCity = mosque.cityName.ifBlank { hierarchy.il }

        return mosque.copy(
            neighborhood = resolvedNeighborhood,
            district = resolvedDistrict,
            cityName = resolvedCity,
            scopeTier = tier
        )
    }

    /**
     * Sorts mosques hierarchically:
     * 1st priority: Bulunulan Mahalle İçi (MAHALLE), sorted closest to farthest
     * 2nd priority: Bulunulan İlçe İçi (ILCE), sorted closest to farthest
     * 3rd priority: Bulunulan İl İçi (IL), sorted closest to farthest
     */
    fun getNearbyMosques(
        userLat: Double,
        userLon: Double,
        limit: Int = MAX_NEARBY_MOSQUES,
        hierarchy: UserLocationHierarchy? = null
    ): List<Pair<MosqueItem, Double>> {
        val effectiveLimit = limit.coerceIn(1, MAX_NEARBY_MOSQUES)
        val nearestCity = TurkishCities.findNearestCity(userLat, userLon)
        val effectiveHierarchy = hierarchy ?: UserLocationHierarchy(
            mahalle = "",
            ilce = "Merkez",
            il = nearestCity.name
        )

        return allMosques
            .map { mosque ->
                val distKm = mosque.calculateDistanceKm(userLat, userLon)
                val classified = classifyMosqueScopeTier(mosque, distKm, effectiveHierarchy)
                classified to distKm
            }
            .sortedWith(
                compareBy<Pair<MosqueItem, Double>> { it.first.scopeTier.rankOrder }
                    .thenBy { it.second }
            )
            .distinctBy { it.first.name.lowercase(Locale("tr", "TR")) }
            .take(effectiveLimit)
    }

    /**
     * Queries both OpenStreetMap Overpass API and Nominatim Search API for real mosques around
     * the user's exact coordinates, classifies each mosque into Mahalle -> İlçe -> İl,
     * and returns up to 10 mosques ordered first by Mahalle (closest first), then İlçe (closest first),
     * then İl (closest first).
     */
    suspend fun fetchLiveNearbyMosques(
        context: Context? = null,
        userLat: Double,
        userLon: Double,
        cityNameHint: String = "",
        isLiveGps: Boolean = false,
        limit: Int = MAX_NEARBY_MOSQUES
    ): Pair<UserLocationHierarchy, List<Pair<MosqueItem, Double>>> = withContext(Dispatchers.IO) {
        val effectiveLimit = limit.coerceIn(1, MAX_NEARBY_MOSQUES)
        val hierarchy = resolveUserLocationHierarchy(
            context = context,
            userLat = userLat,
            userLon = userLon,
            fallbackCityName = cityNameHint,
            isLiveGps = isLiveGps
        )

        val liveItems = mutableListOf<MosqueItem>()

        // Source 1: OpenStreetMap Nominatim bounded search around user's Mahalle / İlçe / İl (very fast & rich address tags)
        try {
            val delta = 0.09 // ~10km box around user
            val left = userLon - delta
            val top = userLat + delta
            val right = userLon + delta
            val bottom = userLat - delta
            val nominatimUrls = listOf(
                "https://nominatim.openstreetmap.org/search?format=jsonv2&q=Cami&viewbox=$left,$top,$right,$bottom&bounded=1&addressdetails=1&limit=35&accept-language=tr",
                "https://nominatim.openstreetmap.org/search?format=jsonv2&q=${URLEncoder.encode("${hierarchy.ilce} ${hierarchy.il} Cami", "UTF-8")}&addressdetails=1&limit=25&accept-language=tr"
            )

            for (nomUrl in nominatimUrls) {
                try {
                    val conn = (URL(nomUrl).openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 4500
                        readTimeout = 4500
                        setRequestProperty("User-Agent", "EzanVaktiAndroidApp/1.0")
                    }
                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                        val arr = JSONArray(responseText)
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            val name = obj.optString("name").trim()
                            if (name.isBlank()) continue
                            val lat = obj.optString("lat").toDoubleOrNull() ?: continue
                            val lon = obj.optString("lon").toDoubleOrNull() ?: continue
                            val addr = obj.optJSONObject("address")
                            val mahalle = addr?.optString("neighbourhood")
                                ?.ifBlank { addr.optString("suburb") }
                                ?.ifBlank { addr.optString("quarter") }
                                ?.ifBlank { addr.optString("village") }
                                .orEmpty()
                            val ilce = addr?.optString("town")
                                ?.ifBlank { addr.optString("city_district") }
                                ?.ifBlank { addr.optString("county") }
                                ?.ifBlank { addr.optString("municipality") }
                                .orEmpty()
                                .ifBlank { hierarchy.ilce }
                            val il = addr?.optString("province")
                                ?.ifBlank { addr.optString("state") }
                                ?.ifBlank { addr.optString("city") }
                                .orEmpty()
                                .ifBlank { hierarchy.il }

                            liveItems.add(
                                MosqueItem(
                                    id = "nom_${obj.optLong("place_id", i.toLong())}",
                                    name = name,
                                    district = ilce,
                                    cityName = il,
                                    lat = lat,
                                    lon = lon,
                                    descriptionTr = if (mahalle.isNotBlank()) "$mahalle, $ilce" else "$ilce, $il",
                                    descriptionEn = if (mahalle.isNotBlank()) "$mahalle, $ilce" else "$ilce, $il",
                                    descriptionAr = if (mahalle.isNotBlank()) "$mahalle، $ilce" else "$ilce، $il",
                                    neighborhood = mahalle
                                )
                            )
                        }
                    }
                    conn.disconnect()
                    if (liveItems.size >= 10) break
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }

        // Source 2: OpenStreetMap Overpass API (captures all neighborhood mosques & masjids within 12km)
        try {
            val query = """
                [out:json][timeout:8];
                (
                  node["amenity"="place_of_worship"]["religion"="muslim"](around:12000,$userLat,$userLon);
                  way["amenity"="place_of_worship"]["religion"="muslim"](around:12000,$userLat,$userLon);
                  node["building"="mosque"](around:12000,$userLat,$userLon);
                  way["building"="mosque"](around:12000,$userLat,$userLon);
                );
                out center 60;
            """.trimIndent()

            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val endpoints = listOf(
                "https://overpass-api.de/api/interpreter?data=$encodedQuery",
                "https://overpass.kumi.systems/api/interpreter?data=$encodedQuery"
            )

            for (urlStr in endpoints) {
                try {
                    val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 5000
                        readTimeout = 5000
                        setRequestProperty("User-Agent", "EzanVaktiAndroidApp/1.0")
                    }
                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                        val root = JSONObject(responseText)
                        val elements = root.optJSONArray("elements")
                        if (elements != null) {
                            for (i in 0 until elements.length()) {
                                val el = elements.getJSONObject(i)
                                val tags = el.optJSONObject("tags") ?: continue
                                val rawName = tags.optString("name:tr")
                                    .ifBlank { tags.optString("name") }
                                    .trim()
                                if (rawName.isBlank()) continue

                                val lat = if (el.has("lat")) {
                                    el.optDouble("lat", Double.NaN)
                                } else {
                                    el.optJSONObject("center")?.optDouble("lat", Double.NaN) ?: Double.NaN
                                }
                                val lon = if (el.has("lon")) {
                                    el.optDouble("lon", Double.NaN)
                                } else {
                                    el.optJSONObject("center")?.optDouble("lon", Double.NaN) ?: Double.NaN
                                }
                                if (lat.isNaN() || lon.isNaN()) continue

                                val mahalleTag = tags.optString("addr:neighbourhood")
                                    .ifBlank { tags.optString("addr:suburb") }
                                    .ifBlank { tags.optString("addr:quarter") }
                                    .trim()
                                val ilceTag = tags.optString("addr:district")
                                    .ifBlank { tags.optString("addr:town") }
                                    .ifBlank { tags.optString("addr:city_district") }
                                    .ifBlank { hierarchy.ilce }
                                    .trim()
                                val streetTag = tags.optString("addr:street").trim()

                                val descTr = buildString {
                                    if (mahalleTag.isNotBlank()) append(normalizeMahalleLabel(mahalleTag))
                                    if (streetTag.isNotBlank()) {
                                        if (isNotEmpty()) append(", ")
                                        append(streetTag)
                                    }
                                    if (isEmpty()) {
                                        if (hierarchy.mahalle.isNotBlank()) {
                                            append("${hierarchy.mahalle} / ${hierarchy.ilce}")
                                        } else {
                                            append("${hierarchy.ilce} / ${hierarchy.il}")
                                        }
                                    }
                                }

                                liveItems.add(
                                    MosqueItem(
                                        id = "osm_${el.optLong("id", i.toLong())}",
                                        name = rawName,
                                        district = ilceTag,
                                        cityName = hierarchy.il,
                                        lat = lat,
                                        lon = lon,
                                        descriptionTr = descTr,
                                        descriptionEn = descTr,
                                        descriptionAr = descTr,
                                        neighborhood = mahalleTag
                                    )
                                )
                            }
                        }
                        conn.disconnect()
                        if (liveItems.isNotEmpty()) break
                    } else {
                        conn.disconnect()
                    }
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }

        // Combine live discovered mosques with same-province offline mosques as fallback
        val combined = mutableListOf<MosqueItem>()
        combined.addAll(liveItems)

        // Add local mosques from the same province (or within 50km) if we need more items
        val provinceMosques = allMosques.filter { m ->
            m.cityName.equals(hierarchy.il, ignoreCase = true) ||
                m.calculateDistanceKm(userLat, userLon) <= 50.0
        }
        for (localMosque in provinceMosques) {
            val isDuplicate = combined.any { existing ->
                existing.name.equals(localMosque.name, ignoreCase = true) ||
                    (abs(existing.lat - localMosque.lat) < 0.0009 && abs(existing.lon - localMosque.lon) < 0.0009)
            }
            if (!isDuplicate) {
                combined.add(localMosque)
            }
        }

        if (combined.isEmpty()) {
            combined.addAll(allMosques)
        }

        // Deduplicate, classify into MAHALLE -> ILCE -> IL, and sort strictly by tier then distance
        val deduplicated = mutableListOf<MosqueItem>()
        for (item in combined) {
            val exists = deduplicated.any { existing ->
                existing.name.equals(item.name, ignoreCase = true) &&
                    abs(existing.lat - item.lat) < 0.003 &&
                    abs(existing.lon - item.lon) < 0.003
            }
            if (!exists) {
                deduplicated.add(item)
            }
        }

        val sortedResult = deduplicated
            .map { mosque ->
                val distKm = mosque.calculateDistanceKm(userLat, userLon)
                val classified = classifyMosqueScopeTier(mosque, distKm, hierarchy)
                classified to distKm
            }
            .sortedWith(
                compareBy<Pair<MosqueItem, Double>> { it.first.scopeTier.rankOrder }
                    .thenBy { it.second }
            )
            .take(effectiveLimit)

        hierarchy to sortedResult
    }

    fun getGoogleMapsSearchUrl(lat: Double, lon: Double): String {
        return "geo:$lat,$lon?q=cami"
    }

    fun getGoogleMapsDirectionsUrl(destLat: Double, destLon: Double, mosqueName: String): String {
        return "https://www.google.com/maps/dir/?api=1&destination=$destLat,$destLon&destination_place_id=$mosqueName"
    }
}
