package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AutoCyanPrimary,
    onPrimary = Color(0xFF041E28),
    primaryContainer = AutoCyanSecondary,
    onPrimaryContainer = Color.White,
    secondary = AutoOrangeAccent,
    onSecondary = Color.Black,
    secondaryContainer = AutoDarkSurfaceVariant,
    onSecondaryContainer = AutoCyanPrimary,
    tertiary = AutoEmerald,
    onTertiary = Color.Black,
    background = AutoDarkBg,
    onBackground = TextPrimary,
    surface = AutoDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = AutoDarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = AutoBorder,
    error = AutoWarning,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AutoLightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = AutoOrangeAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF9A3412),
    tertiary = AutoEmerald,
    onTertiary = Color.White,
    background = AutoLightBg,
    onBackground = Color(0xFF0F172A),
    surface = AutoLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = AutoWarning,
    onError = Color.White
)

@Composable
fun AutoDriveTheme(
    darkTheme: Boolean = true, // Default to high-contrast Dark theme for car dashboard & driving safety
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
