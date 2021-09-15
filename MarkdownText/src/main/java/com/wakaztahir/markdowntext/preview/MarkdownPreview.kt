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
import com.wakaztahir.markdowntext.common.LocalCodeTheme
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.common.LocalMarker
import com.wakaztahir.markdowntext.preview.model.Marker
import com.wakaztahir.markdowntext.preview.components.*
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.*
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.node.*
import org.commonmark.parser.Parser

@Composable
fun MarkdownPreview(
    markdown: String,
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    marker: Marker = LocalMarker.current,
    codeTheme : CodeThemeType = if (MaterialTheme.colors.isLight) CodeThemeType.Default else CodeThemeType.Monokai,
) {
    val parser = LocalCommonMarkParser.current
    val parsed = remember(markdown) { parser.parse(markdown) }

    CompositionLocalProvider(LocalCodeTheme provides codeTheme.theme()) {
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
internal fun MDBlockChildren(parent: Node) {
    var child = parent.firstChild
    while (child != null) {
        MDBlock(node = child)
        child = child.next
    }
}

@Composable
internal fun MDBlock(node: Node) {
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