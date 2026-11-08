package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF261900),
    primaryContainer = EmeraldSurfaceVariant,
    onPrimaryContainer = GoldLight,
    secondary = EmeraldLight,
    onSecondary = EmeraldDark,
    secondaryContainer = EmeraldSurface,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = GoldLight,
    onTertiary = EmeraldDark,
    background = EmeraldDark,
    onBackground = TextPrimaryDark,
    surface = EmeraldNight,
    onSurface = TextPrimaryDark,
    surfaceVariant = EmeraldSurface,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0x33E5A93C),
    outlineVariant = Color(0x1AE5A93C)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightPrimary,
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldBadge,
    onSecondaryContainer = Color(0xFF473300),
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0x330D684F),
    outlineVariant = Color(0x1A0D684F)
)

@Composable
fun EzanVaktiTheme(
    darkTheme: Boolean = true, // Default to deep Islamic night emerald theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
