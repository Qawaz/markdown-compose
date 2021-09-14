package com.wakaztahir.markdowntext.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.markdowntext.model.Marker
import com.wakaztahir.markdowntext.preview.components.*
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.*
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.node.*
import org.commonmark.parser.Parser

val LocalCommonMarkParser = compositionLocalOf { createDefaultParser() }

val LocalMarker = compositionLocalOf { Marker() }

val LocalPrettifyParser = compositionLocalOf { PrettifyParser() }

val LocalCodeTheme = compositionLocalOf { CodeThemeType.Default.theme() }

@Composable
fun MarkdownPreview(
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    marker: Marker = LocalMarker.current,
    markdown: String,
) {
    val parser = LocalCommonMarkParser.current
    val parsed = remember(markdown) { parser.parse(markdown) }

    CompositionLocalProvider(LocalCodeTheme provides if (MaterialTheme.colors.isLight) CodeThemeType.Default.theme() else CodeThemeType.Monokai.theme()) {
        CompositionLocalProvider(LocalMarker provides marker.apply {
            this.colors = colors
            this.typography = typography
            this.preventBulletMarker = false
        }) {
            Column {
                MDBlockChildren(parent = parsed)
            }
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
        is ThematicBreak -> {
            // ignoring
        }
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
        is TableBlock -> MDTable(node = node)
    }
}

internal fun createDefaultParser(): Parser {
    return Parser.builder()
        .extensions(
            listOf(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListItemsExtension.create(),
            )
        ).build()
}