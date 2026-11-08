package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.MosqueItem
import com.example.data.model.MosqueScopeTier
import com.example.data.repository.AppStrings
import com.example.data.repository.MosqueRepository
import com.example.ui.components.GlassCard
import com.example.viewmodel.PrayerViewModel
import java.util.Locale
import kotlin.math.*

enum class QiblaSubTab {
    COMPASS,
    MOSQUES
}

@Composable
fun QiblaCompassScreen(viewModel: PrayerViewModel) {
    val context = LocalContext.current
    val currentCity by viewModel.currentCity.collectAsState()
    val isGpsActive by viewModel.isGpsActive.collectAsState()
    val rawAzimuth by viewModel.compassAzimuth.collectAsState()
    val qiblaBearing by viewModel.qiblaBearing.collectAsState()
    val distanceToKaaba by viewModel.distanceToKaabaKm.collectAsState()
    val hasCompassSensor by viewModel.hasCompassSensor.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val rawNearbyMosques by viewModel.nearbyMosques.collectAsState()
    val isLoadingMosques by viewModel.isLoadingMosques.collectAsState()
    val locationHierarchy by viewModel.userLocationHierarchy.collectAsState()
    val s = AppStrings.get(currentLanguage)

    var currentTab by remember { mutableStateOf(QiblaSubTab.COMPASS) }
    var manualAngleOffset by remember { mutableStateOf(0f) }
    var isManualMode by remember { mutableStateOf(false) }

    // Permission launcher for instant GPS location when sorting mosques
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            val updated = viewModel.refreshFromInstantDeviceLocation()
            if (updated) {
                Toast.makeText(context, s.gpsLocated, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Automatically refresh from instantaneous device GPS location when opening Mosques tab
    LaunchedEffect(currentTab) {
        if (currentTab == QiblaSubTab.MOSQUES) {
            val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                viewModel.refreshFromInstantDeviceLocation()
            } else {
                viewModel.refreshNearbyMosques()
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    // Strictly enforce hierarchical sort: 1. Mahalle İçi -> 2. İlçe İçi -> 3. İl İçi, then closest to farthest (max 10)
    val nearbyMosques = remember(rawNearbyMosques, currentCity, locationHierarchy) {
        val baseList = if (rawNearbyMosques.isNotEmpty()) {
            rawNearbyMosques
        } else {
            MosqueRepository.getNearbyMosques(
                userLat = currentCity.lat,
                userLon = currentCity.lon,
                limit = MosqueRepository.MAX_NEARBY_MOSQUES,
                hierarchy = locationHierarchy
            )
        }
        baseList
            .sortedWith(
                compareBy<Pair<MosqueItem, Double>> { it.first.scopeTier.rankOrder }
                    .thenBy { it.second }
            )
            .take(MosqueRepository.MAX_NEARBY_MOSQUES)
    }

    val effectiveAzimuth = if (isManualMode || !hasCompassSensor) {
        (manualAngleOffset + 360f) % 360f
    } else {
        rawAzimuth
    }

    // Smooth animated rotation
    val animatedCompassRotation by animateFloatAsState(
        targetValue = -effectiveAzimuth,
        animationSpec = spring(stiffness = 300f),
        label = "compassRotation"
    )

    // Calculate angle difference between heading and Qibla
    var angleDifference = (qiblaBearing.toFloat() - effectiveAzimuth)
    while (angleDifference < -180f) angleDifference += 360f
    while (angleDifference > 180f) angleDifference -= 360f

    val isAligned = abs(angleDifference) <= 4.0f

    val glowColor by animateColorAsState(
        targetValue = if (isAligned) Color(0xFF38E5A1) else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 300),
        label = "glowColor"
    )

    val sortedLabel = when (currentLanguage) {
        AppLanguage.TURKISH -> "Önce Mahalle → İlçe → İl içi en yakın camiler (Maks. 10)"
        AppLanguage.ENGLISH -> "Neighborhood → District → Province closest mosques (Max 10)"
        AppLanguage.ARABIC -> "مرتبة حسب الحي ← المنطقة ← المحافظة من الأقرب (الحد الأقصى ١٠)"
    }

    val closestBadgeLabel = when (currentLanguage) {
        AppLanguage.TURKISH -> "EN YAKIN"
        AppLanguage.ENGLISH -> "CLOSEST"
        AppLanguage.ARABIC -> "الأقرب"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
            .testTag("qibla_compass_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dual Sub-Tabs (Pusula vs Yakındaki Camiler)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = { currentTab = QiblaSubTab.COMPASS },
                shape = RoundedCornerShape(12.dp),
                color = if (currentTab == QiblaSubTab.COMPASS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .testTag("qibla_subtab_compass")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (currentTab == QiblaSubTab.COMPASS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = s.compassTab,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (currentTab == QiblaSubTab.COMPASS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                onClick = { currentTab = QiblaSubTab.MOSQUES },
                shape = RoundedCornerShape(12.dp),
                color = if (currentTab == QiblaSubTab.MOSQUES) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .testTag("qibla_subtab_mosques")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (currentTab == QiblaSubTab.MOSQUES) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = s.mosquesTab,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (currentTab == QiblaSubTab.MOSQUES) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (currentTab == QiblaSubTab.COMPASS) {
            // ==================== COMPASS TAB ====================
            Text(
                text = s.qiblaTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Text(
                text = "${currentCity.name} • ${s.qiblaAngle}: %.1f°".format(Locale.US, qiblaBearing),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Alignment Status Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isAligned) Color(0xFF1B4938) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    if (isAligned) Color(0xFF38E5A1) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (isAligned) Color(0xFF38E5A1) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAligned) s.facingQibla else "${s.turnDevice} (%.0f°)".format(Locale.US, angleDifference),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isAligned) Color(0xFF38E5A1) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Continuous animations for the Qibla Astrolabe Compass
            val infiniteTransition = rememberInfiniteTransition(label = "qiblaCompassAnimations")
            val pulseWave1 by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "pulseWave1"
            )
            val pulseWave2 by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2400, delayMillis = 1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "pulseWave2"
            )
            val astrolabeRingRotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 28000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "astrolabeRingRotation"
            )
            val centerBreathScale by infiniteTransition.animateFloat(
                initialValue = 0.96f,
                targetValue = if (isAligned) 1.08f else 1.03f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "centerBreathScale"
            )

            val cardinalLabels = remember(currentLanguage) {
                when (currentLanguage) {
                    AppLanguage.TURKISH -> arrayOf("K", "D", "G", "B")
                    AppLanguage.ENGLISH -> arrayOf("N", "E", "S", "W")
                    AppLanguage.ARABIC -> arrayOf("ش", "شر", "ج", "غ")
                }
            }

            // Dynamic Animated Qibla Astrolabe Compass Dial Box
            Box(
                modifier = Modifier
                    .size(276.dp)
                    .testTag("compass_dial_box"),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                val qiblaBearingFloat = qiblaBearing.toFloat()
                val goldColor = Color(0xFFE5A93C)
                val goldBright = Color(0xFFFFDF85)
                val goldDeep = Color(0xFF9E6B14)
                val emeraldRich = Color(0xFF0E3326)
                val emeraldCore = Color(0xFF071E16)
                val alignedGreen = Color(0xFF38E5A1)

                // 1. Stationary Outer Astrolabe & Top Heading Lubber Line + Expanding Radar Waves
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = size.width / 2f

                    // Expanding Nur / Radar Pulse Waves from Center
                    val waveColor = if (isAligned) alignedGreen else goldColor
                    drawCircle(
                        color = waveColor.copy(alpha = (1f - pulseWave1) * 0.28f),
                        radius = maxRadius * (0.28f + pulseWave1 * 0.68f),
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = waveColor.copy(alpha = (1f - pulseWave2) * 0.22f),
                        radius = maxRadius * (0.28f + pulseWave2 * 0.68f),
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Slowly Counter-Rotating 16-Point Geometric Filigree Bezel
                    rotate(degrees = astrolabeRingRotation, pivot = center) {
                        val ringRadius = maxRadius - 8.dp.toPx()
                        for (k in 0 until 32) {
                            val ang = Math.toRadians((k * 11.25))
                            val r1 = ringRadius - (if (k % 2 == 0) 6.dp.toPx() else 3.dp.toPx())
                            val r2 = ringRadius
                            drawLine(
                                color = goldColor.copy(alpha = if (k % 2 == 0) 0.55f else 0.25f),
                                start = Offset(
                                    center.x + r1 * sin(ang).toFloat(),
                                    center.y - r1 * cos(ang).toFloat()
                                ),
                                end = Offset(
                                    center.x + r2 * sin(ang).toFloat(),
                                    center.y - r2 * cos(ang).toFloat()
                                ),
                                strokeWidth = if (k % 2 == 0) 2.dp.toPx() else 1.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Stationary Top Phone-Heading Pointer (Lubber Line at 0° top)
                    val topPointerPath = Path().apply {
                        moveTo(center.x, 12.dp.toPx())
                        lineTo(center.x - 8.dp.toPx(), 2.dp.toPx())
                        lineTo(center.x + 8.dp.toPx(), 2.dp.toPx())
                        close()
                    }
                    drawPath(
                        path = topPointerPath,
                        color = if (isAligned) alignedGreen else goldBright
                    )
                }

                // 2. 3D Beveled Metallic Bezel & Emerald-Gold Dial Core
                Box(
                    modifier = Modifier
                        .size(248.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isAligned) 3.5.dp else 2.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    if (isAligned) alignedGreen else goldDeep,
                                    if (isAligned) goldBright else goldBright,
                                    if (isAligned) alignedGreen else goldColor,
                                    if (isAligned) goldBright else goldDeep
                                )
                            ),
                            shape = CircleShape
                        )
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    emeraldRich,
                                    emeraldCore,
                                    Color(0xFF04120D)
                                )
                            )
                        )
                )

                // 3. Rotating Compass Rose, Degree Ticks, Cardinal Letters & Sculpted Qibla Needle
                Canvas(
                    modifier = Modifier
                        .size(236.dp)
                        .rotate(animatedCompassRotation)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2f

                    // Inner ornamental concentric gold rings
                    drawCircle(
                        color = goldColor.copy(alpha = 0.25f),
                        radius = radius - 22.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = goldColor.copy(alpha = 0.18f),
                        radius = radius - 52.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // 8-point Subtle Compass Rose Star in the background
                    for (sIdx in 0 until 8) {
                        rotate(degrees = sIdx * 45f, pivot = center) {
                            val starLen = if (sIdx % 2 == 0) radius - 54.dp.toPx() else radius - 72.dp.toPx()
                            val starPath = Path().apply {
                                moveTo(center.x, center.y - starLen)
                                lineTo(center.x - 6.dp.toPx(), center.y)
                                lineTo(center.x + 6.dp.toPx(), center.y)
                                close()
                            }
                            drawPath(
                                path = starPath,
                                color = goldColor.copy(alpha = if (sIdx % 2 == 0) 0.14f else 0.08f)
                            )
                        }
                    }

                    // Draw 360 degree tick marks
                    for (i in 0 until 360 step 5) {
                        val angleRad = Math.toRadians(i.toDouble())
                        val isCardinal = i % 90 == 0
                        val isMajor = i % 30 == 0
                        val tickLen = when {
                            isCardinal -> 15.dp.toPx()
                            isMajor -> 10.dp.toPx()
                            else -> 5.dp.toPx()
                        }
                        val strokeW = when {
                            isCardinal -> 2.6.dp.toPx()
                            isMajor -> 1.6.dp.toPx()
                            else -> 1.dp.toPx()
                        }
                        val tickColor = when {
                            i == 0 -> Color(0xFFFF6B6B) // North marker red/coral accent
                            isCardinal -> goldBright
                            isMajor -> goldColor.copy(alpha = 0.85f)
                            else -> Color.White.copy(alpha = 0.32f)
                        }

                        val startX = center.x + (radius - tickLen) * sin(angleRad).toFloat()
                        val startY = center.y - (radius - tickLen) * cos(angleRad).toFloat()
                        val endX = center.x + radius * sin(angleRad).toFloat()
                        val endY = center.y - radius * cos(angleRad).toFloat()

                        drawLine(
                            color = tickColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw Cardinal Labels (K/N, D/E, G/S, B/W)
                    val textPaint = android.graphics.Paint().apply {
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 13.dp.toPx()
                        typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.BOLD
                        )
                    }
                    val cardinalAngles = intArrayOf(0, 90, 180, 270)
                    for (idx in cardinalAngles.indices) {
                        val deg = cardinalAngles[idx]
                        val rad = Math.toRadians(deg.toDouble())
                        val labelRadius = radius - 34.dp.toPx()
                        val tx = center.x + labelRadius * sin(rad).toFloat()
                        val ty = center.y - labelRadius * cos(rad).toFloat() + 4.dp.toPx()
                        textPaint.color = if (deg == 0) {
                            Color(0xFFFF7A7A).toArgb()
                        } else {
                            goldBright.toArgb()
                        }
                        drawContext.canvas.nativeCanvas.drawText(cardinalLabels[idx], tx, ty, textPaint)
                    }

                    // Draw Animated Qibla Direction Beam & Sculpted Two-Tone Golden Spear Needle
                    rotate(degrees = qiblaBearingFloat, pivot = center) {
                        // Glowing Qibla Ray from center to rim
                        drawLine(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    if (isAligned) alignedGreen else goldBright,
                                    (if (isAligned) alignedGreen else goldColor).copy(alpha = 0.15f)
                                ),
                                startY = center.y - radius + 10.dp.toPx(),
                                endY = center.y
                            ),
                            start = Offset(center.x, center.y - radius + 12.dp.toPx()),
                            end = Offset(center.x, center.y),
                            strokeWidth = 5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Left half of sculpted Qibla Spear Needle (Bright Gold)
                        val leftNeedle = Path().apply {
                            moveTo(center.x, center.y - radius + 14.dp.toPx())
                            lineTo(center.x - 13.dp.toPx(), center.y - 26.dp.toPx())
                            lineTo(center.x, center.y - 34.dp.toPx())
                            close()
                        }
                        drawPath(
                            path = leftNeedle,
                            color = if (isAligned) alignedGreen else goldBright
                        )

                        // Right half of sculpted Qibla Spear Needle (Deep Burnished Gold)
                        val rightNeedle = Path().apply {
                            moveTo(center.x, center.y - radius + 14.dp.toPx())
                            lineTo(center.x + 13.dp.toPx(), center.y - 26.dp.toPx())
                            lineTo(center.x, center.y - 34.dp.toPx())
                            close()
                        }
                        drawPath(
                            path = rightNeedle,
                            color = if (isAligned) Color(0xFF10B981) else goldColor
                        )

                        // Outer Bezel Qibla Target Jewel
                        drawCircle(
                            color = if (isAligned) alignedGreen else goldBright,
                            radius = 6.dp.toPx(),
                            center = Offset(center.x, center.y - radius + 10.dp.toPx())
                        )
                        drawCircle(
                            color = emeraldCore,
                            radius = 2.5.dp.toPx(),
                            center = Offset(center.x, center.y - radius + 10.dp.toPx())
                        )
                    }
                }

                // 4. Center Breathing Kaaba Medallion with Golden Halo
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(centerBreathScale)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = if (isAligned) {
                                    listOf(alignedGreen, goldBright, alignedGreen)
                                } else {
                                    listOf(goldBright, goldColor, goldDeep)
                                }
                            ),
                            shape = CircleShape
                        )
                        .background(Color(0xFF0A241B))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kaaba),
                        contentDescription = "Kâbe-i Muazzama",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two Data Cards: Kaaba Distance & Angle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = s.distanceToKaaba,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%,d km".format(Locale.US, distanceToKaaba),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = s.qiblaAngle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%.1f°".format(Locale.US, qiblaBearing),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = s.calibrateCompass,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

        } else {
            // ==================== NEARBY MOSQUES TAB (MAHALLE -> İLÇE -> İL, MAX 10) ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = s.nearbyMosques,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${nearbyMosques.size}/${MosqueRepository.MAX_NEARBY_MOSQUES}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = sortedLabel,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Instant GPS Location Refresh button
                    FilledTonalIconButton(
                        onClick = {
                            val fineGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            val coarseGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (fineGranted || coarseGranted) {
                                val updated = viewModel.refreshFromInstantDeviceLocation()
                                if (updated) {
                                    Toast.makeText(context, s.gpsLocated, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("refresh_live_location_mosques_button")
                    ) {
                        Icon(
                            imageVector = if (isGpsActive) Icons.Default.MyLocation else Icons.Default.LocationSearching,
                            contentDescription = s.gpsRequest,
                            modifier = Modifier.size(18.dp),
                            tint = if (isGpsActive) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Direct Google Maps button
                    FilledTonalButton(
                        onClick = {
                            val mapUri = Uri.parse(MosqueRepository.getGoogleMapsSearchUrl(currentCity.lat, currentCity.lon))
                            val intent = Intent(Intent.ACTION_VIEW, mapUri)
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Harita uygulaması açılamadı", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(s.openInGoogleMaps, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Detected Mahalle / İlçe / İl Hierarchy Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mosques_location_hierarchy_banner"),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                border = androidx.compose.foundation.BorderStroke(
                    0.6.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isGpsActive) Icons.Default.MyLocation else Icons.Default.Place,
                            contentDescription = null,
                            tint = if (isGpsActive) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = locationHierarchy.formatSummary(currentLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.TURKISH -> "Sıralama: 1. Mahalle İçi → 2. İlçe İçi → 3. İl İçi"
                                    AppLanguage.ENGLISH -> "Order: 1. Neighborhood → 2. District → 3. Province"
                                    AppLanguage.ARABIC -> "الترتيب: ١. الحي ← ٢. المنطقة ← ٣. المحافظة"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!isGpsActive) {
                        TextButton(
                            onClick = {
                                val fineGranted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                val coarseGranted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                if (fineGranted || coarseGranted) {
                                    viewModel.refreshFromInstantDeviceLocation()
                                } else {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = s.gpsRequest,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (isLoadingMosques) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mosques List (Strictly sorted: Mahalle -> İlçe -> İl, and closest #1 to farthest #10)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("nearby_mosques_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                itemsIndexed(
                    items = nearbyMosques,
                    key = { index, pair -> "${pair.first.id}_$index" }
                ) { index, (mosque, distKm) ->
                    val rank = index + 1
                    val isClosest = rank == 1
                    val distText = if (distKm < 1.0) {
                        "%.0f %s".format(Locale.US, distKm * 1000, s.metersAway)
                    } else {
                        "%.2f %s".format(Locale.US, distKm, s.kmAway)
                    }

                    val tierBadgeColor = when (mosque.scopeTier) {
                        MosqueScopeTier.MAHALLE -> Color(0xFF10B981)
                        MosqueScopeTier.ILCE -> Color(0xFFD97706)
                        MosqueScopeTier.IL -> MaterialTheme.colorScheme.primary
                    }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .let { mod ->
                                if (isClosest) {
                                    mod.border(
                                        width = 1.2.dp,
                                        color = Color(0xFF10B981).copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                } else mod
                            }
                            .testTag("mosque_item_${mosque.id}"),
                        shape = RoundedCornerShape(14.dp),
                        elevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank Badge (#1 .. #10)
                            Surface(
                                modifier = Modifier.size(38.dp),
                                shape = CircleShape,
                                color = if (isClosest) {
                                    Color(0xFF10B981).copy(alpha = 0.2f)
                                } else {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#$rank",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isClosest) {
                                            Color(0xFF10B981)
                                        } else {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = mosque.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (isClosest) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.18f)
                                        ) {
                                            Text(
                                                text = closestBadgeLabel,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF10B981),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Tier Badge: MAHALLE İÇİ / İLÇE İÇİ / İL İÇİ
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = tierBadgeColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = mosque.scopeTier.getLocalizedBadge(currentLanguage),
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                            color = tierBadgeColor,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Distance Badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = distText,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = mosque.getFormattedAreaLabel(),
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (mosque.descriptionTr.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = when (currentLanguage) {
                                            AppLanguage.ENGLISH -> mosque.descriptionEn
                                            AppLanguage.ARABIC -> mosque.descriptionAr
                                            AppLanguage.TURKISH -> mosque.descriptionTr
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Directions button
                            IconButton(
                                onClick = {
                                    val geoUri = Uri.parse("geo:${mosque.lat},${mosque.lon}?q=${Uri.encode(mosque.name)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (_: Exception) {
                                        val browserUri = Uri.parse(MosqueRepository.getGoogleMapsDirectionsUrl(mosque.lat, mosque.lon, mosque.name))
                                        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Directions,
                                    contentDescription = s.directions,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
