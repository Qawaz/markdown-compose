package com.mylibrary.demo

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val OrangeDefault = Color(0xFFEC724D)
val OrangeDark = Color(0xFFc75b39)

val BlueDefault = Color(0xFF546CF1)
val BlueLight = Color(0xFF6A7DE7)
val BlueDark = Color(0xFF384794)

val PinkDefault = Color(0xFFE91E63)
val PinkDark = Color(0xFFBB1951)

val GreenDefault = Color(0xFF58CF5D)
val GreenDark = Color(0xFF419644)

val LightDefault = Color(0XFFEFEFEF)
val LightMedium = Color(0xFFF1F1F1)
val LightLow = Color(0xFF989898)

val DarkDefault = Color(0xFF1D1D1D)
val DarkDimmed = Color(0xFF292929)
val DarkMedium = Color(0xFF666666)
val DarkLow = Color(0xFF8F8F8F)

val LightColors = lightColors(
    primary = OrangeDefault,
    primaryVariant = OrangeDark,
    secondary = BlueLight,
    background = LightDefault,
//        surface = LightMedium,
    onPrimary = LightDefault,
    onSecondary = DarkMedium,
    onBackground = DarkDimmed,
    onSurface = DarkDimmed,
)

val DarkColors = darkColors(
    primary = OrangeDefault,
    primaryVariant = OrangeDark,
    secondary = BlueDark,
    background = DarkDimmed,
//        surface = DarkMedium,
    onPrimary = DarkDimmed,
    onSecondary = LightMedium,
    onBackground = LightDefault,
    onSurface = LightDefault,
)