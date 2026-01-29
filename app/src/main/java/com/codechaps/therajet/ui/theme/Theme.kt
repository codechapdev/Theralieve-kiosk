package com.codechaps.therajet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TheraColorTokens.PrimaryDark,
    onPrimary = TheraColorTokens.TextWhite,
    primaryContainer = TheraColorTokens.Primary,
    onPrimaryContainer = TheraColorTokens.TextWhite,
    secondary = TheraColorTokens.Accent,
    onSecondary = TheraColorTokens.TextWhite,
    secondaryContainer = TheraColorTokens.PrimaryDark,
    onSecondaryContainer = TheraColorTokens.TextWhite,
    tertiary = TheraColorTokens.PrimaryLight,
    onTertiary = TheraColorTokens.TextPrimary,
    background = Color(0xFF1A1C1E),
    onBackground = TheraColorTokens.TextWhite,
    surface = Color(0xFF1A1C1E),
    onSurface = TheraColorTokens.TextWhite,
    error = TheraColorTokens.StrokeError,
    onError = TheraColorTokens.TextWhite,
    outline = TheraColorTokens.StrokeColor
)

private val LightColorScheme = lightColorScheme(
    primary = TheraColorTokens.Primary,
    onPrimary = TheraColorTokens.TextWhite,
    primaryContainer = TheraColorTokens.PrimaryLight,
    onPrimaryContainer = TheraColorTokens.TextPrimary,
    secondary = TheraColorTokens.Accent,
    onSecondary = TheraColorTokens.TextWhite,
    secondaryContainer = TheraColorTokens.PrimaryLight,
    onSecondaryContainer = TheraColorTokens.TextPrimary,
    tertiary = TheraColorTokens.PrimaryLight,
    onTertiary = TheraColorTokens.TextPrimary,
    background = TheraColorTokens.Background,
    onBackground = TheraColorTokens.TextPrimary,
    surface = TheraColorTokens.Surface,
    onSurface = TheraColorTokens.TextPrimary,
    error = TheraColorTokens.StrokeError,
    onError = TheraColorTokens.TextWhite,
    outline = TheraColorTokens.StrokeColor
)

@Composable
fun TheraJetTabTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}