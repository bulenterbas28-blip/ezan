package com.example.data.repository

import android.content.Context
import com.example.data.model.Ayah
import com.example.data.model.SurahDetail
import com.example.data.model.SurahMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object QuranRepository {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    // In-memory cache ONLY for complete surah details (all ayahs loaded)
    private val surahCache = mutableMapOf<Int, SurahDetail>()

    // Optional cache directory handle
    private var cacheDir: File? = null

    private const val CACHE_FILE_PREFIX = "surah_v3_"

    fun init(context: Context) {
        if (cacheDir == null) {
            cacheDir = File(context.cacheDir, "quran_cache").apply { mkdirs() }
        }
    }

    // All 114 Surahs of the Holy Quran
    val allSurahs: List<SurahMeta> = listOf(
        SurahMeta(1, "Fâtiha", "الفاتحة", "Açılış, Başlangıç", 7, "Mekke", 1),
        SurahMeta(2, "Bakara", "البقرة", "Sığır, Boğa", 286, "Medine", 1),
        SurahMeta(3, "Âl-i İmrân", "آل عمران", "İmrân Ailesi", 200, "Medine", 3),
        SurahMeta(4, "Nisâ", "النساء", "Kadınlar", 176, "Medine", 4),
        SurahMeta(5, "Mâide", "المائدة", "Sofra", 120, "Medine", 6),
        SurahMeta(6, "En'âm", "الأنعام", "Ehli Hayvanlar", 165, "Mekke", 7),
        SurahMeta(7, "A'râf", "الأعراف", "Yüksek Yerler, Tepe", 206, "Mekke", 8),
        SurahMeta(8, "Enfâl", "الأنفال", "Savaş Ganimetleri", 75, "Medine", 9),
        SurahMeta(9, "Tevbe", "التوبة", "Tövbe, Pişmanlık", 129, "Medine", 10),
        SurahMeta(10, "Yûnus", "يونس", "Yûnus Peygamber", 109, "Mekke", 11),
        SurahMeta(11, "Hûd", "هود", "Hûd Peygamber", 123, "Mekke", 11),
        SurahMeta(12, "Yûsuf", "يوسف", "Yûsuf Peygamber", 111, "Mekke", 12),
        SurahMeta(13, "Ra'd", "الرعد", "Gök Gürültüsü", 43, "Medine", 13),
        SurahMeta(14, "İbrâhîm", "إبراهيم", "İbrâhîm Peygamber", 52, "Mekke", 13),
        SurahMeta(15, "Hicr", "الحجر", "Kayalık Yer", 99, "Mekke", 14),
        SurahMeta(16, "Nahl", "النحل", "Bal Arısı", 128, "Mekke", 14),
        SurahMeta(17, "İsrâ", "الإسراء", "Gece Yürüyüşü", 111, "Mekke", 15),
        SurahMeta(18, "Kehf", "الكهف", "Mağara Halkı", 110, "Mekke", 15),
        SurahMeta(19, "Meryem", "مريم", "Hz. Meryem", 98, "Mekke", 16),
        SurahMeta(20, "Tâhâ", "طه", "Tâ-Hâ Harfleri", 135, "Mekke", 16),
        SurahMeta(21, "Enbiyâ", "الأنبياء", "Peygamberler", 112, "Mekke", 17),
        SurahMeta(22, "Hac", "الحج", "Hac İbadeti", 78, "Medine", 17),
        SurahMeta(23, "Mü'minûn", "المؤمنون", "Müminler, İnananlar", 118, "Mekke", 18),
        SurahMeta(24, "Nûr", "النور", "İlahi Nur, Işık", 64, "Medine", 18),
        SurahMeta(25, "Furkân", "الفرقان", "Hak ile Bâtılı Ayıran", 77, "Mekke", 18),
        SurahMeta(26, "Şuarâ", "الشعراء", "Şairler", 227, "Mekke", 19),
        SurahMeta(27, "Neml", "النمل", "Karınca", 93, "Mekke", 19),
        SurahMeta(28, "Kasas", "القصص", "Tarihi Kıssalar", 88, "Mekke", 20),
        SurahMeta(29, "Ankebût", "العنكبوت", "Örümcek", 69, "Mekke", 20),
        SurahMeta(30, "Rûm", "الروم", "Romalılar, Bizans", 60, "Mekke", 21),
        SurahMeta(31, "Lokmân", "لقمان", "Lokmân Hekim", 34, "Mekke", 21),
        SurahMeta(32, "Secde", "السجدة", "Secde Etmek", 30, "Mekke", 21),
        SurahMeta(33, "Ahzâb", "الأحزاب", "Birleşmiş Ordular", 73, "Medine", 21),
        SurahMeta(34, "Sebe'", "سبإ", "Sebe' Kavmi / Melikesi", 54, "Mekke", 22),
        SurahMeta(35, "Fâtır", "فاطر", "Yaratan, Vareden", 45, "Mekke", 22),
        SurahMeta(36, "Yâsîn", "يس", "Kur'an'ın Kalbi, Ey İnsan", 83, "Mekke", 22),
        SurahMeta(37, "Sâffât", "الصافات", "Sıra Sıra Duranlar", 182, "Mekke", 23),
        SurahMeta(38, "Sâd", "ص", "Sâd Harfi", 88, "Mekke", 23),
        SurahMeta(39, "Zümer", "الزمر", "Zümreler, Topluluklar", 75, "Mekke", 23),
        SurahMeta(40, "Mü'min (Gâfir)", "غافر", "Bağışlayan, İnanan Kişi", 85, "Mekke", 24),
        SurahMeta(41, "Fussilet", "فصلت", "Genişçe Açıklanmış", 54, "Mekke", 24),
        SurahMeta(42, "Şûrâ", "الشورى", "Danışma, İstişare", 53, "Mekke", 25),
        SurahMeta(43, "Zuhruf", "الزخرف", "Altın Ziynet, Süs", 89, "Mekke", 25),
        SurahMeta(44, "Duhân", "الدخان", "Duman, Sis", 59, "Mekke", 25),
        SurahMeta(45, "Câsiye", "الجاثية", "Diz Üstü Çökenler", 37, "Mekke", 25),
        SurahMeta(46, "Ahkâf", "الأحقاف", "Kum Tepeleri", 35, "Mekke", 26),
        SurahMeta(47, "Muhammed", "محمد", "Peygamber Efendimiz (s.a.v.)", 38, "Medine", 26),
        SurahMeta(48, "Fetih", "الفتح", "Büyük Zafer, Fetih", 29, "Medine", 26),
        SurahMeta(49, "Hucurât", "الحجرات", "Odalar, Hücreler", 18, "Medine", 26),
        SurahMeta(50, "Kâf", "ق", "Kâf Harfi", 45, "Mekke", 26),
        SurahMeta(51, "Zâriyât", "الذاريات", "Rüzgârlar", 60, "Mekke", 26),
        SurahMeta(52, "Tûr", "الطور", "Sînâ Dağı", 49, "Mekke", 27),
        SurahMeta(53, "Necm", "النجم", "Kayan Yıldız", 62, "Mekke", 27),
        SurahMeta(54, "Kamer", "القمر", "Ay", 55, "Mekke", 27),
        SurahMeta(55, "Rahmân", "الرحمن", "Sonsuz Merhametli", 78, "Medine", 27),
        SurahMeta(56, "Vâkıa", "الواقعة", "Kıyamet Olayı", 96, "Mekke", 27),
        SurahMeta(57, "Hadîd", "الحديد", "Demir", 29, "Medine", 27),
        SurahMeta(58, "Mücâdele", "المجادلة", "Tartışan Kadın", 22, "Medine", 28),
        SurahMeta(59, "Haşr", "الحشر", "Toplanma, Sürgün", 24, "Medine", 28),
        SurahMeta(60, "Mümtehine", "الممتحنة", "İmtihan Edilen Kadın", 13, "Medine", 28),
        SurahMeta(61, "Saf", "الصف", "Sıra, Dizi Halinde", 14, "Medine", 28),
        SurahMeta(62, "Cuma", "الجمعة", "Cuma Günü ve Namazı", 11, "Medine", 28),
        SurahMeta(63, "Münâfikûn", "المنافقون", "İki Yüzlüler, Münafıklar", 11, "Medine", 28),
        SurahMeta(64, "Tegâbün", "التغابن", "Aldanma ve Kâr Günü", 18, "Medine", 28),
        SurahMeta(65, "Talâk", "الطلاق", "Boşanma Hükümleri", 12, "Medine", 28),
        SurahMeta(66, "Tahrîm", "التحريم", "Haram Kılma", 12, "Medine", 28),
        SurahMeta(67, "Mülk (Tebâreke)", "الملك", "Kainatın Hükümranlığı", 30, "Mekke", 29),
        SurahMeta(68, "Kalem", "القلم", "Kalem ve Yazı", 52, "Mekke", 29),
        SurahMeta(69, "Hâkka", "الحاقة", "Mutlak Gerçek, Kıyamet", 52, "Mekke", 29),
        SurahMeta(70, "Meâric", "المعارج", "Yükselme Yolları, Dereceler", 44, "Mekke", 29),
        SurahMeta(71, "Nûh", "نوح", "Nûh Peygamber", 28, "Mekke", 29),
        SurahMeta(72, "Cin", "الجن", "Görünmeyen Varlıklar", 28, "Mekke", 29),
        SurahMeta(73, "Müzzemmil", "المزمل", "Örtüsüne Bürünen", 20, "Mekke", 29),
        SurahMeta(74, "Müddessir", "المدثر", "Yorganına Bürünen", 56, "Mekke", 29),
        SurahMeta(75, "Kıyâme", "القيامة", "Diriliş Günü", 40, "Mekke", 29),
        SurahMeta(76, "İnsân (Dehr)", "الإنسان", "İnsan ve Zaman", 31, "Medine", 29),
        SurahMeta(77, "Mürselât", "المرسلات", "Gönderilen Rüzgârlar", 50, "Mekke", 29),
        SurahMeta(78, "Nebe' (Amme)", "النبإ", "Büyük Haber, Kıyamet", 40, "Mekke", 30),
        SurahMeta(79, "Nâziât", "النازعات", "Ruhları Çekip Alanlar", 46, "Mekke", 30),
        SurahMeta(80, "Abese", "عبس", "Yüzünü Ekşitti", 42, "Mekke", 30),
        SurahMeta(81, "Tekvîr", "التكوير", "Dürülme, Güneşin Kararması", 29, "Mekke", 30),
        SurahMeta(82, "İnfitâr", "الانفطار", "Göğün Yarılması", 19, "Mekke", 30),
        SurahMeta(83, "Mutaffifîn", "المطففين", "Ölçüde Hile Yapanlar", 36, "Mekke", 30),
        SurahMeta(84, "İnşikâk", "الانشقاق", "Göğün Parçalanması", 25, "Mekke", 30),
        SurahMeta(85, "Bürûc", "البروج", "Yıldız Kümeleri, Burçlar", 22, "Mekke", 30),
        SurahMeta(86, "Târık", "الطارق", "Sabah Yıldızı, Gece Gelen", 17, "Mekke", 30),
        SurahMeta(87, "A'lâ", "الأعلى", "En Yüce Olan Allah", 19, "Mekke", 30),
        SurahMeta(88, "Gâşiye", "الغاشية", "Her Şeyi Kuşatan Kıyamet", 26, "Mekke", 30),
        SurahMeta(89, "Fecr", "الفجر", "Tan Vakti, Şafak", 30, "Mekke", 30),
        SurahMeta(90, "Beled", "البلد", "Kutsal Şehir (Mekke)", 20, "Mekke", 30),
        SurahMeta(91, "Şems", "الشمس", "Güneş", 15, "Mekke", 30),
        SurahMeta(92, "Leyl", "الليل", "Gece", 21, "Mekke", 30),
        SurahMeta(93, "Duhâ", "الضحى", "Kuşluk Vakti", 11, "Mekke", 30),
        SurahMeta(94, "İnşirâh", "الشرح", "Gönül Ferahlığı", 8, "Mekke", 30),
        SurahMeta(95, "Tîn", "التين", "İncir Ağacı", 8, "Mekke", 30),
        SurahMeta(96, "Alak", "العلق", "Yaratılış, Pıhtı, Oku!", 19, "Mekke", 30),
        SurahMeta(97, "Kadir", "القدر", "Kadir Gecesi, Hüküm", 5, "Mekke", 30),
        SurahMeta(98, "Beyyine", "البينة", "Açık Delil, Beyyine", 8, "Medine", 30),
        SurahMeta(99, "Zilzâl", "الزلزلة", "Büyük Yer Sarsıntısı, Deprem", 8, "Medine", 30),
        SurahMeta(100, "Âdiyât", "العاديات", "Nefes Nefese Koşan Atlar", 11, "Mekke", 30),
        SurahMeta(101, "Kâria", "القارعة", "Kapı Çalan Büyük Kıyamet", 11, "Mekke", 30),
        SurahMeta(102, "Tekâsür", "التكاثر", "Çoklukla Övünme Yarışı", 8, "Mekke", 30),
        SurahMeta(103, "Asr", "العصر", "Zaman, Asır", 3, "Mekke", 30),
        SurahMeta(104, "Hümeze", "الهمزة", "Arkadan Çekiştiren, Dedikoducu", 9, "Mekke", 30),
        SurahMeta(105, "Fîl", "الفيل", "Fil Ordusu Olayı", 5, "Mekke", 30),
        SurahMeta(106, "Kureyş", "قريش", "Kureyş Kabilesi", 4, "Mekke", 30),
        SurahMeta(107, "Mâûn", "الماعون", "Yardım ve Zekât", 7, "Mekke", 30),
        SurahMeta(108, "Kevser", "الكوثر", "Bitmeyen Bolluk, Kevser", 3, "Mekke", 30),
        SurahMeta(109, "Kâfirûn", "الكافرون", "İnkârcılar", 6, "Mekke", 30),
        SurahMeta(110, "Nasr", "النصر", "İlahi Yardım ve Zafer", 3, "Medine", 30),
        SurahMeta(111, "Tebbet", "المسد", "Ebû Leheb'in Helâkı", 5, "Mekke", 30),
        SurahMeta(112, "İhlâs", "الإخلاص", "Samimi Tevhid ve İhlas", 4, "Mekke", 30),
        SurahMeta(113, "Felak", "الفلق", "Sabahın Aydınlığı", 5, "Mekke", 30),
        SurahMeta(114, "Nâs", "الناس", "Bütün İnsanlar", 6, "Mekke", 30)
    )

    fun getSurahMeta(number: Int): SurahMeta {
        return allSurahs.find { it.number == number } ?: allSurahs.first()
    }

    /**
     * Synchronously returns a COMPLETE cached or built-in SurahDetail if all ayahs are already available.
     * Never writes incomplete/partial Surahs into surahCache so network loading is never blocked.
     */
    fun getCachedOrBuiltInDetail(surahNumber: Int): SurahDetail? {
        val meta = getSurahMeta(surahNumber)
        surahCache[surahNumber]?.let { cached ->
            if (cached.ayahs.size >= meta.ayahCount) return cached
        }
        readFromDiskCache(surahNumber, meta)?.let { diskDetail ->
            if (diskDetail.ayahs.size >= meta.ayahCount) {
                surahCache[surahNumber] = diskDetail
                return diskDetail
            }
        }
        val builtInAyahs = QuranBuiltInSurahs.getAyahs(surahNumber, meta)
        if (builtInAyahs.size >= meta.ayahCount) {
            val detail = SurahDetail(meta = meta, ayahs = builtInAyahs, isLoadedFromNetwork = true)
            surahCache[surahNumber] = detail
            return detail
        }
        return null
    }

    /**
     * Retrieves Surah details with all authentic ayahs, Turkish Diyanet meal, Turkish pronunciation,
     * and English translation.
     */
    suspend fun getSurahDetail(surahNumber: Int, forceRefresh: Boolean = false): SurahDetail = withContext(Dispatchers.IO) {
        val meta = getSurahMeta(surahNumber)

        // 1. Check in-memory cache (only if complete)
        if (!forceRefresh) {
            surahCache[surahNumber]?.let { cached ->
                if (cached.ayahs.size >= meta.ayahCount) {
                    return@withContext cached
                }
            }
        }

        // 2. Check disk cache (only if complete)
        if (!forceRefresh) {
            readFromDiskCache(surahNumber, meta)?.let { diskDetail ->
                if (diskDetail.ayahs.size >= meta.ayahCount) {
                    surahCache[surahNumber] = diskDetail
                    return@withContext diskDetail
                }
            }
        }

        // 3. Prepare built-in authentic ayahs
        val builtInAyahs = QuranBuiltInSurahs.getAyahs(surahNumber, meta)
        if (builtInAyahs.size >= meta.ayahCount && !forceRefresh) {
            val completeBuiltIn = SurahDetail(meta = meta, ayahs = builtInAyahs, isLoadedFromNetwork = true)
            surahCache[surahNumber] = completeBuiltIn
            return@withContext completeBuiltIn
        }

        // 4. Primary API: api.alquran.cloud with 4 editions (Uthmani Arabic, Diyanet Turkish Meal, Turkish Transliteration, English Sahih)
        fetchFromAlQuranCloud(surahNumber, meta)?.let { networkDetail ->
            surahCache[surahNumber] = networkDetail
            saveToDiskCache(surahNumber, networkDetail.ayahs)
            return@withContext networkDetail
        }

        // 5. Secondary CDN fallback: jsDelivr quran-api
        fetchFromJsDelivrCdn(surahNumber, meta)?.let { cdnDetail ->
            surahCache[surahNumber] = cdnDetail
            saveToDiskCache(surahNumber, cdnDetail.ayahs)
            return@withContext cdnDetail
        }

        // 6. Offline fallback: return built-in authentic ayahs WITHOUT caching incomplete list in surahCache
        SurahDetail(meta = meta, ayahs = builtInAyahs, isLoadedFromNetwork = false)
    }

    private fun fetchFromAlQuranCloud(surahNumber: Int, meta: SurahMeta): SurahDetail? {
        // Try 4-edition bundle first (Arabic + TR Diyanet + TR Transliteration + EN Sahih)
        val urlsToTry = listOf(
            "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,tr.diyanet,tr.transliteration,en.sahih",
            "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,tr.diyanet"
        )

        for (url in urlsToTry) {
            try {
                val request = Request.Builder().url(url).build()
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use
                    val body = response.body?.string()
                    if (body.isNullOrBlank()) return@use

                    val root = JSONObject(body)
                    if (root.optInt("code") != 200) return@use

                    val dataArray = root.getJSONArray("data")
                    if (dataArray.length() < 2) return@use

                    val arabicEdition = dataArray.getJSONObject(0)
                    val turkishEdition = dataArray.getJSONObject(1)
                    val translitEdition = if (dataArray.length() >= 3) dataArray.getJSONObject(2) else null
                    val englishEdition = if (dataArray.length() >= 4) dataArray.getJSONObject(3) else null

                    val arAyahs = arabicEdition.getJSONArray("ayahs")
                    val trAyahs = turkishEdition.getJSONArray("ayahs")
                    val trTranslitAyahs = translitEdition?.optJSONArray("ayahs")
                    val enAyahs = englishEdition?.optJSONArray("ayahs")

                    val count = minOf(arAyahs.length(), trAyahs.length())
                    val parsedAyahs = ArrayList<Ayah>(count)

                    for (i in 0 until count) {
                        val arObj = arAyahs.getJSONObject(i)
                        val trObj = trAyahs.getJSONObject(i)
                        val numberInSurah = arObj.getInt("numberInSurah")
                        val numberInQuran = arObj.getInt("number")

                        val rawArabic = arObj.getString("text")
                        val cleanArabic = stripLeadingBismillahIfNeeded(
                            arabicText = rawArabic,
                            surahNumber = surahNumber,
                            numberInSurah = numberInSurah
                        )

                        val trMeaning = trObj.getString("text").trim()
                        val rawTranslit = if (trTranslitAyahs != null && i < trTranslitAyahs.length()) {
                            trTranslitAyahs.getJSONObject(i).optString("text", "")
                        } else {
                            ""
                        }
                        val enMeaning = if (enAyahs != null && i < enAyahs.length()) {
                            enAyahs.getJSONObject(i).optString("text", trMeaning).trim()
                        } else {
                            trMeaning
                        }

                        val effectivePronunciation = QuranPronunciationHelper.cleanApiTransliteration(
                            raw = rawTranslit,
                            surahNumber = surahNumber,
                            numberInSurah = numberInSurah,
                            arabicText = cleanArabic
                        )

                        parsedAyahs.add(
                            Ayah(
                                numberInSurah = numberInSurah,
                                numberInQuran = numberInQuran,
                                arabicText = cleanArabic,
                                turkishMeaning = trMeaning,
                                englishMeaning = enMeaning,
                                turkishPronunciation = effectivePronunciation,
                                surahNumber = surahNumber
                            )
                        )
                    }

                    if (parsedAyahs.isNotEmpty()) {
                        return SurahDetail(meta = meta, ayahs = parsedAyahs, isLoadedFromNetwork = true)
                    }
                }
            } catch (_: Exception) {
                // Continue to next URL or fallback
            }
        }
        return null
    }

    private fun fetchFromJsDelivrCdn(surahNumber: Int, meta: SurahMeta): SurahDetail? {
        try {
            val arUrl = "https://cdn.jsdelivr.net/gh/fawazahmed0/quran-api@1/editions/ara-quranuthmani/$surahNumber.json"
            val trUrl = "https://cdn.jsdelivr.net/gh/fawazahmed0/quran-api@1/editions/tur-diyanet/$surahNumber.json"
            val trLatUrl = "https://cdn.jsdelivr.net/gh/fawazahmed0/quran-api@1/editions/tur-latinalphabet/$surahNumber.json"
            val enUrl = "https://cdn.jsdelivr.net/gh/fawazahmed0/quran-api@1/editions/eng-ummmuhammad/$surahNumber.json"

            val arBody = executeGetString(arUrl) ?: return null
            val trBody = executeGetString(trUrl) ?: return null
            val trLatBody = executeGetString(trLatUrl)
            val enBody = executeGetString(enUrl)

            val arJson = JSONObject(arBody).getJSONArray("chapter")
            val trJson = JSONObject(trBody).getJSONArray("chapter")
            val trLatJson = trLatBody?.let { runCatching { JSONObject(it).getJSONArray("chapter") }.getOrNull() }
            val enJson = enBody?.let { runCatching { JSONObject(it).getJSONArray("chapter") }.getOrNull() }

            val count = minOf(arJson.length(), trJson.length())
            val parsedAyahs = ArrayList<Ayah>(count)

            for (i in 0 until count) {
                val arObj = arJson.getJSONObject(i)
                val trObj = trJson.getJSONObject(i)
                val numberInSurah = arObj.optInt("verse", i + 1)

                val rawArabic = arObj.getString("text")
                val cleanArabic = stripLeadingBismillahIfNeeded(
                    arabicText = rawArabic,
                    surahNumber = surahNumber,
                    numberInSurah = numberInSurah
                )

                val trMeaning = trObj.getString("text").trim()
                val rawTranslit = if (trLatJson != null && i < trLatJson.length()) {
                    trLatJson.getJSONObject(i).optString("text", "")
                } else {
                    ""
                }
                val enMeaning = if (enJson != null && i < enJson.length()) {
                    enJson.getJSONObject(i).optString("text", trMeaning).trim()
                } else {
                    trMeaning
                }

                val effectivePronunciation = QuranPronunciationHelper.cleanApiTransliteration(
                    raw = rawTranslit,
                    surahNumber = surahNumber,
                    numberInSurah = numberInSurah,
                    arabicText = cleanArabic
                )

                parsedAyahs.add(
                    Ayah(
                        numberInSurah = numberInSurah,
                        numberInQuran = 0,
                        arabicText = cleanArabic,
                        turkishMeaning = trMeaning,
                        englishMeaning = enMeaning,
                        turkishPronunciation = effectivePronunciation,
                        surahNumber = surahNumber
                    )
                )
            }

            if (parsedAyahs.isNotEmpty()) {
                return SurahDetail(meta = meta, ayahs = parsedAyahs, isLoadedFromNetwork = true)
            }
        } catch (_: Exception) {
            // Gracefully ignore and fallback
        }
        return null
    }

    private fun executeGetString(url: String): String? {
        return try {
            httpClient.newCall(Request.Builder().url(url).build()).execute().use { res ->
                if (res.isSuccessful) res.body?.string() else null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * In quran-uthmani from api.alquran.cloud, verse 1 of Surahs 2..114 (except Surah 9)
     * begins with the Bismillah string prepended to the verse text. Since QuranScreen already
     * displays a dedicated Bismillah banner above verse 1, and tr.diyanet / tr.transliteration
     * only contain verse 1 itself, stripping the prepended Bismillah keeps Arabic, Okunuş, and Meal 100% aligned.
     */
    private fun stripLeadingBismillahIfNeeded(
        arabicText: String,
        surahNumber: Int,
        numberInSurah: Int
    ): String {
        if (surahNumber == 1 || surahNumber == 9 || numberInSurah != 1) {
            return arabicText.trim()
        }
        val bismillahPrefixes = listOf(
            "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ",
            "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ",
            "بِسْمِ اللهِ الرَّحْمٰنِ الرَّحِيمِ "
        )
        var cleaned = arabicText.trim()
        for (prefix in bismillahPrefixes) {
            if (cleaned.startsWith(prefix) && cleaned.length > prefix.length) {
                cleaned = cleaned.removePrefix(prefix).trim()
                break
            }
        }
        return cleaned
    }

    private fun saveToDiskCache(surahNumber: Int, ayahs: List<Ayah>) {
        try {
            val dir = cacheDir ?: return
            val file = File(dir, "$CACHE_FILE_PREFIX$surahNumber.json")
            val array = JSONArray()
            for (ayah in ayahs) {
                val obj = JSONObject()
                obj.put("numberInSurah", ayah.numberInSurah)
                obj.put("numberInQuran", ayah.numberInQuran)
                obj.put("arabicText", ayah.arabicText)
                obj.put("turkishMeaning", ayah.turkishMeaning)
                obj.put("englishMeaning", ayah.englishMeaning)
                obj.put("turkishPronunciation", ayah.turkishPronunciation)
                obj.put("surahNumber", ayah.surahNumber)
                array.put(obj)
            }
            file.writeText(array.toString())
        } catch (_: Exception) {
            // Silent cache write failure
        }
    }

    private fun readFromDiskCache(surahNumber: Int, meta: SurahMeta): SurahDetail? {
        try {
            val dir = cacheDir ?: return null
            val file = File(dir, "$CACHE_FILE_PREFIX$surahNumber.json")
            if (!file.exists()) return null

            val text = file.readText()
            if (text.isBlank()) return null

            val array = JSONArray(text)
            if (array.length() < meta.ayahCount) return null

            val ayahs = ArrayList<Ayah>(array.length())
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val numberInSurah = obj.getInt("numberInSurah")
                val arabicText = obj.getString("arabicText")
                val turkishMeaning = obj.getString("turkishMeaning")
                val englishMeaning = obj.optString("englishMeaning", turkishMeaning)
                val savedPronunciation = obj.optString("turkishPronunciation", "")
                val effectivePronunciation = savedPronunciation.ifBlank {
                    QuranPronunciationHelper.getPronunciation(
                        surahNumber = surahNumber,
                        numberInQuran = obj.optInt("numberInQuran", 0),
                        numberInSurah = numberInSurah,
                        arabicText = arabicText
                    )
                }
                ayahs.add(
                    Ayah(
                        numberInSurah = numberInSurah,
                        numberInQuran = obj.optInt("numberInQuran", 0),
                        arabicText = arabicText,
                        turkishMeaning = turkishMeaning,
                        englishMeaning = englishMeaning,
                        turkishPronunciation = effectivePronunciation,
                        surahNumber = surahNumber
                    )
                )
            }
            if (ayahs.size >= meta.ayahCount) {
                return SurahDetail(meta = meta, ayahs = ayahs, isLoadedFromNetwork = true)
            }
        } catch (_: Exception) {
            // Silent cache read error
        }
        return null
    }
}
