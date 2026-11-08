package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.PrayerStatus
import com.example.data.model.PrayerType
import com.example.data.model.SinglePrayerInfo
import com.example.data.repository.AppStrings
import com.example.ui.components.CitySelectionDialog
import com.example.ui.components.GlassCard
import com.example.viewmodel.PrayerViewModel

@Composable
fun MainPrayerScreen(
    viewModel: PrayerViewModel,
    onNavigateToCompass: () -> Unit,
    onNavigateToDhikr: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onGpsRequest: () -> Unit
) {
    val context = LocalContext.current
    val currentCity by viewModel.currentCity.collectAsState()
    val isGpsActive by viewModel.isGpsActive.collectAsState()
    val todayTimes by viewModel.todayPrayerTimes.collectAsState()
    val countdown by viewModel.nextPrayerCountdown.collectAsState()
    val isAdhanPlaying by viewModel.adhanPlayer.isPlaying.collectAsState()
    val playingTitle by viewModel.adhanPlayer.currentPlayingTitle.collectAsState()
    val qiblaAngle by viewModel.qiblaBearing.collectAsState()
    val inspiration by viewModel.todayInspiration.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val strings = AppStrings.get(currentLanguage)

    var showCityDialog by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }

    if (showCityDialog) {
        CitySelectionDialog(
            currentSelectedPlate = currentCity.plateCode,
            onCitySelected = { viewModel.selectCity(it) },
            onGpsRequest = {
                onGpsRequest()
            },
            onDismiss = { showCityDialog = false },
            lang = currentLanguage
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_prayer_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar: City Selector & Date
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ROW 1: App Header Bar (Title & Top-Right Actions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Brand (Icon + Name)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.NightsStay,
                                    contentDescription = "Hilal",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = strings.appName.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
                            )
                            Text(
                                text = "81 ${strings.cityLocationTitle}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    // Top-Right Action Buttons: Language Dropdown + Settings Button (ALWAYS VISIBLE!)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Language Dropdown
                        Box {
                            Surface(
                                onClick = { showLanguageMenu = true },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(currentLanguage.flag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentLanguage.code.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showLanguageMenu,
                                onDismissRequest = { showLanguageMenu = false }
                            ) {
                                AppLanguage.entries.forEach { langOpt ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(langOpt.flag, fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = langOpt.nativeTitle,
                                                    fontWeight = if (langOpt == currentLanguage) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.setLanguage(langOpt)
                                            showLanguageMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Settings Icon Button - GUARANTEED TOP-RIGHT ANCHOR
                        Surface(
                            onClick = onNavigateToSettings,
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("top_settings_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = strings.settingsTitle,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // ROW 2: Location & Date Bar (Compact single-line location max 13-14 chars, zero vertical gap)
                val shortLocationTitle = remember(currentCity.name) {
                    val cleaned = currentCity.name
                        .substringBefore("(")
                        .substringAfterLast(",")
                        .replace("Mahallesi", "", ignoreCase = true)
                        .replace("Mah.", "", ignoreCase = true)
                        .trim()
                        .ifBlank { currentCity.name.trim() }
                    if (cleaned.length <= 14) cleaned else cleaned.take(13).trimEnd() + "…"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // City Selection Pill (strictly single-line, max 13-14 chars)
                    Surface(
                        onClick = { showCityDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .widthIn(max = 168.dp)
                            .testTag("city_selector_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isGpsActive) Icons.Default.MyLocation else Icons.Default.LocationOn,
                                contentDescription = "Konum",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = shortLocationTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Değiştir",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Gregorian and Hijri Date (strictly single-line each)
                    todayTimes?.let { times ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = times.getLocalizedGregorianDate(currentLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = times.getLocalizedHijriDate(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Hero Countdown Banner Card - Vakitler tab background fill + subtle edge shading
        item {
            val tabBgColor = MaterialTheme.colorScheme.background
            val tabSurfaceColor = MaterialTheme.colorScheme.surface
            val tabSurfaceVariant = MaterialTheme.colorScheme.surfaceVariant
            val primaryTone = MaterialTheme.colorScheme.primary
            val edgeShadowColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            val warmAccentBorder = Color(0xFFC6963A)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = primaryTone.copy(alpha = 0.22f),
                        spotColor = primaryTone.copy(alpha = 0.26f)
                    )
                    .testTag("hero_countdown_card"),
                shape = RoundedCornerShape(22.dp),
                color = tabSurfaceColor,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryTone.copy(alpha = 0.38f),
                            warmAccentBorder.copy(alpha = 0.45f),
                            primaryTone.copy(alpha = 0.32f)
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    tabSurfaceVariant.copy(alpha = 0.55f),
                                    tabSurfaceColor,
                                    tabBgColor.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .drawWithContent {
                            drawContent()
                            // Subtle perimeter edge shading (kenarlarda hafif gölgelendirme)
                            val edgeWidth = 18.dp.toPx()
                            // Top inner soft shadow
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(edgeShadowColor, Color.Transparent),
                                    startY = 0f,
                                    endY = edgeWidth
                                ),
                                size = Size(size.width, edgeWidth)
                            )
                            // Bottom inner soft shadow
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, edgeShadowColor),
                                    startY = size.height - edgeWidth,
                                    endY = size.height
                                ),
                                topLeft = Offset(0f, size.height - edgeWidth),
                                size = Size(size.width, edgeWidth)
                            )
                            // Left inner soft shadow
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(edgeShadowColor, Color.Transparent),
                                    startX = 0f,
                                    endX = edgeWidth
                                ),
                                size = Size(edgeWidth, size.height)
                            )
                            // Right inner soft shadow
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, edgeShadowColor),
                                    startX = size.width - edgeWidth,
                                    endX = size.width
                                ),
                                topLeft = Offset(size.width - edgeWidth, 0f),
                                size = Size(edgeWidth, size.height)
                            )
                            // Delicate inner frame highlight
                            val inset = 3.dp.toPx()
                            drawRoundRect(
                                color = primaryTone.copy(alpha = 0.10f),
                                topLeft = Offset(inset, inset),
                                size = Size(size.width - inset * 2, size.height - inset * 2),
                                cornerRadius = CornerRadius(19.dp.toPx(), 19.dp.toPx()),
                                style = Stroke(width = 0.7.dp.toPx())
                            )
                        }
                ) {
                    // Subtle watermark mosque illustration blended softly into Vakitler tab fill
                    Image(
                        painter = painterResource(id = R.drawable.bg_prayer_banner),
                        contentDescription = "Cami Görseli",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0.06f)
                    )

                    // Card Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Next Prayer Tag
                        countdown?.nextPrayer?.let { next ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = primaryTone.copy(alpha = 0.11f),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.8.dp,
                                    primaryTone.copy(alpha = 0.38f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = primaryTone,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val nextPrayerName = AppStrings.prayerName(next.type, currentLanguage)
                                    Text(
                                        text = "${strings.nextPrayer.uppercase()}: ${nextPrayerName.uppercase()} (${next.timeFormatted})",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.4.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Big Digital Countdown Display
                        countdown?.let { cd ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CountdownBox(digit = cd.formattedHours, label = strings.hours.uppercase())
                                Text(
                                    text = ":",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryTone,
                                    modifier = Modifier
                                        .padding(horizontal = 6.dp)
                                        .padding(bottom = 18.dp)
                                )
                                CountdownBox(digit = cd.formattedMinutes, label = strings.minutes.uppercase())
                                Text(
                                    text = ":",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryTone,
                                    modifier = Modifier
                                        .padding(horizontal = 6.dp)
                                        .padding(bottom = 18.dp)
                                )
                                CountdownBox(digit = cd.formattedSeconds, label = strings.seconds.uppercase())
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interval Progress Bar
                            Column(modifier = Modifier.fillMaxWidth()) {
                                LinearProgressIndicator(
                                    progress = { cd.progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = primaryTone,
                                    trackColor = primaryTone.copy(alpha = 0.16f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Audio Player & Qibla Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val nextTitle = countdown?.nextPrayer?.type?.let {
                                        AppStrings.prayerName(it, currentLanguage)
                                    } ?: strings.soundTypeAdhan
                                    viewModel.adhanPlayer.toggle(nextTitle)
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAdhanPlaying) Color(0xFFD32F2F) else primaryTone,
                                    contentColor = if (isAdhanPlaying) Color.White else MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("play_adhan_button")
                            ) {
                                Icon(
                                    imageVector = if (isAdhanPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = strings.soundTypeAdhan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAdhanPlaying) strings.stop else strings.listenArabic,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            OutlinedButton(
                                onClick = onNavigateToCompass,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(44.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = primaryTone.copy(alpha = 0.09f),
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.9.dp,
                                    primaryTone.copy(alpha = 0.42f)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = strings.tabKible,
                                    tint = primaryTone,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "%.0f° ${strings.tabKible}".format(qiblaAngle),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (isAdhanPlaying) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${strings.playingNow}: ${playingTitle ?: strings.soundTypeAdhan}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryTone
                            )
                        }
                    }
                }
            }
        }

        // Quick Navigation Pills Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = strings.tabKuran,
                    subtitle = strings.quranSubtitle,
                    icon = Icons.Default.AutoStories,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToQuran
                )
                QuickActionCard(
                    title = strings.tabKible,
                    subtitle = "%.0f°".format(qiblaAngle),
                    icon = Icons.Default.Explore,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCompass
                )
                QuickActionCard(
                    title = strings.tabTakvim,
                    subtitle = strings.calendarTitle,
                    icon = Icons.Default.CalendarMonth,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCalendar
                )
            }
        }

        // Section Title: Günün Ezan Vakitleri
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.todaysPrayerTimes,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    )
                ) {
                    Text(
                        text = strings.diyanetCalendar,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // 6 Prayer Cards
        todayTimes?.prayersList?.let { prayers ->
            items(prayers, key = { it.type.key }) { prayer ->
                PrayerItemCard(
                    prayer = prayer,
                    currentLanguage = currentLanguage,
                    onToggleSound = {
                        viewModel.togglePrayerSound(prayer.type)
                    }
                )
            }
        }

        // Daily Inspiration Card (Ayet / Hadith)
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_inspiration_card"),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
                borderWidth = 0.6.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (inspiration.type) {
                                    com.example.data.model.InspirationType.AYAT -> strings.verseOfTheDay
                                    com.example.data.model.InspirationType.HADITH -> strings.hadithOfTheDay
                                    com.example.data.model.InspirationType.DUA -> strings.duaOfTheDay
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    strings.appName,
                                    "${inspiration.getLocalizedText(currentLanguage)}\n\n${strings.source}: ${inspiration.getLocalizedSource(currentLanguage)}"
                                )
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, strings.copied, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = strings.copy,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    inspiration.arabicText?.let { arabic ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = arabic,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (currentLanguage != AppLanguage.ARABIC) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "“${inspiration.getLocalizedText(currentLanguage)}”",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "— ${inspiration.getLocalizedSource(currentLanguage)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CountdownBox(digit: String, label: String) {
    val bgFill = MaterialTheme.colorScheme.background
    val surfaceFill = MaterialTheme.colorScheme.surface
    val primaryTone = MaterialTheme.colorScheme.primary
    val edgeShade = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(width = 66.dp, height = 56.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = primaryTone.copy(alpha = 0.20f),
                    spotColor = primaryTone.copy(alpha = 0.25f)
                ),
            shape = RoundedCornerShape(14.dp),
            color = bgFill,
            border = androidx.compose.foundation.BorderStroke(
                width = 0.85.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryTone.copy(alpha = 0.36f),
                        Color(0xFFC6963A).copy(alpha = 0.42f),
                        primaryTone.copy(alpha = 0.28f)
                    )
                )
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                surfaceFill,
                                bgFill,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f)
                            )
                        )
                    )
                    .drawWithContent {
                        drawContent()
                        // Subtle inner edge shading around the digit box
                        val edgePx = 8.dp.toPx()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(edgeShade, Color.Transparent),
                                startY = 0f,
                                endY = edgePx
                            ),
                            size = Size(size.width, edgePx)
                        )
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, edgeShade),
                                startY = size.height - edgePx,
                                endY = size.height
                            ),
                            topLeft = Offset(0f, size.height - edgePx),
                            size = Size(size.width, edgePx)
                        )
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(edgeShade, Color.Transparent),
                                startX = 0f,
                                endX = edgePx
                            ),
                            size = Size(edgePx, size.height)
                        )
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, edgeShade),
                                startX = size.width - edgePx,
                                endX = size.width
                            ),
                            topLeft = Offset(size.width - edgePx, 0f),
                            size = Size(edgePx, size.height)
                        )
                    }
            ) {
                Text(
                    text = digit,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp,
            color = primaryTone
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        border = androidx.compose.foundation.BorderStroke(
            0.6.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PrayerItemCard(
    prayer: SinglePrayerInfo,
    currentLanguage: AppLanguage = AppLanguage.TURKISH,
    onToggleSound: () -> Unit
) {
    val isNext = prayer.status == PrayerStatus.ACTIVE_NEXT
    val isPast = prayer.status == PrayerStatus.PAST
    val s = AppStrings.get(currentLanguage)

    val containerColor = when {
        isNext -> MaterialTheme.colorScheme.primary.copy(alpha = 0.11f)
        isPast -> MaterialTheme.colorScheme.surface.copy(alpha = 0.62f)
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    }

    val borderStroke = if (isNext) {
        androidx.compose.foundation.BorderStroke(
            width = 0.9.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    Color(0xFFD4AF37).copy(alpha = 0.7f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                )
            )
        )
    } else {
        androidx.compose.foundation.BorderStroke(
            width = 0.55.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = if (isPast) 0.14f else 0.24f)
        )
    }

    val iconVector = when (prayer.type) {
        PrayerType.IMSAK -> Icons.Default.WbTwilight
        PrayerType.GUNES -> Icons.Default.WbSunny
        PrayerType.OGLE -> Icons.Default.LightMode
        PrayerType.IKINDI -> Icons.Default.Brightness5
        PrayerType.AKSAM -> Icons.Default.WbTwilight
        PrayerType.YATSI -> Icons.Default.Nightlight
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prayer_card_${prayer.type.key}"),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = borderStroke,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Subtle vertical accent bar for active/next prayer
            if (isNext) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFD4AF37),
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            // Sleek Rounded-Square Prayer Icon Badge
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = when {
                    isNext -> MaterialTheme.colorScheme.primary
                    isPast -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)
                },
                border = if (!isNext) {
                    androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                } else null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = AppStrings.prayerName(prayer.type, currentLanguage),
                        tint = when {
                            isNext -> MaterialTheme.colorScheme.onPrimary
                            isPast -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Prayer Name & Arabic
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppStrings.prayerName(prayer.type, currentLanguage),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isPast) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f) else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = prayer.type.arabicName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isPast) 0.5f else 0.78f)
                    )
                }
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = when (prayer.status) {
                        PrayerStatus.ACTIVE_NEXT -> s.nextPrayer
                        PrayerStatus.PAST -> s.elapsed
                        PrayerStatus.UPCOMING -> s.remainingTime
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isNext) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // Sleek Time Pill
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = if (isNext) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                },
                border = androidx.compose.foundation.BorderStroke(
                    width = 0.5.dp,
                    color = if (isNext) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.14f)
                    }
                )
            ) {
                Text(
                    text = prayer.timeFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isNext -> MaterialTheme.colorScheme.primary
                        isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sound alert toggle button
            IconButton(
                onClick = onToggleSound,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = if (prayer.isSoundAlertEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    contentDescription = if (prayer.isSoundAlertEnabled) "Ezan Bildirimi Açık" else "Ezan Bildirimi Kapalı",
                    tint = if (prayer.isSoundAlertEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
