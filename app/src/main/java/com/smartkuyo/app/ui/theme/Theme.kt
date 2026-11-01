package com.smartkuyo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GoldLight,
    onPrimary = DeepBlack,
    secondary = GoldMuted,
    onSecondary = CreamWhite,
    tertiary = BuddhistPurple,
    background = DeepBlack,
    onBackground = CreamWhite,
    surface = SoftBlack,
    onSurface = SoftWhite,
    surfaceVariant = DarkGray,
    onSurfaceVariant = MutedGray,
    outline = GoldMuted
)

@Composable
fun SmartKuyoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
