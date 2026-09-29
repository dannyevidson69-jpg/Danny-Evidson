package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GoalColorScheme = darkColorScheme(
    primary = GoalGreen,
    onPrimary = Color.Black,
    primaryContainer = GoalGreenGlow,
    onPrimaryContainer = GoalGreen,
    secondary = GoalCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0x3300E5FF),
    onSecondaryContainer = GoalCyan,
    tertiary = GoalMagenta,
    onTertiary = Color.White,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = LiveRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // GOAL is a cinematic video platform, default to dark color scheme
    MaterialTheme(
        colorScheme = GoalColorScheme,
        typography = Typography,
        content = content
    )
}
