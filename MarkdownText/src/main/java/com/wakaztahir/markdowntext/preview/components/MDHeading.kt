package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.preview.model.LocalPreviewRenderer

@Composable
internal fun MDHeading(
    isParentDocument: Boolean,
    level: Int,
    appendContent: AnnotatedString.Builder.() -> Unit,
) {

    val renderer = LocalPreviewRenderer.current

    val style = when (level) {
        1 -> MaterialTheme.typography.h1
        2 -> MaterialTheme.typography.h2
        3 -> MaterialTheme.typography.h3
        4 -> MaterialTheme.typography.h4
        5 -> MaterialTheme.typography.h5
        6 -> MaterialTheme.typography.h6
        else -> {
            return
        }
    }

    val padding = if (isParentDocument) 8.dp else 0.dp
    Box(modifier = Modifier.padding(bottom = padding)) {
        val text = remember {
            buildAnnotatedString {
                appendContent()
                toAnnotatedString()
            }
        }

        renderer.PreviewText(
            text = text,
            style = style
        )
    }
}