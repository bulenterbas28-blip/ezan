package com.example.data.repository

import com.example.data.model.HadithItem

object HadithRepository {

    val allHadiths: List<HadithItem> = listOf(
        HadithItem(
            id = "hadith_1",
            categoryTr = "Niyet ve İhlas",
            categoryEn = "Intention & Sincerity",
            categoryAr = "النية والإخلاص",
            arabicText = "إِنَّمَا الْأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            turkishText = "Ameller ancak niyetlere göredir. Herkesin kazandığı ancak niyet ettiği şeydir.",
            englishText = "Actions are judged by intentions, and every person will get the reward according to what he has intended.",
            sourceTr = "Sahîh-i Buhârî, 1; Sahîh-i Müslim, 1907",
            sourceEn = "Sahih al-Bukhari 1, Sahih Muslim 1907",
            sourceAr = "صحيح البخاري ١، صحيح مسلم ١٩٠٧"
        ),
        HadithItem(
            id = "hadith_2",
            categoryTr = "Namaz ve İbadet",
            categoryEn = "Prayer & Worship",
            categoryAr = "الصلاة والعبادة",
            arabicText = "رَأْسُ الْأَمْرِ الْإِسْلَامُ، وَعَمُودُهُ الصَّلَاةُ",
            turkishText = "İşin başı İslam, direği ise namazdır.",
            englishText = "The head of the matter is Islam, and its pillar is the prayer.",
            sourceTr = "Sünen-i Tirmizî, İmân 8",
            sourceEn = "Jami at-Tirmidhi, Faith 8",
            sourceAr = "جامع الترمذي، الإيمان ٨"
        ),
        HadithItem(
            id = "hadith_3",
            categoryTr = "Namaz ve İbadet",
            categoryEn = "Prayer & Worship",
            categoryAr = "الصلاة والعبادة",
            arabicText = "أَقْرَبُ مَا يَكُونُ الْعَبْدُ مِنْ رَبِّهِ وَهُوَ سَاجِدٌ، فَأَكْثِرُوا الدُّعَاءَ",
            turkishText = "Kulun Rabbine en yakın olduğu an secde halidir; secdede çokça dua edin.",
            englishText = "The closest that a servant comes to his Lord is when he is prostrating, so supplicate abundantly.",
            sourceTr = "Sahîh-i Müslim, Salât 215",
            sourceEn = "Sahih Muslim 482",
            sourceAr = "صحيح مسلم ٤٨٢"
        ),
        HadithItem(
            id = "hadith_4",
            categoryTr = "Ahlak ve Hoşgörü",
            categoryEn = "Manners & Character",
            categoryAr = "الأخلاق والمعاملات",
            arabicText = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
            turkishText = "Gerçek Müslüman, diğer Müslümanların elinden ve dilinden emin olduğu (zarar görmediği) kimsedir.",
            englishText = "The true Muslim is the one from whose tongue and hands other Muslims are safe.",
            sourceTr = "Sahîh-i Buhârî, İmân 4; Sahîh-i Müslim, İmân 64",
            sourceEn = "Sahih al-Bukhari 10, Sahih Muslim 40",
            sourceAr = "صحيح البخاري ١٠، صحيح مسلم ٤٠"
        ),
        HadithItem(
            id = "hadith_5",
            categoryTr = "Ahlak ve Hoşgörü",
            categoryEn = "Manners & Character",
            categoryAr = "الأخلاق والمعاملات",
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            turkishText = "Kardeşinin yüzüne tebessüm etmen senin için bir sadakadır.",
            englishText = "Smiling in the face of your brother is charity for you.",
            sourceTr = "Sünen-i Tirmizî, Birr 36",
            sourceEn = "Jami at-Tirmidhi 1956",
            sourceAr = "جامع الترمذي ١٩٥٦"
        ),
        HadithItem(
            id = "hadith_6",
            categoryTr = "İman ve Kardeşlik",
            categoryEn = "Faith & Brotherhood",
            categoryAr = "الإيمان والأخوة",
            arabicText = "لَا يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لِأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            turkishText = "Sizden biri, kendisi için arzuladığını mümin kardeşi için de arzu etmedikçe (kâmil manada) iman etmiş olmaz.",
            englishText = "None of you truly believes until he loves for his brother what he loves for himself.",
            sourceTr = "Sahîh-i Buhârî, İmân 7; Sahîh-i Müslim, İmân 71",
            sourceEn = "Sahih al-Bukhari 13, Sahih Muslim 45",
            sourceAr = "صحيح البخاري ١٣، صحيح مسلم ٤٥"
        ),
        HadithItem(
            id = "hadith_7",
            categoryTr = "İyilik ve Sadaka",
            categoryEn = "Charity & Generosity",
            categoryAr = "الصدقة والإحسان",
            arabicText = "الْيَدُ الْعُلْيَا خَيْرٌ مِنَ الْيَدِ السُّفْلَى",
            turkishText = "Veren el, alan elden daha hayırlıdır.",
            englishText = "The upper hand (that gives) is better than the lower hand (that receives).",
            sourceTr = "Sahîh-i Buhârî, Zekât 18; Sahîh-i Müslim, Zekât 94",
            sourceEn = "Sahih al-Bukhari 1429, Sahih Muslim 1033",
            sourceAr = "صحيح البخاري ١٤٢٩، صحيح مسلم ١٠٣٣"
        ),
        HadithItem(
            id = "hadith_8",
            categoryTr = "İlim ve Hikmet",
            categoryEn = "Knowledge & Wisdom",
            categoryAr = "العلم والحكمة",
            arabicText = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            turkishText = "İlim öğrenmek, kadın erkek her Müslümana farzdır.",
            englishText = "Seeking knowledge is an obligation upon every Muslim.",
            sourceTr = "Sünen-i İbn Mâce, Mukaddime 17",
            sourceEn = "Sunan Ibn Majah 224",
            sourceAr = "سنن ابن ماجه ٢٢٤"
        ),
        HadithItem(
            id = "hadith_9",
            categoryTr = "Aile ve Anne-Baba",
            categoryEn = "Family & Parents",
            categoryAr = "بر الوالدين والأسرة",
            arabicText = "رِضَى الرَّبِّ فِي رِضَى الْوَالِدِ، وَسَخَطُ الرَّبِّ فِي سَخَطِ الْوَالِدِ",
            turkishText = "Rabbin rızası anne-babanın rızasındadır. Rabbin öfkesi de anne-babanın öfkesindedir.",
            englishText = "The pleasure of the Lord is in the pleasure of the parents, and the anger of the Lord is in the anger of the parents.",
            sourceTr = "Sünen-i Tirmizî, Birr 3",
            sourceEn = "Jami at-Tirmidhi 1899",
            sourceAr = "جامع الترمذي ١٨٩٩"
        ),
        HadithItem(
            id = "hadith_10",
            categoryTr = "Zikir ve Tevbe",
            categoryEn = "Remembrance & Forgiveness",
            categoryAr = "الذكر والاستغفار",
            arabicText = "كَلِمَتَانِ حَبِيبَتَانِ إِلَى الرَّحْمَنِ، خَفِيفَتَانِ عَلَى اللِّسَانِ، ثَقِيلَتَانِ فِي الْمِيزَانِ: سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            turkishText = "Dile hafif, mizanda ağır, Rahmân olan Allah'a çok sevimli iki kelime vardır: Sübhânallâhi ve bi-hamdihî, Sübhânallâhi'l-Azîm (Allah'ı hamd ile tesbih ederim, Yüce Allah her türlü eksiklikten münezzehtir).",
            englishText = "Two phrases are light on the tongue, heavy in the scales, and beloved to the Most Merciful: Subhanallahi wa bihamdihi, Subhanallahil-Azeem.",
            sourceTr = "Sahîh-i Buhârî, Daavât 65; Sahîh-i Müslim, Zikir 31",
            sourceEn = "Sahih al-Bukhari 6406, Sahih Muslim 2694",
            sourceAr = "صحيح البخاري ٦٤٠٦، صحيح مسلم ٢٦٩٤"
        )
    )
}
