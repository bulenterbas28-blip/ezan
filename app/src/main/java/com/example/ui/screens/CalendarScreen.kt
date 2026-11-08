package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DayPrayerTimes
import com.example.data.model.HolidayAlarmConfig
import com.example.data.model.HolidayAlertMode
import com.example.data.model.HolidayReminderTiming
import com.example.data.model.PrayerType
import com.example.data.model.ReligiousEventType
import com.example.data.model.ReligiousHoliday
import com.example.data.repository.AppStrings
import com.example.data.repository.ReligiousDaysRepository
import com.example.viewmodel.PrayerViewModel

@Composable
fun CalendarScreen(viewModel: PrayerViewModel) {
    val context = LocalContext.current
    val currentCity by viewModel.currentCity.collectAsState()
    val monthlyTimes by viewModel.monthlyPrayerTimes.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val holidayAlarmConfigs by viewModel.holidayAlarmConfigs.collectAsState()
    val s = AppStrings.get(currentLanguage)

    // 0 = Aylık Vakitler, 1 = İmsakiye (Sahur & İftar), 2 = Dini Günler & Bayramlar
    var selectedSubTab by remember { mutableIntStateOf(0) }
    var dialogHoliday by remember { mutableStateOf<ReligiousHoliday?>(null) }

    val imsakiyeTabLabel = when (currentLanguage) {
        AppLanguage.TURKISH -> "Ramazan İmsakiyesi"
        AppLanguage.ENGLISH -> "Ramadan Imsakiye"
        AppLanguage.ARABIC -> "إمساكية رمضان"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            .testTag("calendar_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = s.calendarTitle.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = when (selectedSubTab) {
                        0 -> "${currentCity.name} • 30 ${s.day}"
                        1 -> when (currentLanguage) {
                            AppLanguage.TURKISH -> "${currentCity.name} • Sadece Ramazan Ayı İmsakiyesi"
                            AppLanguage.ENGLISH -> "${currentCity.name} • Ramadan Month Imsakiye"
                            AppLanguage.ARABIC -> "${currentCity.name} • إمساكية شهر رمضان المبارك"
                        }
                        else -> s.religiousDaysTab
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = {
                    val summary = when (selectedSubTab) {
                        0 -> {
                            buildString {
                                appendLine("${currentCity.name} - ${s.calendarTitle}")
                                monthlyTimes.take(7).forEach { day ->
                                    appendLine(
                                        "${day.getLocalizedGregorianDate(currentLanguage)}: " +
                                            "${AppStrings.prayerName(PrayerType.IMSAK, currentLanguage)}: ${day.imsak}, " +
                                            "${AppStrings.prayerName(PrayerType.OGLE, currentLanguage)}: ${day.ogle}, " +
                                            "${AppStrings.prayerName(PrayerType.IKINDI, currentLanguage)}: ${day.ikindi}, " +
                                            "${AppStrings.prayerName(PrayerType.AKSAM, currentLanguage)}: ${day.aksam}, " +
                                            "${AppStrings.prayerName(PrayerType.YATSI, currentLanguage)}: ${day.yatsi}"
                                    )
                                }
                            }
                        }
                        1 -> {
                            val ramadanDays = viewModel.getRamadanImsakiyeDays(2026)
                            buildString {
                                appendLine("${currentCity.name} - $imsakiyeTabLabel")
                                ramadanDays.forEachIndexed { idx, day ->
                                    appendLine(
                                        "${idx + 1}. Ramazan (${day.getLocalizedGregorianDate(currentLanguage)}): " +
                                            "Sahur: ${day.imsak} | İftar: ${day.aksam}"
                                    )
                                }
                            }
                        }
                        else -> {
                            val nowMs = System.currentTimeMillis()
                            buildString {
                                appendLine(s.religiousDaysTab)
                                ReligiousDaysRepository.allHolidays
                                    .filter { it.daysRemainingFrom(nowMs) >= 0 }
                                    .take(10)
                                    .forEach { h ->
                                        appendLine("• ${h.getLocalizedTitle(currentLanguage)} - ${h.getLocalizedGregorianDate(currentLanguage)} (${h.getLocalizedHijriDate(currentLanguage)})")
                                    }
                            }
                        }
                    }
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText(s.calendarTitle, summary))
                    Toast.makeText(context, s.copied, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("calendar_share_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = s.share,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub-Tab Switcher: Aylık Vakitler | İmsakiye | Dini Günler & Bayramlar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CalendarSubTabButton(
                    title = s.monthlyPrayersTab,
                    icon = Icons.Default.CalendarMonth,
                    isSelected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("subtab_monthly_prayers")
                )
                CalendarSubTabButton(
                    title = imsakiyeTabLabel,
                    icon = Icons.Default.AlarmOn,
                    isSelected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("subtab_imsakiye")
                )
                CalendarSubTabButton(
                    title = s.religiousDaysTab,
                    icon = Icons.Default.AutoAwesome,
                    isSelected = selectedSubTab == 2,
                    onClick = { selectedSubTab = 2 },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("subtab_religious_days")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedSubTab) {
            0 -> {
                MonthlyPrayersTableContent(
                    monthlyTimes = monthlyTimes,
                    currentLanguage = currentLanguage,
                    s = s,
                    onHolidayBadgeClick = { holiday ->
                        dialogHoliday = holiday
                    }
                )
            }
            1 -> {
                ImsakiyeTabContent(
                    viewModel = viewModel,
                    cityName = currentCity.name,
                    monthlyTimes = monthlyTimes,
                    currentLanguage = currentLanguage,
                    s = s
                )
            }
            else -> {
                ReligiousDaysAndHolidaysContent(
                    currentLanguage = currentLanguage,
                    s = s,
                    holidayAlarmConfigs = holidayAlarmConfigs,
                    onOpenAlarmDialog = { holiday ->
                        dialogHoliday = holiday
                    },
                    onQuickToggleAlarm = { holiday ->
                        val enabled = viewModel.toggleHolidayAlarmQuick(holiday)
                        val toastText = if (enabled) s.holidayAlarmSavedToast else s.holidayAlarmRemovedToast
                        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Religious Holiday Alarm / Notification Dialog
    dialogHoliday?.let { holiday ->
        val existingConfig = holidayAlarmConfigs[holiday.id] ?: HolidayAlarmConfig(
            holidayId = holiday.id,
            isEnabled = false,
            timing = if (holiday.type == ReligiousEventType.KANDIL) {
                HolidayReminderTiming.EVENING_KANDIL
            } else {
                HolidayReminderTiming.MORNING_OF_DAY
            },
            alertMode = HolidayAlertMode.NOTIFICATION_AND_SOUND
        )

        HolidayAlarmDialog(
            holiday = holiday,
            initialConfig = existingConfig,
            currentLanguage = currentLanguage,
            s = s,
            onDismiss = { dialogHoliday = null },
            onSave = { timing, alertMode ->
                viewModel.saveHolidayAlarm(
                    holiday = holiday,
                    isEnabled = true,
                    timing = timing,
                    alertMode = alertMode
                )
                Toast.makeText(context, s.holidayAlarmSavedToast, Toast.LENGTH_SHORT).show()
                dialogHoliday = null
            },
            onDisable = {
                viewModel.saveHolidayAlarm(
                    holiday = holiday,
                    isEnabled = false,
                    timing = existingConfig.timing,
                    alertMode = existingConfig.alertMode
                )
                Toast.makeText(context, s.holidayAlarmRemovedToast, Toast.LENGTH_SHORT).show()
                dialogHoliday = null
            },
            onTestAlert = { alertMode ->
                viewModel.testHolidayAlert(holiday, alertMode)
            }
        )
    }
}

@Composable
private fun CalendarSubTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 42.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ColumnScope.MonthlyPrayersTableContent(
    monthlyTimes: List<DayPrayerTimes>,
    currentLanguage: AppLanguage,
    s: AppStrings.Strings,
    onHolidayBadgeClick: (ReligiousHoliday) -> Unit
) {
    // Table Column Headers
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = s.date,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.8f),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = AppStrings.shortPrayerName(PrayerType.IMSAK, currentLanguage),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = AppStrings.shortPrayerName(PrayerType.GUNES, currentLanguage),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = AppStrings.shortPrayerName(PrayerType.OGLE, currentLanguage),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = AppStrings.shortPrayerName(PrayerType.IKINDI, currentLanguage),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = AppStrings.shortPrayerName(PrayerType.AKSAM, currentLanguage),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = AppStrings.shortPrayerName(PrayerType.YATSI, currentLanguage),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Days List
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        itemsIndexed(monthlyTimes) { index, day ->
            val isToday = index == 0
            val isFriday = day.gregorianDateText.endsWith("Cuma")
            val matchingHoliday = remember(day.gregorianDateText) {
                ReligiousDaysRepository.findHolidayMatchingGregorianText(day.gregorianDateText)
            }

            val rowBg = when {
                isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                matchingHoliday != null -> Color(0xFFD4AF37).copy(alpha = 0.14f)
                isFriday -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            }

            val borderModifier = when {
                isToday -> Modifier.border(
                    1.5.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(12.dp)
                )
                matchingHoliday != null -> Modifier.border(
                    1.dp,
                    Color(0xFFD4AF37).copy(alpha = 0.6f),
                    RoundedCornerShape(12.dp)
                )
                else -> Modifier
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(borderModifier)
                    .let { mod ->
                        if (matchingHoliday != null) {
                            mod.clickable { onHolidayBadgeClick(matchingHoliday) }
                        } else mod
                    },
                shape = RoundedCornerShape(12.dp),
                color = rowBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val localizedGregorian = day.getLocalizedGregorianDate(currentLanguage)
                        val localizedHijri = day.getLocalizedHijriDate(currentLanguage)

                        Column(modifier = Modifier.weight(1.8f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = localizedGregorian.substringBeforeLast(" "),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isToday || isFriday || matchingHoliday != null) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isToday) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = s.today,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else if (isFriday) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = s.friday,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38E5A1)
                                    )
                                }
                            }
                            Text(
                                text = localizedHijri,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        // Times
                        Text(
                            text = day.imsak,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = day.gunes,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = day.ogle,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = day.ikindi,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = day.aksam,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = day.yatsi,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (matchingHoliday != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFD4AF37).copy(alpha = 0.22f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFD4AF37),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = matchingHoliday.getLocalizedTitle(currentLanguage),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = s.setHolidayAlarm,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.ReligiousDaysAndHolidaysContent(
    currentLanguage: AppLanguage,
    s: AppStrings.Strings,
    holidayAlarmConfigs: Map<String, HolidayAlarmConfig>,
    onOpenAlarmDialog: (ReligiousHoliday) -> Unit,
    onQuickToggleAlarm: (ReligiousHoliday) -> Unit
) {
    // Filter: 0 = Upcoming (Yaklaşanlar), 1 = 2026 Yılı, 2 = 2027 Yılı
    var selectedFilter by remember { mutableIntStateOf(0) }
    val nowMs = remember { System.currentTimeMillis() }

    val nextUpcomingHoliday: ReligiousHoliday? = remember(nowMs) {
        ReligiousDaysRepository.allHolidays.firstOrNull { it.daysRemainingFrom(nowMs) >= 0 }
    }

    val displayedHolidays: List<ReligiousHoliday> = remember(selectedFilter, nowMs) {
        when (selectedFilter) {
            0 -> ReligiousDaysRepository.getUpcomingAndCurrentYearHolidays(nowMs)
            1 -> ReligiousDaysRepository.getHolidaysForYear(2026)
            else -> ReligiousDaysRepository.getHolidaysForYear(2027)
        }
    }

    // Filter Chips Row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == 0,
            onClick = { selectedFilter = 0 },
            label = { Text(s.filterUpcomingHolidays, style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.testTag("filter_upcoming_holidays")
        )
        FilterChip(
            selected = selectedFilter == 1,
            onClick = { selectedFilter = 1 },
            label = { Text(s.filterAllHolidays2026, style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.testTag("filter_2026_holidays")
        )
        FilterChip(
            selected = selectedFilter == 2,
            onClick = { selectedFilter = 2 },
            label = { Text(s.filterAllHolidays2027, style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.testTag("filter_2027_holidays")
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .testTag("religious_days_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Hero Banner for Next Upcoming Holiday
        if (selectedFilter == 0 && nextUpcomingHoliday != null) {
            item(key = "hero_${nextUpcomingHoliday.id}") {
                NextReligiousDayHeroCard(
                    holiday = nextUpcomingHoliday,
                    currentLanguage = currentLanguage,
                    s = s,
                    nowMs = nowMs,
                    alarmConfig = holidayAlarmConfigs[nextUpcomingHoliday.id],
                    onConfigureAlarm = { onOpenAlarmDialog(nextUpcomingHoliday) }
                )
            }
        }

        items(displayedHolidays, key = { it.id }) { holiday ->
            val alarmConfig = holidayAlarmConfigs[holiday.id]
            ReligiousHolidayItemCard(
                holiday = holiday,
                currentLanguage = currentLanguage,
                s = s,
                nowMs = nowMs,
                alarmConfig = alarmConfig,
                onOpenAlarmDialog = { onOpenAlarmDialog(holiday) },
                onQuickToggleAlarm = { onQuickToggleAlarm(holiday) }
            )
        }
    }
}

@Composable
private fun NextReligiousDayHeroCard(
    holiday: ReligiousHoliday,
    currentLanguage: AppLanguage,
    s: AppStrings.Strings,
    nowMs: Long,
    alarmConfig: HolidayAlarmConfig?,
    onConfigureAlarm: () -> Unit
) {
    val daysDiff = holiday.daysRemainingFrom(nowMs)
    val countdownLabel = when {
        daysDiff == 0 -> s.today
        daysDiff == 1 -> s.tomorrowLabel
        daysDiff > 1 -> "$daysDiff ${s.daysRemaining}"
        else -> s.passedLabel
    }
    val isAlarmActive = alarmConfig?.isEnabled == true

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = Color(0xFFD4AF37).copy(alpha = 0.7f),
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("next_religious_day_hero"),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFD4AF37).copy(alpha = 0.22f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFD4AF37),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s.upcomingReligiousDay,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD4AF37)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = countdownLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = holiday.getLocalizedTitle(currentLanguage),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${holiday.getLocalizedGregorianDate(currentLanguage)} • ${holiday.getLocalizedHijriDate(currentLanguage)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = holiday.getLocalizedDescription(currentLanguage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onConfigureAlarm,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlarmActive) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_configure_alarm_button")
                ) {
                    Icon(
                        imageVector = if (isAlarmActive) Icons.Default.AlarmOn else Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAlarmActive) {
                            "${s.alarmActiveLabel} (${alarmConfig?.timing?.getShortLabel(currentLanguage)})"
                        } else {
                            s.setHolidayAlarm
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReligiousHolidayItemCard(
    holiday: ReligiousHoliday,
    currentLanguage: AppLanguage,
    s: AppStrings.Strings,
    nowMs: Long,
    alarmConfig: HolidayAlarmConfig?,
    onOpenAlarmDialog: () -> Unit,
    onQuickToggleAlarm: () -> Unit
) {
    val daysDiff = holiday.daysRemainingFrom(nowMs)
    val isPassed = daysDiff < 0
    val isAlarmEnabled = alarmConfig?.isEnabled == true

    val badgeColor = when (holiday.type) {
        ReligiousEventType.BAYRAM -> Color(0xFF10B981)
        ReligiousEventType.KANDIL -> Color(0xFFD4AF37)
        ReligiousEventType.SPECIAL_DAY -> MaterialTheme.colorScheme.primary
    }

    val countdownText = when {
        daysDiff == 0 -> s.today
        daysDiff == 1 -> s.tomorrowLabel
        daysDiff > 1 -> "$daysDiff ${s.daysRemaining}"
        else -> s.passedLabel
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isAlarmEnabled) 1.5.dp else 1.dp,
                color = if (isAlarmEnabled) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onOpenAlarmDialog() }
            .testTag("holiday_card_${holiday.id}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isPassed) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Type badge + Countdown badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = holiday.type.getLocalizedBadge(currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPassed) {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    }
                ) {
                    Text(
                        text = countdownText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPassed) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Alarm Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = holiday.getLocalizedTitle(currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${holiday.getLocalizedGregorianDate(currentLanguage)} • ${holiday.getLocalizedHijriDate(currentLanguage)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Switch(
                    checked = isAlarmEnabled,
                    onCheckedChange = { onQuickToggleAlarm() },
                    modifier = Modifier.testTag("holiday_switch_${holiday.id}")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = holiday.getLocalizedDescription(currentLanguage),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Action Bar: Alarm Config details + Configure Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isAlarmEnabled) Icons.Default.AlarmOn else Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = if (isAlarmEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAlarmEnabled && alarmConfig != null) {
                            "${alarmConfig.timing.getShortLabel(currentLanguage)} • ${alarmConfig.alertMode.getLocalizedLabel(currentLanguage)}"
                        } else {
                            s.setHolidayAlarm
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isAlarmEnabled) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isAlarmEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TextButton(
                    onClick = onOpenAlarmDialog,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("holiday_alarm_btn_${holiday.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAlarmEnabled) s.alarmActiveLabel else s.setHolidayAlarm,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HolidayAlarmDialog(
    holiday: ReligiousHoliday,
    initialConfig: HolidayAlarmConfig,
    currentLanguage: AppLanguage,
    s: AppStrings.Strings,
    onDismiss: () -> Unit,
    onSave: (HolidayReminderTiming, HolidayAlertMode) -> Unit,
    onDisable: () -> Unit,
    onTestAlert: (HolidayAlertMode) -> Unit
) {
    var selectedTiming by remember(initialConfig) { mutableStateOf(initialConfig.timing) }
    var selectedAlertMode by remember(initialConfig) { mutableStateOf(initialConfig.alertMode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = s.holidayAlarmDialogTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${holiday.getLocalizedTitle(currentLanguage)} (${holiday.getLocalizedGregorianDate(currentLanguage)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.holidayAlarmTimeLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                HolidayReminderTiming.entries.forEach { timing ->
                    val isSelected = selectedTiming == timing
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedTiming = timing }
                            .testTag("timing_option_${timing.key}"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedTiming = timing }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timing.getLocalizedLabel(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = s.holidayAlarmTypeLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                HolidayAlertMode.entries.forEach { mode ->
                    val isSelected = selectedAlertMode == mode
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedAlertMode = mode }
                            .testTag("alert_mode_${mode.key}"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedAlertMode = mode }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = mode.getLocalizedLabel(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = { onTestAlert(selectedAlertMode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_holiday_alert_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = s.testHolidayAlertNow,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedTiming, selectedAlertMode) },
                modifier = Modifier.testTag("save_holiday_alarm_button")
            ) {
                Text(s.saveAlarmButton, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (initialConfig.isEnabled) {
                    TextButton(
                        onClick = onDisable,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.testTag("remove_holiday_alarm_button")
                    ) {
                        Text(s.removeAlarmButton)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(s.close)
                }
            }
        }
    )
}

@Composable
private fun ImsakiyeTabContent(
    viewModel: PrayerViewModel,
    cityName: String,
    monthlyTimes: List<DayPrayerTimes>,
    currentLanguage: AppLanguage,
    s: AppStrings.Strings
) {
    val context = LocalContext.current
    val isSahurAlarmEnabled by viewModel.isSahurAlarmEnabled.collectAsState()
    val sahurMinutesBefore by viewModel.sahurMinutesBeforeImsak.collectAsState()
    val sahurPlaySound by viewModel.sahurPlaySound.collectAsState()

    val isIftarAlarmEnabled by viewModel.isIftarAlarmEnabled.collectAsState()
    val iftarMinutesBefore by viewModel.iftarMinutesBeforeAksam.collectAsState()
    val iftarPlaySound by viewModel.iftarPlaySound.collectAsState()

    // Exclusively Ramadan Month İmsakiye: 2026 Ramazan (1447 H) or 2027 Ramazan (1448 H)
    val defaultRamadanYear = remember {
        val cal = java.util.Calendar.getInstance()
        val year = cal.get(java.util.Calendar.YEAR)
        val month = cal.get(java.util.Calendar.MONTH) // 0-based; March is 2
        if (year > 2026 || (year == 2026 && month > java.util.Calendar.MARCH)) 2027 else 2026
    }
    var selectedRamadanYear by remember { mutableIntStateOf(defaultRamadanYear) }

    val ramadanDays = remember(selectedRamadanYear, cityName) {
        viewModel.getRamadanImsakiyeDays(selectedRamadanYear)
    }

    // Check if today falls inside the selected Ramadan month; otherwise show Day 1 (İlk Ramazan Günü)
    val todayDateStr = monthlyTimes.firstOrNull()?.getLocalizedGregorianDate(currentLanguage)
    val activeOrFirstRamadanDayIndex = remember(ramadanDays, todayDateStr, currentLanguage) {
        val matchIdx = if (todayDateStr != null) {
            ramadanDays.indexOfFirst { it.getLocalizedGregorianDate(currentLanguage) == todayDateStr }
        } else -1
        if (matchIdx >= 0) matchIdx else 0
    }
    val isCurrentlyInSelectedRamadan = remember(ramadanDays, todayDateStr, currentLanguage) {
        todayDateStr != null && ramadanDays.any { it.getLocalizedGregorianDate(currentLanguage) == todayDateStr }
    }

    val featuredRamadanDay = ramadanDays.getOrNull(activeOrFirstRamadanDayIndex)
    val kadirGecesiDay = ramadanDays.getOrNull(25) // 26. Gün (27. Gece Kadir Gecesi)
    val arefeDay = ramadanDays.lastOrNull()        // 29. Gün (Arefe / Son İftar)

    val fastingDurationText = remember(featuredRamadanDay, currentLanguage) {
        if (featuredRamadanDay == null) "" else calculateFastingDurationLabel(
            featuredRamadanDay.imsak,
            featuredRamadanDay.aksam,
            currentLanguage
        )
    }

    val hijriYearLabel = if (selectedRamadanYear == 2027) "1448 H" else "1447 H"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("imsakiye_tab_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 1. Ramadan Year Selector Chips (Ramazan Ayına Özel)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val ramadanYears = listOf(
                    2026 to when (currentLanguage) {
                        AppLanguage.TURKISH -> "2026 Ramazan İmsakiyesi (1447 H)"
                        AppLanguage.ENGLISH -> "2026 Ramadan Imsakiye (1447 H)"
                        AppLanguage.ARABIC -> "إمساكية رمضان 2026 (1447 هـ)"
                    },
                    2027 to when (currentLanguage) {
                        AppLanguage.TURKISH -> "2027 Ramazan İmsakiyesi (1448 H)"
                        AppLanguage.ENGLISH -> "2027 Ramadan Imsakiye (1448 H)"
                        AppLanguage.ARABIC -> "إمساكية رمضان 2027 (1448 هـ)"
                    }
                )

                ramadanYears.forEachIndexed { index, (yearVal, label) ->
                    val isSelected = selectedRamadanYear == yearVal
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRamadanYear = yearVal },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("imsakiye_period_chip_$index")
                    )
                }
            }
        }

        // 2. Ramadan Month Sahur & İftar Summary Card
        if (featuredRamadanDay != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("imsakiye_today_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        0.8.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.32f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isCurrentlyInSelectedRamadan) {
                                        when (currentLanguage) {
                                            AppLanguage.TURKISH -> "RAMAZAN ${activeOrFirstRamadanDayIndex + 1}. GÜN SAHUR & İFTAR ($cityName)"
                                            AppLanguage.ENGLISH -> "RAMADAN DAY ${activeOrFirstRamadanDayIndex + 1} SUHOOR & IFTAR ($cityName)"
                                            AppLanguage.ARABIC -> "اليوم ${activeOrFirstRamadanDayIndex + 1} من رمضان - السحور والإفطار ($cityName)"
                                        }
                                    } else {
                                        when (currentLanguage) {
                                            AppLanguage.TURKISH -> "$selectedRamadanYear RAMAZAN AYI İMSAKİYESİ ($cityName)"
                                            AppLanguage.ENGLISH -> "$selectedRamadanYear RAMADAN IMSAKIYE ($cityName)"
                                            AppLanguage.ARABIC -> "إمساكية شهر رمضان $selectedRamadanYear ($cityName)"
                                        }
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isCurrentlyInSelectedRamadan) {
                                        "${featuredRamadanDay.getLocalizedGregorianDate(currentLanguage)} • ${featuredRamadanDay.getLocalizedHijriDate(currentLanguage)}"
                                    } else {
                                        when (currentLanguage) {
                                            AppLanguage.TURKISH -> "1. Gün (İlk Oruç): ${featuredRamadanDay.getLocalizedGregorianDate(currentLanguage)} • $hijriYearLabel"
                                            AppLanguage.ENGLISH -> "Day 1 (First Fast): ${featuredRamadanDay.getLocalizedGregorianDate(currentLanguage)} • $hijriYearLabel"
                                            AppLanguage.ARABIC -> "اليوم الأول: ${featuredRamadanDay.getLocalizedGregorianDate(currentLanguage)} • $hijriYearLabel"
                                        }
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (fastingDurationText.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = fastingDurationText,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // İlk / Günlük Sahur Box
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.6.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isCurrentlyInSelectedRamadan) {
                                            when (currentLanguage) {
                                                AppLanguage.TURKISH -> "BUGÜN SAHUR (İMSAK)"
                                                AppLanguage.ENGLISH -> "TODAY SUHOOR (IMSAK)"
                                                AppLanguage.ARABIC -> "السحور اليوم (الإمساك)"
                                            }
                                        } else {
                                            when (currentLanguage) {
                                                AppLanguage.TURKISH -> "İLK SAHUR (1. GÜN İMSAK)"
                                                AppLanguage.ENGLISH -> "FIRST SUHOOR (DAY 1)"
                                                AppLanguage.ARABIC -> "أول سحور (إمساك اليوم 1)"
                                            }
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = featuredRamadanDay.imsak,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // İlk / Günlük İftar Box
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.6.dp,
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isCurrentlyInSelectedRamadan) {
                                            when (currentLanguage) {
                                                AppLanguage.TURKISH -> "BUGÜN İFTAR (AKŞAM)"
                                                AppLanguage.ENGLISH -> "TODAY IFTAR (MAGHRIB)"
                                                AppLanguage.ARABIC -> "الإفطار اليوم (المغرب)"
                                            }
                                        } else {
                                            when (currentLanguage) {
                                                AppLanguage.TURKISH -> "İLK İFTAR (1. GÜN AKŞAM)"
                                                AppLanguage.ENGLISH -> "FIRST IFTAR (DAY 1)"
                                                AppLanguage.ARABIC -> "أول إفطار (مغرب اليوم 1)"
                                            }
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = featuredRamadanDay.aksam,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Kadir Gecesi & Arefe Quick Info Strip
                        if (kadirGecesiDay != null && arefeDay != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                        Text(
                                            text = when (currentLanguage) {
                                                AppLanguage.TURKISH -> "Kadir Gecesi (26. Gün)"
                                                AppLanguage.ENGLISH -> "Laylat al-Qadr (Day 26)"
                                                AppLanguage.ARABIC -> "ليلة القدر (اليوم 26)"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${kadirGecesiDay.getLocalizedGregorianDate(currentLanguage)} • İftar: ${kadirGecesiDay.aksam}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                        Text(
                                            text = when (currentLanguage) {
                                                AppLanguage.TURKISH -> "Arefe / Son Oruç (29. Gün)"
                                                AppLanguage.ENGLISH -> "Arafah / Last Fast (Day 29)"
                                                AppLanguage.ARABIC -> "وقفة العيد / آخر صيام (29)"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${arefeDay.getLocalizedGregorianDate(currentLanguage)} • İftar: ${arefeDay.aksam}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
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

        // 3. Ramazan Sahur & İftar Optional Notification / Alarm Configuration Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("imsakiye_alarm_settings_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Ramazan Sahur ve İftar Bildirimleri"
                                    AppLanguage.ENGLISH -> "Ramadan Suhoor & Iftar Notifications"
                                    AppLanguage.ARABIC -> "تنبيهات السحور والإفطار لشهر رمضان"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Ramazan ayı boyunca Sahur (İmsak) ve İftar (Akşam) vakitlerinde isteğe bağlı bildirim/ezan uyarısı"
                                    AppLanguage.ENGLISH -> "Optional sound or silent alerts at or before Suhoor and Iftar during Ramadan"
                                    AppLanguage.ARABIC -> "تنبيه صوتي أو إشعار اختياري للسحور والإفطار خلال شهر رمضان"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // --- SAHUR ALARM PANEL ---
                    ImsakiyeAlarmControlPanel(
                        title = when (currentLanguage) {
                            AppLanguage.TURKISH -> "Sahur (İmsak) Bildirimi"
                            AppLanguage.ENGLISH -> "Suhoor (Imsak) Alert"
                            AppLanguage.ARABIC -> "تنبيه السحور (الإمساك)"
                        },
                        subtitle = if (sahurMinutesBefore == 0) {
                            when (currentLanguage) {
                                AppLanguage.TURKISH -> "Tam İmsak vaktinde bildirim"
                                AppLanguage.ENGLISH -> "At exact Imsak time"
                                AppLanguage.ARABIC -> "عند وقت الإمساك تماماً"
                            }
                        } else {
                            when (currentLanguage) {
                                AppLanguage.TURKISH -> "İmsak vaktinden $sahurMinutesBefore dk önce uyandır"
                                AppLanguage.ENGLISH -> "$sahurMinutesBefore min before Imsak"
                                AppLanguage.ARABIC -> "قبل الإمساك بـ $sahurMinutesBefore دقيقة"
                            }
                        },
                        isEnabled = isSahurAlarmEnabled,
                        onToggleEnabled = { enabled ->
                            viewModel.toggleSahurAlarm(enabled)
                            val msg = if (enabled) {
                                when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Ramazan Sahur bildirimi aktif edildi"
                                    AppLanguage.ENGLISH -> "Ramadan Suhoor notification enabled"
                                    AppLanguage.ARABIC -> "تم تفعيل تنبيه السحور"
                                }
                            } else {
                                when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Ramazan Sahur bildirimi kapatıldı"
                                    AppLanguage.ENGLISH -> "Ramadan Suhoor notification disabled"
                                    AppLanguage.ARABIC -> "تم إيقاف تنبيه السحور"
                                }
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        minuteOptions = listOf(0, 15, 30, 45, 60),
                        selectedMinutes = sahurMinutesBefore,
                        onSelectMinutes = { mins ->
                            viewModel.setSahurMinutesBefore(mins)
                        },
                        playSound = sahurPlaySound,
                        onTogglePlaySound = { sound ->
                            viewModel.setSahurPlaySound(sound)
                        },
                        onTestClick = {
                            viewModel.testImsakiyeAlert(isSahur = true)
                            Toast.makeText(
                                context,
                                when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Sahur test bildirimi gönderildi"
                                    AppLanguage.ENGLISH -> "Suhoor test notification sent"
                                    AppLanguage.ARABIC -> "تم إرسال إشعار تجربة السحور"
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        currentLanguage = currentLanguage,
                        testTagPrefix = "sahur_alarm"
                    )

                    // --- IFTAR ALARM PANEL ---
                    ImsakiyeAlarmControlPanel(
                        title = when (currentLanguage) {
                            AppLanguage.TURKISH -> "İftar (Akşam) Bildirimi"
                            AppLanguage.ENGLISH -> "Iftar (Maghrib) Alert"
                            AppLanguage.ARABIC -> "تنبيه الإفطار (المغرب)"
                        },
                        subtitle = if (iftarMinutesBefore == 0) {
                            when (currentLanguage) {
                                AppLanguage.TURKISH -> "Tam İftar (Akşam ezanı) vaktinde bildirim"
                                AppLanguage.ENGLISH -> "At exact Iftar (Maghrib) time"
                                AppLanguage.ARABIC -> "عند وقت الإفطار (المغرب) تماماً"
                            }
                        } else {
                            when (currentLanguage) {
                                AppLanguage.TURKISH -> "İftar vaktinden $iftarMinutesBefore dk önce hazırlık bildirimi"
                                AppLanguage.ENGLISH -> "$iftarMinutesBefore min before Iftar"
                                AppLanguage.ARABIC -> "قبل الإفطار بـ $iftarMinutesBefore دقيقة"
                            }
                        },
                        isEnabled = isIftarAlarmEnabled,
                        onToggleEnabled = { enabled ->
                            viewModel.toggleIftarAlarm(enabled)
                            val msg = if (enabled) {
                                when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Ramazan İftar bildirimi aktif edildi"
                                    AppLanguage.ENGLISH -> "Ramadan Iftar notification enabled"
                                    AppLanguage.ARABIC -> "تم تفعيل تنبيه الإفطار"
                                }
                            } else {
                                when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Ramazan İftar bildirimi kapatıldı"
                                    AppLanguage.ENGLISH -> "Ramadan Iftar notification disabled"
                                    AppLanguage.ARABIC -> "تم إيقاف تنبيه الإفطار"
                                }
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        minuteOptions = listOf(0, 5, 10, 15, 30),
                        selectedMinutes = iftarMinutesBefore,
                        onSelectMinutes = { mins ->
                            viewModel.setIftarMinutesBefore(mins)
                        },
                        playSound = iftarPlaySound,
                        onTogglePlaySound = { sound ->
                            viewModel.setIftarPlaySound(sound)
                        },
                        onTestClick = {
                            viewModel.testImsakiyeAlert(isSahur = false)
                            Toast.makeText(
                                context,
                                when (currentLanguage) {
                                    AppLanguage.TURKISH -> "İftar test bildirimi gönderildi"
                                    AppLanguage.ENGLISH -> "Iftar test notification sent"
                                    AppLanguage.ARABIC -> "تم إرسال إشعار تجربة الإفطار"
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        currentLanguage = currentLanguage,
                        testTagPrefix = "iftar_alarm"
                    )
                }
            }
        }

        // 4. Ramazan İmsakiye Table Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.TURKISH -> "RAMAZAN GÜNÜ"
                            AppLanguage.ENGLISH -> "RAMADAN DAY"
                            AppLanguage.ARABIC -> "أيام رمضان"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1.65f)
                    )
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.TURKISH -> "SAHUR"
                            AppLanguage.ENGLISH -> "SUHOOR"
                            AppLanguage.ARABIC -> "السحور"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.05f)
                    )
                    Text(
                        text = AppStrings.prayerName(PrayerType.GUNES, currentLanguage).take(5),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.92f)
                    )
                    Text(
                        text = AppStrings.prayerName(PrayerType.OGLE, currentLanguage).take(5),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.92f)
                    )
                    Text(
                        text = AppStrings.prayerName(PrayerType.IKINDI, currentLanguage).take(5),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.92f)
                    )
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.TURKISH -> "İFTAR"
                            AppLanguage.ENGLISH -> "IFTAR"
                            AppLanguage.ARABIC -> "الإفطار"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.05f)
                    )
                    Text(
                        text = AppStrings.prayerName(PrayerType.YATSI, currentLanguage).take(5),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.92f)
                    )
                }
            }
        }

        // 5. 29-Day Ramadan İmsakiye List
        itemsIndexed(ramadanDays) { index, dayTimes ->
            val ramadanDayNumber = index + 1
            val isFirstDay = ramadanDayNumber == 1
            val isKadirGecesi = ramadanDayNumber == 26
            val isArefe = ramadanDayNumber == ramadanDays.size
            val isHighlightedRow = isFirstDay || isKadirGecesi || isArefe

            val specialBadgeText = when {
                isFirstDay -> when (currentLanguage) {
                    AppLanguage.TURKISH -> "İlk Oruç"
                    AppLanguage.ENGLISH -> "1st Fast"
                    AppLanguage.ARABIC -> "أول صيام"
                }
                isKadirGecesi -> when (currentLanguage) {
                    AppLanguage.TURKISH -> "Kadir Gecesi"
                    AppLanguage.ENGLISH -> "Laylat al-Qadr"
                    AppLanguage.ARABIC -> "ليلة القدر"
                }
                isArefe -> when (currentLanguage) {
                    AppLanguage.TURKISH -> "Arefe • Son Oruç"
                    AppLanguage.ENGLISH -> "Arafah • Last Fast"
                    AppLanguage.ARABIC -> "وقفة العيد"
                }
                else -> null
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("imsakiye_row_$index"),
                shape = RoundedCornerShape(10.dp),
                color = when {
                    isKadirGecesi -> MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                    isFirstDay || isArefe -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f)
                    index % 2 == 0 -> MaterialTheme.colorScheme.surface
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                },
                border = if (isHighlightedRow) {
                    androidx.compose.foundation.BorderStroke(
                        0.7.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    )
                } else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.65f)) {
                        Text(
                            text = "$ramadanDayNumber. Ramazan • ${dayTimes.getLocalizedGregorianDate(currentLanguage)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isHighlightedRow) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = specialBadgeText ?: "$ramadanDayNumber Ramazan $hijriYearLabel",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (specialBadgeText != null) FontWeight.Bold else FontWeight.Normal,
                                color = if (specialBadgeText != null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Highlighted Sahur (İmsak)
                    Text(
                        text = dayTimes.imsak,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.05f)
                    )
                    Text(
                        text = dayTimes.gunes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.92f)
                    )
                    Text(
                        text = dayTimes.ogle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.92f)
                    )
                    Text(
                        text = dayTimes.ikindi,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.92f)
                    )
                    // Highlighted İftar (Akşam)
                    Text(
                        text = dayTimes.aksam,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.05f)
                    )
                    Text(
                        text = dayTimes.yatsi,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.92f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImsakiyeAlarmControlPanel(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    minuteOptions: List<Int>,
    selectedMinutes: Int,
    onSelectMinutes: (Int) -> Unit,
    playSound: Boolean,
    onTogglePlaySound: (Boolean) -> Unit,
    onTestClick: () -> Unit,
    currentLanguage: AppLanguage,
    testTagPrefix: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isEnabled) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        color = if (isEnabled) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggleEnabled,
                    modifier = Modifier.testTag("${testTagPrefix}_switch")
                )
            }

            // Timing offset chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                minuteOptions.forEach { mins ->
                    val isSelected = selectedMinutes == mins
                    val chipText = if (mins == 0) {
                        when (currentLanguage) {
                            AppLanguage.TURKISH -> "Tam Vakit"
                            AppLanguage.ENGLISH -> "Exact Time"
                            AppLanguage.ARABIC -> "في الوقت"
                        }
                    } else {
                        when (currentLanguage) {
                            AppLanguage.TURKISH -> "$mins dk önce"
                            AppLanguage.ENGLISH -> "$mins min before"
                            AppLanguage.ARABIC -> "قبل $mins د"
                        }
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectMinutes(mins) },
                        label = {
                            Text(
                                text = chipText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("${testTagPrefix}_mins_$mins")
                    )
                }
            }

            // Sound mode & Test button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = playSound,
                    onClick = { onTogglePlaySound(!playSound) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (playSound) Icons.Default.VolumeUp else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (playSound) {
                                when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Ezan / Sesli Uyarı"
                                    AppLanguage.ENGLISH -> "Adhan / Sound Alert"
                                    AppLanguage.ARABIC -> "تنبيه صوتي / أذان"
                                }
                            } else {
                                when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Sadece Bildirim"
                                    AppLanguage.ENGLISH -> "Silent Notification"
                                    AppLanguage.ARABIC -> "إشعار فقط"
                                }
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.testTag("${testTagPrefix}_sound_chip")
                )

                TextButton(
                    onClick = onTestClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("${testTagPrefix}_test_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.TURKISH -> "Test Et"
                            AppLanguage.ENGLISH -> "Test"
                            AppLanguage.ARABIC -> "تجربة"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun calculateFastingDurationLabel(
    imsak: String,
    aksam: String,
    lang: AppLanguage
): String {
    val imsakParts = imsak.split(":")
    val aksamParts = aksam.split(":")
    if (imsakParts.size < 2 || aksamParts.size < 2) return ""
    val imsakMins = (imsakParts[0].toIntOrNull() ?: return "") * 60 + (imsakParts[1].toIntOrNull() ?: return "")
    val aksamMins = (aksamParts[0].toIntOrNull() ?: return "") * 60 + (aksamParts[1].toIntOrNull() ?: return "")
    val diff = (aksamMins - imsakMins).coerceAtLeast(0)
    val hours = diff / 60
    val mins = diff % 60
    return when (lang) {
        AppLanguage.TURKISH -> "Oruç Süresi: $hours sa $mins dk"
        AppLanguage.ENGLISH -> "Fasting: ${hours}h ${mins}m"
        AppLanguage.ARABIC -> "مدة الصيام: $hours س $mins د"
    }
}

