package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.screens.*
import com.example.ui.theme.EzanVaktiTheme
import com.example.viewmodel.PrayerViewModel

import com.example.notification.PrayerNotificationHelper
import com.example.notification.PrayerScheduler

import com.example.data.model.AppLanguage
import com.example.data.repository.AppStrings
import androidx.compose.ui.platform.LocalLayoutDirection

enum class NavDestination(
    val route: String,
    val defaultTitle: String,
    val filledIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val isBottomBarVisible: Boolean = true
) {
    VAKITLER("vakitler", "Vakitler", Icons.Filled.AccessTime, Icons.Outlined.AccessTime),
    KURAN("kuran", "Kur'an", Icons.Filled.AutoStories, Icons.Outlined.AutoStories),
    DUALAR("dualar", "Dualar", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    KIBLE("kible", "Kıble", Icons.Filled.Explore, Icons.Outlined.Explore),
    ZIKIRMATIK("zikirmatik", "Zikir", Icons.Filled.TouchApp, Icons.Outlined.TouchApp),
    AYARLAR("ayarlar", "Ayarlar", Icons.Filled.Settings, Icons.Outlined.Settings, isBottomBarVisible = false),
    TAKVIM("takvim", "Takvim", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, isBottomBarVisible = false);

    fun getTitle(lang: AppLanguage): String {
        val s = AppStrings.get(lang)
        return when (this) {
            VAKITLER -> s.tabVakitler
            KURAN -> s.tabKuran
            DUALAR -> s.tabDualar
            KIBLE -> s.tabKible
            ZIKIRMATIK -> s.tabZikirmatik
            AYARLAR -> s.tabAyarlar
            TAKVIM -> s.tabTakvim
        }
    }
}

class MainActivity : ComponentActivity() {

    private val prayerViewModel: PrayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize WorkManager notification channel and schedule initial alerts
        PrayerNotificationHelper.createNotificationChannel(this)
        PrayerScheduler.scheduleUpcomingPrayers(this)
        com.example.data.repository.QuranRepository.init(this)

        setContent {
            val isDarkTheme by prayerViewModel.isDarkTheme.collectAsState()
            val currentLanguage by prayerViewModel.currentLanguage.collectAsState()

            CompositionLocalProvider(LocalLayoutDirection provides currentLanguage.layoutDirection) {
                EzanVaktiTheme(darkTheme = isDarkTheme) {
                    MainAppContent(
                        viewModel = prayerViewModel,
                        onRequestGps = { checkAndRequestGpsLocation() }
                    )
                }
            }
        }
    }

    private fun checkAndRequestGpsLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            fetchGpsCoordinates()
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchGpsCoordinates(silent: Boolean = false) {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (locationManager == null) {
                if (!silent) {
                    Toast.makeText(this, "Konum servisi bulunamadı", Toast.LENGTH_SHORT).show()
                }
                return
            }

            var bestLocation: Location? = null
            val providers = buildList {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(LocationManager.FUSED_PROVIDER)
                }
                add(LocationManager.GPS_PROVIDER)
                add(LocationManager.NETWORK_PROVIDER)
                add(LocationManager.PASSIVE_PROVIDER)
            }

            for (provider in providers) {
                try {
                    if (locationManager.isProviderEnabled(provider)) {
                        val loc = locationManager.getLastKnownLocation(provider)
                        if (loc != null) {
                            if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                                bestLocation = loc
                            }
                        }
                    }
                } catch (_: Exception) {
                }
            }

            // Request a fresh single update so location is always up-to-date
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    prayerViewModel.setGpsLocation(location.latitude, location.longitude)
                    try {
                        locationManager.removeUpdates(this)
                    } catch (_: Exception) {
                    }
                    if (!silent && bestLocation == null) {
                        Toast.makeText(this@MainActivity, "Konumunuz güncellendi", Toast.LENGTH_SHORT).show()
                    }
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if (bestLocation != null) {
                prayerViewModel.setGpsLocation(bestLocation.latitude, bestLocation.longitude)
                if (!silent) {
                    Toast.makeText(this, "Konumunuz tespit edildi", Toast.LENGTH_SHORT).show()
                }
            }

            try {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    @Suppress("DEPRECATION")
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null)
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    @Suppress("DEPRECATION")
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null)
                } else if (bestLocation == null && !silent) {
                    Toast.makeText(this, "Lütfen cihazınızın konum (GPS) ayarını açın", Toast.LENGTH_LONG).show()
                }
            } catch (_: Exception) {
            }
        } catch (e: Exception) {
            if (!silent) {
                Toast.makeText(this, "Konum alınırken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: PrayerViewModel,
    onRequestGps: () -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(NavDestination.VAKITLER) }
    var isQuranFullScreen by remember { mutableStateOf(false) }
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    // If tab changes away from Quran, reset full-screen
    LaunchedEffect(currentTab) {
        if (currentTab != NavDestination.KURAN) {
            isQuranFullScreen = false
        }
    }

    // Handle back button: if not on Home (VAKITLER), go back to VAKITLER
    BackHandler(enabled = currentTab != NavDestination.VAKITLER) {
        currentTab = NavDestination.VAKITLER
    }

    // Optional Notification Permission launcher for Android 13+ (API 33+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val requestNotificationPermissionIfNeeded = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotif = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasNotif) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Permission launcher for Location (auto-runs on first launch & manual GPS requests)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            (context as? MainActivity)?.fetchGpsCoordinates(silent = false)
        } else {
            Toast.makeText(context, "Konum izni verilmedi; 81 ilden dilediğinizi seçebilirsiniz.", Toast.LENGTH_LONG).show()
        }
        requestNotificationPermissionIfNeeded()
    }

    // On first launch (before the user manually selects a city), automatically detect location and set prayer times
    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (viewModel.shouldAutoDetectLocationOnStartup()) {
            if (fineGranted || coarseGranted) {
                (context as? MainActivity)?.fetchGpsCoordinates(silent = true)
                requestNotificationPermissionIfNeeded()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            requestNotificationPermissionIfNeeded()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = if (isQuranFullScreen && currentTab == NavDestination.KURAN) WindowInsets(0, 0, 0, 0) else WindowInsets.safeDrawing,
        bottomBar = {
            if (!isQuranFullScreen || currentTab != NavDestination.KURAN) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavDestination.entries.filter { it.isBottomBarVisible }.forEach { destination ->
                        val isSelected = currentTab == destination
                        val localizedTitle = destination.getTitle(currentLanguage)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = destination },
                            alwaysShowLabel = true,
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.filledIcon else destination.outlinedIcon,
                                    contentDescription = localizedTitle,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = localizedTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${destination.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "screenCrossfade",
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                when (tab) {
                    NavDestination.VAKITLER -> MainPrayerScreen(
                        viewModel = viewModel,
                        onNavigateToCompass = { currentTab = NavDestination.KIBLE },
                        onNavigateToDhikr = { currentTab = NavDestination.ZIKIRMATIK },
                        onNavigateToCalendar = { currentTab = NavDestination.TAKVIM },
                        onNavigateToQuran = { currentTab = NavDestination.KURAN },
                        onNavigateToSettings = { currentTab = NavDestination.AYARLAR },
                        onGpsRequest = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                    NavDestination.KURAN -> QuranScreen(
                        viewModel = viewModel,
                        onFullScreenChanged = { isQuranFullScreen = it }
                    )
                    NavDestination.DUALAR -> DuasAndHadithsScreen(viewModel = viewModel)
                    NavDestination.KIBLE -> QiblaCompassScreen(viewModel = viewModel)
                    NavDestination.ZIKIRMATIK -> DhikrScreen(viewModel = viewModel)
                    NavDestination.TAKVIM -> CalendarScreen(viewModel = viewModel)
                    NavDestination.AYARLAR -> SettingsScreen(
                        viewModel = viewModel,
                        onGpsRequest = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
