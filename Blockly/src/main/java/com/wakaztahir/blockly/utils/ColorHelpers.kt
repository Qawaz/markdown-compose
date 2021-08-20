package com.wakaztahir.blockly.utils

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

internal fun androidx.compose.ui.graphics.Color.toViewColor(): Int {
    return getViewColor(this)
}

internal fun getViewColor(color: androidx.compose.ui.graphics.Color): Int {
    val argbColor = color.toArgb()
    with(argbColor) {
        return Color.argb(alpha, red, green, blue)
    }
}