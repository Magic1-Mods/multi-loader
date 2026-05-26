package com.magic.loader.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryIndigo.copy(alpha = 0.15f),
    onPrimaryContainer = PrimaryIndigo,
    secondary = SecondaryCyan,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryCyan.copy(alpha = 0.15f),
    onSecondaryContainer = SecondaryCyan,
    tertiary = TertiaryViolet,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryViolet.copy(alpha = 0.15f),
    onTertiaryContainer = TertiaryViolet,
    background = DarkBackground,
    onBackground = Color(0xFFE2E2E8),
    surface = DarkSurface,
    onSurface = Color(0xFFE2E2E8),
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = Color(0xFF8B8B9E),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF3D3D50)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryIndigo.copy(alpha = 0.12f),
    onPrimaryContainer = PrimaryIndigo.copy(alpha = 0.8f),
    secondary = SecondaryCyan.copy(alpha = 0.8f),
    onSecondary = Color.Black,
    secondaryContainer = SecondaryCyan.copy(alpha = 0.12f),
    onSecondaryContainer = SecondaryCyan.copy(alpha = 0.7f),
    tertiary = TertiaryViolet,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryViolet.copy(alpha = 0.12f),
    onTertiaryContainer = TertiaryViolet.copy(alpha = 0.8f),
    background = Color(0xFFF8F9FC),
    onBackground = Color(0xFF1A1A20),
    surface = Color.White,
    onSurface = Color(0xFF1A1A20),
    surfaceVariant = Color(0xFFF0F0F5),
    onSurfaceVariant = Color(0xFF6B6B7B),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFFD1D1DB)
)

@Composable
fun MagicLoaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
