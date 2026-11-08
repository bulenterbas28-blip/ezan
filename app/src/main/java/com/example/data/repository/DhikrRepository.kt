package com.example.data.repository

import com.example.data.model.DailyInspiration
import com.example.data.model.DhikrItem
import com.example.data.model.InspirationType

object DhikrRepository {
    val defaultDhikrs = listOf(
        DhikrItem(
            id = "subhanallah",
            arabic = "سُبْحَانَ اللَّهِ",
            turkishTitle = "Sübhanallah",
            englishTitle = "SubhanAllah",
            turkishPronunciation = "Sübhanallah",
            meaning = "Allah her türlü eksiklikten ve kusurdan münezzehtir.",
            englishMeaning = "Glory be to Allah; He is free from all imperfection.",
            arabicMeaning = "تنزيه الله عز وجل عن كل نقص وعيب.",
            targetCount = 33
        ),
        DhikrItem(
            id = "elhamdulillah",
            arabic = "الْحَمْدُ لِلَّهِ",
            turkishTitle = "Elhamdülillah",
            englishTitle = "Alhamdulillah",
            turkishPronunciation = "Elhamdülillah",
            meaning = "Hamd ve övgü yalnızca âlemlerin Rabbi olan Allah'a aittir.",
            englishMeaning = "All praise and thanks belong solely to Allah, Lord of the worlds.",
            arabicMeaning = "الثناء والشكر لله رب العالمين على جميع نعمه.",
            targetCount = 33
        ),
        DhikrItem(
            id = "allahu_ekber",
            arabic = "اللَّهُ أَكْبَرُ",
            turkishTitle = "Allahu Ekber",
            englishTitle = "Allahu Akbar",
            turkishPronunciation = "Allahu Ekber",
            meaning = "Allah her şeyden yücedir, en büyüktür.",
            englishMeaning = "Allah is the Greatest and Exalted above everything.",
            arabicMeaning = "الله أعظم وأجلّ من كل شيء.",
            targetCount = 33
        ),
        DhikrItem(
            id = "la_ilahe_illallah",
            arabic = "لَا إِلٰهَ إِلَّا اللَّهُ",
            turkishTitle = "Kelime-i Tevhid",
            englishTitle = "Kalimah Tawheed (La ilaha illAllah)",
            turkishPronunciation = "Lâ ilâhe illallâh",
            meaning = "Allah'tan başka hiçbir ilah yoktur.",
            englishMeaning = "There is no deity worthy of worship except Allah.",
            arabicMeaning = "لا معبود بحق إلا الله وحده لا شريك له.",
            targetCount = 100
        ),
        DhikrItem(
            id = "estagfirullah",
            arabic = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ",
            turkishTitle = "Estağfirullah",
            englishTitle = "Astaghfirullah",
            turkishPronunciation = "Estağfirullâhe'l-Azîm",
            meaning = "Yüce Allah'tan bağışlanma ve mağfiret dilerim.",
            englishMeaning = "I seek forgiveness and pardon from Allah, the Almighty.",
            arabicMeaning = "أطلب المغفرة والعفو من الله العظيم.",
            targetCount = 100
        ),
        DhikrItem(
            id = "salavat",
            arabic = "اللَّهُمَّ صَلِّ عَلَىٰ سَيِّدِنَا مُحَمَّدٍ",
            turkishTitle = "Salavat-ı Şerife",
            englishTitle = "Salawat upon the Prophet",
            turkishPronunciation = "Allâhümme salli alâ seyyidinâ Muhammed",
            meaning = "Ey Allah'ım! Efendimiz Hz. Muhammed'e salât ve selâm eyle.",
            englishMeaning = "O Allah! Send blessings and peace upon our Master Prophet Muhammad.",
            arabicMeaning = "اللهم أنزل رحمتك وبركاتك على نبينا محمد ﷺ.",
            targetCount = 100
        ),
        DhikrItem(
            id = "hasbunallah",
            arabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            turkishTitle = "Hasbünallah",
            englishTitle = "HasbunAllahu wa Ni'mal Wakeel",
            turkishPronunciation = "Hasbünallâhu ve ni'mel vekîl",
            meaning = "Allah bize yeter, O ne güzel vekildir.",
            englishMeaning = "Sufficient for us is Allah, and He is the best Disposer of affairs.",
            arabicMeaning = "يكفينا الله عز وجل وهو نعم الحافظ والمعين.",
            targetCount = 33
        )
    )
}

object DailyInspirations {
    val items = listOf(
        DailyInspiration(
            id = "v1",
            type = InspirationType.AYAT,
            arabicText = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            turkishText = "Bilesiniz ki, kalpler ancak Allah'ı anmakla huzur bulur.",
            englishText = "Unquestionably, by the remembrance of Allah do hearts find peace.",
            source = "Ra'd Suresi, 28. Ayet",
            englishSource = "Surah Ar-Ra'd, Verse 28",
            arabicSource = "سورة الرعد، الآية ٢٨"
        ),
        DailyInspiration(
            id = "v2",
            type = InspirationType.HADITH,
            arabicText = "إِنَّ أَحَبَّ الْأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ",
            turkishText = "Allah katında amellerin en sevimlisi, az da olsa devamlı olanıdır.",
            englishText = "The most beloved of deeds to Allah are those that are most consistent, even if they are small.",
            source = "Buhârî, Rikâk 18",
            englishSource = "Sahih al-Bukhari, Riqaq 18",
            arabicSource = "صحيح البخاري، الرقاق ١٨"
        ),
        DailyInspiration(
            id = "v3",
            type = InspirationType.DUA,
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            turkishText = "Rabbimiz! Bize dünyada da iyilik ver, ahirette de iyilik ver ve bizi cehennem azabından koru.",
            englishText = "Our Lord! Grant us good in this world and good in the Hereafter, and protect us from the punishment of the Fire.",
            source = "Bakara Suresi, 201. Ayet",
            englishSource = "Surah Al-Baqarah, Verse 201",
            arabicSource = "سورة البقرة، الآية ٢٠١"
        ),
        DailyInspiration(
            id = "v4",
            type = InspirationType.HADITH,
            arabicText = "الصَّلَاةُ عِمَادُ الدِّينِ",
            turkishText = "Namaz dinin direğidir.",
            englishText = "Prayer is the pillar of religion.",
            source = "Beyhakî, Şuabü'l-Îmân",
            englishSource = "Al-Bayhaqi, Shu'ab al-Iman",
            arabicSource = "البيهقي، شعب الإيمان"
        ),
        DailyInspiration(
            id = "v5",
            type = InspirationType.AYAT,
            arabicText = "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
            turkishText = "Öyleyse siz beni anın ki ben de sizi anayım. Bana şükredin, nankörlük etmeyin.",
            englishText = "So remember Me; I will remember you. And be grateful to Me and do not deny Me.",
            source = "Bakara Suresi, 152. Ayet",
            englishSource = "Surah Al-Baqarah, Verse 152",
            arabicSource = "سورة البقرة، الآية ١٥٢"
        )
    )

    fun getTodayInspiration(): DailyInspiration {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return items[dayOfYear % items.size]
    }
}
