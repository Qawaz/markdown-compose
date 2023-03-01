package com.wakaztahir.markdowncompose.utils

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.wakaztahir.qawazlogger.logIt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


@Composable
internal actual fun imagePainter(url: String): Painter? {
    return imagePainter(url = url, local = false, circleCrop = false)
}

@Composable
fun imagePainter(url: String, local: Boolean = false, circleCrop: Boolean = false): Painter? {
    return if (!local) {
        fetchImage(url)?.let { BitmapPainter(it) }
    } else {
        try {
            BitmapPainter(
                Image.makeFromEncoded(File(url).inputStream().readBytes()).toComposeImageBitmap()
            )
        } catch (e: Exception) {
            e.logIt()
            null
        }
    }
}

@Composable
fun fetchImage(url: String): ImageBitmap? {
    var image by remember(url) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(url) {
        image = loadPicture(url)
    }
    return image
}

suspend fun loadPicture(url: String): ImageBitmap? = withContext(Dispatchers.IO) {
    return@withContext try {
        val jUrl = URL(url)
        val connection: HttpURLConnection = jUrl.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()

        val input: InputStream = connection.inputStream
        Image.makeFromEncoded(input.readBytes()).toComposeImageBitmap()
    } catch (e: Exception) {
        e.logIt()
        null
    }
}