package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.PrayerSurah
import com.example.data.model.SurahCategory
import com.example.data.repository.AppStrings
import com.example.data.repository.HadithRepository
import com.example.data.repository.PrayerSurahsRepository
import com.example.service.AudioPlaybackType
import com.example.service.SurahAudioPlayer
import com.example.ui.components.GlassCard
import com.example.viewmodel.PrayerViewModel

enum class DuasHadithsTab {
    DUAS,
    HADITHS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuasAndHadithsScreen(
    viewModel: PrayerViewModel? = null
) {
    val context = LocalContext.current
    val audioPlayer = remember { SurahAudioPlayer(context) }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.release()
        }
    }

    val currentLanguage = viewModel?.currentLanguage?.collectAsState()?.value ?: AppLanguage.TURKISH
    val s = AppStrings.get(currentLanguage)

    var currentMainTab by remember { mutableStateOf(DuasHadithsTab.DUAS) }
    var selectedDua by remember { mutableStateOf<PrayerSurah?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDuaCategory by remember { mutableStateOf<String?>(null) } // null = All, "namaz", "gunluk"
    var selectedHadithCategory by remember { mutableStateOf<String?>(null) }
    var arabicFontSizeSp by remember { mutableStateOf(24) }
    var showMeaningDetail by remember { mutableStateOf(true) }

    val isPlaying by audioPlayer.isPlaying.collectAsState()
    val isAudioLoading by audioPlayer.isLoading.collectAsState()
    val currentSurahId by audioPlayer.currentPlayingId.collectAsState()
    val currentTrackMode by audioPlayer.playbackType.collectAsState()

    val namazDuaIds = remember {
        setOf(
            "subhaneke",
            "ettehiyyatu",
            "allahumme_salli",
            "allahumme_barik",
            "rabbena_atina",
            "kunut_duasi_1",
            "kunut_duasi_2"
        )
    }

    // Filtered Duas (both Namaz and Daily prayers)
    val allDuas = remember {
        PrayerSurahsRepository.allSurahsAndDuas.filter { it.category == SurahCategory.DUALAR }
    }

    val filteredDuas = remember(searchQuery, selectedDuaCategory, currentLanguage) {
        allDuas.filter { item ->
            val matchesCategory = when (selectedDuaCategory) {
                "namaz" -> item.id in namazDuaIds
                "gunluk" -> item.id !in namazDuaIds
                else -> true
            }

            val q = searchQuery.trim().lowercase()
            val matchesSearch = if (q.isBlank()) true else {
                item.getLocalizedTitle(currentLanguage).lowercase().contains(q) ||
                item.getLocalizedMeaning(currentLanguage).lowercase().contains(q) ||
                item.getLocalizedWhereRecited(currentLanguage).lowercase().contains(q) ||
                item.turkishPronunciation.lowercase().contains(q) ||
                item.arabicTitle.contains(q)
            }

            matchesCategory && matchesSearch
        }
    }

    // Filtered Hadiths
    val filteredHadiths = remember(searchQuery, selectedHadithCategory, currentLanguage) {
        HadithRepository.allHadiths.filter { item ->
            val matchesCategory = selectedHadithCategory == null || item.categoryTr == selectedHadithCategory

            val q = searchQuery.trim().lowercase()
            val matchesSearch = if (q.isBlank()) true else {
                item.getLocalizedCategory(currentLanguage).lowercase().contains(q) ||
                item.getLocalizedText(currentLanguage).lowercase().contains(q) ||
                item.getLocalizedSource(currentLanguage).lowercase().contains(q) ||
                item.arabicText.contains(q)
            }

            matchesCategory && matchesSearch
        }
    }

    // Intercept back button when in Dua detail
    BackHandler(enabled = selectedDua != null) {
        audioPlayer.stop()
        selectedDua = null
    }

    Crossfade(
        targetState = selectedDua,
        label = "duasCrossfade",
        modifier = Modifier.fillMaxSize()
    ) { activeDua ->
        if (activeDua == null) {
            // ==================== LIST VIEW (DUAS & HADITHS) ====================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
                    .testTag("duas_hadiths_screen")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = s.duasHadithsTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = s.duasHadithsSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Font size controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (arabicFontSizeSp > 18) arabicFontSizeSp -= 2 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A-", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        IconButton(
                            onClick = { if (arabicFontSizeSp < 36) arabicFontSizeSp += 2 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A+", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dual Main Tabs: "Dualar" vs "Hadis-i Şerifler"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            audioPlayer.stop()
                            currentMainTab = DuasHadithsTab.DUAS
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (currentMainTab == DuasHadithsTab.DUAS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (currentMainTab == DuasHadithsTab.DUAS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s.duasTab,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMainTab == DuasHadithsTab.DUAS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        onClick = {
                            audioPlayer.stop()
                            currentMainTab = DuasHadithsTab.HADITHS
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (currentMainTab == DuasHadithsTab.HADITHS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (currentMainTab == DuasHadithsTab.HADITHS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s.hadithsTab,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMainTab == DuasHadithsTab.HADITHS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (currentMainTab == DuasHadithsTab.DUAS) s.searchDua else s.searchHadith,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = if (currentMainTab == DuasHadithsTab.DUAS) s.searchDua else s.searchHadith,
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
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sub-category filters
                if (currentMainTab == DuasHadithsTab.DUAS) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedDuaCategory == null,
                            onClick = { selectedDuaCategory = null },
                            label = { Text(s.allDuas, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        FilterChip(
                            selected = selectedDuaCategory == "namaz",
                            onClick = { selectedDuaCategory = "namaz" },
                            label = { Text(s.namazDuas, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        FilterChip(
                            selected = selectedDuaCategory == "gunluk",
                            onClick = { selectedDuaCategory = "gunluk" },
                            label = { Text(s.dailyDuas, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Duas List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(filteredDuas, key = { it.id }) { dua ->
                            val isThisDuaPlaying = isPlaying && currentSurahId == dua.id
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        audioPlayer.stop()
                                        selectedDua = dua
                                    }
                                    .testTag("dua_item_${dua.id}"),
                                shape = RoundedCornerShape(16.dp),
                                elevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Direct Play / Stop button right on the card!
                                    Surface(
                                        onClick = {
                                            if (isThisDuaPlaying) {
                                                audioPlayer.stop()
                                            } else {
                                                audioPlayer.playArabic(dua)
                                            }
                                        },
                                        modifier = Modifier.size(38.dp),
                                        shape = CircleShape,
                                        color = if (isThisDuaPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isThisDuaPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                                contentDescription = s.listenArabic,
                                                tint = if (isThisDuaPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = dua.getLocalizedTitle(currentLanguage),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dua.getLocalizedWhereRecited(currentLanguage).ifBlank {
                                                dua.getLocalizedMeaning(currentLanguage)
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = dua.arabicTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = s.open,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Hadiths List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(filteredHadiths, key = { it.id }) { hadith ->
                            val hadithArabicId = "hadith_ar_${hadith.id}"
                            val hadithMeaningId = "hadith_tr_${hadith.id}"
                            val isPlayingHadithAr = isPlaying && currentSurahId == hadithArabicId
                            val isPlayingHadithMeaning = isPlaying && currentSurahId == hadithMeaningId

                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("hadith_card_${hadith.id}"),
                                shape = RoundedCornerShape(16.dp),
                                elevation = 1.dp
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Header: Category and Actions
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                        ) {
                                            Text(
                                                text = hadith.getLocalizedCategory(currentLanguage),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Play Hadith Arabic
                                            IconButton(
                                                onClick = {
                                                    audioPlayer.playCustomText(
                                                        id = hadithArabicId,
                                                        text = hadith.arabicText,
                                                        langCode = "ar",
                                                        isArabicRecitation = true
                                                    )
                                                },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isPlayingHadithAr) Icons.Default.StopCircle else Icons.Default.VolumeUp,
                                                    contentDescription = s.listenArabic,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            // Play Hadith Meaning
                                            IconButton(
                                                onClick = {
                                                    val langCode = when (currentLanguage) {
                                                        AppLanguage.TURKISH -> "tr"
                                                        AppLanguage.ENGLISH -> "en"
                                                        AppLanguage.ARABIC -> "ar"
                                                    }
                                                    audioPlayer.playCustomText(
                                                        id = hadithMeaningId,
                                                        text = hadith.getLocalizedText(currentLanguage),
                                                        langCode = langCode,
                                                        isArabicRecitation = false
                                                    )
                                                },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isPlayingHadithMeaning) Icons.Default.StopCircle else Icons.Default.RecordVoiceOver,
                                                    contentDescription = s.listenMeaning,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // Copy
                                            IconButton(
                                                onClick = {
                                                    val copyText = "${hadith.arabicText}\n\n${hadith.getLocalizedText(currentLanguage)}\n\n(${hadith.getLocalizedSource(currentLanguage)})"
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText(s.hadithsTab, copyText))
                                                    Toast.makeText(context, s.copied, Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = s.copy,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // Share
                                            IconButton(
                                                onClick = {
                                                    val shareText = "${hadith.arabicText}\n\n${hadith.getLocalizedText(currentLanguage)}\n\n(${hadith.getLocalizedSource(currentLanguage)})"
                                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "text/plain"
                                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                                    }
                                                    context.startActivity(Intent.createChooser(shareIntent, s.share))
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Share,
                                                    contentDescription = s.share,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Arabic Text
                                    Text(
                                        text = hadith.arabicText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = (arabicFontSizeSp - 2).sp,
                                            lineHeight = (arabicFontSizeSp * 1.5).sp
                                        ),
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                        thickness = 0.8.dp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Translation
                                    Text(
                                        text = hadith.getLocalizedText(currentLanguage),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 13.sp,
                                            lineHeight = 19.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Source
                                    Text(
                                        text = "${s.source}: ${hadith.getLocalizedSource(currentLanguage)}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ==================== DUA DETAIL VIEW ====================
            val isPlayingArabic = isPlaying && currentSurahId == activeDua.id && currentTrackMode == AudioPlaybackType.ARABIC
            val isPlayingMeaning = isPlaying && currentSurahId == activeDua.id && currentTrackMode == AudioPlaybackType.MEANING
            val localizedWhereRecited = activeDua.getLocalizedWhereRecited(currentLanguage)
            val localizedBenefits = activeDua.getLocalizedBenefits(currentLanguage)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)
                    .testTag("dua_detail_screen")
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = {
                            audioPlayer.stop()
                            selectedDua = null
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = s.duasTab,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = s.duasTab, style = MaterialTheme.typography.labelMedium)
                    }

                    // Font size controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (arabicFontSizeSp > 18) arabicFontSizeSp -= 2 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A-", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        IconButton(
                            onClick = { if (arabicFontSizeSp < 36) arabicFontSizeSp += 2 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A+", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Scrollable Dua Details
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Header Card
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = activeDua.getLocalizedTitle(currentLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = activeDua.arabicTitle,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (localizedWhereRecited.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${s.whereRecited} $localizedWhereRecited",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Audio & Action Controls (ALWAYS visible for all Duas!)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Listen Arabic Button (Always Enabled!)
                            Button(
                                onClick = {
                                    audioPlayer.playArabic(activeDua)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPlayingArabic) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("play_dua_arabic_btn")
                            ) {
                                if (isAudioLoading && currentTrackMode == AudioPlaybackType.ARABIC) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(s.loading, fontSize = 11.sp)
                                } else {
                                    Icon(
                                        imageVector = if (isPlayingArabic) Icons.Default.Stop else Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isPlayingArabic) s.stop else s.listenArabic, fontSize = 11.sp)
                                }
                            }

                            // 2. Listen Meaning Button (Always Enabled!)
                            OutlinedButton(
                                onClick = {
                                    audioPlayer.playTurkishMeaning(activeDua, currentLanguage)
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("play_dua_meaning_btn")
                            ) {
                                if (isAudioLoading && currentTrackMode == AudioPlaybackType.MEANING) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(s.loading, fontSize = 11.sp)
                                } else {
                                    Icon(
                                        imageVector = if (isPlayingMeaning) Icons.Default.Stop else Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isPlayingMeaning) s.stop else s.listenMeaning, fontSize = 11.sp)
                                }
                            }

                            // 3. Copy Button
                            OutlinedButton(
                                onClick = {
                                    val copyText = "${activeDua.getLocalizedTitle(currentLanguage)}\n\n${activeDua.arabicText}\n\n${s.pronunciation}:\n${activeDua.turkishPronunciation}\n\n${s.meaning}:\n${activeDua.getLocalizedMeaning(currentLanguage)}"
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText(s.duasTab, copyText))
                                    Toast.makeText(context, s.copied, Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(s.copy, fontSize = 11.sp)
                            }

                            // 4. Share Button
                            OutlinedButton(
                                onClick = {
                                    val shareText = "${activeDua.getLocalizedTitle(currentLanguage)}\n\n${activeDua.arabicText}\n\n${s.pronunciation}:\n${activeDua.turkishPronunciation}\n\n${s.meaning}:\n${activeDua.getLocalizedMeaning(currentLanguage)}"
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, s.share))
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(s.share, fontSize = 11.sp)
                            }
                        }
                    }

                    // Arabic Text Card
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = s.arabicTextLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = activeDua.arabicText,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontSize = arabicFontSizeSp.sp,
                                        lineHeight = (arabicFontSizeSp * 1.6).sp
                                    ),
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Pronunciation Card
                    if (activeDua.turkishPronunciation.isNotBlank()) {
                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = s.pronunciation,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = activeDua.turkishPronunciation,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Meaning Card
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = s.meaning,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(
                                        onClick = { showMeaningDetail = !showMeaningDetail },
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (showMeaningDetail) s.hideMeaning else s.showMeaning, fontSize = 11.sp)
                                    }
                                }

                                if (showMeaningDetail) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = activeDua.getLocalizedMeaning(currentLanguage),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Benefits Card
                    if (!localizedBenefits.isNullOrBlank()) {
                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = s.virtuesAndBenefits,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = localizedBenefits,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
