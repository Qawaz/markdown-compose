package com.wakaztahir.markdowntext.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter
import kotlinx.coroutines.CoroutineDispatcher

@Composable
internal actual fun imagePainter(url: String): Painter? {
    return rememberImagePainter(data = url)
}