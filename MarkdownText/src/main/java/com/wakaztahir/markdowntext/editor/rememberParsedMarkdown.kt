package com.wakaztahir.markdowntext.editor

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.common.LocalMarker
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.*

class ParsedMarkdown(val marker: Marker) {
    lateinit var parent: Node
        internal set
    val items = mutableStateListOf<Node>()
}

@Composable
fun rememberParsedMarkdown(
    markdown: String,
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    marker: Marker = LocalMarker.current.apply {
        this.colors = colors
        this.typography = typography
    },
): ParsedMarkdown {

    val parser = LocalCommonMarkParser.current

    return ParsedMarkdown(marker = marker).apply {
        val document = parser.parse(markdown)

        this.parent = document

        // Extracting blocks
        extractChildNodes(this, document)

    }
}

private fun extractChildNodes(
    parsedMarkdown: ParsedMarkdown,
    parent: Node
) {

    var node = parent.firstChild
    while (node != null) {
        when (node) {
            is Document -> extractChildNodes(parsedMarkdown = parsedMarkdown, node)
            is BlockQuote -> {
                parsedMarkdown.items.add(node)
            }
            is ThematicBreak -> {
                // ignoring
            }
            is Heading -> {
                parsedMarkdown.items.add(node)
            }
            is Paragraph -> {
                parsedMarkdown.items.add(node)
            }
            is FencedCodeBlock -> {
                parsedMarkdown.items.add(node)
            }
            is IndentedCodeBlock -> {
                parsedMarkdown.items.add(node)
            }
            is Image -> {
                parsedMarkdown.items.add(node)
            }
            is BulletList -> {
                parsedMarkdown.items.add(node)
            }
            is OrderedList -> {
                parsedMarkdown.items.add(node)
            }
            is HtmlInline -> {
                //ignoring for now
            }
            is HtmlBlock -> {
                //ignoring for now
            }
            is TableBlock -> {
                //ignoring for now
            }
        }
        node = node.next
    }
}