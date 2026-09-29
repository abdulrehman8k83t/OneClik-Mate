package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorPalette = darkColorScheme(
    primary = NeonAmber,
    onPrimary = OLEDBlack,
    primaryContainer = Color(0xFF332000),
    onPrimaryContainer = NeonAmber,
    secondary = TealBlur,
    onSecondary = OLEDBlack,
    secondaryContainer = Color(0xFF00274D),
    onSecondaryContainer = TealBlur,
    background = OLEDBlack,
    onBackground = HighContrastWhite,
    surface = DeepSlateGrey,
    onSurface = Color(0xFFE5E5EA),
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = MutedGrey,
    outline = SurfaceBorder,
    outlineVariant = Color(0xFF3A3A3C),
    error = AlertRed,
    onError = OLEDBlack
)

@Composable
fun VidmateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = Typography,
        content = content
    )
}
