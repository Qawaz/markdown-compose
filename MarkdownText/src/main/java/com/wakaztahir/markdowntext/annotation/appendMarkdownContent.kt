package com.wakaztahir.markdowntext.annotation

import androidx.compose.ui.text.AnnotatedString
import com.wakaztahir.markdowntext.model.Marker
import com.wakaztahir.markdowntext.model.parse
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.*

fun AnnotatedString.Builder.appendMarkdownContent(marker: Marker, input: String) =
    appendMarkdownContent(
        marker = marker,
        parent = marker.parse(input)
    )

fun AnnotatedString.Builder.appendMarkdownContent(marker: Marker, parent: Node) {
    var node = parent.firstChild
    while (node != null) {
        when (node) {
            is HardLineBreak -> append("\n")
            is Paragraph -> appendParagraph(marker, node)
            is Text -> appendText(node)
            is Emphasis -> appendEmphasis(marker, node)
            is StrongEmphasis -> appendStrongEmphasis(marker, node)
            is Heading -> appendHeading(marker, node)
            is Strikethrough -> appendStrikethrough(marker, node)
            is Link -> appendLink(marker, node)
            is BlockQuote -> appendBlockquote(marker, node)
            is Image -> appendImage(marker, node)
            is Code -> appendCode(marker,node)
        }
        node = node.next
    }
}