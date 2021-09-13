package com.wakaztahir.markdowntext.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.wakaztahir.markdowntext.model.Marker
import com.wakaztahir.markdowntext.model.parse
import com.wakaztahir.markdowntext.preview.components.*
import org.commonmark.ext.gfm.tables.*
import org.commonmark.node.*

val LocalMarker = compositionLocalOf { Marker() }

@Composable
fun MarkdownPreview(
    markdown: String,
) {
    val marker = remember { Marker() }
    val parsed = remember(markdown) { marker.parse(markdown) }

    CompositionLocalProvider(LocalMarker provides marker) {
        Column {
            MDBlockChildren(parent = parsed)
        }

    }

}


@Composable
fun MDBlockChildren(parent: Node) {
    var child = parent.firstChild
    while (child != null) {
        MDBlock(node = child)
        child = child.next
    }
}

@Composable
fun MDBlock(node: Node) {
    when (node) {
        is Document -> MDBlockChildren(node)
        is BlockQuote -> MDBlockQuote(node)
        is ThematicBreak -> MDThematicBreak(node)
        is Heading -> MDHeading(node)
        is Paragraph -> MDParagraph(node)
        is FencedCodeBlock -> MDFencedCodeBlock(node)
        is IndentedCodeBlock -> MDIndentedCodeBlock(node)
        is Image -> MDImage(node)
        is BulletList -> MDBulletList(node)
        is OrderedList -> MDOrderedList(node)
        is HtmlInline -> {

        }
        is HtmlBlock -> {

        }
        is TableBlock -> {
        }
        is TableHead -> {
        }
        is TableBody -> {
        }
        is TableRow -> {
        }
        is TableCell -> {
        }
    }
}

