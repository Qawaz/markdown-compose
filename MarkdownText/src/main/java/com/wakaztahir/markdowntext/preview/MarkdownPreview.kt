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
import com.wakaztahir.markdowntext.preview.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.components.*
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.*

@Composable
fun MarkdownPreview(
    modifier: Modifier = Modifier,
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
    val marker = LocalMarker.current
    when (node) {
        is Document -> MDBlockChildren(node)
        is BlockQuote -> MDBlockQuote {
            appendMarkdownContent(marker,node)
        }
        is ThematicBreak -> {
            // ignoring
        }
        is Heading -> {
            if (node.level in 0..6) {
                MDHeading(isParentDocument = node.parent is Document, level = node.level) {
                    appendMarkdownContent(marker, node)
                }
            } else {
                // not a heading
                MDBlockChildren(parent = node)
            }
        }
        is Paragraph -> {
            if (node.firstChild is Image && node.firstChild == node.lastChild) {
                // Paragraph with single image
                MDImage(
                    destination = (node.firstChild as Image).destination ?: "",
                    title = (node.firstChild as Image).title ?: ""
                )
            } else {
                MDParagraph(isParentDocument = node.parent is Document) {
                    appendMarkdownContent(marker, node)
                }
            }
        }
        is FencedCodeBlock -> MDFencedCodeBlock(
            isParentDocument = node.parent is Document,
            info = node.info ?: "",
            literal = node.literal ?: "",
            fenceChar = node.fenceChar,
            fenceIndent = node.fenceIndent,
            fenceLength = node.fenceLength
        )
        is IndentedCodeBlock -> MDIndentedCodeBlock(
            isParentDocument = node.parent is Document,
            literal = node.literal
        )
        is Image -> MDImage(
            destination = node.destination ?: "",
            title = node.title ?: ""
        )
        is BulletList -> MDBulletList(node)
        is OrderedList -> MDOrderedList(node)
        is HtmlInline -> {

        }
        is HtmlBlock -> {

        }
        is TableBlock -> MDTable(node = node)
    }
}