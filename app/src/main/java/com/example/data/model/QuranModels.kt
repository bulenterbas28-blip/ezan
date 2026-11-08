package com.example.data.model

data class SurahMeta(
    val number: Int,
    val nameTr: String,
    val nameAr: String,
    val meaningTr: String,
    val ayahCount: Int,
    val revelationPlace: String, // "Mekke" veya "Medine"
    val juz: Int,
    val audioUrl: String = "https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/$number.mp3"
) {
    fun getLocalizedName(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> nameTr
        AppLanguage.ENGLISH -> nameTr
        AppLanguage.ARABIC -> nameAr
    }

    fun getLocalizedRevelationPlace(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> revelationPlace
        AppLanguage.ENGLISH -> if (revelationPlace == "Mekke") "Meccan" else "Medinan"
        AppLanguage.ARABIC -> if (revelationPlace == "Mekke") "مكية" else "مدنية"
    }

    fun getLocalizedMeaning(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> meaningTr
        AppLanguage.ENGLISH -> englishMeanings[number] ?: meaningTr
        AppLanguage.ARABIC -> nameAr
    }

    companion object {
        private val englishMeanings = mapOf(
            1 to "The Opening", 2 to "The Cow", 3 to "Family of Imran", 4 to "The Women",
            5 to "The Table Spread", 6 to "The Cattle", 7 to "The Heights", 8 to "The Spoils of War",
            9 to "The Repentance", 10 to "Jonah", 11 to "Hud", 12 to "Joseph",
            13 to "The Thunder", 14 to "Abraham", 15 to "The Rocky Tract", 16 to "The Bee",
            17 to "The Night Journey", 18 to "The Cave", 19 to "Mary", 20 to "Ta-Ha",
            21 to "The Prophets", 22 to "The Pilgrimage", 23 to "The Believers", 24 to "The Light",
            25 to "The Criterion", 26 to "The Poets", 27 to "The Ant", 28 to "The Stories",
            29 to "The Spider", 30 to "The Romans", 31 to "Luqman", 32 to "The Prostration",
            33 to "The Combined Forces", 34 to "Sheba", 35 to "Originator", 36 to "Ya-Sin",
            37 to "Those Who Set The Ranks", 38 to "The Letter Sad", 39 to "The Troops", 40 to "The Forgiver",
            41 to "Explained in Detail", 42 to "The Consultation", 43 to "The Ornaments of Gold", 44 to "The Smoke",
            45 to "The Crouching", 46 to "The Wind-Curved Sandhills", 47 to "Muhammad", 48 to "The Victory",
            49 to "The Rooms", 50 to "The Letter Qaf", 51 to "The Winnowing Winds", 52 to "The Mount",
            53 to "The Star", 54 to "The Moon", 55 to "The Beneficent", 56 to "The Inevitable",
            57 to "The Iron", 58 to "The Pleading Woman", 59 to "The Exile", 60 to "She That Is To Be Examined",
            61 to "The Ranks", 62 to "The Congregation", 63 to "The Hypocrites", 64 to "The Mutual Disillusion",
            65 to "The Divorce", 66 to "The Prohibition", 67 to "The Sovereignty", 68 to "The Pen",
            69 to "The Reality", 70 to "The Ascending Stairways", 71 to "Noah", 72 to "The Jinn",
            73 to "The Enshrouded One", 74 to "The Cloaked One", 75 to "The Resurrection", 76 to "The Man",
            77 to "The Emissaries", 78 to "The Tidings", 79 to "Those Who Drag Forth", 80 to "He Frowned",
            81 to "The Overthrowing", 82 to "The Cleaving", 83 to "The Defrauding", 84 to "The Sundering",
            85 to "The Mansions of the Stars", 86 to "The Morning Star", 87 to "The Most High", 88 to "The Overwhelming",
            89 to "The Dawn", 90 to "The City", 91 to "The Sun", 92 to "The Night",
            93 to "The Morning Hours", 94 to "The Relief", 95 to "The Fig", 96 to "The Clot",
            97 to "The Power", 98 to "The Clear Proof", 99 to "The Earthquake", 100 to "The Courser",
            101 to "The Calamity", 102 to "The Rivalry in World Increase", 103 to "The Declining Day", 104 to "The Traducer",
            105 to "The Elephant", 106 to "Quraysh", 107 to "The Small Kindnesses", 108 to "The Abundance",
            109 to "The Disbelievers", 110 to "The Divine Support", 111 to "The Palm Fiber", 112 to "The Sincerity",
            113 to "The Daybreak", 114 to "Mankind"
        )
    }
}

data class Ayah(
    val numberInSurah: Int,
    val numberInQuran: Int,
    val arabicText: String,
    val turkishMeaning: String,
    val englishMeaning: String = turkishMeaning,
    val turkishPronunciation: String = "",
    val audioUrl: String? = null,
    val surahNumber: Int = 0
) {
    fun getLocalizedMeaning(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> turkishMeaning
        AppLanguage.ENGLISH -> englishMeaning.ifBlank { turkishMeaning }
        AppLanguage.ARABIC -> arabicText
    }

    fun getEffectivePronunciation(fallbackSurahNumber: Int = surahNumber): String {
        if (turkishPronunciation.isNotBlank()) return turkishPronunciation
        return com.example.data.repository.QuranPronunciationHelper.getPronunciation(
            surahNumber = fallbackSurahNumber,
            numberInQuran = numberInQuran,
            numberInSurah = numberInSurah,
            arabicText = arabicText
        )
    }
}

data class SurahDetail(
    val meta: SurahMeta,
    val ayahs: List<Ayah>,
    val isLoadedFromNetwork: Boolean = false
)

data class QuranBookmark(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val timestampMillis: Long
)
