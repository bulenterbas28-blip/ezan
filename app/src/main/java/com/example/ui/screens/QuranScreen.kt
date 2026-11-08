package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Ayah
import com.example.data.model.SurahDetail
import com.example.data.model.SurahMeta
import com.example.data.repository.AppStrings
import com.example.data.repository.QuranRepository
import com.example.data.repository.UserPreferences
import com.example.service.AudioPlaybackType
import com.example.service.SurahAudioPlayer
import com.example.ui.components.GlassCard
import com.example.ui.theme.MushafEmerald
import com.example.ui.theme.MushafGoldBorder
import com.example.ui.theme.MushafGoldLight
import com.example.ui.theme.MushafInkPrimary
import com.example.ui.theme.MushafInkSecondary
import com.example.ui.theme.MushafPaperOuterBg
import com.example.ui.theme.MushafPaperPageBg
import com.example.ui.theme.MushafPaperVerseBg
import com.example.viewmodel.PrayerViewModel
import kotlinx.coroutines.launch

enum class QuranDisplayMode(val labelTr: String, val labelEn: String, val labelAr: String) {
    ARABIC_AND_MEAL("Arapça + Meal", "Arabic + Translation", "عربي + ترجمة"),
    ARABIC_AND_PRONUNCIATION("Arapça + Okunuş", "Arabic + Reading", "عربي + النطق"),
    ALL_IN_ONE("Hepsi (Arapça + Okunuş + Meal)", "All (Arabic + Reading + Translation)", "الكل (عربي + نطق + ترجمة)"),
    ONLY_ARABIC("Yalnız Arapça", "Arabic Only", "عربي فقط"),
    ONLY_MEAL("Yalnız Meal", "Translation Only", "الترجمة فقط");

    fun getLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.TURKISH -> labelTr
        AppLanguage.ENGLISH -> labelEn
        AppLanguage.ARABIC -> labelAr
    }
}

enum class QuranFilterCategory {
    ALL,
    POPULAR,
    JUZ,
    MECCAN,
    MEDINAN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: PrayerViewModel? = null,
    onFullScreenChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        QuranRepository.init(context)
    }

    val prefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val audioPlayer = remember { SurahAudioPlayer(context) }

    val currentLanguage = viewModel?.currentLanguage?.collectAsState()?.value ?: AppLanguage.TURKISH
    val s = AppStrings.get(currentLanguage)

    var isFullScreen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isFullScreen) {
        onFullScreenChanged(isFullScreen)
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.release()
            onFullScreenChanged(false)
        }
    }

    var selectedSurahMeta by remember { mutableStateOf<SurahMeta?>(null) }
    var surahDetail by remember { mutableStateOf<SurahDetail?>(null) }
    var isLoadingSurah by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf(QuranFilterCategory.ALL) }
    var selectedJuzNumber by remember { mutableStateOf<Int?>(null) }
    var displayMode by remember { mutableStateOf(QuranDisplayMode.ARABIC_AND_MEAL) }
    var arabicFontSizeSp by remember { mutableStateOf(25) }

    // Bookmark states
    var bookmarkedSurahNum by remember { mutableStateOf(prefs.bookmarkSurahNumber) }
    var bookmarkedSurahName by remember { mutableStateOf(prefs.bookmarkSurahName) }
    var bookmarkedAyahNum by remember { mutableStateOf(prefs.bookmarkAyahNumber) }

    val isPlaying by audioPlayer.isPlaying.collectAsState()
    val isAudioLoading by audioPlayer.isLoading.collectAsState()
    val isLooping by audioPlayer.isLooping.collectAsState()
    val audioProgress by audioPlayer.progress.collectAsState()
    val currentTrackMode by audioPlayer.playbackType.collectAsState()
    val currentPlayingAyahNum by audioPlayer.currentPlayingAyahNumber.collectAsState()

    // Key popular surahs set
    val popularSurahNumbers = remember {
        setOf(1, 36, 55, 56, 67, 78, 93, 94, 97, 103, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114)
    }

    // Filtered surahs list
    val filteredSurahs = remember(searchQuery, activeCategory, selectedJuzNumber, currentLanguage) {
        QuranRepository.allSurahs.filter { surah ->
            val matchesCategory = when (activeCategory) {
                QuranFilterCategory.ALL -> true
                QuranFilterCategory.POPULAR -> popularSurahNumbers.contains(surah.number)
                QuranFilterCategory.JUZ -> if (selectedJuzNumber != null) surah.juz == selectedJuzNumber else true
                QuranFilterCategory.MECCAN -> surah.revelationPlace == "Mekke"
                QuranFilterCategory.MEDINAN -> surah.revelationPlace == "Medine"
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().lowercase()
                surah.nameTr.lowercase().contains(q) ||
                surah.meaningTr.lowercase().contains(q) ||
                surah.getLocalizedMeaning(currentLanguage).lowercase().contains(q) ||
                surah.nameAr.contains(q) ||
                surah.number.toString() == q ||
                "cüz ${surah.juz}".contains(q) ||
                "juz ${surah.juz}".contains(q)
            }

            matchesCategory && matchesSearch
        }
    }

    // Immediate synchronous loading + background enrichment
    fun loadSurah(meta: SurahMeta, forceRefresh: Boolean = false) {
        surahDetail = QuranRepository.getCachedOrBuiltInDetail(meta.number)

        scope.launch {
            isLoadingSurah = true
            try {
                val detail = QuranRepository.getSurahDetail(meta.number, forceRefresh)
                surahDetail = detail
            } catch (_: Exception) {
                // Keep the built-in detail intact
            } finally {
                isLoadingSurah = false
            }
        }
    }

    // Load surah detail when a surah is selected
    LaunchedEffect(selectedSurahMeta) {
        val meta = selectedSurahMeta
        if (meta != null) {
            loadSurah(meta)
        } else {
            surahDetail = null
            isFullScreen = false
        }
    }

    // Intercept back button when inside a surah or full screen
    BackHandler(enabled = selectedSurahMeta != null || isFullScreen) {
        if (isFullScreen) {
            isFullScreen = false
        } else {
            audioPlayer.stop()
            selectedSurahMeta = null
        }
    }

    Crossfade(
        targetState = selectedSurahMeta,
        label = "quranModeCrossfade",
        modifier = Modifier.fillMaxSize()
    ) { surah ->
        if (surah == null) {
            // ==================== 1. SURAH INDEX LIST ====================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
                    .testTag("quran_index_screen")
            ) {
                // Header Row (Adaptive)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = s.quranTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp,
                            maxLines = 1
                        )
                        Text(
                            text = s.quranSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = s.quranBadge114,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Resume Bookmark Card (if saved)
                if (prefs.bookmarkTimestamp > 0L) {
                    val bookmarkedMeta = QuranRepository.getSurahMeta(bookmarkedSurahNum)
                    val localizedBookmarkSurahName = bookmarkedMeta?.getLocalizedName(currentLanguage) ?: bookmarkedSurahName
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val meta = QuranRepository.getSurahMeta(bookmarkedSurahNum)
                                selectedSurahMeta = meta
                            }
                            .testTag("resume_bookmark_card"),
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        borderWidth = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = s.continueReading,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "$localizedBookmarkSurahName, $bookmarkedAyahNum. ${s.ayah}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val meta = QuranRepository.getSurahMeta(bookmarkedSurahNum)
                                    selectedSurahMeta = meta
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(s.open, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = s.searchSurah,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = s.searchSurah,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = s.close)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("quran_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Category Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeCategory == QuranFilterCategory.ALL && selectedJuzNumber == null,
                        onClick = {
                            activeCategory = QuranFilterCategory.ALL
                            selectedJuzNumber = null
                        },
                        label = { Text(s.filterAllSurahs, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    FilterChip(
                        selected = activeCategory == QuranFilterCategory.POPULAR,
                        onClick = {
                            activeCategory = QuranFilterCategory.POPULAR
                            selectedJuzNumber = null
                        },
                        label = { Text(s.filterPopular, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    FilterChip(
                        selected = activeCategory == QuranFilterCategory.JUZ,
                        onClick = {
                            activeCategory = QuranFilterCategory.JUZ
                            if (selectedJuzNumber == null) selectedJuzNumber = 30
                        },
                        label = {
                            Text(
                                text = if (selectedJuzNumber != null) "${selectedJuzNumber}. ${s.juz}" else s.filterJuz,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    FilterChip(
                        selected = activeCategory == QuranFilterCategory.MECCAN,
                        onClick = {
                            activeCategory = QuranFilterCategory.MECCAN
                            selectedJuzNumber = null
                        },
                        label = { Text(s.filterMeccan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    FilterChip(
                        selected = activeCategory == QuranFilterCategory.MEDINAN,
                        onClick = {
                            activeCategory = QuranFilterCategory.MEDINAN
                            selectedJuzNumber = null
                        },
                        label = { Text(s.filterMedinan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Scrollable 30 Juz Selector Bar
                if (activeCategory == QuranFilterCategory.JUZ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..30).forEach { juzNum ->
                            val isSelected = selectedJuzNumber == juzNum
                            Surface(
                                onClick = { selectedJuzNumber = juzNum },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.height(30.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (juzNum) {
                                            30 -> "30. ${s.juz} (Amme)"
                                            29 -> "29. ${s.juz} (Tebâreke)"
                                            1 -> "1. ${s.juz} (Elif-Lâm)"
                                            else -> "$juzNum. ${s.juz}"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 114 Surahs LazyColumn
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { surahMeta ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    audioPlayer.stop()
                                    selectedSurahMeta = surahMeta
                                }
                                .testTag("surah_item_${surahMeta.number}"),
                            shape = RoundedCornerShape(14.dp),
                            elevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Surah Number Disc
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${surahMeta.number}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Localized Name & Details
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${surahMeta.number}. ${surahMeta.getLocalizedName(currentLanguage)}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                        ) {
                                            Text(
                                                text = surahMeta.getLocalizedRevelationPlace(currentLanguage),
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${surahMeta.getLocalizedMeaning(currentLanguage)} • ${surahMeta.ayahCount} ${s.ayah} • ${surahMeta.juz}. ${s.juz}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Arabic Name
                                Text(
                                    text = surahMeta.nameAr,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = s.open,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ==================== 2. SURAH READER / MUSHAF PAPER VIEW ====================
            val listState = rememberLazyListState()
            val hasPrevious = surah.number > 1
            val hasNext = surah.number < 114
            val ayahsList = surahDetail?.ayahs ?: emptyList()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MushafPaperOuterBg,
                                MushafPaperPageBg,
                                MushafPaperOuterBg
                            )
                        )
                    )
                    .padding(
                        start = if (isFullScreen) 4.dp else 8.dp,
                        end = if (isFullScreen) 4.dp else 8.dp,
                        top = if (isFullScreen) 4.dp else 6.dp,
                        bottom = if (isFullScreen) 4.dp else 6.dp
                    )
                    .testTag("quran_reader_screen")
            ) {
                // Mushaf Page Sheet with double-ruled gold frame & subtle paper texture
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp))
                        .border(
                            width = 2.dp,
                            color = MushafGoldBorder,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    color = MushafPaperPageBg
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                // Subtle horizontal paper grain lines
                                val stepPx = 28.dp.toPx()
                                var y = stepPx
                                while (y < size.height) {
                                    drawLine(
                                        color = MushafGoldBorder.copy(alpha = 0.06f),
                                        start = Offset(12.dp.toPx(), y),
                                        end = Offset(size.width - 12.dp.toPx(), y),
                                        strokeWidth = 1f
                                    )
                                    y += stepPx
                                }
                                // Inner decorative Mushaf frame rule
                                val inset = 6.dp.toPx()
                                drawRoundRect(
                                    color = MushafGoldBorder.copy(alpha = 0.45f),
                                    topLeft = Offset(inset, inset),
                                    size = Size(size.width - inset * 2, size.height - inset * 2),
                                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                            .padding(
                                start = if (isFullScreen) 10.dp else 12.dp,
                                end = if (isFullScreen) 10.dp else 12.dp,
                                top = 10.dp,
                                bottom = 8.dp
                            )
                    ) {
                        // Top Navigation Bar on Mushaf Paper
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    audioPlayer.stop()
                                    selectedSurahMeta = null
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MushafEmerald.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, MushafEmerald.copy(alpha = 0.35f)),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = s.surahsButton,
                                        modifier = Modifier.size(15.dp),
                                        tint = MushafEmerald
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = s.surahsButton,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MushafEmerald
                                    )
                                }
                            }

                            // Middle title with Mushaf ornament
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .padding(horizontal = 6.dp)
                            ) {
                                Text(
                                    text = "${surah.number}. ${surah.getLocalizedName(currentLanguage)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MushafInkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${surah.juz}. ${s.juz} • ${surah.ayahCount} ${s.ayah}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MushafInkSecondary
                                )
                            }

                            // Action icons: Font size & Full screen
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                IconButton(
                                    onClick = { if (arabicFontSizeSp > 18) arabicFontSizeSp -= 2 },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text(
                                        text = "A-",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MushafInkPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { if (arabicFontSizeSp < 38) arabicFontSizeSp += 2 },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text(
                                        text = "A+",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MushafInkPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { isFullScreen = !isFullScreen },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = if (isFullScreen) s.exitFullScreen else s.fullScreen,
                                        modifier = Modifier.size(20.dp),
                                        tint = MushafEmerald
                                    )
                                }
                            }
                        }

                        // Scrollable Mushaf Reader Content
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            // Item 1: Illuminated Mushaf Surah Title Plaque (only if not fullscreen)
                            if (!isFullScreen) {
                                item(key = "header_meta") {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFFF1E4C3),
                                        border = BorderStroke(1.5.dp, MushafGoldBorder)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        colors = listOf(
                                                            MushafGoldBorder.copy(alpha = 0.18f),
                                                            Color(0xFFFDF8EA),
                                                            MushafGoldBorder.copy(alpha = 0.18f)
                                                        )
                                                    )
                                                )
                                                .padding(vertical = 12.dp, horizontal = 14.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "۞ ${surah.nameAr} ۞",
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MushafEmerald
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${surah.number}. ${surah.getLocalizedName(currentLanguage)}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MushafInkPrimary
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${surah.getLocalizedMeaning(currentLanguage)} • ${surah.ayahCount} ${s.ayah} • ${surah.getLocalizedRevelationPlace(currentLanguage)} • ${surah.juz}. ${s.juz}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = MushafInkSecondary,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }

                                // Item 2: Audio Player & Display Mode Panel on Paper
                                item(key = "audio_and_mode_panel") {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFFF3E9CF),
                                        border = BorderStroke(1.dp, MushafGoldBorder.copy(alpha = 0.55f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            // Display Mode Chips
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                QuranDisplayMode.entries.forEach { mode ->
                                                    val isSelected = displayMode == mode
                                                    Surface(
                                                        onClick = { displayMode = mode },
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSelected) MushafEmerald else MushafPaperVerseBg,
                                                        border = BorderStroke(
                                                            1.dp,
                                                            if (isSelected) MushafEmerald else MushafGoldBorder.copy(alpha = 0.45f)
                                                        )
                                                    ) {
                                                        Box(
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = mode.getLabel(currentLanguage),
                                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                color = if (isSelected) Color.White else MushafInkPrimary
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // Audio Controls Row: Both Arabic Recitation & Localized Meaning Audio
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val isPlayingArabic = isPlaying && currentTrackMode == AudioPlaybackType.ARABIC
                                                val isPlayingMeaning = isPlaying && currentTrackMode == AudioPlaybackType.MEANING

                                                // 1. Play Surah in Arabic
                                                Button(
                                                    onClick = {
                                                        audioPlayer.playSurahAyahs(surah.number, ayahsList)
                                                    },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isPlayingArabic) Color(0xFFB88218) else MushafEmerald,
                                                        contentColor = Color.White
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                    modifier = Modifier.testTag("play_surah_arabic_btn")
                                                ) {
                                                    if (isAudioLoading && currentTrackMode == AudioPlaybackType.ARABIC) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(14.dp),
                                                            strokeWidth = 2.dp,
                                                            color = Color.White
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(s.loading, fontSize = 11.sp)
                                                    } else {
                                                        Icon(
                                                            imageVector = if (isPlayingArabic) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(if (isPlayingArabic) s.pause else s.listenSurah, fontSize = 11.sp)
                                                    }
                                                }

                                                // 2. Play Surah Meaning
                                                OutlinedButton(
                                                    onClick = {
                                                        audioPlayer.playSurahMeaning(surah.number, ayahsList, currentLanguage)
                                                    },
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = BorderStroke(1.dp, MushafEmerald),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = MushafEmerald
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                    modifier = Modifier.testTag("play_surah_meaning_btn")
                                                ) {
                                                    if (isAudioLoading && currentTrackMode == AudioPlaybackType.MEANING) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(14.dp),
                                                            strokeWidth = 2.dp,
                                                            color = MushafEmerald
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(s.loading, fontSize = 11.sp)
                                                    } else {
                                                        Icon(
                                                            imageVector = if (isPlayingMeaning) Icons.Default.Stop else Icons.Default.RecordVoiceOver,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(if (isPlayingMeaning) s.stop else s.listenMeaning, fontSize = 11.sp)
                                                    }
                                                }

                                                if (isPlaying || isAudioLoading) {
                                                    IconButton(onClick = { audioPlayer.stop() }, modifier = Modifier.size(32.dp)) {
                                                        Icon(
                                                            imageVector = Icons.Default.Stop,
                                                            contentDescription = s.stop,
                                                            tint = MushafInkPrimary
                                                        )
                                                    }
                                                }

                                                IconButton(onClick = { audioPlayer.toggleLoop() }, modifier = Modifier.size(32.dp)) {
                                                    Icon(
                                                        imageVector = Icons.Default.Repeat,
                                                        contentDescription = s.repeat,
                                                        tint = if (isLooping) MushafEmerald else MushafInkSecondary.copy(alpha = 0.5f),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }

                                            if (isPlaying || currentPlayingAyahNum != null) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                if (currentPlayingAyahNum != null) {
                                                    Text(
                                                        text = "${s.playingNow}: $currentPlayingAyahNum. ${s.ayah}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MushafEmerald,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                }
                                                LinearProgressIndicator(
                                                    progress = { audioProgress },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(3.dp)
                                                        .clip(RoundedCornerShape(2.dp)),
                                                    color = MushafEmerald,
                                                    trackColor = MushafGoldLight
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Item 3: Bismillah Banner on Mushaf Paper (All surahs except Surah 9 Tevbe)
                            if (surah.number != 9) {
                                item(key = "bismillah_banner") {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFFF6EBD1),
                                        border = BorderStroke(1.2.dp, MushafGoldBorder)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 10.dp, horizontal = 14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                                style = MaterialTheme.typography.titleLarge.copy(fontSize = (arabicFontSizeSp + 2).sp),
                                                fontWeight = FontWeight.Bold,
                                                color = MushafInkPrimary,
                                                textAlign = TextAlign.Center
                                            )
                                            if (displayMode != QuranDisplayMode.ONLY_ARABIC) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = s.bismillahMeaning,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = MushafInkSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Loading state while fetching full Surah from network
                            if (isLoadingSurah && ayahsList.isEmpty()) {
                                item(key = "surah_loading_card") {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        color = MushafPaperVerseBg,
                                        border = BorderStroke(1.dp, MushafGoldBorder.copy(alpha = 0.55f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 28.dp, horizontal = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                strokeWidth = 3.dp,
                                                color = MushafEmerald
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "${surah.getLocalizedName(currentLanguage)} (${surah.ayahCount} ${s.ayah}) ${s.loading}...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MushafInkPrimary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }

                            // Ayahs List on Mushaf Paper
                            items(ayahsList, key = { it.numberInSurah }) { ayah ->
                                val isBookmarked = bookmarkedSurahNum == surah.number && bookmarkedAyahNum == ayah.numberInSurah
                                val isThisAyahPlaying = currentPlayingAyahNum == ayah.numberInSurah && isPlaying

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ayah_card_${ayah.numberInSurah}"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = when {
                                        isThisAyahPlaying -> Color(0xFFEDE0BC)
                                        isBookmarked -> Color(0xFFF4E8C8)
                                        else -> MushafPaperVerseBg
                                    },
                                    border = BorderStroke(
                                        width = if (isThisAyahPlaying || isBookmarked) 1.6.dp else 1.dp,
                                        color = when {
                                            isThisAyahPlaying -> MushafEmerald
                                            isBookmarked -> MushafGoldBorder
                                            else -> MushafGoldBorder.copy(alpha = 0.42f)
                                        }
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        // Header: Ornate Mushaf Ayah Rosette Medallion & Actions
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                // Mushaf Ayah Medallion
                                                Surface(
                                                    modifier = Modifier.size(30.dp),
                                                    shape = CircleShape,
                                                    color = if (isThisAyahPlaying) MushafEmerald else Color(0xFFEADAB3),
                                                    border = BorderStroke(1.2.dp, MushafGoldBorder)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = "${ayah.numberInSurah}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = if (isThisAyahPlaying) Color.White else MushafInkPrimary
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "${ayah.numberInSurah}. ${s.ayah}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MushafEmerald
                                                )
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                // Play single Ayah Arabic recitation
                                                IconButton(
                                                    onClick = {
                                                        if (isThisAyahPlaying) {
                                                            audioPlayer.stop()
                                                        } else {
                                                            audioPlayer.playSingleAyah(
                                                                surahNumber = surah.number,
                                                                ayah = ayah,
                                                                playMeaning = false,
                                                                lang = currentLanguage
                                                            )
                                                        }
                                                    },
                                                    modifier = Modifier.size(30.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isThisAyahPlaying) Icons.Default.StopCircle else Icons.Default.VolumeUp,
                                                        contentDescription = s.listenAyah,
                                                        tint = MushafEmerald,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                // Play single Ayah Meaning
                                                IconButton(
                                                    onClick = {
                                                        audioPlayer.playSingleAyah(
                                                            surahNumber = surah.number,
                                                            ayah = ayah,
                                                            playMeaning = true,
                                                            lang = currentLanguage
                                                        )
                                                    },
                                                    modifier = Modifier.size(30.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.RecordVoiceOver,
                                                        contentDescription = s.listenMeaning,
                                                        tint = MushafInkSecondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }

                                                // Bookmark button
                                                IconButton(
                                                    onClick = {
                                                        prefs.saveQuranBookmark(surah.number, surah.nameTr, ayah.numberInSurah)
                                                        bookmarkedSurahNum = surah.number
                                                        bookmarkedSurahName = surah.nameTr
                                                        bookmarkedAyahNum = ayah.numberInSurah
                                                        Toast.makeText(
                                                            context,
                                                            "${surah.getLocalizedName(currentLanguage)} ${ayah.numberInSurah}. ${s.ayah} - ${s.bookmarkedToast}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                                        contentDescription = s.bookmark,
                                                        tint = if (isBookmarked) MushafEmerald else MushafInkSecondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }

                                                // Copy button
                                                IconButton(
                                                    onClick = {
                                                        val pronunciation = ayah.getEffectivePronunciation(surah.number)
                                                        val copyText = buildString {
                                                            append("${surah.getLocalizedName(currentLanguage)} (${ayah.numberInSurah}. ${s.ayah}):\n\n")
                                                            append("${ayah.arabicText}\n\n")
                                                            if (pronunciation.isNotBlank()) {
                                                                append("${s.pronunciation}:\n$pronunciation\n\n")
                                                            }
                                                            append("${s.meaning}:\n${ayah.getLocalizedMeaning(currentLanguage)}")
                                                        }
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        clipboard.setPrimaryClip(ClipData.newPlainText(s.ayah, copyText))
                                                        Toast.makeText(context, s.copyToast, Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = s.copy,
                                                        tint = MushafInkSecondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // 1. Arabic Verse Text with Mushaf Verse End Ornament
                                        if (displayMode != QuranDisplayMode.ONLY_MEAL) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = "${ayah.arabicText} ﴿${ayah.numberInSurah}﴾",
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontSize = arabicFontSizeSp.sp,
                                                    lineHeight = (arabicFontSizeSp * 1.65).sp
                                                ),
                                                fontWeight = FontWeight.SemiBold,
                                                color = MushafInkPrimary,
                                                textAlign = TextAlign.Right,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        // 2. Arabic Pronunciation (Okunuş) on Warm Paper Strip
                                        val showPronunciation = displayMode == QuranDisplayMode.ARABIC_AND_PRONUNCIATION ||
                                            displayMode == QuranDisplayMode.ALL_IN_ONE

                                        if (showPronunciation) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFFF3E8CC),
                                                border = BorderStroke(1.dp, MushafGoldBorder.copy(alpha = 0.45f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                                    Text(
                                                        text = s.pronunciationLabel,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                        fontWeight = FontWeight.Bold,
                                                        color = MushafEmerald
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = ayah.getEffectivePronunciation(surah.number),
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontSize = (arabicFontSizeSp - 10).coerceAtLeast(13).sp,
                                                            lineHeight = ((arabicFontSizeSp - 10).coerceAtLeast(13) * 1.45).sp
                                                        ),
                                                        fontWeight = FontWeight.Medium,
                                                        color = MushafInkPrimary
                                                    )
                                                }
                                            }
                                        }

                                        // 3. Localized Meaning (Meal)
                                        val showMeaning = displayMode == QuranDisplayMode.ARABIC_AND_MEAL ||
                                            displayMode == QuranDisplayMode.ALL_IN_ONE ||
                                            displayMode == QuranDisplayMode.ONLY_MEAL

                                        if (showMeaning) {
                                            if (displayMode != QuranDisplayMode.ONLY_MEAL) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                HorizontalDivider(
                                                    color = MushafGoldBorder.copy(alpha = 0.35f),
                                                    thickness = 0.8.dp
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                            Text(
                                                text = ayah.getLocalizedMeaning(currentLanguage),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = (arabicFontSizeSp - 10).coerceAtLeast(13).sp,
                                                    lineHeight = ((arabicFontSizeSp - 10).coerceAtLeast(13) * 1.5).sp
                                                ),
                                                color = MushafInkSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            // Retry button if offline and full surah could not be fetched yet
                            if (!isLoadingSurah && ayahsList.size < surah.ayahCount) {
                                item(key = "surah_retry_full_load") {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF3E8CC),
                                        border = BorderStroke(1.dp, MushafGoldBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${surah.getLocalizedName(currentLanguage)} (${surah.ayahCount} ${s.ayah})",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MushafInkPrimary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Button(
                                                onClick = { loadSurah(surah, forceRefresh = true) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MushafEmerald,
                                                    contentColor = Color.White
                                                ),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Tümünü Yükle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            // Navigation to Previous and Next Surah on Mushaf Paper
                            item(key = "navigation_surahs_bar") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (hasPrevious) {
                                        OutlinedButton(
                                            onClick = {
                                                audioPlayer.stop()
                                                selectedSurahMeta = QuranRepository.getSurahMeta(surah.number - 1)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, MushafGoldBorder),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MushafInkPrimary
                                            ),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(s.previousSurah, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    if (hasNext) {
                                        Button(
                                            onClick = {
                                                audioPlayer.stop()
                                                selectedSurahMeta = QuranRepository.getSurahMeta(surah.number + 1)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MushafEmerald,
                                                contentColor = Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(s.nextSurah, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
