package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = Color.White,
    secondary = SkyBlueAccent,
    onSecondary = Color.Black,
    secondaryContainer = CardSurfaceNavy,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = RadiantGold,
    onTertiary = Color.Black,
    background = DeepSpaceNavy,
    onBackground = TextPrimaryDark,
    surface = SurfaceDarkNavy,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardSurfaceNavy,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardStrokeNavy,
    error = CrimsonRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = SkyBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFF6FF),
    onSecondaryContainer = Color(0xFF1E40AF),
    tertiary = RadiantGold,
    onTertiary = Color.Black,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = CardSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = CardStrokeLight,
    error = CrimsonRed,
    onError = Color.White
)

@Composable
fun NexusVpnTheme(
    darkTheme: Boolean = true, // commercial VPN apps default to sleek cyber dark theme
    dynamicColor: Boolean = false, // Keep brand aesthetic consistent
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
