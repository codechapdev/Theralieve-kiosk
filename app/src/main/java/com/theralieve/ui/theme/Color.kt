package com.theralieve.ui.theme

import androidx.compose.ui.graphics.Color

object TheraColorTokens {
    val Primary = Color(0xFF2196F3)
    val PrimaryDark = Color(0xFF1976D2)
    val PrimaryLight = Color(0xFFBBDEFB)
    val Accent = Color(0xFF03A9F4)

    val BackgroundGradientStart = Color(0xFF87CEEB)
    val BackgroundGradientEnd = Color(0xFFFFFFFF)
    val Surface = Color(0xFFFFFFFF)
    val Background = Color(0xFFF5F5F5)

    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
    val TextTertiary = Color(0xFF9E9E9E)
    val TextWhite = Color(0xFFFFFFFF)
    val TextGreen = Color(0xFF188639)

    val ButtonPrimary = Primary
    val ButtonPrimaryPressed = PrimaryDark
    val ButtonDisabled = Color(0xFFBDBDBD)
    val ButtonText = TextWhite

//    val StrokeColor = Color(0xFFE6E6E6)
    val StrokeColor = Color.Gray
    val StrokeFocused = Primary
    val StrokeError = Color(0xFFF44336)
    val InputBackground = Color(0xFFFFFFFF)
    val InputText = TextPrimary
    val InputHint = TextTertiary
    val RippleLight = Color(0x1F000000)
    val RippleDark = Color(0x1FFFFFFF)
}