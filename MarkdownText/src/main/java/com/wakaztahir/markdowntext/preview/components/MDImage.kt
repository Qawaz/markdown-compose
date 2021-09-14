package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberImagePainter
import org.commonmark.node.Image
import org.commonmark.node.Node
import org.commonmark.node.Visitor

@Composable
internal fun MDImage(node: Image,modifier: Modifier = Modifier) {
    if (node.destination.isNotEmpty()) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Image(
                painter = rememberImagePainter(node.destination),
                contentDescription = node.title ?: ""
            )
        }
    }
}