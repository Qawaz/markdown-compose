package com.wakaztahir.markdowncompose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Composable
internal actual fun imagePainter(url: String): Painter? {
    return rememberImagePainter(data = url)
}

internal actual fun currentTimeMillis(): Long = System.currentTimeMillis()
internal actual val IODispatcher: CoroutineDispatcher = Dispatchers.IO