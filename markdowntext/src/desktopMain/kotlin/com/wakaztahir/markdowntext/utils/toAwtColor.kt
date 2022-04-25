package com.wakaztahir.markdowntext.utils

import java.awt.Color

fun androidx.compose.ui.graphics.Color.toAwtColor(): Color {
    return Color(this.red, this.green, this.blue)
}