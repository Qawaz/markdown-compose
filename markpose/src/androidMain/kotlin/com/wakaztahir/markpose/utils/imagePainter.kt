package com.wakaztahir.markpose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter

@Composable
internal actual fun imagePainter(url: String): Painter? {
    return rememberImagePainter(data = url)
}