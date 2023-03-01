package com.wakaztahir.markdowncompose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.js.Date

@Composable
internal actual fun imagePainter(url: String): Painter? {
    return null
}

internal actual fun currentTimeMillis(): Long = Date.now().toLong()
internal actual val IODispatcher: CoroutineDispatcher get() = Dispatchers.Default