package com.example.data.repository

import com.example.data.model.Ayah
import com.example.data.model.SurahMeta

/**
 * Built-in authentic Quran texts for immediate offline reading and fallback.
 * Every verse here is 100% authentic (never synthetic or placeholder).
 * Complete Surahs (1, 87, 91-114) have all verses included; longer Surahs include
 * authentic key sections as an offline-only fallback when network is unavailable.
 */
object QuranBuiltInSurahs {

    fun getAyahs(number: Int, meta: SurahMeta): List<Ayah> {
        val raw = when (number) {
            1 -> fatiha
            2 -> bakaraKey
            3 -> aliImranKey
            36 -> yasin
            48 -> fetih
            55 -> rahman
            56 -> vakia
            59 -> hasrKey
            62 -> cuma
            67 -> mulk
            78 -> nebe
            79 -> naziat
            80 -> abese
            81 -> tekvir
            82 -> infitar
            83 -> mutaffifin
            84 -> insikak
            85 -> buruc
            86 -> tarik
            87 -> ala
            88 -> gasiye
            89 -> fecr
            90 -> beled
            91 -> sems
            92 -> leyl
            93 -> duha
            94 -> insirah
            95 -> tin
            96 -> alak
            97 -> kadir
            98 -> beyyine
            99 -> zilzal
            100 -> adiyat
            101 -> karia
            102 -> tekasur
            103 -> asr
            104 -> humeze
            105 -> fil
            106 -> kureys
            107 -> maun
            108 -> kevser
            109 -> kafirun
            110 -> nasr
            111 -> tebbet
            112 -> ihlas
            113 -> felak
            114 -> nas
            else -> emptyList()
        }
        return raw.map { ayah ->
            val pron = ayah.turkishPronunciation.ifBlank {
                QuranPronunciationHelper.getPronunciation(
                    surahNumber = number,
                    numberInQuran = ayah.numberInQuran,
                    numberInSurah = ayah.numberInSurah,
                    arabicText = ayah.arabicText
                )
            }
            ayah.copy(surahNumber = number, turkishPronunciation = pron)
        }
    }

    private val fatiha = listOf(
        Ayah(1, 1, "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "Rahmân ve Rahîm olan Allah'ın adıyla.", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
        Ayah(2, 2, "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ", "Hamd, âlemlerin Rabbi olan Allah'a aittir.", "[All] praise is [due] to Allah, Lord of the worlds."),
        Ayah(3, 3, "ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "O Rahmândır, Rahîmdir.", "The Entirely Merciful, the Especially Merciful."),
        Ayah(4, 4, "مَٰلِكِ يَوْمِ ٱلدِّينِ", "Din (hesap ve ceza) gününün yegâne sahibidir.", "Sovereign of the Day of Recompense."),
        Ayah(5, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz.", "It is You we worship and You we ask for help."),
        Ayah(6, 6, "ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ", "Bizi dosdoğru yola ilet.", "Guide us to the straight path."),
        Ayah(7, 7, "صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ", "Kendilerine nimet verdiğin kimselerin yoluna; gazaba uğrayanların ve sapanlarınkine değil.", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.")
    )

    private val bakaraKey = listOf(
        Ayah(1, 8, "الٓمٓ", "Elif, Lâm, Mîm.", "Alif, Lam, Meem."),
        Ayah(2, 9, "ذَٰلِكَ ٱلْكِتَٰبُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًۭى لِّلْمُتَّقِينَ", "Bu, kendisinde hiçbir şüphe bulunmayan, takvâ sahipleri için bir hidayet rehberi olan kitaptır.", "This is the Book about which there is no doubt, a guidance for those conscious of Allah."),
        Ayah(3, 10, "ٱلَّذِينَ يُؤْمِنُونَ بِٱلْغَيْبِ وَيُقِيمُونَ ٱلصَّلَوٰةَ وَمِمَّا رَزَقْنَٰهُمْ يُنفِقُونَ", "Onlar gayba inanırlar, namazı dosdoğru kılarlar ve kendilerine verdiğimiz rızıktan infak ederler.", "Who believe in the unseen, establish prayer, and spend out of what We have provided for them."),
        Ayah(4, 11, "وَٱلَّذِينَ يُؤْمِنُونَ بِمَآ أُنزِلَ إِلَيْكَ وَمَآ أُنزِلَ مِن قَبْلِكَ وَبِٱلْءَاخِرَةِ هُمْ يُوقِنُونَ", "Sana indirilene ve senden önce indirilenlere iman ederler; ahirete de kesin olarak inanırlar.", "And who believe in what has been revealed to you, and what was revealed before you, and of the Hereafter they are certain."),
        Ayah(5, 12, "أُو۟لَٰٓئِكَ عَلَىٰ هُدًۭى مِّن رَّبِّهِمْ ۖ وَأُو۟لَٰٓئِكَ هُمُ ٱلْمُفْلِحُونَ", "İşte onlar Rablerinden gelen bir hidayet üzeredirler ve kurtuluşa erenler ancak onlardır.", "Those are upon [right] guidance from their Lord, and it is those who are the successful."),
        Ayah(255, 262, "ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلْحَىُّ ٱلْقَيُّومُ ۚ لَا تَأْخُذُهُۥ سِنَةٌۭ وَلَا نَوْمٌۭ ۚ لَّهُۥ مَا فِى ٱلسَّمَٰوَٰتِ وَمَا فِى ٱلْأَرْضِ ۗ مَن ذَا ٱلَّذِى يَشْفَعُ عِندَهُۥٓ إِلَّا بِإِذْنِهِۦ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَىْءٍۢ مِّنْ عِلْمِهِۦٓ إِلَّا بِمَا شَآءَ ۚ وَسِعَ كُرْسِيُّهُ ٱلسَّمَٰوَٰتِ وَٱلْأَرْضَ ۖ وَلَا يَـُٔودُهُۥ حِفْظُهُمَا ۚ وَهُوَ ٱلْعَلِىُّ ٱلْعَظِيمُ", "Âyetü'l-Kürsî: Allah, O'ndan başka ilah olmayandır; Hayy'dır (ebedî diridir), Kayyûm'dur (bütün varlığı ayakta tutandır). O'nu ne bir uyuklama tutar ne de uyku. Göklerde ve yerde ne varsa O'nundur. İzni olmadan katında şefaat edecek kimdir? O, kullarının önündekini de arkasındakini de bilir. O'nun ilminden, dilediği kadarından başka hiçbir şeyi kavrayamazlar. O'nun kürsüsü gökleri ve yeri kuşatmıştır. Onları koruyup gözetmek O'na ağır gelmez. O çok yücedir, çok büyüktür.", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep..."),
        Ayah(285, 292, "ءَامَنَ ٱلرَّسُولُ بِمَآ أُنزِلَ إِلَيْهِ مِن رَّبِّهِۦ وَٱلْمُؤْمِنُونَ ۚ كُلٌّ ءَامَنَ بِٱللَّهِ وَمَلَٰٓئِكَتِهِۦ وَكُتُبِهِۦ وَرُسُلِهِۦ لَا نُفَرِّقُ بَيْنَ أَحَدٍۢ مِّن رُّسُلِهِۦ ۚ وَقَالُوا۟ سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ ٱلْمَصِيرُ", "Âmenerrasûlü (1): Peygamber, Rabbinden kendisine indirilene iman etti, müminler de. Hepsi Allah'a, meleklerine, kitaplarına ve peygamberlerine inandılar. 'Biz O'nun peygamberleri arasında hiçbir ayrım yapmayız' dediler ve: 'İşittik ve itaat ettik. Bağışlamanı dileriz ey Rabbimiz, dönüş ancak Sana'dır' dediler.", "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers..."),
        Ayah(286, 293, "لَا يُكَلِّفُ ٱللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا ٱكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَآ إِن نَّسِينَآ أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَآ إِصْرًۭا كَمَا حَمَلْتَهُۥ عَلَى ٱلَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِۦ ۖ وَٱعْفُ عَنَّا وَٱغْفِرْ لَنَا وَٱرْحَمْنَآ ۚ أَنتَ مَوْلَىٰنَا فَٱنصُرْنَا عَلَى ٱلْقَوْمِ ٱلْكَٰفِرِينَ", "Âmenerrasûlü (2): Allah hiçbir kimseyi güç yetiremeyeceği bir şeyle yükümlü tutmaz. Kazandığı iyilik kendi lehine, işlediği kötülük de kendi aleyhinedir. 'Ey Rabbimiz! Unutursak veya hata edersek bizi sorumlu tutma! Ey Rabbimiz! Bizden öncekilere yüklediğin gibi bize ağır yük yükleme! Ey Rabbimiz! Güç yetiremeyeceğimiz şeyleri bize taşıtma! Bizi affet, bizi bağışla, bize merhamet et! Sen bizim Mevlâmızsın; kâfirler topluluğuna karşı bize yardım et!'", "Allah does not charge a soul except [with that within] its capacity. It will have [the consequence of] what [good] it has gained...")
    )

    private val aliImranKey = listOf(
        Ayah(1, 294, "الٓمٓ", "Elif, Lâm, Mîm.", "Alif, Lam, Meem."),
        Ayah(2, 295, "ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلْحَىُّ ٱلْقَيُّومُ", "Allah, kendisinden başka ilah olmayandır; Hayy ve Kayyûm'dur.", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of existence."),
        Ayah(3, 296, "نَزَّلَ عَلَيْكَ ٱلْكِتَٰبَ بِٱلْحَقِّ مُصَدِّقًۭا لِّمَا بَيْنَ يَدَيْهِ وَأَنزَلَ ٱلتَّوْرَىٰةَ وَٱلْإِنجِيلَ", "O, kendinden öncekileri tasdik edici olarak sana Kitab'ı hak ile indirdi; daha önce Tevrat'ı ve İncil'i de indirmişti.", "He has sent down upon you, [O Muhammad], the Book in truth, confirming what was before it. And He revealed the Torah and the Gospel."),
        Ayah(4, 297, "مِن قَبْلُ هُدًۭى لِّلنَّاسِ وَأَنزَلَ ٱلْفُرْقَانَ ۗ إِنَّ ٱلَّذِينَ كَفَرُوا۟ بِـَٔايَٰتِ ٱللَّهِ لَهُمْ عَذَابٌۭ شَدِيدٌۭ ۗ وَٱللَّهُ عَزِيزٌۭ ذُو ٱنتِقَامٍ", "İnsanlara hidayet rehberi olarak; Furkan'ı da O indirdi. Şüphesiz Allah'ın âyetlerini inkâr edenler için çetin bir azap vardır. Allah mutlak güç ve intikam sahibidir.", "Before, as guidance for the people. And He revealed the Criterion. Indeed, those who disbelieve in the verses of Allah will have a severe punishment..."),
        Ayah(5, 298, "إِنَّ ٱللَّهَ لَا يَخْفَىٰ عَلَيْهِ شَىْءٌۭ فِى ٱلْأَرْضِ وَلَا فِى ٱلسَّمَآءِ", "Şüphesiz yerde ve gökte hiçbir şey Allah'a gizli kalmaz.", "Indeed, from Allah nothing is hidden in the earth nor in the heaven.")
    )

    private val yasin = listOf(
        Ayah(1, 3706, "يسٓ", "Yâ Sîn.", "Ya, Seen."),
        Ayah(2, 3707, "وَٱلْقُرْءَانِ ٱلْحَكِيمِ", "Hikmet dolu Kur'an'a andolsun ki,", "By the wise Qur'an."),
        Ayah(3, 3708, "إِنَّكَ لَمِنَ ٱلْمُرْسَلِينَ", "Sen şüphesiz peygamberlerdensin,", "Indeed you are of the messengers,"),
        Ayah(4, 3709, "عَلَىٰ صِرَٰطٍۢ مُّسْتَقِيمٍۢ", "Dosdoğru bir yol üzerindesin.", "On a straight path."),
        Ayah(5, 3710, "تَنزِيلَ ٱلْعَزِيزِ ٱلرَّحِيمِ", "Bu Kur'an, mutlak güç ve engin merhamet sahibi Allah tarafından indirilmiştir.", "[This is] a revelation of the Exalted in Might, the Merciful,"),
        Ayah(6, 3711, "لِتُنذِرَ قَوْمًۭا مَّآ أُنذِرَ ءَابَآؤُهُمْ فَهُمْ غَٰفِلُونَ", "Babaları uyarılmamış, bu yüzden kendileri de gaflet içinde olan bir toplumu uyarman için.", "That you may warn a people whose forefathers were not warned, so they are unaware."),
        Ayah(7, 3712, "لَقَدْ حَقَّ ٱلْقَوْلُ عَلَىٰٓ أَكْثَرِهِمْ فَهُمْ لَا يُؤْمِنُونَ", "Andolsun ki onların çoğuna o azap sözü hak olmuştur; artık onlar inanmazlar.", "Already the word has come into effect upon most of them, so they do not believe."),
        Ayah(8, 3713, "إِنَّا جَعَلْنَا فِىٓ أَعْنَٰقِهِمْ أَغْلَٰلًۭا فَهِىَ إِلَى ٱلْأَذْقَانِ فَهُم مُّقْمَحُونَ", "Biz onların boyunlarına öyle halkalar geçirdik ki çenelerine dayanmıştır; başları yukarı kalkıktır.", "Indeed, We have put shackles on their necks, and they are to their chins, so their heads are [kept] aloft."),
        Ayah(9, 3714, "وَجَعَلْنَا مِنۢ بَيْنِ أَيْدِيهِمْ سَدًّۭا وَمِنْ خَلْفِهِمْ سَدًّۭا فَأَغْشَيْنَٰهُمْ فَهُمْ لَا يُبْصِرُونَ", "Önlerinden ve arkalarından bir set çektik, onları perdeledik; artık göremezler.", "And We have put before them a barrier and behind them a barrier and covered them, so they do not see."),
        Ayah(10, 3715, "وَسَوَآءٌ عَلَيْهِمْ ءَأَنذَرْتَهُمْ أَمْ لَمْ تُنذِرْهُمْ لَا يُؤْمِنُونَ", "Onları uyarsan da uyarmasan da onlar için birdir, inanmazlar.", "And it is all the same for them whether you warn them or do not warn them - they will not believe."),
        Ayah(11, 3716, "إِنَّمَا تُنذِرُ مَنِ ٱتَّبَعَ ٱلذِّكْرَ وَخَشِىَ ٱلرَّحْمَٰنَ بِٱلْغَيْبِ ۖ فَبَشِّرْهُ بِمَغْفِرَةٍۢ وَأَجْرٍۢ كَرِيمٍ", "Sen ancak Kur'an'a uyan ve görmediği halde Rahmân'dan korkan kimseyi uyarabilirsin. İşte böylesini mağfiret ve cömertçe bir mükâfatla müjdele.", "You can only warn one who follows the message and fears the Most Merciful unseen..."),
        Ayah(12, 3717, "إِنَّا نَحْنُ نُحْىِ ٱلْمَوْتَىٰ وَنَكْتُبُ مَا قَدَّمُوا۟ وَءَاثَٰرَهُمْ ۚ وَكُلَّ شَىْءٍ أَحْصَيْنَٰهُ فِىٓ إِمَامٍۢ مُّبِينٍۢ", "Şüphesiz ölüleri dirilten, işledikleri amelleri ve geride bıraktıkları eserleri kaydeden biziz. Biz her şeyi apaçık bir kütükte sayıp tespit etmişizdir.", "Indeed, it is We who bring the dead to life and record what they have put forth and what they left behind..."),
        Ayah(58, 3763, "سَلَٰمٌۭ قَوْلًۭا مِّن رَّبٍّۢ رَّحِيمٍۢ", "Engin merhamet sahibi Rab'den onlara bir söz olarak 'Selâm' vardır.", "[And] 'Peace,' a word from a Merciful Lord."),
        Ayah(82, 3787, "إِنَّمَآ أَمْرُهُۥٓ إِذَآ أَرَادَ شَيْـًٔا أَن يَقُولَ لَهُۥ كُن فَيَكُونُ", "Bir şeyi dilediği zaman O'nun emri sadece ona 'Ol!' demektir, o da hemen oluverir.", "His command is only when He intends a thing that He says to it, 'Be,' and it is."),
        Ayah(83, 3788, "فَسُبْحَٰنَ ٱلَّذِى بِيَدِهِۦ مَلَكُوتُ كُلِّ شَىْءٍۢ وَإِلَيْهِ تُرْجَعُونَ", "Her şeyin mutlak hükümranlığı elinde olan Allah yüceler yücesidir. Ve siz de ancak O'na döndürüleceksiniz.", "So exalted is He in whose hand is the realm of all things, and to Him you will be returned.")
    )

    private val mulk = listOf(
        Ayah(1, 5242, "تَبَٰرَكَ ٱلَّذِى بِيَدِهِ ٱلْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَىْءٍۢ قَدِيرٌ", "Mutlak hükümranlık elinde olan Allah ne yücedir ve O her şeye hakkıyla kâdirdir.", "Blessed is He in whose hand is dominion, and He is over all things competent -"),
        Ayah(2, 5243, "ٱلَّذِى خَلَقَ ٱلْمَوْتَ وَٱلْحَيَوٰةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًۭا ۚ وَهُوَ ٱلْعَزِيزُ ٱلْغَفُورُ", "Hanginizin daha güzel amel işleyeceğini sınamak için ölümü ve hayatı yaratan O'dur. O azîzdir, çok bağışlayandır.", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -"),
        Ayah(3, 5244, "ٱلَّذِى خَلَقَ سَبْعَ سَمَٰوَٰتٍۢ طِبَاقًۭا ۖ مَّا تَرَىٰ فِى خَلْقِ ٱلرَّحْمَٰنِ مِن تَفَٰوُتٍۢ ۖ فَٱرْجِعِ ٱلْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍۢ", "O, yedi göğü birbiriyle uyumlu tabakalar halinde yaratandır. Rahmân'ın yaratışında hiçbir düzensizlik göremezsin. Gözünü çevir de bak, bir kusur görebilir misin?", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency..."),
        Ayah(4, 5245, "ثُمَّ ٱرْجِعِ ٱلْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ ٱلْبَصَرُ خَاسِئًۭا وَهُوَ حَسِيرٌۭ", "Sonra gözünü iki kez daha çevir bak; göz aradığı kusuru bulamayarak hor, hakir ve yorgun olarak sana döner.", "Then return [your] vision twice again. [Your] vision will return to you humbled while it is fatigued."),
        Ayah(5, 5246, "وَلَقَدْ زَيَّنَّا ٱلسَّمَآءَ ٱلدُّنْيَا بِمَصَٰبِيحَ وَجَعَلْنَٰهَا رُجُومًۭا لِّلشَّيَٰطِينِ ۖ وَأَعْتَدْنَا لَهُمْ عَذَابَ ٱلسَّعِيرِ", "Andolsun biz en yakın göğü kandillerle (yıldızlarla) süsledik ve onları şeytanlar için atış taneleri kıldık. Onlara alevli ateş azabını hazırladık.", "And We have certainly beautified the nearest heaven with stars and have made [from] them what is thrown at the devils..."),
        Ayah(12, 5253, "إِنَّ ٱلَّذِينَ يَخْشَوْنَ رَبَّهُم بِٱلْغَيْبِ لَهُم مَّغْفِرَةٌۭ وَأَجْرٌۭ كَبِيرٌۭ", "Görmedikleri halde Rablerinden korkup ürperenler için bir bağışlanma ve büyük bir mükâfat vardır.", "Indeed, those who fear their Lord unseen will have forgiveness and great reward."),
        Ayah(13, 5254, "وَأَسِرُّوا۟ قَوْلَكُمْ أَوِ ٱجْهَرُوا۟ بِهِۦٓ ۖ إِنَّهُۥ عَلِيمٌۢ بِذَاتِ ٱلصُّدُورِ", "Sözünüzü ister gizleyin ister açığa vurun; şüphesiz O, kalplerin özünü hakkıyla bilendir.", "And conceal your speech or publicize it; indeed, He is Knowing of that within the breasts."),
        Ayah(14, 5255, "أَلَا يَعْلَمُ مَنْ خَلَقَ وَهُوَ ٱللَّطِيفُ ٱلْخَبِيرُ", "Yaratan hiç bilmez mi? O, en ince işleri bilen, her şeyden haberdar olandır.", "Does He who created not know, while He is the Subtle, the Acquainted?"),
        Ayah(23, 5264, "قُلْ هُوَ ٱلَّذِىٓ أَنشَأَكُمْ وَجَعَلَ لَكُمُ ٱلسَّمْعَ وَٱلْأَبْصَٰرَ وَٱلْأَفْـِٔدَةَ ۖ قَلِيلًۭا مَّا تَشْكُرُونَ", "De ki: Sizi yaratan, size işitme duyusu, gözler ve kalpler veren O'dur. Ne kadar da az şükrediyorsunuz!", "Say, 'It is He who has produced you and made for you hearing and vision and hearts; little are you grateful.'"),
        Ayah(30, 5271, "قُلْ أَرَءَيْتُمْ إِنْ أَصْبَحَ مَآؤُكُمْ غَوْرًۭا فَمَن يَأْتِيكُم بِمَآءٍۢ مَّعِينٍۭ", "De ki: 'Söyleyin bakalım; eğer suyunuz yerin dibine çekiliverse, size kim tatlı ve berrak bir akar su getirebilir?'", "Say, 'Have you considered: if your water was to become sunken [into the earth], then who could bring you flowing water?'")
    )

    private val nebe = listOf(
        Ayah(1, 5673, "عَمَّ يَتَسَآءَلُونَ", "Neyi soruşturup duruyorlar?", "About what are they asking one another?"),
        Ayah(2, 5674, "عَنِ ٱلنَّبَإِ ٱلْعَظِيمِ", "O büyük haberi (kıyameti) mi?", "About the great news -"),
        Ayah(3, 5675, "ٱلَّذِى هُمْ فِيهِ مُخْتَلِفُونَ", "Ki onlar onda derin bir ayrılığa düşmektedirler.", "That over which they are in disagreement."),
        Ayah(4, 5676, "كَلَّا سَيَعْلَمُونَ", "Hayır! Yakında bilecekler.", "No! They are going to know."),
        Ayah(5, 5677, "ثُمَّ كَلَّا سَيَعْلَمُونَ", "Sonra yine hayır! Yakında bilecekler.", "Then, no! They are going to know."),
        Ayah(6, 5678, "أَلَمْ نَجْعَلِ ٱلْأَرْضَ مِهَٰدًۭا", "Biz yeri bir beşik (döşek) yapmadık mı?", "Have We not made the earth a resting place?"),
        Ayah(7, 5679, "وَٱلْجِبَالَ أَوْتَادًۭا", "Dağları da birer kazık?", "And the mountains as stakes?"),
        Ayah(8, 5680, "وَخَلَقْنَٰكُمْ أَزْوَٰجًۭا", "Sizi çiftler halinde yarattık.", "And We created you in pairs"),
        Ayah(9, 5681, "وَجَعَلْنَا نَوْمَكُمْ سُبَاتًۭا", "Uykunuzu bir dinlenme kıldık.", "And made your sleep [a means for] rest"),
        Ayah(10, 5682, "وَجَعَلْنَا ٱلَّيْلَ لِبَاسًۭا", "Geceyi bir örtü yaptık.", "And made the night as clothing"),
        Ayah(11, 5683, "وَجَعَلْنَا ٱلنَّهَارَ مَعَاشًۭا", "Gündüzü de maişet (geçim) vakti kıldık.", "And made the day for livelihood"),
        Ayah(31, 5703, "إِنَّ لِلْمُتَّقِينَ مَفَازًا", "Şüphesiz takvâ sahipleri için bir kurtuluş ve murada eriş vardır;", "Indeed, for the righteous is attainment -"),
        Ayah(32, 5704, "حَدَآئِقَ وَأَعْنَٰبًۭا", "Nice bahçeler, üzüm bağları,", "Gardens and grapevines"),
        Ayah(40, 5712, "إِنَّآ أَنذَرْنَٰكُمْ عَذَابًۭا قَرِيبًۭا يَوْمَ يَنظُرُ ٱلْمَرْءُ مَا قَدَّمَتْ يَدَاهُ وَيَقُولُ ٱلْكَافِرُ يَٰلَيْتَنِى كُنتُ تُرَٰبًۢا", "Şüphesiz biz sizi yakın bir azap ile uyardık. O gün kişi kendi ellerinin önden gönderdiği amellere bakar ve kâfir: 'Ah, keşke ben bir toprak olsaydım!' der.", "Indeed, We have warned you of a near punishment on the Day when a man will observe what his hands have put forth and the disbeliever will say, 'Oh, I wish that I were dust!'")
    )

    private val naziat = listOf(
        Ayah(1, 5713, "وَٱلنَّٰزِعَٰتِ غَرْقًۭا", "Söküp koparanlara andolsun,", "By those [angels] who extract with violence"),
        Ayah(2, 5714, "وَٱلنَّٰشِطَٰتِ نَشْطًۭا", "Ruhları kolaylıkla çekip alanlara,", "And those who draw out gently"),
        Ayah(3, 5715, "وَٱلسَّٰبِحَٰتِ سَبْحًۭا", "Yüzüp yüzüp gidenlere,", "And those who glide as if swimming"),
        Ayah(4, 5716, "فَٱلسَّٰبِقَٰتِ سَبْقًۭا", "Yarıştıkça yarışanlara,", "And those who race each other in a race"),
        Ayah(5, 5717, "فَٱلْمُدَبِّرَٰتِ أَمْرًۭا", "Ve işleri çekip çevirenlere andolsun ki,", "And those who arrange [each] matter,"),
        Ayah(46, 5758, "كَأَنَّهُمْ يَوْمَ يَرَوْنَهَا لَمْ يَلْبَثُوٓا۟ إِلَّا عَشِيَّةً أَوْ ضُحَىٰهَا", "Onu gördükleri gün sanki dünyada sadece bir akşam vakti veya onun kuşluğu kadar kalmış gibi olurlar.", "It will be, on the Day they see it, as though they had not remained [in the world] except for an afternoon or a morning thereof.")
    )

    private val abese = listOf(
        Ayah(1, 5759, "عَبَسَ وَتَوَلَّىٰٓ", "Yüzünü ekşitti ve geri döndü,", "The Prophet frowned and turned away"),
        Ayah(2, 5760, "أَن جَآءَهُ ٱلْأَعْمَىٰ", "Yanına o âmâ geldi diye.", "Because there came to him the blind man"),
        Ayah(3, 5761, "وَمَا يُدْرِيكَ لَعَلَّهُۥ يَزَّكَّىٰٓ", "Nereden bilirsin, belki de o arınacaktı?", "But what would make you perceive, [O Muhammad], that perhaps he might be purified"),
        Ayah(34, 5792, "يَوْمَ يَفِرُّ ٱلْمَرْءُ مِنْ أَخِيهِ", "O gün kişi kardeşinden kaçar,", "On the Day a man will flee from his brother"),
        Ayah(35, 5793, "وَأُمِّهِۦ وَأَبِيهِ", "Annesinden ve babasından,", "And his mother and his father"),
        Ayah(36, 5794, "وَصَٰحِبَتِهِۦ وَبَنِيهِ", "Eşinden ve oğullarından.", "And his wife and his children:")
    )

    private val tekvir = listOf(
        Ayah(1, 5801, "إِذَا ٱلشَّمْسُ كُوِّرَتْ", "Güneş dürüldüğü zaman,", "When the sun is wrapped up [in darkness]"),
        Ayah(2, 5802, "وَإِذَا ٱلنُّجُومُ ٱنكَدَرَتْ", "Yıldızlar kararıp döküldüğü zaman,", "And when the stars fall, dispersing"),
        Ayah(3, 5803, "وَإِذَا ٱلْجِبَالُ سُيِّرَتْ", "Dağlar yürütüldüğü zaman,", "And when the mountains are removed")
    )

    private val infitar = listOf(
        Ayah(1, 5830, "إِذَا ٱلسَّمَآءُ ٱنفَطَرَتْ", "Gök yarıldığı zaman,", "When the sky breaks apart"),
        Ayah(2, 5831, "وَإِذَا ٱلْكَوَاكِبُ ٱنتَثَرَتْ", "Yıldızlar saçıldığı zaman,", "And when the stars are scattered"),
        Ayah(6, 5835, "يَٰٓأَيُّهَا ٱلْإِنسَٰنُ مَا غَرَّكَ بِرَبِّكَ ٱلْكَرِيمِ", "Ey insan! Seni cömert olan Rabbine karşı aldatan nedir?", "O mankind, what has deceived you concerning your Lord, the Generous,")
    )

    private val mutaffifin = listOf(
        Ayah(1, 5849, "وَيْلٌۭ لِّلْمُطَفِّفِينَ", "Eksik ölçüp tartanların vay haline!", "Woe to those who give less [than due],"),
        Ayah(2, 5850, "ٱلَّذِينَ إِذَا ٱكْتَالُوا۟ عَلَى ٱلنَّاسِ يَسْتَوْفُونَ", "Onlar insanlardan alırken tam ölçerler,", "Who, when they take measure from people, take in full."),
        Ayah(3, 5851, "وَإِذَا كَالُوهُمْ أَو وَّزَنُوهُمْ يُخْسِرُونَ", "Fakat kendileri onlara ölçüp tarttıklarında eksiltirler.", "But if they give by measure or by weight to them, they cause loss.")
    )

    private val insikak = listOf(
        Ayah(1, 5885, "إِذَا ٱلسَّمَآءُ ٱنشَقَّتْ", "Gök yarıldığı zaman,", "When the sky has split open"),
        Ayah(2, 5886, "وَأَذِنَتْ لِرَبِّهَا وَحُقَّتْ", "Ve Rabbine boyun eğip hakkını yerine getirdiği zaman,", "And has listened to its Lord and was obligated [to do so]"),
        Ayah(6, 5890, "يَٰٓأَيُّهَا ٱلْإِنسَٰنُ إِنَّكَ كَادِحٌ إِلَىٰ رَبِّكَ كَدْحًۭا فَمُلَٰقِيهِ", "Ey insan! Şüphesiz sen Rabbine doğru çaba üstüne çaba gösterirsin; sonunda O'na kavuşacaksın.", "O mankind, indeed you are laboring toward your Lord with [great] exertion and will meet it.")
    )

    private val buruc = listOf(
        Ayah(1, 5910, "وَٱلسَّمَآءِ ذَاتِ ٱلْبُرُوجِ", "Burçlarla dolu göğe andolsun,", "By the sky containing great stars"),
        Ayah(2, 5911, "وَٱلْيَوْمِ ٱلْمَوْعُودِ", "O vaad edilen güne andolsun,", "And [by] the promised Day"),
        Ayah(14, 5923, "وَهُوَ ٱلْغَفُورُ ٱلْوَدُودُ", "O, çok bağışlayandır, çok sevendir,", "And He is the Forgiving, the Affectionate,"),
        Ayah(21, 5930, "بَلْ هُوَ قُرْءَانٌۭ مَّجِيدٌۭ", "Hayır, o şerefli bir Kur'an'dır,", "But this is an honored Qur'an"),
        Ayah(22, 5931, "فِى لَوْحٍۢ مَّحْفُوظٍۭ", "Levh-i Mahfûz'dadır.", "[Inscribed] in a Preserved Slate.")
    )

    private val tarik = listOf(
        Ayah(1, 5932, "وَٱلسَّمَآءِ وَٱلطَّارِقِ", "Göğe ve Târık'a andolsun,", "By the sky and the night comer -"),
        Ayah(2, 5933, "وَمَآ أَدْرَىٰكَ مَا ٱلطَّارِقُ", "Târık'ın ne olduğunu sen bilir misin?", "And what can make you know what is the night comer?"),
        Ayah(3, 5934, "ٱلنَّجْمُ ٱلثَّاقِبُ", "O, karanlığı delen parlak bir yıldızdır.", "It is the piercing star -")
    )

    // 87. A'lâ (Full 19 Ayahs)
    private val ala = listOf(
        Ayah(1, 5949, "سَبِّحِ ٱسْمَ رَبِّكَ ٱلْأَعْلَى", "Yüce Rabbinin ismini tesbih et,", "Exalt the name of your Lord, the Most High,"),
        Ayah(2, 5950, "ٱلَّذِى خَلَقَ فَسَوَّىٰ", "O ki yaratıp kusursuz bir düzen verdi,", "Who created and proportioned"),
        Ayah(3, 5951, "وَٱلَّذِى قَدَّرَ فَهَدَىٰ", "Takdir edip yol gösterdi,", "And who destined and [then] guided"),
        Ayah(4, 5952, "وَٱلَّذِىٓ أَخْرَجَ ٱلْمَرْعَىٰ", "Yemyeşil otlağı çıkardı,", "And who brings out the pasture"),
        Ayah(5, 5953, "فَجَعَلَهُۥ غُثَآءً أَحْوَىٰ", "Sonra da onu kapkara bir kuru çerçöpe çevirdi.", "And [then] makes it black stubble."),
        Ayah(6, 5954, "سَنُقْرِئُكَ فَلَا تَنسَىٰٓ", "Sana Kur'an'ı okutacağız ve sen unutmayacaksın;", "We will make you recite, [O Muhammad], and you will not forget,"),
        Ayah(7, 5955, "إِلَّا مَا شَآءَ ٱللَّهُ ۚ إِنَّهُۥ يَعْلَمُ ٱلْجَهْرَ وَمَا يَخْفَىٰ", "Allah'ın dilediği müstesna. Şüphesiz O, açığı da gizleneni de bilir.", "Except what Allah should will. Indeed, He knows what is declared and what is hidden."),
        Ayah(8, 5956, "وَنُيَسِّرُكَ لِلْيُسْرَىٰ", "Seni en kolay olana muvaffak kılacağız.", "And We will ease you toward ease."),
        Ayah(9, 5957, "فَذَكِّرْ إِن نَّفَعَتِ ٱلذِّكْرَىٰ", "O halde öğüt fayda verirse öğüt ver!", "So remind, if the reminder should benefit;"),
        Ayah(10, 5958, "سَيَذَّكَّرُ مَن يَخْشَىٰ", "Allah'tan korkan öğüt alacaktır,", "He who fears [Allah] will be reminded."),
        Ayah(11, 5959, "وَيَتَجَنَّبُهَا ٱلْأَشْقَى", "En bedbaht olan ise ondan kaçınacaktır;", "But the wretched one will avoid it -"),
        Ayah(12, 5960, "ٱلَّذِى يَصْلَى ٱلنَّارَ ٱلْكُبْرَىٰ", "O ki en büyük ateşe girecektir.", "[He] who will [enter and] burn in the greatest Fire,"),
        Ayah(13, 5961, "ثُمَّ لَا يَمُوتُ فِيهَا وَلَا يَحْيَىٰ", "Sonra orada ne ölür ne de dirilir.", "Neither dying therein nor living."),
        Ayah(14, 5962, "قَدْ أَفْلَحَ مَن تَزَكَّىٰ", "Doğrusu arınan kurtuluşa ermiştir,", "He has certainly succeeded who purifies himself"),
        Ayah(15, 5963, "وَذَكَرَ ٱسْمَ رَبِّهِۦ فَصَلَّىٰ", "Rabbinin ismini anıp namaz kılan.", "And mentions the name of his Lord and prays."),
        Ayah(16, 5964, "بَلْ تُؤْثِرُونَ ٱلْحَيَوٰةَ ٱلدُّنْيَا", "Fakat siz dünya hayatını tercih ediyorsunuz.", "But you prefer the worldly life,"),
        Ayah(17, 5965, "وَٱلْءَاخِرَةُ خَيْرٌۭ وَأَبْقَىٰٓ", "Oysa ahiret daha hayırlı ve daha kalıcıdır.", "While the Hereafter is better and more enduring."),
        Ayah(18, 5966, "إِنَّ هَٰذَا لَفِى ٱلصُّحُفِ ٱلْأُولَىٰ", "Şüphesiz bu hükümler ilk sahifelerde de vardır;", "Indeed, this is in the former scriptures,"),
        Ayah(19, 5967, "صُحُفِ إِبْرَٰهِيمَ وَمُوسَىٰ", "İbrâhîm ve Mûsâ'nın sahifelerinde.", "The scriptures of Abraham and Moses.")
    )

    private val gasiye = listOf(
        Ayah(1, 5968, "هَلْ أَتَىٰكَ حَدِيثُ ٱلْغَاشِيَةِ", "Her şeyi kuşatan kıyametin haberi sana geldi mi?", "Has there reached you the report of the Overwhelming [event]?"),
        Ayah(21, 5988, "فَذَكِّرْ إِنَّمَآ أَنتَ مُذَكِّرٌۭ", "Öyleyse öğüt ver; sen ancak bir öğüt vericisin,", "So remind, [O Muhammad]; you are only a reminder."),
        Ayah(22, 5989, "لَّسْتَ عَلَيْهِم بِمُصَيْطِرٍ", "Onların üzerinde zorba bir baskıcı değilsin.", "You are not over them a controller.")
    )

    private val fecr = listOf(
        Ayah(1, 5994, "وَٱلْفَجْرِ", "Tan yerinin ağarmasına andolsun,", "By the dawn"),
        Ayah(2, 5995, "وَلَيَالٍ عَشْرٍۢ", "On geceye andolsun,", "And [by] ten nights"),
        Ayah(27, 6020, "يَٰٓأَيَّتُهَا ٱلنَّفْسُ ٱلْمُطْمَئِنَّةُ", "Ey huzura kavuşmuş nefis!", "To the righteous it will be said, 'O reassured soul,'"),
        Ayah(28, 6021, "ٱرْجِعِىٓ إِلَىٰ رَبِّكِ رَاضِيَةًۭ مَّرْضِيَّةًۭ", "Dön Rabbine, O'ndan razı ve O'nun rızasını kazanmış olarak!", "'Return to your Lord, well-pleased and pleasing [to Him],'"),
        Ayah(29, 6022, "فَٱدْخُلِى فِى عِبَٰدِى", "Gir kullarımın arasına,", "'And enter among My [righteous] servants'"),
        Ayah(30, 6023, "وَٱدْخُلِى جَنَّتِى", "Ve gir cennetime!", "'And enter My Paradise.'")
    )

    private val beled = listOf(
        Ayah(1, 6024, "لَآ أُقْسِمُ بِهَٰذَا ٱلْبَلَدِ", "Andolsun bu kutsal beldeye (Mekke'ye),", "I swear by this city, Makkah -"),
        Ayah(2, 6025, "وَأَنتَ حِلٌّۢ بِهَٰذَا ٱلْبَلَدِ", "Ki sen bu beldede serbestçe oturmaktasın,", "And you, [O Muhammad], are free of restriction in this city -"),
        Ayah(17, 6040, "ثُمَّ كَانَ مِنَ ٱلَّذِينَ ءَامَنُوا۟ وَتَوَاصَوْا۟ بِٱلصَّبْرِ وَتَوَاصَوْا۟ بِٱلْمَرْحَمَةِ", "Sonra iman edenlerden, birbirine sabrı ve merhameti tavsiye edenlerden olmak...", "And then being among those who believed and advised one another to patience and advised one another to compassion.")
    )

    // 91. Şems (Full 15 Ayahs)
    private val sems = listOf(
        Ayah(1, 6044, "وَٱلشَّمْسِ وَضُحَىٰهَا", "Güneşe ve kuşluk vaktindeki aydınlığına andolsun,", "By the sun and its brightness"),
        Ayah(2, 6045, "وَٱلْقَمَرِ إِذَا تَلَىٰهَا", "Onu takip ettiği zaman aya,", "And [by] the moon when it follows it"),
        Ayah(3, 6046, "وَٱلنَّهَارِ إِذَا جَلَّىٰهَا", "Onu ortaya çıkardığı zaman gündüze,", "And [by] the day when it displays it"),
        Ayah(4, 6047, "وَٱلَّيْلِ إِذَا يَغْشَىٰهَا", "Onu bürüdüğü zaman geceye,", "And [by] the night when it covers it"),
        Ayah(5, 6048, "وَٱلسَّمَآءِ وَمَا بَنَىٰهَا", "Göğe ve onu bina edene,", "And [by] the sky and He who constructed it"),
        Ayah(6, 6049, "وَٱلْأَرْضِ وَمَا طَحَىٰهَا", "Yere ve onu yayıp döşeyene,", "And [by] the earth and He who spread it"),
        Ayah(7, 6050, "وَنَفْسٍۢ وَمَا سَوَّىٰهَا", "Nefse ve onu düzgün bir biçimde şekillendirene,", "And [by] the soul and He who proportioned it"),
        Ayah(8, 6051, "فَأَلْهَمَهَا فُجُورَهَا وَتَقْوَىٰهَا", "Sonra da ona kötülüğünü ve takvasını ilham edene andolsun ki,", "And inspired it [with discernment of] its wickedness and its righteousness,"),
        Ayah(9, 6052, "قَدْ أَفْلَحَ مَن زَكَّىٰهَا", "Nefsini arındıran elbette kurtuluşa ermiştir.", "He has succeeded who purifies it,"),
        Ayah(10, 6053, "وَقَدْ خَابَ مَن دَسَّىٰهَا", "Onu günaha gömüp kirleten ise ziyana uğramıştır.", "And he has failed who instills it [with corruption]."),
        Ayah(11, 6054, "كَذَّبَتْ ثَمُودُ بِطَغْوَىٰهَآ", "Semûd kavmi azgınlığı yüzünden hakikati yalanladı,", "Thamud denied [their prophet] by reason of their transgression,"),
        Ayah(12, 6055, "إِذِ ٱنۢبَعَثَ أَشْقَىٰهَا", "En bedbahtları ileri atıldığı zaman,", "When the most wretched of them was sent forth."),
        Ayah(13, 6056, "فَقَالَ لَهُمْ رَسُولُ ٱللَّهِ نَاقَةَ ٱللَّهِ وَسُقْيَٰهَا", "Allah'ın elçisi onlara: 'Allah'ın devesine ve onun su nöbetine dokunmayın!' demişti.", "And the messenger of Allah [Salih] said to them, '[Do not harm] the she-camel of Allah or [prevent her from] her drink.'"),
        Ayah(14, 6057, "فَكَذَّبُوهُ فَعَقَرُوهَا فَدَمْدَمَ عَلَيْهِمْ رَبُّهُم بِذَنۢبِهِمْ فَسَوَّىٰهَا", "Fakat onu yalanladılar ve deveyi boğazladılar. Rableri de günahları yüzünden başlarına azap geçirip orayı yerle bir etti.", "But they denied him and hamstrung her. So their Lord brought down upon them destruction for their sin and made it equal [upon all of them]."),
        Ayah(15, 6058, "وَلَا يَخَافُ عُقْبَٰهَا", "Allah, bunun akıbetinden asla çekinmez.", "And He does not fear the consequence thereof.")
    )

    // 92. Leyl (Full 21 Ayahs)
    private val leyl = listOf(
        Ayah(1, 6059, "وَٱلَّيْلِ إِذَا يَغْشَىٰ", "Karanlığıyla ortalığı bürüdüğü zaman geceye andolsun,", "By the night when it covers"),
        Ayah(2, 6060, "وَٱلنَّهَارِ إِذَا تَجَلَّىٰ", "Aydınlandığı zaman gündüze,", "And [by] the day when it appears"),
        Ayah(3, 6061, "وَمَا خَلَقَ ٱلذَّكَرَ وَٱلْأُنثَىٰٓ", "Erkeği ve dişiyi yaratana andolsun ki,", "And [by] He who created the male and female,"),
        Ayah(4, 6062, "إِنَّ سَعْيَكُمْ لَشَتَّىٰ", "Şüphesiz sizin çabalarınız çeşit çeşittir.", "Indeed, your efforts are diverse."),
        Ayah(5, 6063, "فَأَمَّا مَنْ أَعْطَىٰ وَٱتَّقَىٰ", "Kim verir ve Allah'a karşı gelmekten sakınırsa,", "As for he who gives and fears Allah"),
        Ayah(6, 6064, "وَصَدَّقَ بِٱلْحُسْنَىٰ", "Ve en güzel olanı (kelime-i tevhidi ve cenneti) tasdik ederse,", "And believes in the best [reward],"),
        Ayah(7, 6065, "فَسَنُيَسِّرُهُۥ لِلْيُسْرَىٰ", "Biz de onu en kolay olana (huzur ve cennete) muvaffak kılarız.", "We will ease him toward ease."),
        Ayah(8, 6066, "وَأَمَّا مَنۢ بَخِلَ وَٱسْتَغْنَىٰ", "Kim de cimrilik eder ve kendini müstağni sayarsa,", "But as for he who withholds and considers himself free of need"),
        Ayah(9, 6067, "وَكَذَّبَ بِٱلْحُسْنَىٰ", "Ve en güzel olanı yalanlarsa,", "And denies the best [reward],"),
        Ayah(10, 6068, "فَسَنُيَسِّرُهُۥ لِلْعُسْرَىٰ", "Onu da en zor olana (azaba) sürükleriz.", "We will ease him toward difficulty."),
        Ayah(11, 6069, "وَمَا يُغْنِى عَنْهُ مَالُهُۥٓ إِذَا تَرَدَّىٰٓ", "Çukura yuvarlandığı zaman malı ona hiçbir fayda sağlamaz.", "And what will his wealth avail him when he falls?"),
        Ayah(12, 6070, "إِنَّ عَلَيْنَا لَلْهُدَىٰ", "Şüphesiz doğru yolu göstermek bize aittir.", "Indeed, [incumbent] upon Us is guidance."),
        Ayah(13, 6071, "وَإِنَّ لَنَا لَلْءَاخِرَةَ وَٱلْأُولَىٰ", "Şüphesiz ahiret de dünya da bizimdir.", "And indeed, to Us belongs the Hereafter and the first [life]."),
        Ayah(14, 6072, "فَأَنذَرْتُكُمْ نَارًۭا تَلَظَّىٰ", "Sizi alev saçan bir ateşe karşı uyardım.", "So I have warned you of a Fire which is blazing."),
        Ayah(15, 6073, "لَا يَصْلَىٰهَآ إِلَّا ٱلْأَشْقَى", "Oraya ancak en bedbaht olan girer;", "None will [enter to] burn therein except the most wretched one"),
        Ayah(16, 6074, "ٱلَّذِى كَذَّبَ وَتَوَلَّىٰ", "O ki yalanlamış ve yüz çevirmiştir.", "Who had denied and turned away."),
        Ayah(17, 6075, "وَسَيُجَنَّبُهَا ٱلْأَتْقَى", "En çok sakınan (takvâ sahibi) ise ondan uzak tutulacaktır;", "But the righteous one will beجنب avoided it -"),
        Ayah(18, 6076, "ٱلَّذِى يُؤْتِى مَالَهُۥ يَتَزَكَّىٰ", "O ki arınmak için malını hayra verir.", "[He] who gives [from] his wealth to purify himself"),
        Ayah(19, 6077, "وَمَا لِأَحَدٍ عِندَهُۥ مِن نِّعْمَةٍۢ تُجْزَىٰٓ", "Onun yanında hiç kimsenin karşılığı verilecek bir minneti yoktur;", "And not [giving] for anyone who has [done him] a favor to be rewarded"),
        Ayah(20, 6078, "إِلَّا ٱبْتِغَآءَ وَجْهِ رَبِّهِ ٱلْأَعْلَىٰ", "O sadece yüce Rabbinin rızasını aramak için verir.", "But only seeking the countenance of his Lord, Most High."),
        Ayah(21, 6079, "وَلَسَوْفَ يَرْضَىٰ", "Ve elbette yakında hoşnut olacaktır.", "And he is going to be satisfied.")
    )

    // 93. Duhâ (Full 11 Ayahs)
    private val duha = listOf(
        Ayah(1, 6080, "وَٱلضُّحَىٰ", "Kuşluk vaktine andolsun,", "By the morning brightness"),
        Ayah(2, 6081, "وَٱلَّيْلِ إِذَا سَجَىٰ", "Sükûnete kavuştuğunda geceye andolsun ki,", "And [by] the night when it covers with darkness,"),
        Ayah(3, 6082, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Rabbin seni bırakmadı ve sana darılmadı.", "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you]."),
        Ayah(4, 6083, "وَلَلْءَاخِرَةُ خَيْرٌۭ لَّكَ مِنَ ٱلْأُولَىٰ", "Ahiret senin için dünyadan elbette daha hayırlıdır.", "And the Hereafter is better for you than the first [life]."),
        Ayah(5, 6084, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰٓ", "Rabbin sana lütfedecek ve sen de hoşnut olacaksın.", "And your Lord is going to give you, and you will be satisfied."),
        Ayah(6, 6085, "أَلَمْ يَجِدْكَ يَتِيمًۭا فَـَٔاوَىٰ", "O seni yetim bulup barındırmadı mı?", "Did He not find you an orphan and give [you] refuge?"),
        Ayah(7, 6086, "وَوَجَدَكَ ضَآلًّۭا فَهَدَىٰ", "Seni yolunu kaybetmiş bulup doğru yola iletmedi mi?", "And He found you lost and guided [you],"),
        Ayah(8, 6087, "وَوَجَدَكَ عَآئِلًۭا فَأَغْنَىٰ", "Seni ihtiyaç içinde bulup zengin kılmadı mı?", "And He found you poor and made [you] self-sufficient."),
        Ayah(9, 6088, "فَأَمَّا ٱلْيَتِيمَ فَلَا تَقْهَرْ", "Öyleyse yetimi sakın hor görme,", "So as for the orphan, do not oppress [him]."),
        Ayah(10, 6089, "وَأَمَّا ٱلسَّآئِلَ فَلَا تَنْهَرْ", "El açıp isteyeni sakın azarlama,", "And as for the petitioner, do not repel [him]."),
        Ayah(11, 6090, "وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ", "Ve Rabbinin nimetini minnetle yâd et!", "But as for the favor of your Lord, report [it].")
    )

    // 94. İnşirâh (Full 8 Ayahs)
    private val insirah = listOf(
        Ayah(1, 6091, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Biz senin göğsünü açıp ferahlatmadık mı?", "Did We not expand for you, [O Muhammad], your breast?"),
        Ayah(2, 6092, "وَوَضَعْنَا عَنكَ وِزْرَكَ", "Ağır yükünü üzerinden indirmedik mi?", "And We removed from you your burden"),
        Ayah(3, 6093, "ٱلَّذِىٓ أَنقَضَ ظَهْرَكَ", "Ki o yük belini bükmüştü.", "Which had weighed upon your back"),
        Ayah(4, 6094, "وَرَفَعْنَا لَكَ ذِكْرَكَ", "Senin şanını ve zikrini yüceltmedik mi?", "And raised high for you your repute."),
        Ayah(5, 6095, "فَإِنَّ مَعَ ٱلْعُسْرِ يُسْرًا", "Elbette her zorlukla beraber bir kolaylık vardır.", "For indeed, with hardship [will be] ease."),
        Ayah(6, 6096, "إِنَّ مَعَ ٱلْعُسْرِ يُسْرًا", "Gerçekten her güçlükle beraber kesin bir kolaylık vardır.", "Indeed, with hardship [will be] ease."),
        Ayah(7, 6097, "فَإِذَا فَرَغْتَ فَٱنصَبْ", "Öyleyse bir işi bitirince hemen diğerine koyul,", "So when you have finished [your duties], labor hard"),
        Ayah(8, 6098, "وَإِلَىٰ رَبِّكَ فَٱرْغَب", "Ve yalnız Rabbine yönelip yalvar!", "And to your Lord direct [your] longing.")
    )

    // 95. Tîn (Full 8 Ayahs)
    private val tin = listOf(
        Ayah(1, 6099, "وَٱلتِّينِ وَٱلزَّيْتُونِ", "İncire ve zeytine andolsun,", "By the fig and the olive"),
        Ayah(2, 6100, "وَطُورِ سِينِينَ", "Sînâ Dağı'na,", "And [by] Mount Sinai"),
        Ayah(3, 6101, "وَهَٰذَا ٱلْبَلَدِ ٱلْأَمِينِ", "Ve bu güvenli beldeye (Mekke'ye) andolsun ki,", "And [by] this secure city [Makkah],"),
        Ayah(4, 6102, "لَقَدْ خَلَقْنَا ٱلْإِنسَٰنَ فِىٓ أَحْسَنِ تَقْوِيمٍۢ", "Biz insanı en güzel biçimde yarattık.", "We have certainly created man in the best of stature;"),
        Ayah(5, 6103, "ثُمَّ رَدَدْنَٰهُ أَسْفَلَ سَٰفِلِينَ", "Sonra onu aşağıların en aşağısına indirdik.", "Then We returned him to the lowest of the low,"),
        Ayah(6, 6104, "إِلَّا ٱلَّذِينَ ءَامَنُوا۟ وَعَمِلُوا۟ ٱلصَّٰلِحَٰتِ فَلَهُمْ أَجْرٌ غَيْرُ مَمْنُونٍۢ", "Ancak iman edip salih amel işleyenler müstesnadır; onlar için kesintisiz bir mükâfat vardır.", "Except for those who believe and do righteous deeds, for they will have a reward uninterrupted."),
        Ayah(7, 6105, "فَمَا يُكَذِّبُكَ بَعْدُ بِٱلدِّينِ", "Öyleyse artık hesap gününü sana yalanlatan nedir?", "So what yet causes you to deny the Recompense?"),
        Ayah(8, 6106, "أَلَيْسَ ٱللَّهُ بِأَحْكَمِ ٱلْحَٰكِمِينَ", "Allah, hüküm verenlerin en üstünü değil midir?", "Is not Allah the most just of judges?")
    )

    // 96. Alak (Full 19 Ayahs)
    private val alak = listOf(
        Ayah(1, 6107, "ٱقْرَأْ بِٱسْمِ رَبِّكَ ٱلَّذِى خَلَقَ", "Yaratan Rabbinin adıyla oku!", "Recite in the name of your Lord who created -"),
        Ayah(2, 6108, "خَلَقَ ٱلْإِنسَٰنَ مِنْ عَلَقٍ", "O, insanı bir alaktan (embriyodan) yarattı.", "Created man from a clinging substance."),
        Ayah(3, 6109, "ٱقْرَأْ وَرَبُّكَ ٱلْأَكْرَمُ", "Oku! Senin Rabbin sonsuz kerem sahibidir;", "Recite, and your Lord is the most Generous -"),
        Ayah(4, 6110, "ٱلَّذِى عَلَّمَ بِٱلْقَلَمِ", "O ki kalemle yazmayı öğretti,", "Who taught by the pen -"),
        Ayah(5, 6111, "عَلَّمَ ٱلْإِنسَٰنَ مَا لَمْ يَعْلَمْ", "İnsana bilmediği şeyleri öğretti.", "Taught man that which he knew not."),
        Ayah(6, 6112, "كَلَّآ إِنَّ ٱلْإِنسَٰنَ لَيَطْغَىٰٓ", "Hayır! Gerçekten insan azar,", "No! [But] indeed, man transgresses"),
        Ayah(7, 6113, "أَن رَّءَاهُ ٱسْتَغْنَىٰٓ", "Kendini müstağni (zengin ve kendine yeterli) gördüğü için.", "Because he sees himself self-sufficient."),
        Ayah(8, 6114, "إِنَّ إِلَىٰ رَبِّكَ ٱلرُّجْعَىٰٓ", "Şüphesiz dönüş yalnız Rabbinedir.", "Indeed, to your Lord is the return."),
        Ayah(9, 6115, "أَرَءَيْتَ ٱلَّذِى يَنْهَىٰ", "Gördün mü şu engelleyeni,", "Have you seen the one who forbids"),
        Ayah(10, 6116, "عَبْدًا إِذَا صَلَّىٰٓ", "Namaz kıldığı zaman bir kulu?", "A servant when he prays?"),
        Ayah(11, 6117, "أَرَءَيْتَ إِن كَانَ عَلَى ٱلْهُدَىٰٓ", "Ne dersin, ya o kul doğru yol üzere ise,", "Have you seen if he is upon guidance"),
        Ayah(12, 6118, "أَوْ أَمَرَ بِٱلتَّقْوَىٰٓ", "Yahut Allah'a karşı gelmekten sakınmayı emrediyorsa?", "Or enjoins righteousness?"),
        Ayah(13, 6119, "أَرَءَيْتَ إِن كَذَّبَ وَتَوَلَّىٰٓ", "Ne dersin, ya o engelleyen yalanlıyor ve yüz çeviriyorsa?", "Have you seen if he denies and turns away -"),
        Ayah(14, 6120, "أَلَمْ يَعْلَم بِأَنَّ ٱللَّهَ يَرَىٰ", "Allah'ın her şeyi gördüğünü bilmiyor mu?", "Does he not know that Allah sees?"),
        Ayah(15, 6121, "كَلَّا لَئِن لَّمْ يَنتَهِ لَنَسْفَعًۢا بِٱلنَّاصِيَةِ", "Hayır! Eğer vazgeçmezse, derhal onu alnından (perçeminden) yakalarız;", "No! If he does not desist, We will surely drag him by the forelock -"),
        Ayah(16, 6122, "نَاصِيَةٍۢ كَٰذِبَةٍ خَاطِئَةٍۢ", "O yalancı ve günahkâr alnından!", "A lying, sinning forelock."),
        Ayah(17, 6123, "فَلْيَدْعُ نَادِيَهُۥ", "O zaman kurultayını (taraftarlarını) çağırsın!", "Then let him call his associates;"),
        Ayah(18, 6124, "سَنَدْعُ ٱلزَّبَانِيَةَ", "Biz de zebânîleri çağıracağız!", "We will call the angels of Hell."),
        Ayah(19, 6125, "كَلَّا لَا تُطِعْهُ وَٱسْجُدْ وَٱقْتَرِب ۩", "Hayır! Ona boyun eğme; Rabbine secde et ve O'na yaklaş!", "No! Do not obey him. But prostrate and draw near [to Allah].")
    )

    // 97. Kadir (Full 5 Ayahs)
    private val kadir = listOf(
        Ayah(1, 6126, "إِنَّآ أَنزَلْنَٰهُ فِى لَيْلَةِ ٱلْقَدْرِ", "Şüphesiz biz onu (Kur'an'ı) Kadir Gecesi'nde indirdik.", "Indeed, We sent the Qur'an down during the Night of Decree."),
        Ayah(2, 6127, "وَمَآ أَدْرَىٰكَ مَا لَيْلَةُ ٱلْقَدْرِ", "Kadir Gecesi'nin ne olduğunu sen bilir misin?", "And what can make you know what is the Night of Decree?"),
        Ayah(3, 6128, "لَيْلَةُ ٱلْقَدْرِ خَيْرٌۭ مِّنْ أَلْفِ شَهْرٍۢ", "Kadir Gecesi bin aydan daha hayırlıdır.", "The Night of Decree is better than a thousand months."),
        Ayah(4, 6129, "تَنَزَّلُ ٱلْمَلَٰٓئِكَةُ وَٱلرُّوحُ فِيهَا بِإِذْنِ رَبِّهِم مِّن كُلِّ أَمْرٍۢ", "Melekler ve Ruh (Cebrail) o gecede Rablerinin izniyle her türlü iş için iner de iner.", "The angels and the Spirit descend therein by permission of their Lord for every matter."),
        Ayah(5, 6130, "سَلَٰمٌ هِىَ حَتَّىٰ مَطْلَعِ ٱلْفَجْرِ", "O gece, tan yeri ağarana kadar süren bir selâmettir.", "Peace it is until the emergence of dawn.")
    )

    // 98. Beyyine (Full 8 Ayahs)
    private val beyyine = listOf(
        Ayah(1, 6131, "لَمْ يَكُنِ ٱلَّذِينَ كَفَرُوا۟ مِنْ أَهْلِ ٱلْكِتَٰبِ وَٱلْمُشْرِكِينَ مُنفَكِّينَ حَتَّىٰ تَأْتِيَهُمُ ٱلْبَيِّنَةُ", "Kitap ehlinden ve müşriklerden inkâr edenler, kendilerine apaçık delil gelinceye kadar vazgeçecek değillerdi.", "Those who disbelieved among the People of the Scripture and the polytheists were not to be parted [from misbelief] until there came to them clear evidence -"),
        Ayah(2, 6132, "رَسُولٌۭ مِّنَ ٱللَّهِ يَتْلُوا۟ صُحُفًۭا مُّطَهَّرَةًۭ", "O delil, tertemiz sahifeleri okuyan, Allah tarafından gönderilmiş bir elçidir.", "A Messenger from Allah, reciting purified scriptures"),
        Ayah(3, 6133, "فِيهَا كُتُبٌۭ قَيِّمَةٌۭ", "O sahifelerde dosdoğru ve sağlam hükümler vardır.", "Within which are correct writings."),
        Ayah(4, 6134, "وَمَا تَفَرَّقَ ٱلَّذِينَ أُوتُوا۟ ٱلْكِتَٰبَ إِلَّا مِنۢ بَعْدِ مَا جَآءَتْهُمُ ٱلْبَيِّنَةُ", "Kendilerine kitap verilenler, ancak o apaçık delil kendilerine geldikten sonra ayrılığa düştüler.", "Nor did those who were given the Scripture become divided until after there had come to them clear evidence."),
        Ayah(5, 6135, "وَمَآ أُمِرُوٓا۟ إِلَّا لِيَعْبُدُوا۟ ٱللَّهَ مُخْلِصِينَ لَهُ ٱلدِّينَ حُنَفَآءَ وَيُقِيمُوا۟ ٱلصَّلَوٰةَ وَيُؤْتُوا۟ ٱلزَّكَوٰةَ ۚ وَذَٰلِكَ دِينُ ٱلْقَيِّمَةِ", "Halbuki onlara ancak, dini yalnız Allah'a has kılarak, hakka yönelen kimseler olarak O'na ibadet etmeleri, namazı kılmaları ve zekâtı vermeleri emrolunmuştu. İşte dosdoğru din budur.", "And they were not commanded except to worship Allah, [being] sincere to Him in religion, inclining to truth, and to establish prayer and to give zakah. And that is the correct religion."),
        Ayah(6, 6136, "إِنَّ ٱلَّذِينَ كَفَرُوا۟ مِنْ أَهْلِ ٱلْكِتَٰبِ وَٱلْمُشْرِكِينَ فِى نَارِ جَهَنَّمَ خَٰلِدِينَ فِيهَآ ۚ أُو۟لَٰٓئِكَ هُمْ شَرُّ ٱلْبَرِيَّةِ", "Şüphesiz kitap ehlinden ve müşriklerden inkâr edenler, içinde ebedî kalmak üzere cehennem ateşindedirler. İşte onlar yaratılmışların en kötüsüdürler.", "Indeed, they who disbelieved among the People of the Scripture and the polytheists will be in the fire of Hell, abiding eternally therein. Those are the worst of creatures."),
        Ayah(7, 6137, "إِنَّ ٱلَّذِينَ ءَامَنُوا۟ وَعَمِلُوا۟ ٱلصَّٰلِحَٰتِ أُو۟لَٰٓئِكَ هُمْ خَيْرُ ٱلْبَرِيَّةِ", "İman edip salih ameller işleyenler ise; işte onlar da yaratılmışların en hayırlısıdırlar.", "Indeed, they who have believed and done righteous deeds - those are the best of creatures."),
        Ayah(8, 6138, "جَزَآؤُهُمْ عِندَ رَبِّهِمْ جَنَّٰتُ عَدْنٍۢ تَجْرِى مِن تَحْتِهَا ٱلْأَنْهَٰرُ خَٰلِدِينَ فِيهَآ أَبَدًۭا ۖ رَّضِىَ ٱللَّهُ عَنْهُمْ وَرَضُوا۟ عَنْهُ ۚ ذَٰلِكَ لِمَنْ خَشِىَ رَبَّهُۥ", "Onların Rableri katındaki mükâfatları, içinden ırmaklar akan, içinde ebedî kalacakları Adn cennetleridir. Allah onlardan razı olmuştur, onlar da O'ndan razı olmuşlardır. İşte bu, Rabbinden korkup saygı duyan kimse içindir.", "Their reward with Allah will be gardens of perpetual residence beneath which rivers flow, wherein they will abide forever, Allah being pleased with them and they with Him. That is for whoever has feared his Lord.")
    )

    // 99. Zilzâl (Full 8 Ayahs)
    private val zilzal = listOf(
        Ayah(1, 6139, "إِذَا زُلْزِلَتِ ٱلْأَرْضُ زِلْزَالَهَا", "Yer o dehşetli sarsıntısıyla sarsıldığı zaman,", "When the earth is shaken with its [final] earthquake"),
        Ayah(2, 6140, "وَأَخْرَجَتِ ٱلْأَرْضُ أَثْقَالَهَا", "Ve yer ağırlıklarını dışarı attığı zaman,", "And the earth discharges its burdens"),
        Ayah(3, 6141, "وَقَالَ ٱلْإِنسَٰنُ مَا لَهَا", "Ve insan: 'Ona ne oluyor?' dediği vakit;", "And man says, 'What is [wrong] with it?' -"),
        Ayah(4, 6142, "يَوْمَئِذٍۢ تُحَدِّثُ أَخْبَارَهَا", "İşte o gün yer, kendi haberlerini anlatır,", "That Day, it will report its news"),
        Ayah(5, 6143, "بِأَنَّ رَبَّكَ أَوْحَىٰ لَهَا", "Çünkü Rabbin ona öyle vahyetmiştir.", "Because your Lord has commanded it."),
        Ayah(6, 6144, "يَوْمَئِذٍۢ يَصْدُرُ ٱلنَّاسُ أَشْتَاتًۭا لِّيُرَوْا۟ أَعْمَٰلَهُمْ", "O gün insanlar amellerinin kendilerine gösterilmesi için bölük bölük kabirlerinden çıkarlar.", "That Day, the people will depart separated [into categories] to be shown [the result of] their deeds."),
        Ayah(7, 6145, "فَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ خَيْرًۭا يَرَهُۥ", "Kim zerre miktarı bir hayır işlemişse onu görür.", "So whoever does an atom's weight of good will see it,"),
        Ayah(8, 6146, "وَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍۢ شَرًّۭا يَرَهُۥ", "Ve kim zerre miktarı bir kötülük işlemişse onu görür.", "And whoever does an atom's weight of evil will see it.")
    )

    // 100. Âdiyât (Full 11 Ayahs)
    private val adiyat = listOf(
        Ayah(1, 6147, "وَٱلْعَٰدِيَٰتِ ضَبْحًۭا", "Soluk soluğa koşan atlara andolsun,", "By the racers, panting,"),
        Ayah(2, 6148, "فَٱلْمُورِيَٰتِ قَدْحًۭا", "Tırnaklarıyla çakarak kıvılcım saçanlara,", "And the producers of sparks [when] striking"),
        Ayah(3, 6149, "فَٱلْمُغِيرَٰتِ صُبْحًۭا", "Sabah vakti akına çıkanlara,", "And the chargers at dawn,"),
        Ayah(4, 6150, "فَأَثَرْنَ بِهِۦ نَقْعًۭا", "Orada tozu dumana katanlara,", "Stirring up thereby [clouds of] dust,"),
        Ayah(5, 6151, "فَوَسَطْنَ بِهِۦ جَمْعًا", "Derken o topluluğun ortasına dalanlara andolsun ki,", "Arriving thereby in the center collectively,"),
        Ayah(6, 6152, "إِنَّ ٱلْإِنسَٰنَ لِرَبِّهِۦ لَكَنُودٌۭ", "Gerçekten insan Rabbine karşı çok nankördür.", "Indeed mankind, to his Lord, is ungrateful."),
        Ayah(7, 6153, "وَإِنَّهُۥ عَلَىٰ ذَٰلِكَ لَشَهِيدٌۭ", "Şüphesiz kendisi de buna bizzat şahittir.", "And indeed, he is to that a witness."),
        Ayah(8, 6154, "وَإِنَّهُۥ لِحُبِّ ٱلْخَيْرِ لَشَدِيدٌ", "Ve gerçekten o, mal sevgisine aşırı derecede düşkündür.", "And indeed he is, in love of wealth, intense."),
        Ayah(9, 6155, "أَفَلَا يَعْلَمُ إِذَا بُعْثِرَ مَا فِى ٱلْقُبُورِ", "Peki o bilmez mi ki, kabirlerde bulunanlar çıkarıldığı,", "But does he not know that when the contents of the graves are scattered"),
        Ayah(10, 6156, "وَحُصِّلَ مَا فِى ٱلصُّدُورِ", "Ve kalplerde gizlenenler ortaya konulduğu zaman,", "And that within the breasts is obtained,"),
        Ayah(11, 6157, "إِنَّ رَبَّهُم بِهِمْ يَوْمَئِذٍۢ لَّخَبِيرٌۢ", "Şüphesiz Rableri o gün onlardan tamamıyla haberdardır!", "Indeed, their Lord with them, that Day, is [fully] Acquainted.")
    )

    // 101. Kâria (Full 11 Ayahs)
    private val karia = listOf(
        Ayah(1, 6158, "ٱلْقَارِعَةُ", "Kapı çalan büyük kıyamet!", "The Striking Calamity -"),
        Ayah(2, 6159, "مَا ٱلْقَارِعَةُ", "Nedir o kapı çalan büyük kıyamet?", "What is the Striking Calamity?"),
        Ayah(3, 6160, "وَمَآ أَدْرَىٰكَ مَا ٱلْقَارِعَةُ", "O kapı çalan kıyametin ne olduğunu sen bilir misin?", "And what can make you know what is the Striking Calamity?"),
        Ayah(4, 6161, "يَوْمَ يَكُونُ ٱلنَّاسُ كَٱلْفَرَاشِ ٱلْمَبْثُوثِ", "O gün insanlar çırpınıp yayılan pervaneler gibi olur,", "It is the Day when people will be like moths, dispersed,"),
        Ayah(5, 6162, "وَتَكُونُ ٱلْجِبَالُ كَٱلْعِهْنِ ٱلْمَنفُوشِ", "Dağlar da atılmış renkli yünler gibi olur.", "And the mountains will be like wool, fluffed up."),
        Ayah(6, 6163, "فَأَمَّا مَن ثَقُلَتْ مَوَٰزِينُهُۥ", "İşte o vakit kimin tartıları ağır gelirse,", "Then as for one whose scales are heavy [with good deeds],"),
        Ayah(7, 6164, "فَهُوَ فِى عِيشَةٍۢ رَّاضِيَةٍۢ", "O, hoşnut kalacağı mutlu bir hayat içindedir.", "He will be in a pleasant life."),
        Ayah(8, 6165, "وَأَمَّا مَنْ خَفَّتْ مَوَٰزِينُهُۥ", "Fakat kimin de tartıları hafif gelirse,", "But as for one whose scales are light,"),
        Ayah(9, 6166, "فَأُمُّهُۥ هَاوِيَةٌۭ", "Onun sığınağı Hâviye (uçurum) olacaktır.", "His refuge will be an abyss."),
        Ayah(10, 6167, "وَمَآ أَدْرَىٰكَ مَا هِيَهْ", "Onun ne olduğunu sen bilir misin?", "And what can make you know what that is?"),
        Ayah(11, 6168, "نَارٌ حَامِيَةٌۢ", "O, kızgın mı kızgın bir ateştir!", "It is a Fire, intensely hot.")
    )

    // 102. Tekâsür (Full 8 Ayahs)
    private val tekasur = listOf(
        Ayah(1, 6169, "أَلْهَىٰكُمُ ٱلتَّكَاثُرُ", "Çoklukla övünmek sizi öylesine oyaladı ki,", "Competition in [worldly] increase diverts you"),
        Ayah(2, 6170, "حَتَّىٰ زُرْتُمُ ٱلْمَقَابِرَ", "Hatta kabirleri ziyaret ettiniz (ölüleri bile sayacak kadar ileri gittiniz).", "Until you visit the graveyards."),
        Ayah(3, 6171, "كَلَّا سَوْفَ تَعْلَمُونَ", "Hayır! Yakında bileceksiniz!", "No! You are going to know."),
        Ayah(4, 6172, "ثُمَّ كَلَّا سَوْفَ تَعْلَمُونَ", "Elbette ve elbette yakında bileceksiniz!", "Then, no! You are going to know."),
        Ayah(5, 6173, "كَلَّا لَوْ تَعْلَمُونَ عِلْمَ ٱلْيَقِينِ", "Hayır! Kesin bir bilgiyle (ilme'l-yakîn) bilmiş olsaydınız (böyle yapmazdınız).", "No! If you only knew with knowledge of certainty..."),
        Ayah(6, 6174, "لَتَرَوُنَّ ٱلْجَحِيمَ", "Andolsun ki o cehennemi mutlaka göreceksiniz!", "You will surely see the Hellfire."),
        Ayah(7, 6175, "ثُمَّ لَتَرَوُنَّهَا عَيْنَ ٱلْيَقِينِ", "Sonra yine andolsun ki onu gözlerinizle (ayne'l-yakîn) göreceksiniz!", "Then you will surely see it with the eye of certainty."),
        Ayah(8, 6176, "ثُمَّ لَتُسْـَٔلُنَّ يَوْمَئِذٍ عَنِ ٱلنَّعِيمِ", "Sonra o gün, size verilen her nimetten mutlaka hesaba çekileceksiniz!", "Then you will surely be asked that Day about pleasure.")
    )

    // 103. Asr (Full 3 Ayahs)
    private val asr = listOf(
        Ayah(1, 6177, "وَٱلْعَصْرِ", "Asra (zamana) yemin olsun ki,", "By time,"),
        Ayah(2, 6178, "إِنَّ ٱلْإِنسَٰنَ لَفِى خُسْرٍ", "İnsan gerçekten büyük bir ziyan içindedir.", "Indeed, mankind is in loss,"),
        Ayah(3, 6179, "إِلَّا ٱلَّذِينَ ءَامَنُوا۟ وَعَمِلُوا۟ ٱلصَّٰلِحَٰتِ وَتَوَاصَوْا۟ بِٱلْحَقِّ وَتَوَاصَوْا۟ بِٱلصَّبْرِ", "Ancak iman edip salih ameller işleyenler, birbirlerine hakkı tavsiye edenler ve sabrı tavsiye edenler müstesnadır.", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.")
    )

    // 104. Hümeze (Full 9 Ayahs)
    private val humeze = listOf(
        Ayah(1, 6180, "وَيْلٌۭ لِّكُلِّ هُمَزَةٍۢ لُّمَزَةٍ", "Arkadan çekiştiren, kaş göz işaretiyle alay eden herkesin vay haline!", "Woe to every scorner and mocker"),
        Ayah(2, 6181, "ٱلَّذِى جَمَعَ مَالًۭا وَعَدَّدَهُۥ", "O ki mal toplayıp onu tekrar tekrar sayar,", "Who collects wealth and [continuously] counts it."),
        Ayah(3, 6182, "يَحْسَبُ أَنَّ مَالَهُۥٓ أَخْلَدَهُۥ", "Malının kendisini ebedî yaşatacağını sanır.", "He thinks that his wealth will make him immortal."),
        Ayah(4, 6183, "كَلَّا ۖ لَيُنۢبَذَنَّ فِى ٱلْحُطَمَةِ", "Hayır! Andolsun ki o, Hutame'ye (kırıp geçiren cehenneme) atılacaktır.", "No! He will surely be thrown into the Crusher."),
        Ayah(5, 6184, "وَمَآ أَدْرَىٰكَ مَا ٱلْحُطَمَةُ", "Hutame'nin ne olduğunu sen bilir misin?", "And what can make you know what is the Crusher?"),
        Ayah(6, 6185, "نَارُ ٱللَّهِ ٱلْمُوقَدَةُ", "O, Allah'ın tutuşturulmuş ateşidir,", "It is the fire of Allah, [eternally] fueled,"),
        Ayah(7, 6186, "ٱلَّتِى تَطَّلِعُ عَلَى ٱلْأَفْـِٔدَةِ", "Ki yüreklerin ta üstüne çıkar (kalplere işler).", "Which mounts directed at the hearts."),
        Ayah(8, 6187, "إِنَّهَا عَلَيْهِم مُّؤْصَدَةٌۭ", "Şüphesiz o ateş onların üzerine kapatılıp kilitlenmiştir,", "Indeed, Hellfire will be closed down upon them"),
        Ayah(9, 6188, "فِى عَمَدٍۢ مُّمَدَّدَةٍۭ", "Uzatılmış yüksek sütunlar arasında.", "In extended columns.")
    )

    // 105. Fîl (Full 5 Ayahs)
    private val fil = listOf(
        Ayah(1, 6189, "أَلَمْ تَرَ كَيْفَ فَعَلَ رَبُّكَ بِأَصْحَٰبِ ٱلْفِيلِ", "Görmedin mi Rabbin fil sahiplerine ne yaptı?", "Have you not considered, [O Muhammad], how your Lord dealt with the companions of the elephant?"),
        Ayah(2, 6190, "أَلَمْ يَجْعَلْ كَيْدَهُمْ فِى تَضْلِيلٍۢ", "Onların tuzaklarını boşa çıkarmadı mı?", "Did He not make their plan into misguidance?"),
        Ayah(3, 6191, "وَأَرْسَلَ عَلَيْهِمْ طَيْرًا أَبَابِيلَ", "Üzerlerine sürü sürü Ebâbîl kuşları gönderdi.", "And He sent against them birds in flocks,"),
        Ayah(4, 6192, "تَرْمِيهِم بِحِجَارَةٍۢ مِّن سِجِّيلٍۢ", "Onlara pişkin tuğladan taşlar atıyorlardı.", "Striking them with stones of hard clay,"),
        Ayah(5, 6193, "فَجَعَلَهُمْ كَعَصْفٍۢ مَّأْكُولٍۭ", "Sonunda onları yenilmiş ekin yaprağı gibi kılıverdi.", "And He made them like eaten straw.")
    )

    // 106. Kureyş (Full 4 Ayahs)
    private val kureys = listOf(
        Ayah(1, 6194, "لِإِيلَٰفِ قُرَيْشٍ", "Kureyş'in güvenliği ve uzlaşması için,", "For the accustomed security of the Quraysh -"),
        Ayah(2, 6195, "إِۦلَٰفِهِمْ رِحْلَةَ ٱلشِّتَآءِ وَٱلصَّيْفِ", "Kış ve yaz yolculuklarında sağlanan uzlaşma hürmetine,", "Their accustomed security [in] the caravan of winter and summer -"),
        Ayah(3, 6196, "فَلْيَعْبُدُوا۟ رَبَّ هَٰذَا ٱلْبَيْتِ", "Artık bu Beyt'in (Kâbe'nin) Rabbine ibadet etsinler;", "Let them worship the Lord of this House,"),
        Ayah(4, 6197, "ٱلَّذِىٓ أَطْعَمَهُم مِّن جُوعٍۢ وَءَامَنَهُم مِّنْ خَوْفٍۭ", "O Rab ki, onları açlıktan kurtarıp doyurdu ve her türlü korkudan emin kıldı.", "Who has fed them, [saving them] from hunger and made them safe, [saving them] from fear.")
    )

    // 107. Mâûn (Full 7 Ayahs)
    private val maun = listOf(
        Ayah(1, 6198, "أَرَءَيْتَ ٱلَّذِى يُكَذِّبُ بِٱلدِّينِ", "Gördün mü o din ve hesabı yalanlayanı?", "Have you seen the one who denies the Recompense?"),
        Ayah(2, 6199, "فَذَٰلِكَ ٱلَّذِى يَدُعُّ ٱلْيَتِيمَ", "İşte o, yetimi itip kakan kimsedir.", "For that is the one who drives away the orphan"),
        Ayah(3, 6200, "وَلَا يَحُضُّ عَلَىٰ طَعَامِ ٱلْمِسْكِينِ", "Yoksula yedirmeyi teşvik de etmez.", "And does not encourage the feeding of the poor."),
        Ayah(4, 6201, "فَوَيْلٌۭ لِّلْمُصَلِّينَ", "Yazıklar olsun o namaz kılanlara ki,", "So woe to those who pray"),
        Ayah(5, 6202, "ٱلَّذِينَ هُمْ عَن صَلَاتِهِمْ سَاهُونَ", "Onlar namazlarından gafildirler,", "[But] who are heedless of their prayer -"),
        Ayah(6, 6203, "ٱلَّذِينَ هُمْ يُرَآءُونَ", "Onlar ancak gösteriş yaparlar,", "Those who make a show [of their deeds]"),
        Ayah(7, 6204, "وَيَمْنَعُونَ ٱلْمَاعُونَ", "Ve en ufak bir yardımı bile esirgerler.", "And withhold [simple] assistance.")
    )

    // 108. Kevser (Full 3 Ayahs)
    private val kevser = listOf(
        Ayah(1, 6205, "إِنَّآ أَعْطَيْنَٰكَ ٱلْكَوْثَرَ", "Şüphesiz biz sana Kevser'i verdik.", "Indeed, We have granted you, [O Muhammad], al-Kawthar."),
        Ayah(2, 6206, "فَصَلِّ لِرَبِّكَ وَٱنْحَرْ", "Öyleyse Rabbin için namaz kıl ve kurban kes.", "So pray to your Lord and sacrifice [to Him alone]."),
        Ayah(3, 6207, "إِنَّ شَانِئَكَ هُوَ ٱلْأَبْتَرُ", "Doğrusu sana kin besleyen, asıl soyu kesik olanın ta kendisidir.", "Indeed, your enemy is the one cut off.")
    )

    // 109. Kâfirûn (Full 6 Ayahs)
    private val kafirun = listOf(
        Ayah(1, 6208, "قُلْ يَٰٓأَيُّهَا ٱلْكَٰفِرُونَ", "De ki: Ey inkârcılar!", "Say, 'O disbelievers,'"),
        Ayah(2, 6209, "لَآ أَعْبُدُ مَا تَعْبُدُونَ", "Ben sizin taptıklarınıza tapmam.", "I do not worship what you worship."),
        Ayah(3, 6210, "وَلَآ أَنتُمْ عَٰبِدُونَ مَآ أَعْبُدُ", "Siz de benim ibadet ettiğime ibadet edecek değilsiniz.", "Nor are you worshippers of what I worship."),
        Ayah(4, 6211, "وَلَآ أَنَا۠ عَابِدٌۭ مَّا عَبَدتُّمْ", "Ben sizin taptıklarınıza asla tapacak değilim.", "Nor will I be a worshipper of what you worship."),
        Ayah(5, 6212, "وَلَآ أَنتُمْ عَٰبِدُونَ مَآ أَعْبُدُ", "Siz de benim ibadet ettiğime ibadet edecek değilsiniz.", "Nor will you be worshippers of what I worship."),
        Ayah(6, 6213, "لَكُمْ دِينُكُمْ وَلِىَ دِينِ", "Sizin dininiz size, benim dinim banadır.", "For you is your religion, and for me is my religion.")
    )

    // 110. Nasr (Full 3 Ayahs)
    private val nasr = listOf(
        Ayah(1, 6214, "إِذَا جَآءَ نَصْرُ ٱللَّهِ وَٱلْفَتْحُ", "Allah'ın yardımı ve fetih geldiği zaman,", "When the victory of Allah has come and the conquest,"),
        Ayah(2, 6215, "وَرَأَيْتَ ٱلنَّاسَ يَدْخُلُونَ فِى دِينِ ٱللَّهِ أَفْوَاجًۭا", "Ve insanların dalga dalga Allah'ın dinine girdiklerini gördüğün vakit,", "And you see the people entering into the religion of Allah in multitudes,"),
        Ayah(3, 6216, "فَسَبِّحْ بِحَمْدِ رَبِّكَ وَٱسْتَغْفِرْهُ ۚ إِنَّهُۥ كَانَ تَوَّابًۢا", "Rabbini hamd ile tesbih et ve O'ndan bağışlanma dile. Şüphesiz O, tevbeleri çokça kabul edendir.", "Then exalt [Him] with praise of your Lord and ask forgiveness of Him. Indeed, He is ever Accepting of repentance.")
    )

    // 111. Tebbet (Full 5 Ayahs)
    private val tebbet = listOf(
        Ayah(1, 6217, "تَبَّتْ يَدَآ أَبِى لَهَبٍۢ وَتَبَّ", "Ebû Leheb'in iki eli kurusun; kurudu da!", "May the hands of Abu Lahab be ruined, and ruined is he."),
        Ayah(2, 6218, "مَآ أَغْنَىٰ عَنْهُ مَالُهُۥ وَمَا كَسَبَ", "Ona ne malı fayda verdi ne de kazandığı.", "His wealth will not avail him or that which he gained."),
        Ayah(3, 6219, "سَيَصْلَىٰ نَارًۭا ذَاتَ لَهَبٍۢ", "O, alevli bir ateşe girecektir;", "He will [enter to] burn in a Fire of [blazing] flame"),
        Ayah(4, 6220, "وَٱمْرَأَتُهُۥ حَمَّالَةَ ٱلْحَطَبِ", "Odun taşıyıcısı karısı da,", "And his wife [as well] - the carrier of firewood."),
        Ayah(5, 6221, "فِى جِيدِهَا حَبْلٌۭ مِّن مَّسَدٍۭ", "Boynunda bükülmüş liften bir urganla.", "Around her neck is a rope of [twisted] fiber.")
    )

    // 112. İhlâs (Full 4 Ayahs)
    private val ihlas = listOf(
        Ayah(1, 6222, "قُلْ هُوَ ٱللَّهُ أَحَدٌ", "De ki: O Allah tektir.", "Say, 'He is Allah, [who is] One,'"),
        Ayah(2, 6223, "ٱللَّهُ ٱلصَّمَدُ", "Allah Samed'dir (her şey O'na muhtaçtır, O hiçbir şeye muhtaç değildir).", "Allah, the Eternal Refuge."),
        Ayah(3, 6224, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "O doğurmamış ve doğmamıştır.", "He neither begets nor is born,"),
        Ayah(4, 6225, "وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌۢ", "O'nun hiçbir dengi yoktur.", "Nor is there to Him any equivalent.")
    )

    // 113. Felak (Full 5 Ayahs)
    private val felak = listOf(
        Ayah(1, 6226, "قُلْ أَعُوذُ بِرَبِّ ٱلْفَلَقِ", "De ki: Sabahın Rabbine sığınırım,", "Say, 'I seek refuge in the Lord of daybreak'"),
        Ayah(2, 6227, "مِن شَرِّ مَا خَلَقَ", "Yarattığı şeylerin şerrinden,", "From the evil of that which He created"),
        Ayah(3, 6228, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Karanlığı çöktüğü zaman gecenin şerrinden,", "And from the evil of darkness when it settles"),
        Ayah(4, 6229, "وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِى ٱلْعُقَدِ", "Düğümlere üfleyen büyücülerin şerrinden,", "And from the evil of the blowers in knots"),
        Ayah(5, 6230, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Ve haset ettiği vakit hasetçinin şerrinden.", "And from the evil of an envier when he envies.")
    )

    // 114. Nâs (Full 6 Ayahs)
    private val nas = listOf(
        Ayah(1, 6231, "قُلْ أَعُوذُ بِرَبِّ ٱلنَّاسِ", "De ki: İnsanların Rabbine sığınırım,", "Say, 'I seek refuge in the Lord of mankind,'"),
        Ayah(2, 6232, "مَلِكِ ٱلنَّاسِ", "İnsanların Hükümranına,", "The Sovereign of mankind."),
        Ayah(3, 6233, "إِلَٰهِ ٱلنَّاسِ", "İnsanların İlahına,", "The God of mankind,"),
        Ayah(4, 6234, "مِن شَرِّ ٱلْوَسْوَاسِ ٱلْخَنَّاسِ", "O sinsi vesvesecinin şerrinden,", "From the evil of the retreating whisperer -"),
        Ayah(5, 6235, "ٱلَّذِى يُوَسْوِسُ فِى صُدُورِ ٱلنَّاسِ", "Ki o insanların göğüslerine vesvese fısıldar;", "Who whispers [evil] into the breasts of mankind -"),
        Ayah(6, 6236, "مِنَ ٱلْجِنَّةِ وَٱلنَّاسِ", "Gerek cinlerden gerekse insanlardan.", "From among the jinn and mankind.")
    )

    private val fetih = listOf(
        Ayah(1, 4584, "إِنَّا فَتَحْنَا لَكَ فَتْحًۭا مُّبِينًۭا", "Şüphesiz biz sana apaçık bir fetih (zafer) ihsan ettik.", "Indeed, We have given you, [O Muhammad], a clear conquest"),
        Ayah(2, 4585, "لِّيَغْفِرَ لَكَ ٱللَّهُ مَا تَقَدَّمَ مِن ذَنۢبِكَ وَمَا تَأَخَّرَ وَيُتِمَّ نِعْمَتَهُۥ عَلَيْكَ وَيَهْدِيَكَ صِرَٰطًۭا مُّسْتَقِيمًۭا", "Böylece Allah senin geçmiş ve gelecek günahlarını bağışlar, sana olan nimetini tamamlar ve seni dosdoğru yola iletir.", "That Allah may forgive for you what preceded of your sin and what will follow and complete His favor upon you and guide you to a straight path"),
        Ayah(29, 4612, "مُّحَمَّدٌۭ رَّسُولُ ٱللَّهِ ۚ وَٱلَّذِينَ مَعَهُۥٓ أَشِدَّآءُ عَلَى ٱلْكُفَّارِ رُحَمَآءُ بَيْنَهُمْ ۖ تَرَىٰهُمْ رُكَّعًۭا سُجَّدًۭا يَبْتَغُونَ فَضْلًۭا مِّنَ ٱللَّهِ وَرِضْوَٰنًۭا", "Muhammed Allah'ın elçisidir. O'nun yanında olanlar da kâfirlere karşı çetin, kendi aralarında merhametlidirler. Onları rükû ve secdede, Allah'tan lütuf ve rıza dilerken görürsün...", "Muhammad is the Messenger of Allah; and those with him are forceful against the disbelievers, merciful among themselves...")
    )

    private val rahman = listOf(
        Ayah(1, 4902, "ٱلرَّحْمَٰنُ", "Rahmân (sonsuz merhamet sahibi Allah),", "The Most Merciful"),
        Ayah(2, 4903, "عَلَّمَ ٱلْقُرْءَانَ", "Kur'an'ı öğretti.", "Taught the Qur'an,"),
        Ayah(3, 4904, "خَلَقَ ٱلْإِنسَٰنَ", "İnsanı yarattı.", "Created man,"),
        Ayah(4, 4905, "عَلَّمَهُ ٱلْبَيَانَ", "Ona beyanı (anlama ve anlatmayı) belletti.", "[And] taught him eloquence."),
        Ayah(13, 4914, "فَبِأَىِّ ءَالَآءِ رَبِّكُمَا تُكَذِّبَانِ", "Öyleyse Rabbinizin hangi nimetlerini yalanlayabilirsiniz?", "So which of the favors of your Lord would you deny?")
    )

    private val vakia = listOf(
        Ayah(1, 4980, "إِذَا وَقَعَتِ ٱلْوَاقِعَةُ", "O gerçekleşecek olan (kıyamet) koptuğu zaman,", "When the Occurrence occurs,"),
        Ayah(2, 4981, "لَيْسَ لِوَقْعَتِهَا كَاذِبَةٌ", "Onun gerçekleşmesini yalanlayacak hiç kimse yoktur.", "There is, at its occurrence, no denial."),
        Ayah(10, 4989, "وَٱلسَّٰبِقُونَ ٱلسَّٰبِقُونَ", "Öncüler ise (iman ve fazilette) öncülerdir.", "And the forerunners, the forerunners -"),
        Ayah(11, 4990, "أُو۟لَٰٓئِكَ ٱلْمُقَرَّبُونَ", "İşte onlar (Allah'a) en yakın kılınanlardır.", "Those are the ones brought near [to Allah]")
    )

    private val hasrKey = listOf(
        Ayah(21, 5147, "لَوْ أَنزَلْنَا هَٰذَا ٱلْقُرْءَانَ عَلَىٰ جَبَلٍۢ لَّرَأَيْتَهُۥ خَٰشِعًۭا مُّتَصَدِّعًۭا مِّنْ خَشْيَةِ ٱللَّهِ ۚ وَتِلْكَ ٱلْأَمْثَٰلُ نَضْرِبُهَا لِلنَّاسِ لَعَلَّهُمْ يَتَفَكَّرُونَ", "Eğer biz bu Kur'an'ı bir dağa indirseydik, elbette sen onu Allah korkusundan baş eğerek parça parça olmuş görürdün. İşte biz bu misalleri insanlara düşünsünler diye veriyoruz.", "If We had sent down this Qur'an upon a mountain, you would have seen it humbled and coming apart from fear of Allah..."),
        Ayah(22, 5148, "هُوَ ٱللَّهُ ٱلَّذِى لَآ إِلَٰهَ إِلَّا هُوَ ۖ عَٰلِمُ ٱلْغَيْبِ وَٱلشَّهَٰدَةِ ۖ هُوَ ٱلرَّحْمَٰنُ ٱلرَّحِيمُ", "O, kendisinden başka hiçbir ilah bulunmayan Allah'tır. Gaybı da görünen âlemi de bilendir. O Rahmândır, Rahîmdir.", "He is Allah, other than whom there is no deity, Knower of the unseen and the witnessed. He is the Entirely Merciful, the Especially Merciful."),
        Ayah(23, 5149, "هُوَ ٱللَّهُ ٱلَّذِى لَآ إِلَٰهَ إِلَّا هُوَ ٱلْمَلِكُ ٱلْقُدُّوسُ ٱلسَّلَٰمُ ٱلْمُؤْمِنُ ٱلْمُهَيْمِنُ ٱلْعَزِيزُ ٱلْجَبَّارُ ٱلْمُتَكَبِّرُ ۚ سُبْحَٰنَ ٱللَّهِ عَمَّا يُشْرِكُونَ", "O, kendisinden başka hiçbir ilah bulunmayan Melik'tir, Kuddûs'tür, Selâm'dır, Mü'min'dir, Müheymin'dir, Azîz'dir, Cebbâr'dır, Mütekebbir'dir. Allah müşriklerin ortak koştuklarından münezzehtir.", "He is Allah, other than whom there is no deity, the Sovereign, the Pure, the Perfection, the Bestower of Faith..."),
        Ayah(24, 5150, "هُوَ ٱللَّهُ ٱلْخَٰلِقُ ٱلْبَارِئُ ٱلْمُصَوِّرُ ۖ لَهُ ٱلْأَسْمَآءُ ٱلْحُسْنَىٰ ۚ يُسَبِّحُ لَهُۥ مَا فِى ٱلسَّمَٰوَٰتِ وَٱلْأَرْضِ ۖ وَهُوَ ٱلْعَزِيزُ ٱلْحَكِيمُ", "O, yaratan, yoktan var eden, şekil veren Allah'tır. En güzel isimler O'nundur. Göklerde ve yerde olan her şey O'nu tesbih eder. O azîzdir, hakîmdir.", "He is Allah, the Creator, the Inventor, the Fashioner; to Him belong the best names. Whatever is in the heavens and earth is exalting Him. And He is the Exalted in Might, the Wise.")
    )

    private val cuma = listOf(
        Ayah(1, 5174, "يُسَبِّحُ لِلَّهِ مَا فِى ٱلسَّمَٰوَٰتِ وَمَا فِى ٱلْأَرْضِ ٱلْمَلِكِ ٱلْقُدُّوسِ ٱلْعَزِيزِ ٱلْحَكِيمِ", "Göklerde ve yerde olan her şey, mutlak hükümran, mukaddes, mutlak güç ve hikmet sahibi olan Allah'ı tesbih eder.", "Whatever is in the heavens and whatever is on the earth is exalting Allah, the Sovereign, the Pure, the Exalted in Might, the Wise."),
        Ayah(9, 5182, "يَٰٓأَيُّهَا ٱلَّذِينَ ءَامَنُوٓا۟ إِذَا نُودِىَ لِلصَّلَوٰةِ مِن يَوْمِ ٱلْجُمُعَةِ فَٱسْعَوْا۟ إِلَىٰ ذِكْرِ ٱللَّهِ وَذَرُوا۟ ٱلْبَيْعَ ۚ ذَٰلِكُمْ خَيْرٌۭ لَّكُمْ إِن كُنتُمْ تَعْلَمُونَ", "Ey iman edenler! Cuma günü namaza çağrıldığınız zaman hemen Allah'ı anmaya koşun ve alışverişi bırakın. Eğer bilirseniz bu sizin için daha hayırlıdır.", "O you who have believed, when [the adhan] is called for the prayer on the day of Jumu'ah [Friday], then proceed to the remembrance of Allah and leave trade..."),
        Ayah(10, 5183, "فَإِذَا قُضِيَتِ ٱلصَّلَوٰةُ فَٱنتَشِرُوا۟ فِى ٱلْأَرْضِ وَٱبْتَغُوا۟ مِن فَضْلِ ٱللَّهِ وَٱذْكُرُوا۟ ٱللَّهَ كَثِيرًۭا لَّعَلَّكُمْ تُفْلِحُونَ", "Namaz kılınınca artık yeryüzüne dağılın ve Allah'ın lütfundan rızık arayın. Allah'ı çokça anın ki kurtuluşa eresiniz.", "And when the prayer has been concluded, disperse within the land and seek from the bounty of Allah, and remember Allah often that you may succeed."),
        Ayah(11, 5184, "وَإِذَا رَأَوْا۟ تِجَٰرَةً أَوْ لَهْوًا ٱنفَضُّوٓا۟ إِلَيْهَا وَتَرَكُوكَ قَآئِمًۭا ۚ قُلْ مَا عِندَ ٱللَّهِ خَيْرٌۭ مِّنَ ٱللَّهْوِ وَمِنَ ٱلتِّجَٰرَةِ ۚ وَٱللَّهُ خَيْرُ ٱلرَّٰزِقِينَ", "Onlar bir ticaret veya bir eğlence gördükleri zaman hemen dağılıp ona koştular ve seni ayakta bıraktılar. De ki: 'Allah katında olan, eğlenceden de ticaretten de daha hayırlıdır. Allah rızık verenlerin en hayırlısıdır.'", "But when they saw a transaction or a diversion, [O Muhammad], they rushed to it and left you standing. Say, 'What is with Allah is better than diversion and than a transaction, and Allah is the best of providers.'")
    )
}
