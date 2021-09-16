package com.wakaztahir.markdowntext.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.markdowntext.common.LocalCodeTheme
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.common.LocalMarker
import com.wakaztahir.markdowntext.preview.components.*
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.*

@Composable
fun MarkdownPreview(
    modifier : Modifier = Modifier,
    markdown: String,
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    marker: Marker = LocalMarker.current,
) {
    val parser = LocalCommonMarkParser.current
    val parsed = remember(markdown) { parser.parse(markdown) }
    val isLight = MaterialTheme.colors.isLight
    val codeTheme = remember(isLight) {
        if (isLight) CodeThemeType.Default.theme() else CodeThemeType.Monokai.theme()
    }

    CompositionLocalProvider(LocalCodeTheme provides codeTheme) {
        CompositionLocalProvider(LocalMarker provides marker.apply {
            this.colors = colors
            this.typography = typography
            this.preventBulletMarker = false
        }) {
            Column(modifier = modifier) {
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