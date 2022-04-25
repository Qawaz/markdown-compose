package com.wakaztahir.markdowntext.utils

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb

internal fun androidx.compose.ui.graphics.Color.toViewColor(): Int {
    val argbColor = this.toArgb()
    with(argbColor) {
        return Color.argb(Color.alpha(this), Color.red(this), Color.green(this), Color.blue(this))
    }
}