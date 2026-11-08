package com.example.data.repository

import com.example.data.model.AppLanguage
import com.example.data.model.PrayerType

object AppStrings {

    fun get(lang: AppLanguage): Strings = when (lang) {
        AppLanguage.TURKISH -> TurkishStrings
        AppLanguage.ENGLISH -> EnglishStrings
        AppLanguage.ARABIC -> ArabicStrings
    }

    fun prayerName(type: PrayerType, lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> when (type) {
            PrayerType.IMSAK -> "İmsak"
            PrayerType.GUNES -> "Güneş"
            PrayerType.OGLE -> "Öğle"
            PrayerType.IKINDI -> "İkindi"
            PrayerType.AKSAM -> "Akşam"
            PrayerType.YATSI -> "Yatsı"
        }
        AppLanguage.ENGLISH -> when (type) {
            PrayerType.IMSAK -> "Fajr"
            PrayerType.GUNES -> "Sunrise"
            PrayerType.OGLE -> "Dhuhr"
            PrayerType.IKINDI -> "Asr"
            PrayerType.AKSAM -> "Maghrib"
            PrayerType.YATSI -> "Isha"
        }
        AppLanguage.ARABIC -> when (type) {
            PrayerType.IMSAK -> "الفجر"
            PrayerType.GUNES -> "الشروق"
            PrayerType.OGLE -> "الظهر"
            PrayerType.IKINDI -> "العصر"
            PrayerType.AKSAM -> "المغرب"
            PrayerType.YATSI -> "العشاء"
        }
    }

    fun shortPrayerName(type: PrayerType, lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> when (type) {
            PrayerType.IMSAK -> "İms"
            PrayerType.GUNES -> "Güneş"
            PrayerType.OGLE -> "Öğle"
            PrayerType.IKINDI -> "İkindi"
            PrayerType.AKSAM -> "Akşam"
            PrayerType.YATSI -> "Yatsı"
        }
        AppLanguage.ENGLISH -> when (type) {
            PrayerType.IMSAK -> "Fajr"
            PrayerType.GUNES -> "Sun"
            PrayerType.OGLE -> "Dhuhr"
            PrayerType.IKINDI -> "Asr"
            PrayerType.AKSAM -> "Magh"
            PrayerType.YATSI -> "Isha"
        }
        AppLanguage.ARABIC -> when (type) {
            PrayerType.IMSAK -> "فجر"
            PrayerType.GUNES -> "شروق"
            PrayerType.OGLE -> "ظهر"
            PrayerType.IKINDI -> "عصر"
            PrayerType.AKSAM -> "مغرب"
            PrayerType.YATSI -> "عشاء"
        }
    }

    interface Strings {
        val appName: String

        // Navigation Tabs
        val tabVakitler: String
        val tabKuran: String
        val tabDualar: String
        val tabKible: String
        val tabZikirmatik: String
        val tabAyarlar: String
        val tabTakvim: String

        // Prayer Home
        val remainingTime: String
        val nextPrayer: String
        val currentPrayer: String
        val elapsed: String
        val prayerTimeArrived: String
        val now: String
        val hours: String
        val minutes: String
        val seconds: String
        val citySelect: String
        val searchCity: String
        val gpsLocated: String
        val gpsRequest: String
        val verseOfTheDay: String
        val hadithOfTheDay: String
        val duaOfTheDay: String
        val share: String
        val copied: String
        val todaysPrayerTimes: String
        val diyanetCalendar: String
        val playingNow: String
        val today: String
        val friday: String
        val selectedLabel: String

        // Qibla Compass & Nearby Mosques
        val qiblaTitle: String
        val facingQibla: String
        val turnDevice: String
        val distanceToKaaba: String
        val qiblaAngle: String
        val compassAngle: String
        val calibrateCompass: String
        val testMode: String
        val degrees: String
        val nearbyMosques: String
        val nearbyMosquesSubtitle: String
        val searchMosquesNearMe: String
        val directions: String
        val openInGoogleMaps: String
        val kmAway: String
        val metersAway: String
        val compassTab: String
        val mosquesTab: String

        // Dhikr / Tasbih
        val tasbihTitle: String
        val totalDhikr: String
        val target: String
        val reset: String
        val resetConfirmTitle: String
        val resetConfirmDesc: String
        val cancel: String
        val confirm: String
        val vibration: String
        val tapCount: String

        // Duas & Hadiths
        val duasHadithsTitle: String
        val duasHadithsSubtitle: String
        val duasTab: String
        val hadithsTab: String
        val namazDuas: String
        val dailyDuas: String
        val allDuas: String
        val allHadiths: String
        val searchDua: String
        val searchHadith: String
        val pronunciation: String
        val meaning: String
        val showMeaning: String
        val hideMeaning: String
        val listenArabic: String
        val listenMeaning: String
        val loading: String
        val arabicTextLabel: String
        val virtuesAndBenefits: String
        val source: String
        val whereRecited: String

        // Quran
        val quranTitle: String
        val quranSubtitle: String
        val quranBadge114: String
        val surahsButton: String
        val bismillahMeaning: String
        val listenAyah: String
        val resumeBookmark: String
        val continueReading: String
        val searchSurah: String
        val filterAllSurahs: String
        val filterMeccan: String
        val filterMedinan: String
        val filterJuz: String
        val filterPopular: String
        val ayah: String
        val juz: String
        val open: String
        val fullScreen: String
        val exitFullScreen: String
        val menu: String
        val close: String
        val fontSize: String
        val listenSurah: String
        val pause: String
        val stop: String
        val repeat: String
        val bookmark: String
        val bookmarkedToast: String
        val copy: String
        val copyToast: String
        val previousSurah: String
        val nextSurah: String
        val modeArabicAndMeal: String
        val modeArabicAndPronunciation: String
        val modeAllInOne: String
        val modeOnlyArabic: String
        val modeOnlyMeal: String
        val pronunciationLabel: String

        // Audio & Background Playback settings
        val alertSoundTypeTitle: String
        val soundTypeAdhan: String
        val soundTypeMelody: String
        val soundTypeVibrationOnly: String
        val soundVolumeTitle: String
        val playWhenScreenOffTitle: String
        val playWhenScreenOffSubtitle: String
        val soundTestTitle: String
        val testSoundButton: String
        val backgroundPlaybackPanelTitle: String
        val backgroundPlaybackPanelSubtitle: String
        val silentLabel: String
        val backgroundPermissionActiveBanner: String

        // Settings
        val settingsTitle: String
        val languageTitle: String
        val languageSubtitle: String
        val cityLocationTitle: String
        val gpsTitle: String
        val gpsSubtitle: String
        val temkinTitle: String
        val temkinSubtitle: String
        val minuteUnit: String
        val notificationsTitle: String
        val notificationsSubtitle: String
        val testNotificationButton: String
        val prayerSoundsTitle: String
        val batteryOptimizationTitle: String
        val batteryOptimizationDesc: String
        val darkTheme: String
        val lightTheme: String
        val calendarTitle: String
        val date: String
        val day: String
        val hijri: String
        val monthlyPrayersTab: String
        val religiousDaysTab: String
        val upcomingReligiousDay: String
        val filterUpcomingHolidays: String
        val filterAllHolidays2026: String
        val filterAllHolidays2027: String
        val daysRemaining: String
        val tomorrowLabel: String
        val passedLabel: String
        val setHolidayAlarm: String
        val alarmActiveLabel: String
        val holidayAlarmDialogTitle: String
        val holidayAlarmTimeLabel: String
        val holidayAlarmTypeLabel: String
        val testHolidayAlertNow: String
        val saveAlarmButton: String
        val removeAlarmButton: String
        val holidayAlarmSavedToast: String
        val holidayAlarmRemovedToast: String
        val shareAndRecommendTitle: String
        val shareAndRecommendSubtitle: String
        val shareAndRecommendButton: String
        val shareAppMessage: String
        val producerSectionTitle: String
        val producerStatement: String
        val producerSubtitle: String
    }

    object TurkishStrings : Strings {
        override val appName = "Ezan Vakti"
        override val tabVakitler = "Vakitler"
        override val tabKuran = "Kur'an"
        override val tabDualar = "Dualar"
        override val tabKible = "Kıble"
        override val tabZikirmatik = "Zikir"
        override val tabAyarlar = "Ayarlar"
        override val tabTakvim = "Takvim"

        override val remainingTime = "Kalan Süre"
        override val nextPrayer = "Sonraki Vakit"
        override val currentPrayer = "Şu Anki Vakit"
        override val elapsed = "geçti"
        override val prayerTimeArrived = "Vakit Girdi"
        override val now = "Şimdi"
        override val hours = "sa"
        override val minutes = "dk"
        override val seconds = "sn"
        override val citySelect = "Şehir Seçin"
        override val searchCity = "Şehir ara (Örn: İstanbul, 34)..."
        override val gpsLocated = "GPS Konumu"
        override val gpsRequest = "GPS ile Otomatik Bul"
        override val verseOfTheDay = "Günün Âyet-i Kerîmesi"
        override val hadithOfTheDay = "Günün Hadis-i Şerifi"
        override val duaOfTheDay = "Günün Duâsı"
        override val share = "Paylaş"
        override val copied = "Panoya kopyalandı"
        override val todaysPrayerTimes = "Günün Ezan Vakitleri"
        override val diyanetCalendar = "Diyanet Takvimi"
        override val playingNow = "Çalıyor"
        override val today = "Bugün"
        override val friday = "Cuma"
        override val selectedLabel = "Seçili"

        override val qiblaTitle = "Kıble Pusulası"
        override val facingQibla = "Kıble Yönündesiniz!"
        override val turnDevice = "Telefonu çevirin"
        override val distanceToKaaba = "Kâbe'ye Uzaklık"
        override val qiblaAngle = "Kıble Açısı"
        override val compassAngle = "Pusula Açısı"
        override val calibrateCompass = "Pusulayı kalibre etmek için telefonu 8 çizecek şekilde sallayın"
        override val testMode = "Simülasyon Modu"
        override val degrees = "°"
        override val nearbyMosques = "Yakındaki Camiler"
        override val nearbyMosquesSubtitle = "Konumunuza en yakın cami ve mescitler"
        override val searchMosquesNearMe = "Haritada Yakın Camileri Ara"
        override val directions = "Yol Tarifi"
        override val openInGoogleMaps = "Haritada Aç"
        override val kmAway = "km uzaklıkta"
        override val metersAway = "m uzaklıkta"
        override val compassTab = "Kıble Pusulası"
        override val mosquesTab = "Yakındaki Camiler"

        override val tasbihTitle = "Dijital Zikirmatik"
        override val totalDhikr = "Toplam Zikir"
        override val target = "Hedef"
        override val reset = "Sıfırla"
        override val resetConfirmTitle = "Sayacı Sıfırla"
        override val resetConfirmDesc = "Mevcut zikir sayacını sıfırlamak istediğinize emin misiniz?"
        override val cancel = "İptal"
        override val confirm = "Sıfırla"
        override val vibration = "Titreşim"
        override val tapCount = "Zikretmek İçin Dokun"

        override val duasHadithsTitle = "Dualar & Hadis-i Şerifler"
        override val duasHadithsSubtitle = "Namaz duaları, günlük dualar ve sahih hadisler"
        override val duasTab = "Dualar"
        override val hadithsTab = "Hadis-i Şerifler"
        override val namazDuas = "Namaz Duaları"
        override val dailyDuas = "Günlük Dualar"
        override val allDuas = "Tüm Dualar"
        override val allHadiths = "Tüm Hadisler"
        override val searchDua = "Dua adı veya anlamı ara..."
        override val searchHadith = "Hadis veya konu ara..."
        override val pronunciation = "Türkçe Okunuşu"
        override val meaning = "Türkçe Anlamı (Meali)"
        override val showMeaning = "Anlamı Göster"
        override val hideMeaning = "Anlamı Gizle"
        override val listenArabic = "Arapça Dinle"
        override val listenMeaning = "Meali Dinle"
        override val loading = "Yükleniyor..."
        override val arabicTextLabel = "Arapça Metin"
        override val virtuesAndBenefits = "Fazileti ve Önemi"
        override val source = "Kaynak"
        override val whereRecited = "Nerede Okunur?"

        override val quranTitle = "Kur'an-ı Kerim"
        override val quranSubtitle = "114 Sûre • Diyanet Meali & Arapça Hat"
        override val quranBadge114 = "114 Sûre • 30 Cüz"
        override val surahsButton = "Sûreler"
        override val bismillahMeaning = "Rahmân ve Rahîm olan Allah'ın adıyla"
        override val listenAyah = "Âyeti Dinle"
        override val resumeBookmark = "Kaldığınız Yerden Devam Edin"
        override val continueReading = "Okumaya Devam Et"
        override val searchSurah = "Sûre adı, numarası veya anlamı ara..."
        override val filterAllSurahs = "Tümü (114)"
        override val filterMeccan = "Mekkî (86)"
        override val filterMedinan = "Medenî (28)"
        override val filterJuz = "Cüzler (30)"
        override val filterPopular = "Namaz & Faziletli"
        override val ayah = "Âyet"
        override val juz = "Cüz"
        override val open = "Aç"
        override val fullScreen = "Tam Ekran"
        override val exitFullScreen = "Çık"
        override val menu = "Menü"
        override val close = "Kapat"
        override val fontSize = "Yazı Boyutu"
        override val listenSurah = "Sûreyi Dinle"
        override val pause = "Duraklat"
        override val stop = "Durdur"
        override val repeat = "Tekrarla"
        override val bookmark = "İşaretle"
        override val bookmarkedToast = "Âyet işaretlendi"
        override val copy = "Kopyala"
        override val copyToast = "Metin kopyalandı"
        override val previousSurah = "Önceki Sûre"
        override val nextSurah = "Sonraki Sûre"
        override val modeArabicAndMeal = "Arapça + Meal"
        override val modeArabicAndPronunciation = "Arapça + Okunuş"
        override val modeAllInOne = "Hepsi (Okunuş + Meal)"
        override val modeOnlyArabic = "Yalnız Arapça"
        override val modeOnlyMeal = "Yalnız Meal"
        override val pronunciationLabel = "Türkçe Okunuş:"

        override val alertSoundTypeTitle = "Bildirim Sesi Türü"
        override val soundTypeAdhan = "Ezan Sesi"
        override val soundTypeMelody = "Standart Melodi"
        override val soundTypeVibrationOnly = "Yalnızca Titreşim"
        override val soundVolumeTitle = "Ses Şiddeti Düzeyi"
        override val playWhenScreenOffTitle = "Ekran Kapalıyken Çalma İzni"
        override val playWhenScreenOffSubtitle = "Cihaz kilitli ve ekran kapalıyken arka planda ezan/melodi sesini çal"
        override val soundTestTitle = "Ses Testi"
        override val testSoundButton = "Seçili Sesi Test Et"
        override val backgroundPlaybackPanelTitle = "Ses ve Arka Planda Çalma Yönetimi"
        override val backgroundPlaybackPanelSubtitle = "Ezan veya melodi seçimi, ses şiddeti ve kilitli ekran izinleri"
        override val silentLabel = "Sessiz"
        override val backgroundPermissionActiveBanner = "Arka plan izni aktif: Ekran kilitliyken tam vaktinde çalar"

        override val settingsTitle = "Ayarlar"
        override val languageTitle = "Uygulama Dili"
        override val languageSubtitle = "Türkçe, İngilizce veya Arapça seçin"
        override val cityLocationTitle = "Şehir ve Konum"
        override val gpsTitle = "GPS Otomatik Konum"
        override val gpsSubtitle = "En yakın 81 il merkezini otomatik belirler"
        override val temkinTitle = "Temkin Vakti Düzeltmesi"
        override val temkinSubtitle = "Diyanet vakitlerine dakika ekleme/çıkarma"
        override val minuteUnit = "dakika"
        override val notificationsTitle = "Vakit Bildirimleri & Ezan Sesi"
        override val notificationsSubtitle = "Ekran kapalıyken bile ezan çalması ve bildirim gönderme"
        override val testNotificationButton = "Test Bildirimi ve Ezan Sesi Çal"
        override val prayerSoundsTitle = "Vakitlere Özel Ezan Sesi"
        override val batteryOptimizationTitle = "Kilitli Ekranda Ezan Çalma"
        override val batteryOptimizationDesc = "Cihazınız uyku modundayken ezanın tam vaktinde çalabilmesi için tam ekran alarm bildirimleri ve uyanma sistemi entegre edilmiştir."
        override val darkTheme = "Karanlık Tema"
        override val lightTheme = "Aydınlık Tema"
        override val calendarTitle = "Hicrî & Miladî Takvim"
        override val date = "Tarih"
        override val day = "Gün"
        override val hijri = "Hicrî"
        override val monthlyPrayersTab = "Aylık Vakitler"
        override val religiousDaysTab = "Dini Günler & Bayramlar"
        override val upcomingReligiousDay = "Yaklaşan Mübarek Gün / Kandil"
        override val filterUpcomingHolidays = "Yaklaşanlar"
        override val filterAllHolidays2026 = "2026 Yılı"
        override val filterAllHolidays2027 = "2027 Yılı"
        override val daysRemaining = "gün kaldı"
        override val tomorrowLabel = "Yarın"
        override val passedLabel = "Geçti"
        override val setHolidayAlarm = "Alarm / Bildirim Kur"
        override val alarmActiveLabel = "Hatırlatıcı Aktif"
        override val holidayAlarmDialogTitle = "Dini Gün Alarm & Bildirim Ayarı"
        override val holidayAlarmTimeLabel = "Hatırlatma Zamanı"
        override val holidayAlarmTypeLabel = "Uyarı Türü (Alarm veya Bildirim)"
        override val testHolidayAlertNow = "Uyarıyı Şimdi Test Et"
        override val saveAlarmButton = "Alarmı Kaydet"
        override val removeAlarmButton = "Alarmı Kapat"
        override val holidayAlarmSavedToast = "Dini gün alarmı ve bildirimi kuruldu"
        override val holidayAlarmRemovedToast = "Dini gün hatırlatıcısı kapatıldı"
        override val shareAndRecommendTitle = "Uygulamayı Paylaş ve Tavsiye Et"
        override val shareAndRecommendSubtitle = "Uygulamayı aileniz ve dostlarınızla paylaşarak hayra vesile olun"
        override val shareAndRecommendButton = "Uygulamayı Paylaş ve Tavsiye Et"
        override val shareAppMessage = "Ezan Vakti, Kur'an-ı Kerim (Arapça, Türkçe Okunuş, Meal ve Sesli Dinleme), Dualar, Hadisler, Kıble Pusulası, Yakındaki Camiler ve Dini Günler Takvimi özellikleriyle bu güzel uygulamayı mutlaka denemelisin! (Üretici: Bülent ERBAŞ)"
        override val producerSectionTitle = "Üretici ve Uygulama Bilgisi"
        override val producerStatement = "Üretici: Bülent ERBAŞ"
        override val producerSubtitle = "Bu uygulama Bülent ERBAŞ tarafından özenle hazırlanmıştır."
    }

    object EnglishStrings : Strings {
        override val appName = "Prayer Times"
        override val tabVakitler = "Prayers"
        override val tabKuran = "Quran"
        override val tabDualar = "Duas"
        override val tabKible = "Qibla"
        override val tabZikirmatik = "Tasbih"
        override val tabAyarlar = "Settings"
        override val tabTakvim = "Calendar"

        override val remainingTime = "Time Remaining"
        override val nextPrayer = "Next Prayer"
        override val currentPrayer = "Current Prayer"
        override val elapsed = "passed"
        override val prayerTimeArrived = "Prayer Time!"
        override val now = "Now"
        override val hours = "h"
        override val minutes = "m"
        override val seconds = "s"
        override val citySelect = "Select City"
        override val searchCity = "Search city..."
        override val gpsLocated = "GPS Location"
        override val gpsRequest = "Use GPS Location"
        override val verseOfTheDay = "Verse of the Day"
        override val hadithOfTheDay = "Hadith of the Day"
        override val duaOfTheDay = "Supplication of the Day"
        override val share = "Share"
        override val copied = "Copied to clipboard"
        override val todaysPrayerTimes = "Today's Prayer Times"
        override val diyanetCalendar = "Diyanet Schedule"
        override val playingNow = "Playing"
        override val today = "Today"
        override val friday = "Friday"
        override val selectedLabel = "Selected"

        override val qiblaTitle = "Qibla Compass"
        override val facingQibla = "Facing Qibla!"
        override val turnDevice = "Rotate device"
        override val distanceToKaaba = "Distance to Kaaba"
        override val qiblaAngle = "Qibla Direction"
        override val compassAngle = "Compass Heading"
        override val calibrateCompass = "Wave device in figure-8 to calibrate compass"
        override val testMode = "Simulation Mode"
        override val degrees = "°"
        override val nearbyMosques = "Nearby Mosques"
        override val nearbyMosquesSubtitle = "Nearest mosques and masjids to your location"
        override val searchMosquesNearMe = "Search Mosques on Map"
        override val directions = "Get Directions"
        override val openInGoogleMaps = "Open in Maps"
        override val kmAway = "km away"
        override val metersAway = "m away"
        override val compassTab = "Compass"
        override val mosquesTab = "Mosques"

        override val tasbihTitle = "Tasbih Counter"
        override val totalDhikr = "Total Dhikr"
        override val target = "Target"
        override val reset = "Reset"
        override val resetConfirmTitle = "Reset Counter"
        override val resetConfirmDesc = "Are you sure you want to reset current count?"
        override val cancel = "Cancel"
        override val confirm = "Reset"
        override val vibration = "Vibration"
        override val tapCount = "Tap to Count"

        override val duasHadithsTitle = "Duas & Hadiths"
        override val duasHadithsSubtitle = "Prayer supplications, daily duas, and authentic hadiths"
        override val duasTab = "Duas"
        override val hadithsTab = "Hadiths"
        override val namazDuas = "Salah Duas"
        override val dailyDuas = "Daily Duas"
        override val allDuas = "All Duas"
        override val allHadiths = "All Hadiths"
        override val searchDua = "Search dua..."
        override val searchHadith = "Search hadith or topic..."
        override val pronunciation = "Transliteration"
        override val meaning = "Meaning / Translation"
        override val showMeaning = "Show Meaning"
        override val hideMeaning = "Hide Meaning"
        override val listenArabic = "Listen Arabic"
        override val listenMeaning = "Listen Meaning"
        override val loading = "Loading..."
        override val arabicTextLabel = "Arabic Text"
        override val virtuesAndBenefits = "Virtues & Significance"
        override val source = "Source"
        override val whereRecited = "Where Recited?"

        override val quranTitle = "The Holy Quran"
        override val quranSubtitle = "114 Surahs with Uthmani Arabic script and translation"
        override val quranBadge114 = "114 Surahs • 30 Juz"
        override val surahsButton = "Surahs"
        override val bismillahMeaning = "In the name of Allah, the Entirely Merciful, the Especially Merciful"
        override val listenAyah = "Listen Ayah"
        override val resumeBookmark = "Continue where you left off"
        override val continueReading = "Continue Reading"
        override val searchSurah = "Search surah name or number..."
        override val filterAllSurahs = "All (114)"
        override val filterMeccan = "Meccan (86)"
        override val filterMedinan = "Medinan (28)"
        override val filterJuz = "Juz (30)"
        override val filterPopular = "Salah & Key Surahs"
        override val ayah = "Ayah"
        override val juz = "Juz"
        override val open = "Open"
        override val fullScreen = "Full Screen"
        override val exitFullScreen = "Exit"
        override val menu = "Menu"
        override val close = "Close"
        override val fontSize = "Font Size"
        override val listenSurah = "Listen Surah"
        override val pause = "Pause"
        override val stop = "Stop"
        override val repeat = "Repeat"
        override val bookmark = "Bookmark"
        override val bookmarkedToast = "Ayah bookmarked"
        override val copy = "Copy"
        override val copyToast = "Copied to clipboard"
        override val previousSurah = "Previous Surah"
        override val nextSurah = "Next Surah"
        override val modeArabicAndMeal = "Arabic + Meaning"
        override val modeArabicAndPronunciation = "Arabic + Reading"
        override val modeAllInOne = "All (Reading + Meaning)"
        override val modeOnlyArabic = "Arabic Only"
        override val modeOnlyMeal = "Meaning Only"
        override val pronunciationLabel = "Pronunciation:"

        override val alertSoundTypeTitle = "Notification Sound Type"
        override val soundTypeAdhan = "Adhan Sound"
        override val soundTypeMelody = "Standard Melody"
        override val soundTypeVibrationOnly = "Vibration Only"
        override val soundVolumeTitle = "Sound Volume Level"
        override val playWhenScreenOffTitle = "Play When Screen Is Locked"
        override val playWhenScreenOffSubtitle = "Allow playing adhan/melody in background when device is locked"
        override val soundTestTitle = "Sound Test"
        override val testSoundButton = "Test Selected Sound"
        override val backgroundPlaybackPanelTitle = "Sound & Background Playback Management"
        override val backgroundPlaybackPanelSubtitle = "Adhan or melody selection, volume levels, and lock-screen permissions"
        override val silentLabel = "Muted"
        override val backgroundPermissionActiveBanner = "Background permission active: Plays on time even when screen is locked"

        override val settingsTitle = "Settings"
        override val languageTitle = "App Language"
        override val languageSubtitle = "Choose Turkish, English, or Arabic"
        override val cityLocationTitle = "City & Location"
        override val gpsTitle = "GPS Automatic Location"
        override val gpsSubtitle = "Finds your nearest city automatically"
        override val temkinTitle = "Calculation Offset (Temkin)"
        override val temkinSubtitle = "Add or subtract minutes for calculation"
        override val minuteUnit = "minutes"
        override val notificationsTitle = "Prayer Alerts & Adhan"
        override val notificationsSubtitle = "Play full adhan and show alerts even when locked"
        override val testNotificationButton = "Test Notification & Play Adhan"
        override val prayerSoundsTitle = "Per-Prayer Adhan Sound"
        override val batteryOptimizationTitle = "Adhan on Lock Screen"
        override val batteryOptimizationDesc = "Exact AlarmClock scheduling and full-screen alerts ensure the adhan sounds even when your device is locked in deep sleep."
        override val darkTheme = "Dark Theme"
        override val lightTheme = "Light Theme"
        override val calendarTitle = "Islamic & Gregorian Calendar"
        override val date = "Date"
        override val day = "Day"
        override val hijri = "Hijri"
        override val monthlyPrayersTab = "Monthly Times"
        override val religiousDaysTab = "Religious Days & Eids"
        override val upcomingReligiousDay = "Next Upcoming Sacred Day"
        override val filterUpcomingHolidays = "Upcoming"
        override val filterAllHolidays2026 = "Year 2026"
        override val filterAllHolidays2027 = "Year 2027"
        override val daysRemaining = "days left"
        override val tomorrowLabel = "Tomorrow"
        override val passedLabel = "Passed"
        override val setHolidayAlarm = "Set Alarm / Alert"
        override val alarmActiveLabel = "Alarm Active"
        override val holidayAlarmDialogTitle = "Religious Day Alarm & Notification"
        override val holidayAlarmTimeLabel = "Reminder Timing"
        override val holidayAlarmTypeLabel = "Alert Mode (Alarm or Notification)"
        override val testHolidayAlertNow = "Test Alert Now"
        override val saveAlarmButton = "Save Alarm"
        override val removeAlarmButton = "Turn Off Alarm"
        override val holidayAlarmSavedToast = "Religious day alarm & notification scheduled"
        override val holidayAlarmRemovedToast = "Religious day reminder turned off"
        override val shareAndRecommendTitle = "Share & Recommend App"
        override val shareAndRecommendSubtitle = "Share this app with your family and friends"
        override val shareAndRecommendButton = "Share & Recommend App"
        override val shareAppMessage = "I highly recommend this comprehensive Prayer Times, Holy Quran (Audio, Transliteration & Translation), Duas, Hadiths, Qibla Compass, Nearby Mosques, and Religious Calendar app! (Producer: Bülent ERBAŞ)"
        override val producerSectionTitle = "Producer & App Info"
        override val producerStatement = "Producer: Bülent ERBAŞ"
        override val producerSubtitle = "This application was produced and developed by Bülent ERBAŞ."
    }

    object ArabicStrings : Strings {
        override val appName = "أوقات الصلاة"
        override val tabVakitler = "المواقيت"
        override val tabKuran = "القرآن"
        override val tabDualar = "الأدعية"
        override val tabKible = "القبلة"
        override val tabZikirmatik = "المسبحة"
        override val tabAyarlar = "الإعدادات"
        override val tabTakvim = "التقويم"

        override val remainingTime = "الوقت المتبقي"
        override val nextPrayer = "الصلاة القادمة"
        override val currentPrayer = "الصلاة الحالية"
        override val elapsed = "مضت"
        override val prayerTimeArrived = "حان وقت الصلاة"
        override val now = "الآن"
        override val hours = "س"
        override val minutes = "د"
        override val seconds = "ث"
        override val citySelect = "اختر المدينة"
        override val searchCity = "ابحث عن مدينة..."
        override val gpsLocated = "موقع GPS"
        override val gpsRequest = "تحديد الموقع عبر GPS"
        override val verseOfTheDay = "آية اليوم"
        override val hadithOfTheDay = "حديث اليوم"
        override val duaOfTheDay = "دعاء اليوم"
        override val share = "مشاركة"
        override val copied = "تم النسخ"
        override val todaysPrayerTimes = "مواقيت الصلاة اليوم"
        override val diyanetCalendar = "تقويم الشؤون الدينية"
        override val playingNow = "قيد التشغيل"
        override val today = "اليوم"
        override val friday = "الجمعة"
        override val selectedLabel = "محدد"

        override val qiblaTitle = "بوصلة القبلة"
        override val facingQibla = "أنت باتجاه القبلة!"
        override val turnDevice = "قم بتدوير الجهاز"
        override val distanceToKaaba = "المسافة إلى الكعبة"
        override val qiblaAngle = "زاوية القبلة"
        override val compassAngle = "زاوية البوصلة"
        override val calibrateCompass = "حرك الهاتف على شكل 8 لمعايرة البوصلة"
        override val testMode = "وضع المحاكاة"
        override val degrees = "°"
        override val nearbyMosques = "المساجد القريبة"
        override val nearbyMosquesSubtitle = "أقرب المساجد والمصليات لموقعك الحالي"
        override val searchMosquesNearMe = "بحث عن المساجد في الخريطة"
        override val directions = "الاتجاهات"
        override val openInGoogleMaps = "فتح في الخرائط"
        override val kmAway = "كم تقريباً"
        override val metersAway = "متر تقريباً"
        override val compassTab = "البوصلة"
        override val mosquesTab = "المساجد"

        override val tasbihTitle = "المسبحة الإلكترونية"
        override val totalDhikr = "مجموع التسبيحات"
        override val target = "الهدف"
        override val reset = "تصفير"
        override val resetConfirmTitle = "إعادة ضبط العداد"
        override val resetConfirmDesc = "هل تريد تصفير عداد التسبيح الحالي؟"
        override val cancel = "إلغاء"
        override val confirm = "تأكيد"
        override val vibration = "الاهتزاز"
        override val tapCount = "اضغط للتسبيح"

        override val duasHadithsTitle = "الأدعية والأحاديث"
        override val duasHadithsSubtitle = "أدعية الصلاة، أذكار اليوم والليلة، وأحاديث نبوية شريفة"
        override val duasTab = "الأدعية"
        override val hadithsTab = "الأحاديث الشريفة"
        override val namazDuas = "أدعية الصلاة"
        override val dailyDuas = "أدعية يومية"
        override val allDuas = "جميع الأدعية"
        override val allHadiths = "جميع الأحاديث"
        override val searchDua = "ابحث عن دعاء..."
        override val searchHadith = "ابحث في الأحاديث..."
        override val pronunciation = "النطق الصوتي"
        override val meaning = "المعنى والترجمة"
        override val showMeaning = "عرض المعنى"
        override val hideMeaning = "إخفاء المعنى"
        override val listenArabic = "استماع بالعربية"
        override val listenMeaning = "استماع للمعنى"
        override val loading = "جارٍ التحميل..."
        override val arabicTextLabel = "النص العربي"
        override val virtuesAndBenefits = "الفضائل والأهمية"
        override val source = "المصدر"
        override val whereRecited = "موضع القراءة"

        override val quranTitle = "القرآن الكريم"
        override val quranSubtitle = "١١٤ سورة بالرسم العثماني الشريف والترجمة"
        override val quranBadge114 = "١١٤ سورة • ٣٠ جزء"
        override val surahsButton = "السور"
        override val bismillahMeaning = "بسم الله الرحمن الرحيم"
        override val listenAyah = "استماع للآية"
        override val resumeBookmark = "تابع القراءة من حيث توقفت"
        override val continueReading = "متابعة القراءة"
        override val searchSurah = "ابحث عن اسم السورة أو رقمها..."
        override val filterAllSurahs = "الكل (١١٤)"
        override val filterMeccan = "مكية (٨٦)"
        override val filterMedinan = "مدنية (٢٨)"
        override val filterJuz = "الأجزاء (٣٠)"
        override val filterPopular = "سور الصلاة والفضائل"
        override val ayah = "آية"
        override val juz = "جزء"
        override val open = "فتح"
        override val fullScreen = "ملء الشاشة"
        override val exitFullScreen = "خروج"
        override val menu = "القائمة"
        override val close = "إغلاق"
        override val fontSize = "حجم الخط"
        override val listenSurah = "استماع للسورة"
        override val pause = "إيقاف مؤقت"
        override val stop = "إيقاف"
        override val repeat = "تكرار"
        override val bookmark = "إشارة مرجعية"
        override val bookmarkedToast = "تم حفظ الإشارة المرجعية"
        override val copy = "نسخ"
        override val copyToast = "تم نسخ النص"
        override val previousSurah = "السورة السابقة"
        override val nextSurah = "السورة التالية"
        override val modeArabicAndMeal = "عربي + ترجمة"
        override val modeArabicAndPronunciation = "عربي + النطق"
        override val modeAllInOne = "الكل (عربي + نطق + ترجمة)"
        override val modeOnlyArabic = "عربي فقط"
        override val modeOnlyMeal = "ترجمة فقط"
        override val pronunciationLabel = "طريقة النطق:"

        override val alertSoundTypeTitle = "نوع صوت التنبيه"
        override val soundTypeAdhan = "صوت الأذان"
        override val soundTypeMelody = "نغمة عادية"
        override val soundTypeVibrationOnly = "اهتزاز فقط"
        override val soundVolumeTitle = "مستوى شدة الصوت"
        override val playWhenScreenOffTitle = "التشغيل عند إغلاق الشاشة"
        override val playWhenScreenOffSubtitle = "السماح بتشغيل الأذان في الخلفية حتى عندما يكون الجهاز مقفلاً"
        override val soundTestTitle = "تجربة الصوت"
        override val testSoundButton = "تجربة الصوت المختار"
        override val backgroundPlaybackPanelTitle = "إدارة الصوت والتشغيل في الخلفية"
        override val backgroundPlaybackPanelSubtitle = "نوع الأذان، مستوى الصوت وأذونات التشغيل عند قفل الشاشة"
        override val silentLabel = "صامت"
        override val backgroundPermissionActiveBanner = "إذن التشغيل في الخلفية مفعل: يعمل في وقته حتى عند قفل الشاشة"

        override val settingsTitle = "الإعدادات"
        override val languageTitle = "لغة التطبيق"
        override val languageSubtitle = "اختر التركية، الإنجليزية، أو العربية"
        override val cityLocationTitle = "المدينة والموقع"
        override val gpsTitle = "الموقع التلقائي عبر GPS"
        override val gpsSubtitle = "تحديد أقرب مدينة لموقعك تلقائياً"
        override val temkinTitle = "تعديل وقت الصلاة (دقائق)"
        override val temkinSubtitle = "إضافة أو إنقاص دقائق من الحساب"
        override val minuteUnit = "دقائق"
        override val notificationsTitle = "تنبيهات الصلاة وصوت الأذان"
        override val notificationsSubtitle = "تشغيل الأذان كاملاً حتى عند قفل الشاشة"
        override val testNotificationButton = "تجربة التنبيه وصوت الأذان"
        override val prayerSoundsTitle = "أصوات الأذان لكل صلاة"
        override val batteryOptimizationTitle = "الأذان أثناء قفل الشاشة"
        override val batteryOptimizationDesc = "تم تفعيل التنبيهات الدقيقة بملء الشاشة لضمان تشغيل الأذان بدقة حتى أثناء وضع السكون وقفل الشاشة."
        override val darkTheme = "الوضع الليلي"
        override val lightTheme = "الوضع الفاتح"
        override val calendarTitle = "التقويم الهجري والميلادي"
        override val date = "التاريخ"
        override val day = "اليوم"
        override val hijri = "الهجري"
        override val monthlyPrayersTab = "مواقيت الشهر"
        override val religiousDaysTab = "الأيام الدينية والأعياد"
        override val upcomingReligiousDay = "المناسبة الدينية القادمة"
        override val filterUpcomingHolidays = "القادمة"
        override val filterAllHolidays2026 = "عام ٢٠٢٦"
        override val filterAllHolidays2027 = "عام ٢٠٢٧"
        override val daysRemaining = "أيام متبقية"
        override val tomorrowLabel = "غداً"
        override val passedLabel = "انقضت"
        override val setHolidayAlarm = "ضبط منبه / إشعار"
        override val alarmActiveLabel = "المنبه مفعل"
        override val holidayAlarmDialogTitle = "إعداد منبه وإشعار المناسبة الدينية"
        override val holidayAlarmTimeLabel = "وقت التذكير"
        override val holidayAlarmTypeLabel = "نوع التنبيه (منبه صوتي أو إشعار)"
        override val testHolidayAlertNow = "تجربة التنبيه الآن"
        override val saveAlarmButton = "حفظ المنبه"
        override val removeAlarmButton = "إيقاف المنبه"
        override val holidayAlarmSavedToast = "تم ضبط منبه وإشعار المناسبة الدينية"
        override val holidayAlarmRemovedToast = "تم إيقاف تذكير المناسبة الدينية"
        override val shareAndRecommendTitle = "مشاركة التطبيق والتوصية به"
        override val shareAndRecommendSubtitle = "شارك التطبيق مع العائلة والأصدقاء لتعم الفائدة"
        override val shareAndRecommendButton = "مشاركة التطبيق والتوصية به"
        override val shareAppMessage = "أنصحكم بتجربة تطبيق أوقات الصلاة والقرآن الكريم والأدعية والأحاديث وبوصلة القبلة والمساجد القريبة والتقويم الهجري! (المنتج: Bülent ERBAŞ)"
        override val producerSectionTitle = "المنتج ومعلومات التطبيق"
        override val producerStatement = "المنتج: Bülent ERBAŞ"
        override val producerSubtitle = "تم إنتاج وتطوير هذا التطبيق بعناية بواسطة Bülent ERBAŞ."
    }
}
