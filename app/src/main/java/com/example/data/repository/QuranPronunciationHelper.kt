package com.example.data.repository

/**
 * Authentic Turkish Latin pronunciation (okunuş) lookup dictionary, API transliteration cleaner,
 * and Uthmani-to-Turkish phonetic converter for the Holy Quran.
 */
object QuranPronunciationHelper {

    // Indexed by "surahNumber:numberInSurah" to prevent any global ayah index offset errors
    private val surahAyahPronunciations = mapOf(
        // 1. Fâtiha (1-7)
        "1:1" to "Bismillâhir-rahmânir-rahîm.",
        "1:2" to "El-hamdü lillâhi rabbil-'âlemîn.",
        "1:3" to "Er-rahmânir-rahîm.",
        "1:4" to "Mâliki yevmid-dîn.",
        "1:5" to "İyyâke na'büdü ve iyyâke neste'în.",
        "1:6" to "İhdinas-sırâtal-müstekîm.",
        "1:7" to "Sırâtallezîne en'amte 'aleyhim, ğayril-mağdûbi 'aleyhim veled-dâllîn.",

        // 2. Bakara (Elif-Lâm-Mîm, Âyetü'l-Kürsî, Âmenerrasûlü)
        "2:1" to "Elif, Lâm, Mîm.",
        "2:2" to "Zâlikel-kitâbü lâ raybe fîh, hüden lil-müttekîn.",
        "2:3" to "Ellezîne yü'minûne bil-ğaybi ve yükîmûnes-salâte ve mimmâ razaknâhüm yünfikûn.",
        "2:4" to "Vellezîne yü'minûne bimâ ünzile ileyke ve mâ ünzile min kablike ve bil-âhirati hüm yûkınûn.",
        "2:5" to "Ülâike 'alâ hüden min rabbihim ve ülâike hümül-müflihûn.",
        "2:255" to "Allâhü lâ ilâhe illâ hüvel-hayyül-kayyûm. Lâ te'huzühû sinetün ve lâ nevm. Lehû mâ fis-semâvâti ve mâ fil-ard. Men zellezî yeşfe'u 'indehû illâ bi-iznih. Ya'lemü mâ beyne eydîhim ve mâ halfehüm ve lâ yühîtûne bi-şey'in min 'ilmihî illâ bimâ şâ'. Vesi'a kürsiyyühüs-semâvâti vel-arda ve lâ yeûdühû hıfzuhümâ ve hüvel-'aliyyül-'azîm.",
        "2:285" to "Âmener-rasûlü bimâ ünzile ileyhi min rabbihî vel-mü'minûn. Küllün âmene billâhi ve melâiketihî ve kütübihî ve rusülih. Lâ nüferriku beyne ehadin min rusülih. Ve kâlû semi'nâ ve ata'nâ ğufrâneke rabbenâ ve ileykel-masîr.",
        "2:286" to "Lâ yükellifullâhü nefsen illâ vüs'ahâ. Lehâ mâ kesebet ve 'aleyhâ mektesebet. Rabbenâ lâ tüâhiznâ in nesînâ ev ahta'nâ. Rabbenâ ve lâ tahmil 'aleynâ ısran kemâ hameltehû 'alellezîne min kablinâ. Rabbenâ ve lâ tühammilnâ mâ lâ tâkate lenâ bih. Va'fü 'annâ, vağfir lenâ, verhamnâ, ente mevlânâ fensurnâ 'alel-kavmil-kâfirîn.",

        // 3. Âl-i İmrân (1-5)
        "3:1" to "Elif, Lâm, Mîm.",
        "3:2" to "Allâhü lâ ilâhe illâ hüvel-hayyül-kayyûm.",
        "3:3" to "Nezzelel-'aleykel-kitâbe bil-hakkı musaddikan limâ beyne yedeyhi ve enzelet-tevrâte vel-incîl.",
        "3:4" to "Min kablü hüden lin-nâsi ve enzelel-furkân. İnnellezîne keferû bi-âyâtillâhi lehüm 'azâbün şedîd, vallâhü 'azîzün züntikâm.",
        "3:5" to "İnnallâhe lâ yahfâ 'aleyhi şey'ün fil-ardı ve lâ fis-semâ'.",

        // 36. Yâsîn
        "36:1" to "Yâ, Sîn.",
        "36:2" to "Vel-kur'ânil-hakîm.",
        "36:3" to "İnneke leminel-mürselîn.",
        "36:4" to "'Alâ sırâtın müstekîm.",
        "36:5" to "Tenzîlel-'azîzir-rahîm.",
        "36:6" to "Litünzira kavmen mâ ünzira âbâühüm fehüm ğâfilûn.",
        "36:7" to "Lekad hakkal-kavlü 'alâ ekserihim fehüm lâ yü'minûn.",
        "36:8" to "İnnâ ce'alnâ fî a'nâkıhim ağlâlen fehiye ilel-ezkâni fehüm mukmehûn.",
        "36:9" to "Ve ce'alnâ min beyni eydîhim sedden ve min halfihim sedden feağşeynâhüm fehüm lâ yübsırûn.",
        "36:10" to "Ve sevâün 'aleyhim eenzertehüm em lem tünzirhüm lâ yü'minûn.",
        "36:11" to "İnnemâ tünziru menittebe'az-zikra ve haşiyer-rahmâne bil-ğayb, febeşşirhü bimağfiratin ve ecrin kerîm.",
        "36:12" to "İnnâ nahnü nuhyil-mevtâ ve nektübü mâ kaddemû ve âsârahüm, ve külle şey'in ahsaynâhü fî imâmin mübîn.",
        "36:58" to "Selâmün kavlen min rabbin rahîm.",
        "36:82" to "İnnemâ emruhû izâ erâde şey'en en yekûle lehû kün feyekûn.",
        "36:83" to "Fesübhânellezî biyedihî melekûtü külli şey'in ve ileyhi türce'ûn.",

        // 48. Fetih
        "48:1" to "İnnâ fetahnâ leke fethan mübînâ.",
        "48:2" to "Liyagfira lekallâhü mâ tekaddeme min zenbike ve mâ teahhara ve yütimme ni'metehû 'aleyke ve yehdiyeke sırâtan müstekîmâ.",
        "48:29" to "Muhammedün rasûlullâh, vellezîne me'ahû eşiddâü 'alel-küffâri ruhamâü beynehüm terâhüm rukke'an sücceden yebteğûne fadlen minallâhi ve rıdvânâ.",

        // 55. Rahmân
        "55:1" to "Er-rahmân.",
        "55:2" to "'Allemel-kur'ân.",
        "55:3" to "Halekal-insân.",
        "55:4" to "'Allemehül-beyân.",
        "55:13" to "Febi-eyyi âlâi rabbikümâ tükezzibân.",

        // 56. Vâkıa
        "56:1" to "İzâ veka'atil-vâkı'ah.",
        "56:2" to "Leyse livak'atihâ kâzibeh.",
        "56:10" to "Ves-sâbikûnes-sâbikûn.",
        "56:11" to "Ülâikel-mukarrabûn.",

        // 59. Haşr (Hüvallâhüllezî)
        "59:21" to "Lev enzelnâ hâzel-kur'âne 'alâ cebelin lera'eytehû hâşi'an mütesaddi'an min haşyetillâh, ve tilkel-emsâlü nadribühâ lin-nâsi le'allehüm yetefekkerûn.",
        "59:22" to "Hüvallâhüllezî lâ ilâhe illâ hû, 'âlimül-ğaybi veş-şehâdeh, hüver-rahmânür-rahîm.",
        "59:23" to "Hüvallâhüllezî lâ ilâhe illâ hüvel-melikül-kuddûsüs-selâmül-mü'minül-müheyminül-'azîzül-cebbârul-mütekebbir, sübhânallâhi 'ammâ yüşrikûn.",
        "59:24" to "Hüvallâhül-hâlikul-bâri'ül-musavviru lehül-esmâül-hüsnâ, yüsebbihu lehû mâ fis-semâvâti vel-ard, ve hüvel-'azîzül-hakîm.",

        // 62. Cuma
        "62:1" to "Yüsebbihu lillâhi mâ fis-semâvâti ve mâ fil-ardıl-melikil-kuddûsil-'azîzil-hakîm.",
        "62:9" to "Yâ eyyühellezîne âmenû izâ nûdiye lis-salâti min yevmil-cumu'ati fes'av ilâ zikrillâhi ve zerul-bey', zâliküm hayrun leküm in küntüm ta'lemûn.",
        "62:10" to "Fe-izâ kudıyetis-salâtü fenteşirû fil-ardı vebteğû min fadlillâhi vezkürullâhe kesîran le'alleküm tüflihûn.",
        "62:11" to "Ve izâ ra'ev ticâraten ev lehveninfaddû ileyhâ ve terakûke kâimâ, kul mâ 'indallâhi hayrun minel-lehvi ve minet-ticârah, vallâhü hayrur-râzıkîn.",

        // 67. Mülk
        "67:1" to "Tebârekellezî biyedihil-mülkü ve hüve 'alâ külli şey'in kadîr.",
        "67:2" to "Ellezî halekal-mevte vel-hayâte liyeblüveküm eyyüküm ahsenü 'amelâ, ve hüvel-'azîzül-ğafûr.",
        "67:3" to "Ellezî haleka seb'a semâvâtin tıbâkâ, mâ terâ fî halkır-rahmâni min tefâvüt, ferci'ıl-basara hel terâ min futûr.",
        "67:4" to "Sümmerci'ıl-basara kerrateyni yenkalib ileykel-basaru hâsi'en ve hüve hasîr.",
        "67:5" to "Ve lekad zeyyennes-semâed-dünyâ bimesâbîha ve ce'alnâhâ rucûmen liş-şeyâtîni ve a'tednâ lehüm 'azâbes-se'îr.",
        "67:12" to "İnnellezîne yahşevne rabbehüm bil-ğaybi lehüm mağfiratün ve ecrun kebîr.",
        "67:13" to "Ve esirrû kavleküm evicherû bih, innehû 'alîmün bizâtis-sudûr.",
        "67:14" to "Elâ ya'lemü men halak, ve hüvel-latîfül-habîr.",
        "67:23" to "Kul hüvellezî enşe'eküm ve ce'ale lekümüs-sem'a vel-ebsâra vel-ef'ideh, kalîlen mâ teşkürûn.",
        "67:30" to "Kul era'eytüm in asbaha mâüküm ğavran femen ye'tîküm bimâin me'în.",

        // 78. Nebe
        "78:1" to "'Amme yetesâelûn.",
        "78:2" to "'Anin-nebe'il-'azîm.",
        "78:3" to "Ellezî hüm fîhi muhtelifûn.",
        "78:4" to "Kellâ seya'lemûn.",
        "78:5" to "Sümme kellâ seya'lemûn.",
        "78:6" to "Elem nec'alil-arda mihâdâ.",
        "78:7" to "Vel-cibâle evtâdâ.",
        "78:8" to "Ve halaknâküm ezvâcâ.",
        "78:9" to "Ve ce'alnâ nevmeküm sübâtâ.",
        "78:10" to "Ve ce'alnel-leyle libâsâ.",
        "78:11" to "Ve ce'alnen-nehâra me'âşâ.",
        "78:31" to "İnne lil-müttekîne mefâzâ.",
        "78:32" to "Hadâika ve a'nâbâ.",
        "78:40" to "İnnâ enzernâküm 'azâben karîben yevme yenzurul-mer'ü mâ kaddemet yedâhü ve yekûlül-kâfiru yâ leytenî küntü türâbâ.",

        // 87. A'lâ
        "87:1" to "Sebbihısme rabbikel-a'lâ.",
        "87:2" to "Ellezî haleka fesevvâ.",
        "87:3" to "Vellezî kaddera fehedâ.",
        "87:4" to "Vellezî ahracel-mer'â.",
        "87:5" to "Fece'alehû ğusâen ahvâ.",
        "87:6" to "Senukri'üke felâ tensâ.",
        "87:7" to "İllâ mâ şâallâh, innehû ya'lemül-cehra ve mâ yahfâ.",
        "87:8" to "Ve nüyessiruke lil-yüsrâ.",
        "87:9" to "Fezekkir in nefe'atiz-zikrâ.",
        "87:10" to "Seyezekkeru men yahşâ.",
        "87:11" to "Ve yetecennebühel-eşkâ.",
        "87:12" to "Ellezî yaslen-nâral-kübrâ.",
        "87:13" to "Sümme lâ yemûtü fîhâ ve lâ yahyâ.",
        "87:14" to "Kad efleha men tezekkâ.",
        "87:15" to "Ve zekerasme rabbihî fesallâ.",
        "87:16" to "Bel tü'sirûnel-hayâted-dünyâ.",
        "87:17" to "Vel-âhiratü hayrun ve ebkâ.",
        "87:18" to "İnne hâzâ lefis-suhufil-ûlâ.",
        "87:19" to "Suhufi ibrâhîme ve mûsâ.",

        // 91. Şems (1-15)
        "91:1" to "Veş-şemsi ve duhâhâ.",
        "91:2" to "Vel-kameri izâ telâhâ.",
        "91:3" to "Ven-nehâri izâ cellâhâ.",
        "91:4" to "Vel-leyli izâ yağşâhâ.",
        "91:5" to "Ves-semâi ve mâ benâhâ.",
        "91:6" to "Vel-ardı ve mâ tahâhâ.",
        "91:7" to "Ve nefsin ve mâ sevvâhâ.",
        "91:8" to "Feelhemehâ fücûrahâ ve takvâhâ.",
        "91:9" to "Kad efleha men zekkâhâ.",
        "91:10" to "Ve kad hâbe men dessâhâ.",
        "91:11" to "Kezzebet semûdü bitağvâhâ.",
        "91:12" to "İzinbe'ase eşkâhâ.",
        "91:13" to "Fekâle lehüm rasûlullâhi nâkatallâhi ve sukyâhâ.",
        "91:14" to "Fekezzebûhü fe'akarûhâ fedemdeme 'aleyhim rabbühüm bizenbihim fesevvâhâ.",
        "91:15" to "Ve lâ yehâfü 'ukbâhâ.",

        // 92. Leyl (1-21)
        "92:1" to "Vel-leyli izâ yağşâ.",
        "92:2" to "Ven-nehâri izâ tecellâ.",
        "92:3" to "Ve mâ halekaz-zekera vel-ünsâ.",
        "92:4" to "İnne sa'yeküm leşettâ.",
        "92:5" to "Feemmâ men a'tâ vettekâ.",
        "92:6" to "Ve saddeka bil-hüsnâ.",
        "92:7" to "Fesenüyessiruhû lil-yüsrâ.",
        "92:8" to "Ve emmâ men bahile vestağnâ.",
        "92:9" to "Ve kezzebe bil-hüsnâ.",
        "92:10" to "Fesenüyessiruhû lil-'usrâ.",
        "92:11" to "Ve mâ yuğnî 'anhü mâlühû izâ teraddâ.",
        "92:12" to "İnne 'aleynâ lel-hüdâ.",
        "92:13" to "Ve inne lenâ lel-âhirate vel-ûlâ.",
        "92:14" to "Feenzertüküm nâran telezzâ.",
        "92:15" to "Lâ yaslâhâ illel-eşkâ.",
        "92:16" to "Ellezî kezzebe ve tevellâ.",
        "92:17" to "Ve seyücennebühel-etkâ.",
        "92:18" to "Ellezî yü'tî mâlehû yetezekkâ.",
        "92:19" to "Ve mâ li-ehadin 'indehû min ni'metin tüczâ.",
        "92:20" to "İllabtiğâe vechi rabbihil-a'lâ.",
        "92:21" to "Ve lesevfe yerdâ.",

        // 93. Duhâ (1-11)
        "93:1" to "Ved-duhâ.",
        "93:2" to "Vel-leyli izâ secâ.",
        "93:3" to "Mâ vedde'ake rabbüke ve mâ kalâ.",
        "93:4" to "Ve lel-âhiratü hayrun leke minel-ûlâ.",
        "93:5" to "Ve lesevfe yu'tîke rabbüke feterdâ.",
        "93:6" to "Elem yecidke yetîmen fe-âvâ.",
        "93:7" to "Ve vecedeke dâllen fehedâ.",
        "93:8" to "Ve vecedeke 'âilen feağnâ.",
        "93:9" to "Feemmel-yetîme felâ takher.",
        "93:10" to "Ve emmes-sâile felâ tenher.",
        "93:11" to "Ve emmâ bini'meti rabbike fehaddis.",

        // 94. İnşirâh (1-8)
        "94:1" to "Elem neşrah leke sadrak.",
        "94:2" to "Ve veda'nâ 'anke vizrak.",
        "94:3" to "Ellezî enkada zahrak.",
        "94:4" to "Ve refa'nâ leke zikrak.",
        "94:5" to "Fe-inne me'al-'usri yüsrâ.",
        "94:6" to "İnne me'al-'usri yüsrâ.",
        "94:7" to "Fe-izâ ferağte fensab.",
        "94:8" to "Ve ilâ rabbike ferğab.",

        // 95. Tîn (1-8)
        "95:1" to "Vet-tîni vez-zeytûn.",
        "95:2" to "Ve tûri sînîn.",
        "95:3" to "Ve hâzel-beledil-emîn.",
        "95:4" to "Lekad halaknel-insâne fî ahseni takvîm.",
        "95:5" to "Sümme radednâhü esfele sâfilîn.",
        "95:6" to "İllellezîne âmenû ve 'amilus-sâlihâti felehüm ecrun ğayru memnûn.",
        "95:7" to "Femâ yükezzibüke ba'dü bid-dîn.",
        "95:8" to "Eleysallâhü bi-ahkemil-hâkimîn.",

        // 96. Alak (1-19)
        "96:1" to "İkra' bismi rabbikellezî halak.",
        "96:2" to "Halekal-insâne min 'alak.",
        "96:3" to "İkra' ve rabbükel-ekram.",
        "96:4" to "Ellezî 'alleme bil-kalem.",
        "96:5" to "'Allemel-insâne mâ lem ya'lem.",
        "96:6" to "Kellâ innel-insâne leyatğâ.",
        "96:7" to "En ra'âhüstağnâ.",
        "96:8" to "İnne ilâ rabbiker-ruc'â.",
        "96:9" to "Era'eytellezî yenhâ.",
        "96:10" to "'Abden izâ sallâ.",
        "96:11" to "Era'eyte in kâne 'alel-hüdâ.",
        "96:12" to "Ev emera bit-takvâ.",
        "96:13" to "Era'eyte in kezzebe ve tevellâ.",
        "96:14" to "Elem ya'lem bi-ennallâhe yerâ.",
        "96:15" to "Kellâ lein lem yentehi lenesfe'an bin-nâsıyeh.",
        "96:16" to "Nâsıyetin kâzibetin hâtı'eh.",
        "96:17" to "Felyed'u nâdiyeh.",
        "96:18" to "Sened'uz-zebâniyeh.",
        "96:19" to "Kellâ lâ tütı'hü vescüd vakterib.",

        // 97. Kadir (1-5)
        "97:1" to "İnnâ enzelnâhü fî leyletil-kadr.",
        "97:2" to "Ve mâ edrâke mâ leyletül-kadr.",
        "97:3" to "Leyletül-kadri hayrun min elfi şehr.",
        "97:4" to "Tenezzelül-melâiketü ver-rûhu fîhâ bi-izni rabbihim min külli emr.",
        "97:5" to "Selâmün hiye hattâ matla'ıl-fecr.",

        // 98. Beyyine (1-8)
        "98:1" to "Lem yekünillezîne keferû min ehlil-kitâbi vel-müşrikîne münfekkîne hattâ te'tiyehümül-beyyineh.",
        "98:2" to "Rasûlün minallâhi yetlû suhufen mutahherah.",
        "98:3" to "Fîhâ kütübün kayyimeh.",
        "98:4" to "Ve mâ teferrakallezîne ûtül-kitâbe illâ min ba'di mâ câethümül-beyyineh.",
        "98:5" to "Ve mâ ümirû illâ liya'büdullâhe muhlisîne lehüd-dîne hunefâe ve yükîmus-salâte ve yü'tüz-zekâh, ve zâlike dînül-kayyimeh.",
        "98:6" to "İnnellezîne keferû min ehlil-kitâbi vel-müşrikîne fî nâri cehenneme hâlidîne fîhâ, ülâike hüm şerrul-beriyyeh.",
        "98:7" to "İnnellezîne âmenû ve 'amilus-sâlihâti ülâike hüm hayrul-beriyyeh.",
        "98:8" to "Cezâühüm 'inde rabbihim cennâtü 'adnin tecrî min tahtihel-enhâru hâlidîne fîhâ ebedâ, radıyallâhü 'anhüm ve radû 'anh, zâlike limen haşiye rabbeh.",

        // 99. Zilzâl (1-8)
        "99:1" to "İzâ zülziletil-ardu zilzâlehâ.",
        "99:2" to "Ve ahracetil-ardu eskâlehâ.",
        "99:3" to "Ve kâlel-insânü mâ lehâ.",
        "99:4" to "Yevmeizin tühaddisü ahbârahâ.",
        "99:5" to "Bi-enne rabbeke evhâ lehâ.",
        "99:6" to "Yevmeizin yasdürun-nâsü eştâten liyürav a'mâlehüm.",
        "99:7" to "Femen ya'mel miskâle zerratin hayran yerah.",
        "99:8" to "Ve men ya'mel miskâle zerratin şerran yerah.",

        // 100. Âdiyât (1-11)
        "100:1" to "Vel-'âdiyâti dabhâ.",
        "100:2" to "Fel-mûriyâti kadhâ.",
        "100:3" to "Fel-muğîrâti subhâ.",
        "100:4" to "Feeserne bihî nak'â.",
        "100:5" to "Fevesatne bihî cem'â.",
        "100:6" to "İnnel-insâne lirabbihî lekenûd.",
        "100:7" to "Ve innehû 'alâ zâlike leşehîd.",
        "100:8" to "Ve innehû lihubbil-hayri leşedîd.",
        "100:9" to "Efelâ ya'lemü izâ bu'sira mâ fil-kubûr.",
        "100:10" to "Ve hussıle mâ fis-sudûr.",
        "100:11" to "İnne rabbehüm bihim yevmeizin lehabîr.",

        // 101. Kâria (1-11)
        "101:1" to "El-kâri'ah.",
        "101:2" to "Mel-kâri'ah.",
        "101:3" to "Ve mâ edrâke mel-kâri'ah.",
        "101:4" to "Yevme yekûnün-nâsü kel-ferâşil-mebsûs.",
        "101:5" to "Ve tekûnül-cibâlü kel-'ıhnil-menfûş.",
        "101:6" to "Feemmâ men sekulet mevâzînüh.",
        "101:7" to "Fehüve fî 'îşetin râdıyeh.",
        "101:8" to "Ve emmâ men haffet mevâzînüh.",
        "101:9" to "Feümmühû hâviyeh.",
        "101:10" to "Ve mâ edrâke mâ hiyeh.",
        "101:11" to "Nârun hâmiyeh.",

        // 102. Tekâsür (1-8)
        "102:1" to "Elhâkümüt-tekâsür.",
        "102:2" to "Hattâ zürtümül-mekâbir.",
        "102:3" to "Kellâ sevfe ta'lemûn.",
        "102:4" to "Sümme kellâ sevfe ta'lemûn.",
        "102:5" to "Kellâ lev ta'lemûne 'ılmel-yakîn.",
        "102:6" to "Leteravünnel-cehîm.",
        "102:7" to "Sümme leteravünnehâ 'aynel-yakîn.",
        "102:8" to "Sümme letüs'elünne yevmeizin 'anin-na'îm.",

        // 103. Asr (1-3)
        "103:1" to "Vel-'asr.",
        "103:2" to "İnnel-insâne lefî husr.",
        "103:3" to "İllellezîne âmenû ve 'amilus-sâlihâti ve tevâsav bil-hakkı ve tevâsav bis-sabr.",

        // 104. Hümeze (1-9)
        "104:1" to "Veylün likülli hümezetin lümezeh.",
        "104:2" to "Ellezî ceme'a mâlen ve 'addedeh.",
        "104:3" to "Yahsebü enne mâlehû ahledeh.",
        "104:4" to "Kellâ leyünbezenne fil-hutameh.",
        "104:5" to "Ve mâ edrâke mel-hutameh.",
        "104:6" to "Nârullâhil-mûkadeh.",
        "104:7" to "Elletî tettali'u 'alel-ef'ideh.",
        "104:8" to "İnnehâ 'aleyhim mü'sadeh.",
        "104:9" to "Fî 'amedin mümeddedeh.",

        // 105. Fîl (1-5)
        "105:1" to "Elem tera keyfe fe'ale rabbüke bi-ashâbil-fîl.",
        "105:2" to "Elem yec'al keydehüm fî tadlîl.",
        "105:3" to "Ve ersele 'aleyhim tayran ebâbîl.",
        "105:4" to "Termîhim bi-hicâratin min siccîl.",
        "105:5" to "Fece'alehüm ke'asfin me'kûl.",

        // 106. Kureyş (1-4)
        "106:1" to "Li-îlâfi kureyş.",
        "106:2" to "Îlâfihim rıhleteş-şitâi ves-sayf.",
        "106:3" to "Felya'büdû rabbe hâzel-beyt.",
        "106:4" to "Ellezî at'amehüm min cû'in ve âmenehüm min havf.",

        // 107. Mâûn (1-7)
        "107:1" to "Era'eytellezî yükezzibü bid-dîn.",
        "107:2" to "Fezâlikellezî yedu''ul-yetîm.",
        "107:3" to "Ve lâ yehuddu 'alâ ta'âmil-miskîn.",
        "107:4" to "Feveylün lil-musallîn.",
        "107:5" to "Ellezîne hüm 'an salâtihim sâhûn.",
        "107:6" to "Ellezîne hüm yürâûn.",
        "107:7" to "Ve yemne'ûnel-mâ'ûn.",

        // 108. Kevser (1-3)
        "108:1" to "İnnâ a'taynâkel-kevser.",
        "108:2" to "Fesalli lirabbike venhar.",
        "108:3" to "İnne şâni'eke hüvel-ebter.",

        // 109. Kâfirûn (1-6)
        "109:1" to "Kul yâ eyyühel-kâfirûn.",
        "109:2" to "Lâ a'büdü mâ ta'büdûn.",
        "109:3" to "Ve lâ entüm 'âbidûne mâ a'büd.",
        "109:4" to "Ve lâ ene 'âbidün mâ 'abedtüm.",
        "109:5" to "Ve lâ entüm 'âbidûne mâ a'büd.",
        "109:6" to "Leküm dînüküm veliye dîn.",

        // 110. Nasr (1-3)
        "110:1" to "İzâ câe nasrullâhi vel-feth.",
        "110:2" to "Ve ra'eyten-nâse yedhulûne fî dînillâhi efvâcâ.",
        "110:3" to "Fesebbih bihamdi rabbike vestağfirh, innehû kâne tevvâbâ.",

        // 111. Tebbet (1-5)
        "111:1" to "Tebbet yedâ ebî lehebin ve tebb.",
        "111:2" to "Mâ ağnâ 'anhü mâlühû ve mâ keseb.",
        "111:3" to "Seyaslâ nâran zâte leheb.",
        "111:4" to "Vemraetühû hammâletel-hatab.",
        "111:5" to "Fî cîdihâ hablün min mesed.",

        // 112. İhlâs (1-4)
        "112:1" to "Kul hüvallâhü ehad.",
        "112:2" to "Allâhüs-samed.",
        "112:3" to "Lem yelid ve lem yûled.",
        "112:4" to "Ve lem yekün lehû küfüven ehad.",

        // 113. Felak (1-5)
        "113:1" to "Kul e'ûzü birabbil-felak.",
        "113:2" to "Min şerri mâ halak.",
        "113:3" to "Ve min şerri ğâsikın izâ vekab.",
        "113:4" to "Ve min şerrin-neffâsâti fil-'ukad.",
        "113:5" to "Ve min şerri hâsidin izâ hased.",

        // 114. Nâs (1-6)
        "114:1" to "Kul e'ûzü birabbin-nâs.",
        "114:2" to "Melikin-nâs.",
        "114:3" to "İlâhin-nâs.",
        "114:4" to "Min şerril-vesvâsil-hannâs.",
        "114:5" to "Ellezî yüvesvisü fî sudûrin-nâs.",
        "114:6" to "Minel-cinneti ven-nâs."
    )

    // Hurûf-u Mukattaa (Disconnected letters at the start of 29 Surahs)
    private val mukattaaBySurah = mapOf(
        "2:1" to "Elif, Lâm, Mîm.",
        "3:1" to "Elif, Lâm, Mîm.",
        "7:1" to "Elif, Lâm, Mîm, Sâd.",
        "10:1" to "Elif, Lâm, Râ. Tilke âyâtül-kitâbil-hakîm.",
        "11:1" to "Elif, Lâm, Râ. Kitâbün uhkimet âyâtühû sümme fussılet min ledün hakîmin habîr.",
        "12:1" to "Elif, Lâm, Râ. Tilke âyâtül-kitâbil-mübîn.",
        "13:1" to "Elif, Lâm, Mîm, Râ. Tilke âyâtül-kitâb, vellezî ünzile ileyke min rabbikel-hakku ve lâkinne ekseran-nâsi lâ yü'minûn.",
        "14:1" to "Elif, Lâm, Râ. Kitâbün enzelnâhü ileyke lituhricen-nâse minez-zulümâti ilen-nûri bi-izni rabbihim ilâ sırâtıl-'azîzil-hamîd.",
        "15:1" to "Elif, Lâm, Râ. Tilke âyâtül-kitâbi ve kur'ânin mübîn.",
        "19:1" to "Kâf, Hâ, Yâ, 'Ayn, Sâd.",
        "20:1" to "Tâ, Hâ.",
        "26:1" to "Tâ, Sîn, Mîm.",
        "27:1" to "Tâ, Sîn. Tilke âyâtül-kur'âni ve kitâbin mübîn.",
        "28:1" to "Tâ, Sîn, Mîm.",
        "29:1" to "Elif, Lâm, Mîm.",
        "30:1" to "Elif, Lâm, Mîm.",
        "31:1" to "Elif, Lâm, Mîm.",
        "32:1" to "Elif, Lâm, Mîm.",
        "36:1" to "Yâ, Sîn.",
        "38:1" to "Sâd, vel-kur'âni ziz-zikr.",
        "40:1" to "Hâ, Mîm.",
        "41:1" to "Hâ, Mîm.",
        "42:1" to "Hâ, Mîm.",
        "42:2" to "'Ayn, Sîn, Kâf.",
        "43:1" to "Hâ, Mîm.",
        "44:1" to "Hâ, Mîm.",
        "45:1" to "Hâ, Mîm.",
        "46:1" to "Hâ, Mîm.",
        "50:1" to "Kâf, vel-kur'ânil-mecîd.",
        "68:1" to "Nûn, vel-kalemi ve mâ yesturûn."
    )

    fun getExactOrNull(surahNumber: Int, numberInSurah: Int): String? {
        if (surahNumber <= 0 || numberInSurah <= 0) return null
        val key = "$surahNumber:$numberInSurah"
        return surahAyahPronunciations[key] ?: mukattaaBySurah[key]
    }

    /**
     * Cleans and formats raw transliteration received from api.alquran.cloud (tr.transliteration)
     * or jsdelivr (tur-latinalphabet), prioritizing curated exact matches where available.
     */
    fun cleanApiTransliteration(
        raw: String?,
        surahNumber: Int,
        numberInSurah: Int,
        arabicText: String
    ): String {
        getExactOrNull(surahNumber, numberInSurah)?.let { return it }

        if (!raw.isNullOrBlank()) {
            var cleaned = raw
                .replace(Regex("<[^>]*>"), "")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&amp;", "&")
                .replace(Regex("\\s+"), " ")
                .trim()

            if (cleaned.isNotEmpty()) {
                cleaned = cleaned.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
                if (!cleaned.endsWith(".") && !cleaned.endsWith("!") && !cleaned.endsWith("?")) {
                    cleaned += "."
                }
                return cleaned
            }
        }

        return transliterateArabicToTurkish(arabicText)
    }

    fun getPronunciation(
        surahNumber: Int = 0,
        numberInQuran: Int,
        numberInSurah: Int,
        arabicText: String
    ): String {
        // 1. Check exact surah:ayah map first
        getExactOrNull(surahNumber, numberInSurah)?.let { return it }

        // 2. Fallback to Uthmani-to-Turkish phonetic transliteration
        return transliterateArabicToTurkish(arabicText)
    }

    /**
     * Converts Quranic Uthmani Arabic script into natural Turkish Latin reading (Okunuş).
     */
    fun transliterateArabicToTurkish(arabic: String): String {
        if (arabic.isBlank()) return ""

        // Check standalone Huruf-u Mukattaa patterns
        val trimmed = arabic.trim()
        when (trimmed) {
            "الٓمٓ" -> return "Elif, Lâm, Mîm."
            "الٓمٓصٓ" -> return "Elif, Lâm, Mîm, Sâd."
            "الٓر" -> return "Elif, Lâm, Râ."
            "الٓمٓر" -> return "Elif, Lâm, Mîm, Râ."
            "كهيعص", "كٓهيعٓصٓ" -> return "Kâf, Hâ, Yâ, 'Ayn, Sâd."
            "طه" -> return "Tâ, Hâ."
            "طسم", "طسٓمٓ" -> return "Tâ, Sîn, Mîm."
            "طس", "طسٓ" -> return "Tâ, Sîn."
            "يس", "يسٓ" -> return "Yâ, Sîn."
            "حم", "حمٓ" -> return "Hâ, Mîm."
            "عسق", "عٓسٓقٓ" -> return "'Ayn, Sîn, Kâf."
        }

        // Pre-normalize Uthmani pause marks, small high signs, tatweel, and common sacred words
        var norm = trimmed
            .replace(Regex("[ۛۖۗۙۚۜ۞۩ۭۢ۟۠ۡـٓ]"), "")
            .replace("ٱللَّهِ", "allâhi")
            .replace("اللَّهِ", "allâhi")
            .replace("ٱللَّهُ", "allâhü")
            .replace("اللَّهُ", "allâhü")
            .replace("ٱللَّهَ", "allâhe")
            .replace("اللَّهَ", "allâhe")
            .replace("لِلَّهِ", "lillâhi")
            .replace("بِٱللَّهِ", "billâhi")
            .replace("ٱلرَّحْمَٰنِ", "er-rahmâni")
            .replace("ٱلرَّحِيمِ", "er-rahîmi")
            .replace("أُو۟لَٰئِكَ", "ülâike")
            .replace("أُولَٰئِكَ", "ülâike")
            .replace("ذَٰلِكَ", "zâlike")
            .replace("ذَٰلِكُمْ", "zâliküm")
            .replace("هَٰذَا", "hâzâ")
            .replace("هَٰذِهِ", "hâzihî")
            .replace("هَٰؤُلَاءِ", "hâülâi")
            .replace("إِلَٰهَ", "ilâhe")
            .replace("إِلَٰهُ", "ilâhü")
            .replace("لَٰكِنْ", "lâkin")
            .replace("لَٰكِنَّ", "lâkinne")
            .replace("ٱلسَّمَٰوَٰتِ", "es-semâvâti")
            .replace("ٱلصَّلَوٰةَ", "es-salâte")
            .replace("ٱلزَّكَوٰةَ", "ez-zekâte")
            .replace("ٱلْحَيَوٰةَ", "el-hayâte")

        val words = norm.split(Regex("\\s+")).filter { it.isNotBlank() }
        val transliteratedWords = words.map { word -> transliterateWord(word) }
        var result = transliteratedWords.joinToString(" ")
            .replace(Regex("\\s+"), " ")
            .trim()

        if (result.isNotEmpty()) {
            result = result.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            if (!result.endsWith(".") && !result.endsWith("!") && !result.endsWith("?")) {
                result += "."
            }
        }
        return result
    }

    private val heavyConsonants = setOf('ح', 'خ', 'ص', 'ض', 'ط', 'ظ', 'ع', 'غ', 'ق', 'ر')
    private val sunLetters = setOf('ت', 'ث', 'د', 'ذ', 'ر', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ل', 'ن')

    private fun transliterateWord(word: String): String {
        // If already partially Latin from sacred-word replacement
        if (word.all { it in 'a'..'z' || it in 'A'..'Z' || it in "âîûçğıöşüÇĞİÖŞÜ'-.," }) {
            return word
        }

        val sb = StringBuilder()
        var i = 0
        val len = word.length
        var lastArabicConsonant = ' '

        // Handle leading definite article (ٱل / ال)
        if (len >= 3 && (word[0] == 'ٱ' || word[0] == 'ا') && word[1] == 'ل') {
            val nextIdx = if (word[2] == 'ْ') 3 else 2
            if (nextIdx < len && word[nextIdx] in sunLetters) {
                val sunSound = mapConsonant(word[nextIdx])
                sb.append("e").append(sunSound).append("-").append(sunSound)
                lastArabicConsonant = word[nextIdx]
                i = nextIdx + 1
                // Skip shaddah on sun letter since we already doubled it
                if (i < len && word[i] == 'ّ') i++
            } else {
                sb.append("el-")
                i = nextIdx
            }
        }

        while (i < len) {
            val c = word[i]
            val next = if (i + 1 < len) word[i + 1] else ' '
            val next2 = if (i + 2 < len) word[i + 2] else ' '

            when (c) {
                // Pass-through already mapped Latin characters
                in 'a'..'z', in 'A'..'Z', 'â', 'î', 'û', 'ü', 'ö', 'ç', 'ş', 'ğ', 'ı', '-' -> {
                    sb.append(c)
                }
                'ء', 'أ', 'ؤ', 'ئ' -> {
                    lastArabicConsonant = c
                    if (i == 0) {
                        // Initial hamza gets its vowel from the following haraka
                        if (next !in "ًٌٍَُِ") sb.append("e")
                    } else if (next == 'ْ') {
                        sb.append("'")
                        i++ // consume sukun
                    } else {
                        sb.append("'")
                    }
                }
                'إ' -> {
                    lastArabicConsonant = c
                    if (next == 'ِ') {
                        sb.append("i")
                        i++
                    } else {
                        sb.append("i")
                    }
                }
                'آ' -> {
                    sb.append("â")
                }
                'ٱ' -> {
                    // Wasla in middle of word (e.g. وَٱلْ...)
                    if (next == 'ل') {
                        val afterLam = if (i + 2 < len && word[i + 2] == 'ْ') i + 3 else i + 2
                        if (afterLam < len && word[afterLam] in sunLetters) {
                            val sunSound = mapConsonant(word[afterLam])
                            sb.append(sunSound).append("-")
                            // Let the loop visit the sun letter next
                            i = afterLam - 1
                        } else {
                            sb.append("l-")
                            i = afterLam - 1
                        }
                    } else if (i == 0) {
                        sb.append("i")
                    }
                }
                'ا' -> {
                    // Silent alif at end of plural verb (after و)
                    val prev = if (i > 0) word[i - 1] else ' '
                    if (prev != 'و' && prev != 'َ' && prev != 'ً') {
                        if (i == 0) sb.append("e") else sb.append("â")
                    }
                }
                'ى' -> {
                    val prev = if (i > 0) word[i - 1] else ' '
                    if (prev != 'َ' && prev != 'ِ') {
                        sb.append("â")
                    }
                }
                'ة' -> {
                    if (i == len - 1 || (i == len - 2 && next in "ًٌٍَُِْ")) {
                        // At end of word, taa marbuta with vowel or pausal h
                        if (next in "ًٌٍَُِ") {
                            sb.append("t")
                        } else {
                            sb.append("h")
                        }
                    } else {
                        sb.append("t")
                    }
                    lastArabicConsonant = c
                }
                'و' -> {
                    lastArabicConsonant = c
                    sb.append("v")
                }
                'ي' -> {
                    lastArabicConsonant = c
                    sb.append("y")
                }
                'ب', 'ت', 'ث', 'ج', 'ح', 'خ', 'د', 'ذ', 'ر', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ع', 'غ', 'ف', 'ق', 'ك', 'ل', 'م', 'ن', 'ه' -> {
                    lastArabicConsonant = c
                    val mapped = mapConsonant(c)
                    if (next == 'ّ') {
                        sb.append(mapped).append(mapped)
                        i++ // consume shaddah
                    } else {
                        sb.append(mapped)
                    }
                }
                // Vowels & Long-vowel combinations
                'َ' -> { // Fatha
                    if (next == 'ا' || (next == 'ى' && next2 != 'َ' && next2 != 'ِ' && next2 != 'ُ') || next == 'ٰ') {
                        sb.append("â")
                        i++ // consume long vowel carrier
                        if (i + 1 < len && word[i + 1] == 'ٰ') i++
                    } else {
                        sb.append(if (lastArabicConsonant in heavyConsonants) "a" else "e")
                    }
                }
                'ِ' -> { // Kasra
                    if ((next == 'ي' || next == 'ى') && next2 !in "ًٌٍَُِّ") {
                        sb.append("î")
                        i++ // consume ya
                    } else {
                        sb.append("i")
                    }
                }
                'ُ' -> { // Damma
                    if (next == 'و' && next2 !in "ًٌٍَُِّ") {
                        sb.append("û")
                        i++ // consume waw
                        if (i + 1 < len && word[i + 1] == 'ا') i++ // consume silent plural alif
                    } else {
                        sb.append(if (lastArabicConsonant in heavyConsonants) "u" else "ü")
                    }
                }
                'ً' -> { // Fathatan
                    sb.append(if (lastArabicConsonant in heavyConsonants) "an" else "en")
                    if (next == 'ا' || next == 'ى') i++ // consume tanween carrier alif
                }
                'ٍ' -> sb.append("in")
                'ٌ' -> sb.append(if (lastArabicConsonant in heavyConsonants) "un" else "ün")
                'ٰ' -> sb.append("â")
                'ْ' -> {} // Sukun
                'ّ' -> {
                    if (sb.isNotEmpty()) {
                        val last = sb.last()
                        if (last.isLetter() && last !in "aeıioöuüAEIİOÖUÜâîû") {
                            sb.append(last)
                        }
                    }
                }
            }
            i++
        }

        return sb.toString()
            .replace("ââ", "â")
            .replace("îî", "î")
            .replace("ûû", "û")
            .replace("eâ", "â")
            .replace("aâ", "â")
            .replace("iî", "î")
            .replace("üû", "û")
            .replace("uû", "û")
    }

    private fun mapConsonant(c: Char): String = when (c) {
        'ب' -> "b"
        'ت', 'ة', 'ط' -> "t"
        'ث', 'س', 'ص' -> "s"
        'ج' -> "c"
        'ح', 'خ', 'ه' -> "h"
        'د', 'ض' -> "d"
        'ذ', 'ز', 'ظ' -> "z"
        'ر' -> "r"
        'ش' -> "ş"
        'ع' -> "'"
        'غ' -> "ğ"
        'ف' -> "f"
        'ق', 'ك' -> "k"
        'ل' -> "l"
        'م' -> "m"
        'ن' -> "n"
        'و' -> "v"
        'ي', 'ى' -> "y"
        else -> ""
    }
}
