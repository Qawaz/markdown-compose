package com.wakaztahir.markdowntext.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import coil.compose.rememberImagePainter
import com.wakaztahir.markdowntext.model.BlockData

class ImageBlockData(val title: String, val url: String) : BlockData()

@Composable
fun ImageComponent(data: ImageBlockData) {
    if (data.url.isNotEmpty()) {
        Image(
            painter = rememberImagePainter(data.url),
            contentDescription = data.title
        )
    }
}