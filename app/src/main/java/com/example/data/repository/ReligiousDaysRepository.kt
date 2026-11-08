package com.example.data.repository

import com.example.data.model.ReligiousEventType
import com.example.data.model.ReligiousHoliday
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object ReligiousDaysRepository {

    val allHolidays: List<ReligiousHoliday> = listOf(
        // ==================== 2026 DİNİ GÜNLER, KANDİLLER VE BAYRAMLAR ====================
        ReligiousHoliday(
            id = "2026_mirac",
            year = 2026, month = 1, day = 15, weekdayTr = "Perşembe",
            type = ReligiousEventType.KANDIL,
            titleTr = "Miraç Kandili",
            titleEn = "Lailat al-Miraj (Night of Ascension)",
            titleAr = "ليلة الإسراء والمعراج",
            hijriDateTr = "26 Recep 1447",
            hijriDateEn = "26 Rajab 1447",
            hijriDateAr = "٢٦ رجب ١٤٤٧",
            descriptionTr = "Peygamber Efendimiz'in (s.a.v.) Mescid-i Harâm'dan Mescid-i Aksâ'ya ve oradan ilahi huzura yükseldiği, beş vakit namazın farz kılındığı mübarek gecedir.",
            descriptionEn = "The blessed night when Prophet Muhammad (peace be upon him) ascended from Masjid al-Haram to Masjid al-Aqsa and the heavens, and the five daily prayers were enjoined.",
            descriptionAr = "الليلة المباركة التي أُسري فيها بالنبي ﷺ من المسجد الحرام إلى المسجد الأقصى وفرضت فيها الصلوات الخمس."
        ),
        ReligiousHoliday(
            id = "2026_berat",
            year = 2026, month = 2, day = 2, weekdayTr = "Pazartesi",
            type = ReligiousEventType.KANDIL,
            titleTr = "Berat Kandili",
            titleEn = "Mid-Sha'ban (Lailat al-Bara'ah)",
            titleAr = "ليلة النصف من شعبان (ليلة البراءة)",
            hijriDateTr = "14 Şaban 1447",
            hijriDateEn = "14 Sha'ban 1447",
            hijriDateAr = "١٤ شعبان ١٤٤٧",
            descriptionTr = "Günahlardan arınma, ilahi af ve mağfirete kavuşma müjdesi taşıyan, Ramazan-ı Şerif'in habercisi olan mübarek gecedir.",
            descriptionEn = "The sacred night of forgiveness and divine mercy in mid-Sha'ban, heralding the arrival of holy Ramadan.",
            descriptionAr = "ليلة المغفرة والعتق من النيران والتأهب لاستقبال شهر رمضان المبارك."
        ),
        ReligiousHoliday(
            id = "2026_ramazan_baslangic",
            year = 2026, month = 2, day = 19, weekdayTr = "Perşembe",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Ramazan-ı Şerif'in Başlangıcı",
            titleEn = "First Day of Ramadan",
            titleAr = "أول أيام شهر رمضان المبارك",
            hijriDateTr = "1 Ramazan 1447",
            hijriDateEn = "1 Ramadan 1447",
            hijriDateAr = "١ رمضان ١٤٤٧",
            descriptionTr = "On bir ayın sultanı, Kur'an, oruç, rahmet ve bereket ayı Ramazan-ı Şerif'in ilk günüdür.",
            descriptionEn = "The first day of the blessed month of Ramadan, the month of fasting, mercy, and the revelation of the Holy Quran.",
            descriptionAr = "بداية شهر الصيام والقيام وتلاوة القرآن والرحمة والغفران."
        ),
        ReligiousHoliday(
            id = "2026_kadir",
            year = 2026, month = 3, day = 16, weekdayTr = "Pazartesi",
            type = ReligiousEventType.KANDIL,
            titleTr = "Kadir Gecesi",
            titleEn = "Laylat al-Qadr (Night of Decree)",
            titleAr = "ليلة القدر المباركة",
            hijriDateTr = "26 Ramazan 1447",
            hijriDateEn = "26 Ramadan 1447",
            hijriDateAr = "٢٦ رمضان ١٤٤٧",
            descriptionTr = "Kur'an-ı Kerim'in indirildiği ve 'bin aydan daha hayırlı' olduğu bildirilen en faziletli gecedir.",
            descriptionEn = "The Night of Power in which the Quran was revealed, described in the Quran as better than a thousand months.",
            descriptionAr = "ليلة نزول القرآن الكريم وهي خير من ألف شهر، تتنزل فيها الملائكة والروح."
        ),
        ReligiousHoliday(
            id = "2026_ramazan_arefe",
            year = 2026, month = 3, day = 19, weekdayTr = "Perşembe",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Ramazan Bayramı Arefesi",
            titleEn = "Eve of Eid al-Fitr (Arafah)",
            titleAr = "وقفة عيد الفطر المبارك",
            hijriDateTr = "29 Ramazan 1447",
            hijriDateEn = "29 Ramadan 1447",
            hijriDateAr = "٢٩ رمضان ١٤٤٧",
            descriptionTr = "Ramazan ayının son günü ve Ramazan Bayramı'na kavuşmanın arifesidir.",
            descriptionEn = "The final day of fasting in Ramadan and the eve of Eid al-Fitr.",
            descriptionAr = "آخر أيام شهر رمضان المبارك واستقبال عيد الفطر السعيد."
        ),
        ReligiousHoliday(
            id = "2026_ramazan_bayrami_1",
            year = 2026, month = 3, day = 20, weekdayTr = "Cuma",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Ramazan Bayramı (1. Gün)",
            titleEn = "Eid al-Fitr (Day 1)",
            titleAr = "عيد الفطر المبارك (اليوم الأول)",
            hijriDateTr = "1 Şevval 1447",
            hijriDateEn = "1 Shawwal 1447",
            hijriDateAr = "١ شوال ١٤٤٧",
            descriptionTr = "Oruç ibadetinin tamamlanmasının ardından bayram namazı, sıla-i rahim ve sevinçle idrak edilen Ramazan Bayramı'nın ilk günüdür.",
            descriptionEn = "The first day of Eid al-Fitr, celebrated with Eid prayer, charity (Fitrah), and visiting family after completing Ramadan.",
            descriptionAr = "أول أيام عيد الفطر السعيد، يوم الفرح وصلة الأرحام وصلاة العيد."
        ),
        ReligiousHoliday(
            id = "2026_ramazan_bayrami_2",
            year = 2026, month = 3, day = 21, weekdayTr = "Cumartesi",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Ramazan Bayramı (2. Gün)",
            titleEn = "Eid al-Fitr (Day 2)",
            titleAr = "عيد الفطر المبارك (اليوم الثاني)",
            hijriDateTr = "2 Şevval 1447",
            hijriDateEn = "2 Shawwal 1447",
            hijriDateAr = "٢ شوال ١٤٤٧",
            descriptionTr = "Ramazan Bayramı'nın ikinci günüdür; akraba, komşu ve büyüklerin ziyaret edildiği mübarek bayram günüdür.",
            descriptionEn = "The second day of Eid al-Fitr, dedicated to visiting relatives, neighbors, and sharing joy.",
            descriptionAr = "ثاني أيام عيد الفطر المبارك وصلة الأرحام."
        ),
        ReligiousHoliday(
            id = "2026_ramazan_bayrami_3",
            year = 2026, month = 3, day = 22, weekdayTr = "Pazar",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Ramazan Bayramı (3. Gün)",
            titleEn = "Eid al-Fitr (Day 3)",
            titleAr = "عيد الفطر المبارك (اليوم الثالث)",
            hijriDateTr = "3 Şevval 1447",
            hijriDateEn = "3 Shawwal 1447",
            hijriDateAr = "٣ شوال ١٤٤٧",
            descriptionTr = "Ramazan Bayramı'nın üçüncü ve son günüdür.",
            descriptionEn = "The third and final day of Eid al-Fitr.",
            descriptionAr = "ثالث أيام عيد الفطر السعيد."
        ),
        ReligiousHoliday(
            id = "2026_kurban_arefe",
            year = 2026, month = 5, day = 26, weekdayTr = "Salı",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Kurban Bayramı Arefesi (Arafat Günü)",
            titleEn = "Day of Arafah (Eve of Eid al-Adha)",
            titleAr = "يوم عرفة المبارك",
            hijriDateTr = "9 Zilhicce 1447",
            hijriDateEn = "9 Dhu al-Hijjah 1447",
            hijriDateAr = "٩ ذو الحجة ١٤٤٧",
            descriptionTr = "Sabah namazından itibaren teşrik tekbirlerinin başladığı, hacıların Arafat'ta vakfe yaptığı ve orucunun iki yıllık günaha kefaret olduğu mübarek gündür.",
            descriptionEn = "The sacred Day of Arafah when pilgrims stand in prayer at Mount Arafat and Tashreeq Takbirs begin after Fajr prayer.",
            descriptionAr = "يوم الحج الأكبر ووقوف الحجاج بعرفة وبدء تكبيرات التشريق من فجر يوم عرفة."
        ),
        ReligiousHoliday(
            id = "2026_kurban_bayrami_1",
            year = 2026, month = 5, day = 27, weekdayTr = "Çarşamba",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Kurban Bayramı (1. Gün)",
            titleEn = "Eid al-Adha (Day 1)",
            titleAr = "عيد الأضحى المبارك (اليوم الأول)",
            hijriDateTr = "10 Zilhicce 1447",
            hijriDateEn = "10 Dhu al-Hijjah 1447",
            hijriDateAr = "١٠ ذو الحجة ١٤٤٧",
            descriptionTr = "Bayram namazı, kurban ibadeti ve ihtiyaçaplarla paylaşma heyecanıyla idrak edilen Kurban Bayramı'nın birinci günüdür.",
            descriptionEn = "The first day of Eid al-Adha, marked by Eid prayer, Qurbani sacrifice, and sharing with the needy.",
            descriptionAr = "أول أيام عيد الأضحى المبارك، يوم النحر وصلاة العيد والتكبير."
        ),
        ReligiousHoliday(
            id = "2026_kurban_bayrami_2",
            year = 2026, month = 5, day = 28, weekdayTr = "Perşembe",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Kurban Bayramı (2. Gün)",
            titleEn = "Eid al-Adha (Day 2)",
            titleAr = "عيد الأضحى المبارك (اليوم الثاني)",
            hijriDateTr = "11 Zilhicce 1447",
            hijriDateEn = "11 Dhu al-Hijjah 1447",
            hijriDateAr = "١١ ذو الحجة ١٤٤٧",
            descriptionTr = "Kurban Bayramı'nın ikinci günü ve teşrik günlerindendir.",
            descriptionEn = "The second day of Eid al-Adha and the days of Tashreeq.",
            descriptionAr = "ثاني أيام عيد الأضحى المبارك وأيام التشريق."
        ),
        ReligiousHoliday(
            id = "2026_kurban_bayrami_3",
            year = 2026, month = 5, day = 29, weekdayTr = "Cuma",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Kurban Bayramı (3. Gün)",
            titleEn = "Eid al-Adha (Day 3)",
            titleAr = "عيد الأضحى المبارك (اليوم الثالث)",
            hijriDateTr = "12 Zilhicce 1447",
            hijriDateEn = "12 Dhu al-Hijjah 1447",
            hijriDateAr = "١٢ ذو الحجة ١٤٤٧",
            descriptionTr = "Kurban Bayramı'nın üçüncü günü ve teşrik tekbirlerinin devam ettiği mübarek gündür.",
            descriptionEn = "The third day of Eid al-Adha.",
            descriptionAr = "ثالث أيام عيد الأضحى المبارك."
        ),
        ReligiousHoliday(
            id = "2026_kurban_bayrami_4",
            year = 2026, month = 5, day = 30, weekdayTr = "Cumartesi",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Kurban Bayramı (4. Gün)",
            titleEn = "Eid al-Adha (Day 4)",
            titleAr = "عيد الأضحى المبارك (اليوم الرابع)",
            hijriDateTr = "13 Zilhicce 1447",
            hijriDateEn = "13 Dhu al-Hijjah 1447",
            hijriDateAr = "١٣ ذو الحجة ١٤٤٧",
            descriptionTr = "Kurban Bayramı'nın dördüncü günüdür; ikindi namazıyla birlikte teşrik tekbirleri sona erer.",
            descriptionEn = "The fourth and final day of Eid al-Adha; Tashreeq Takbirs conclude with the Asr prayer.",
            descriptionAr = "رابع أيام عيد الأضحى المبارك وتختتم فيه تكبيرات التشريق عصر هذا اليوم."
        ),
        ReligiousHoliday(
            id = "2026_hicri_yilbasi",
            year = 2026, month = 6, day = 16, weekdayTr = "Salı",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Hicrî Yılbaşı (1448)",
            titleEn = "Islamic New Year (1448 AH)",
            titleAr = "رأس السنة الهجرية (١٤٤٨ هـ)",
            hijriDateTr = "1 Muharrem 1448",
            hijriDateEn = "1 Muharram 1448",
            hijriDateAr = "١ محرم ١٤٤٨",
            descriptionTr = "Peygamber Efendimiz'in (s.a.v.) Mekke'den Medine'ye hicretini esas alan Hicrî 1448 yılının ilk günü ve Muharrem ayının başlangıcıdır.",
            descriptionEn = "The first day of Muharram and the beginning of the 1448 Hijri year, commemorating the Prophet's Hijrah from Mecca to Medina.",
            descriptionAr = "غرة شهر الله المحرم وبداية العام الهجري الجديد ١٤٤٨ هـ."
        ),
        ReligiousHoliday(
            id = "2026_asure",
            year = 2026, month = 6, day = 25, weekdayTr = "Perşembe",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Aşûre Günü",
            titleEn = "Day of Ashura",
            titleAr = "يوم عاشوراء المبارك",
            hijriDateTr = "10 Muharrem 1448",
            hijriDateEn = "10 Muharram 1448",
            hijriDateAr = "١٠ محرم ١٤٤٨",
            descriptionTr = "Muharrem ayının 10. günü; peygamberler tarihinde pek çok ilahi kurtuluşun gerçekleştiği, oruç ve paylaşma ile ihya edilen mübarek gündür.",
            descriptionEn = "The 10th day of Muharram, observed with voluntary fasting, gratitude, and sharing Ashura.",
            descriptionAr = "اليوم العاشر من شهر محرم، يوم مبارك يُستحب صيامه مع يوم قبله أو يوم بعده."
        ),
        ReligiousHoliday(
            id = "2026_mevlid",
            year = 2026, month = 8, day = 24, weekdayTr = "Pazartesi",
            type = ReligiousEventType.KANDIL,
            titleTr = "Mevlid Kandili",
            titleEn = "Mawlid al-Nabi (Birth of the Prophet)",
            titleAr = "ذكرى المولد النبوي الشريف",
            hijriDateTr = "11 Rebîülevvel 1448",
            hijriDateEn = "11 Rabi' al-Awwal 1448",
            hijriDateAr = "١١ ربيع الأول ١٤٤٨",
            descriptionTr = "Âlemlere rahmet olarak gönderilen Peygamber Efendimiz Hz. Muhammed'in (s.a.v.) dünyayı teşrif ettiği kutlu gecedir.",
            descriptionEn = "The blessed night commemorating the birth of Prophet Muhammad (peace be upon him), sent as a mercy to all worlds.",
            descriptionAr = "الليلة المباركة التي وُلد فيها سيدنا محمد ﷺ رحمةً للعالمين."
        ),
        ReligiousHoliday(
            id = "2026_uc_aylar_ve_regaib",
            year = 2026, month = 12, day = 10, weekdayTr = "Perşembe",
            type = ReligiousEventType.KANDIL,
            titleTr = "Üç Ayların Başlangıcı & Regâib Kandili",
            titleEn = "Three Holy Months & Laylat al-Raghaib",
            titleAr = "بداية الأشهر الحرم وليلة الرغائب",
            hijriDateTr = "1 Recep 1448",
            hijriDateEn = "1 Rajab 1448",
            hijriDateAr = "١ رجب ١٤٤٨",
            descriptionTr = "Recep, Şaban ve Ramazan aylarını kapsayan mübarek Üç Ayların başlangıcı ve Recep ayının ilk Cuma gecesi olan Regâib Kandili'dir.",
            descriptionEn = "Marks the beginning of the three blessed months (Rajab, Sha'ban, Ramadan) and Laylat al-Raghaib, the first Friday night of Rajab.",
            descriptionAr = "غرة شهر رجب المبارك وبداية الأشهر الثلاثة المباركة وليلة الرغائب (أول ليلة جمعة من رجب)."
        ),

        // ==================== 2027 DİNİ GÜNLER, KANDİLLER VE BAYRAMLAR ====================
        ReligiousHoliday(
            id = "2027_mirac",
            year = 2027, month = 1, day = 4, weekdayTr = "Pazartesi",
            type = ReligiousEventType.KANDIL,
            titleTr = "Miraç Kandili",
            titleEn = "Lailat al-Miraj (Night of Ascension)",
            titleAr = "ليلة الإسراء والمعراج",
            hijriDateTr = "26 Recep 1448",
            hijriDateEn = "26 Rajab 1448",
            hijriDateAr = "٢٦ رجب ١٤٤٨",
            descriptionTr = "Peygamber Efendimiz'in (s.a.v.) Mescid-i Harâm'dan Mescid-i Aksâ'ya ve ilahi huzura yükseldiği, beş vakit namazın farz kılındığı mübarek gecedir.",
            descriptionEn = "The blessed night of the Prophet's miraculous Night Journey and Ascension, when the five daily prayers were prescribed.",
            descriptionAr = "ذكرى معجزة الإسراء والمعراج وفرض الصلوات الخمس."
        ),
        ReligiousHoliday(
            id = "2027_berat",
            year = 2027, month = 1, day = 22, weekdayTr = "Cuma",
            type = ReligiousEventType.KANDIL,
            titleTr = "Berat Kandili",
            titleEn = "Mid-Sha'ban (Lailat al-Bara'ah)",
            titleAr = "ليلة النصف من شعبان (ليلة البراءة)",
            hijriDateTr = "14 Şaban 1448",
            hijriDateEn = "14 Sha'ban 1448",
            hijriDateAr = "١٤ شعبان ١٤٤٨",
            descriptionTr = "Şaban ayının ortasında ilahi af, mağfiret ve rızıkların takdir edildiği mübarek Berat gecesidir.",
            descriptionEn = "The blessed night of forgiveness and mercy in the middle of Sha'ban.",
            descriptionAr = "ليلة المغفرة والرحمة في النصف من شعبان."
        ),
        ReligiousHoliday(
            id = "2027_ramazan_baslangic",
            year = 2027, month = 2, day = 8, weekdayTr = "Pazartesi",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Ramazan-ı Şerif'in Başlangıcı",
            titleEn = "First Day of Ramadan",
            titleAr = "أول أيام شهر رمضان المبارك",
            hijriDateTr = "1 Ramazan 1448",
            hijriDateEn = "1 Ramadan 1448",
            hijriDateAr = "١ رمضان ١٤٤٨",
            descriptionTr = "Oruç, Kur'an, teravih ve infak ayı olan mübarek Ramazan-ı Şerif'in ilk günüdür.",
            descriptionEn = "The first day of the holy month of Ramadan.",
            descriptionAr = "أول أيام شهر رمضان المبارك."
        ),
        ReligiousHoliday(
            id = "2027_kadir",
            year = 2027, month = 3, day = 5, weekdayTr = "Cuma",
            type = ReligiousEventType.KANDIL,
            titleTr = "Kadir Gecesi",
            titleEn = "Laylat al-Qadr (Night of Decree)",
            titleAr = "ليلة القدر المباركة",
            hijriDateTr = "26 Ramazan 1448",
            hijriDateEn = "26 Ramadan 1448",
            hijriDateAr = "٢٦ رمضان ١٤٤٨",
            descriptionTr = "Kur'an-ı Kerim'in indirildiği ve bin aydan daha hayırlı olan mübarek gecedir.",
            descriptionEn = "The Night of Decree, better than a thousand months.",
            descriptionAr = "ليلة القدر خير من ألف شهر."
        ),
        ReligiousHoliday(
            id = "2027_ramazan_arefe",
            year = 2027, month = 3, day = 8, weekdayTr = "Pazartesi",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Ramazan Bayramı Arefesi",
            titleEn = "Eve of Eid al-Fitr (Arafah)",
            titleAr = "وقفة عيد الفطر المبارك",
            hijriDateTr = "29 Ramazan 1448",
            hijriDateEn = "29 Ramadan 1448",
            hijriDateAr = "٢٩ رمضان ١٤٤٨",
            descriptionTr = "Ramazan-ı Şerif'in son günü ve bayram arifesidir.",
            descriptionEn = "The final day of Ramadan and eve of Eid al-Fitr.",
            descriptionAr = "آخر أيام شهر رمضان المبارك."
        ),
        ReligiousHoliday(
            id = "2027_ramazan_bayrami_1",
            year = 2027, month = 3, day = 9, weekdayTr = "Salı",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Ramazan Bayramı (1. Gün)",
            titleEn = "Eid al-Fitr (Day 1)",
            titleAr = "عيد الفطر المبارك (اليوم الأول)",
            hijriDateTr = "1 Şevval 1448",
            hijriDateEn = "1 Shawwal 1448",
            hijriDateAr = "١ شوال ١٤٤٨",
            descriptionTr = "Ramazan Bayramı'nın birinci günüdür.",
            descriptionEn = "The first day of Eid al-Fitr.",
            descriptionAr = "أول أيام عيد الفطر المبارك."
        ),
        ReligiousHoliday(
            id = "2027_ramazan_bayrami_2",
            year = 2027, month = 3, day = 10, weekdayTr = "Çarşamba",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Ramazan Bayramı (2. Gün)",
            titleEn = "Eid al-Fitr (Day 2)",
            titleAr = "عيد الفطر المبارك (اليوم الثاني)",
            hijriDateTr = "2 Şevval 1448",
            hijriDateEn = "2 Shawwal 1448",
            hijriDateAr = "٢ شوال ١٤٤٨",
            descriptionTr = "Ramazan Bayramı'nın ikinci günüdür.",
            descriptionEn = "The second day of Eid al-Fitr.",
            descriptionAr = "ثاني أيام عيد الفطر المبارك."
        ),
        ReligiousHoliday(
            id = "2027_ramazan_bayrami_3",
            year = 2027, month = 3, day = 11, weekdayTr = "Perşembe",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Ramazan Bayramı (3. Gün)",
            titleEn = "Eid al-Fitr (Day 3)",
            titleAr = "عيد الفطر المبارك (اليوم الثالث)",
            hijriDateTr = "3 Şevval 1448",
            hijriDateEn = "3 Shawwal 1448",
            hijriDateAr = "٣ شوال ١٤٤٨",
            descriptionTr = "Ramazan Bayramı'nın üçüncü günüdür.",
            descriptionEn = "The third day of Eid al-Fitr.",
            descriptionAr = "ثالث أيام عيد الفطر المبارك."
        ),
        ReligiousHoliday(
            id = "2027_kurban_arefe",
            year = 2027, month = 5, day = 15, weekdayTr = "Cumartesi",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Kurban Bayramı Arefesi (Arafat Günü)",
            titleEn = "Day of Arafah (Eve of Eid al-Adha)",
            titleAr = "يوم عرفة المبارك",
            hijriDateTr = "9 Zilhicce 1448",
            hijriDateEn = "9 Dhu al-Hijjah 1448",
            hijriDateAr = "٩ ذو الحجة ١٤٤٨",
            descriptionTr = "Kurban Bayramı Arefesi ve teşrik tekbirlerinin başladığı mübarek Arafat günüdür.",
            descriptionEn = "The sacred Day of Arafah and eve of Eid al-Adha.",
            descriptionAr = "يوم عرفة المبارك وبدء تكبيرات التشريق."
        ),
        ReligiousHoliday(
            id = "2027_kurban_bayrami_1",
            year = 2027, month = 5, day = 16, weekdayTr = "Pazar",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Kurban Bayramı (1. Gün)",
            titleEn = "Eid al-Adha (Day 1)",
            titleAr = "عيد الأضحى المبارك (اليوم الأول)",
            hijriDateTr = "10 Zilhicce 1448",
            hijriDateEn = "10 Dhu al-Hijjah 1448",
            hijriDateAr = "١٠ ذو الحجة ١٤٤٨",
            descriptionTr = "Kurban Bayramı'nın birinci günüdür.",
            descriptionEn = "The first day of Eid al-Adha.",
            descriptionAr = "أول أيام عيد الأضحى المبارك."
        ),
        ReligiousHoliday(
            id = "2027_kurban_bayrami_2",
            year = 2027, month = 5, day = 17, weekdayTr = "Pazartesi",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Kurban Bayramı (2. Gün)",
            titleEn = "Eid al-Adha (Day 2)",
            titleAr = "عيد الأضحى المبارك (اليوم الثاني)",
            hijriDateTr = "11 Zilhicce 1448",
            hijriDateEn = "11 Dhu al-Hijjah 1448",
            hijriDateAr = "١١ ذو الحجة ١٤٤٨",
            descriptionTr = "Kurban Bayramı'nın ikinci günüdür.",
            descriptionEn = "The second day of Eid al-Adha.",
            descriptionAr = "ثاني أيام عيد الأضحى المبارك."
        ),
        ReligiousHoliday(
            id = "2027_kurban_bayrami_3",
            year = 2027, month = 5, day = 18, weekdayTr = "Salı",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Kurban Bayramı (3. Gün)",
            titleEn = "Eid al-Adha (Day 3)",
            titleAr = "عيد الأضحى المبارك (اليوم الثالث)",
            hijriDateTr = "12 Zilhicce 1448",
            hijriDateEn = "12 Dhu al-Hijjah 1448",
            hijriDateAr = "١٢ ذو الحجة ١٤٤٨",
            descriptionTr = "Kurban Bayramı'nın üçüncü günüdür.",
            descriptionEn = "The third day of Eid al-Adha.",
            descriptionAr = "ثالث أيام عيد الأضحى المبارك."
        ),
        ReligiousHoliday(
            id = "2027_kurban_bayrami_4",
            year = 2027, month = 5, day = 19, weekdayTr = "Çarşamba",
            type = ReligiousEventType.BAYRAM,
            titleTr = "Kurban Bayramı (4. Gün)",
            titleEn = "Eid al-Adha (Day 4)",
            titleAr = "عيد الأضحى المبارك (اليوم الرابع)",
            hijriDateTr = "13 Zilhicce 1448",
            hijriDateEn = "13 Dhu al-Hijjah 1448",
            hijriDateAr = "١٣ ذو الحجة ١٤٤٨",
            descriptionTr = "Kurban Bayramı'nın dördüncü günüdür.",
            descriptionEn = "The fourth day of Eid al-Adha.",
            descriptionAr = "رابع أيام عيد الأضحى المبارك."
        ),
        ReligiousHoliday(
            id = "2027_hicri_yilbasi",
            year = 2027, month = 6, day = 6, weekdayTr = "Pazar",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Hicrî Yılbaşı (1449)",
            titleEn = "Islamic New Year (1449 AH)",
            titleAr = "رأس السنة الهجرية (١٤٤٩ هـ)",
            hijriDateTr = "1 Muharrem 1449",
            hijriDateEn = "1 Muharram 1449",
            hijriDateAr = "١ محرم ١٤٤٩",
            descriptionTr = "Hicrî 1449 yılının ve Muharrem ayının birinci günüdür.",
            descriptionEn = "The first day of Muharram 1449 AH.",
            descriptionAr = "غرة شهر محرم وبداية العام الهجري ١٤٤٩ هـ."
        ),
        ReligiousHoliday(
            id = "2027_asure",
            year = 2027, month = 6, day = 15, weekdayTr = "Salı",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Aşûre Günü",
            titleEn = "Day of Ashura",
            titleAr = "يوم عاشوراء المبارك",
            hijriDateTr = "10 Muharrem 1449",
            hijriDateEn = "10 Muharram 1449",
            hijriDateAr = "١٠ محرم ١٤٤٩",
            descriptionTr = "Muharrem ayının 10. günü olan mübarek Aşûre günüdür.",
            descriptionEn = "The 10th day of Muharram, the blessed Day of Ashura.",
            descriptionAr = "اليوم العاشر من شهر الله المحرم."
        ),
        ReligiousHoliday(
            id = "2027_mevlid",
            year = 2027, month = 8, day = 14, weekdayTr = "Cumartesi",
            type = ReligiousEventType.KANDIL,
            titleTr = "Mevlid Kandili",
            titleEn = "Mawlid al-Nabi (Birth of the Prophet)",
            titleAr = "ذكرى المولد النبوي الشريف",
            hijriDateTr = "11 Rebîülevvel 1449",
            hijriDateEn = "11 Rabi' al-Awwal 1449",
            hijriDateAr = "١١ ربيع الأول ١٤٤٩",
            descriptionTr = "Peygamber Efendimiz'in (s.a.v.) dünyayı şereflendirdiği Mevlid Kandili gecesidir.",
            descriptionEn = "The blessed night commemorating the birth of Prophet Muhammad (peace be upon him).",
            descriptionAr = "ذكرى مولد سيد الكائنات محمد ﷺ."
        ),
        ReligiousHoliday(
            id = "2027_uc_aylar",
            year = 2027, month = 11, day = 29, weekdayTr = "Pazartesi",
            type = ReligiousEventType.SPECIAL_DAY,
            titleTr = "Üç Ayların Başlangıcı (Recep Ayı)",
            titleEn = "Beginning of the Three Holy Months",
            titleAr = "بداية الأشهر الثلاثة المباركة (رجب)",
            hijriDateTr = "1 Recep 1449",
            hijriDateEn = "1 Rajab 1449",
            hijriDateAr = "١ رجب ١٤٤٩",
            descriptionTr = "Mübarek Recep, Şaban ve Ramazan aylarının başlangıcıdır.",
            descriptionEn = "The first day of Rajab, marking the start of the three blessed months.",
            descriptionAr = "غرة شهر رجب الفرد وبداية الأشهر المباركة."
        ),
        ReligiousHoliday(
            id = "2027_regaib",
            year = 2027, month = 12, day = 2, weekdayTr = "Perşembe",
            type = ReligiousEventType.KANDIL,
            titleTr = "Regâib Kandili",
            titleEn = "Laylat al-Raghaib",
            titleAr = "ليلة الرغائب المباركة",
            hijriDateTr = "4 Recep 1449",
            hijriDateEn = "4 Rajab 1449",
            hijriDateAr = "٤ رجب ١٤٤٩",
            descriptionTr = "Recep ayının ilk Cuma gecesi olan rahmet ve mağfiret gecesi Regâib Kandili'dir.",
            descriptionEn = "The first Friday night of Rajab, a night of divine grace and supplication.",
            descriptionAr = "أول ليلة جمعة من شهر رجب المبارك."
        ),
        ReligiousHoliday(
            id = "2027_mirac_2",
            year = 2027, month = 12, day = 24, weekdayTr = "Cuma",
            type = ReligiousEventType.KANDIL,
            titleTr = "Miraç Kandili",
            titleEn = "Lailat al-Miraj (Night of Ascension)",
            titleAr = "ليلة الإسراء والمعراج",
            hijriDateTr = "26 Recep 1449",
            hijriDateEn = "26 Rajab 1449",
            hijriDateAr = "٢٦ رجب ١٤٤٩",
            descriptionTr = "Peygamber Efendimiz'in (s.a.v.) ilahi huzura yükseldiği mübarek Miraç gecesidir.",
            descriptionEn = "The blessed Night of Ascension (Isra and Mi'raj).",
            descriptionAr = "ليلة الإسراء والمعراج المباركة."
        )
    )

    fun getHolidaysForYear(year: Int): List<ReligiousHoliday> {
        return allHolidays.filter { it.year == year }
    }

    fun getUpcomingAndCurrentYearHolidays(nowMillis: Long = System.currentTimeMillis()): List<ReligiousHoliday> {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale("tr", "TR")).apply {
            timeInMillis = nowMillis
        }
        val currentYear = cal.get(Calendar.YEAR)
        return allHolidays.filter { it.year == currentYear || it.year == currentYear + 1 }
    }

    fun findHolidayMatchingGregorianText(gregorianDateText: String): ReligiousHoliday? {
        // gregorianDateText format: "10 Aralık 2026 Perşembe"
        val monthNamesTr = arrayOf(
            "", "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        )
        return allHolidays.find { h ->
            val prefix = "${h.day} ${monthNamesTr[h.month]} ${h.year}"
            gregorianDateText.startsWith(prefix)
        }
    }
}
