package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.LocalMarker
import com.wakaztahir.markdowntext.preview.MDBlockChildren
import com.wakaztahir.markdowntext.preview.MarkdownText
import org.commonmark.node.Document
import org.commonmark.node.Heading

@Composable
fun MDHeading(heading: Heading) {

    val marker = LocalMarker.current

    val style = when (heading.level) {
        1 -> MaterialTheme.typography.h1
        2 -> MaterialTheme.typography.h2
        3 -> MaterialTheme.typography.h3
        4 -> MaterialTheme.typography.h4
        5 -> MaterialTheme.typography.h5
        6 -> MaterialTheme.typography.h6
        else -> {
            // Not a header...
            MDBlockChildren(heading)
            return
        }
    }

    val padding = if (heading.parent is Document) 8.dp else 0.dp
    Box(modifier = Modifier.padding(bottom = padding)) {
        val text = remember {
            buildAnnotatedString {
                appendMarkdownContent(marker, heading)
                toAnnotatedString()
            }
        }

        MarkdownText(
            text = text,
            style = style
        )
    }
}