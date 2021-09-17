package com.wakaztahir.markdowntext.preview.annotation

import androidx.compose.ui.text.AnnotatedString
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.*

/**
 * Appends Styles for all the children nodes of the [parent]
 */
internal fun AnnotatedString.Builder.appendMarkdownContent(marker: Marker, parent: Node?) {
    if(parent!=null) {
        var node = parent.firstChild
        while (node != null) {
            appendMarkdownNode(marker, node)
            node = node.next
        }
    }
}

/**
 * Appends styles for [node] and its children
 */
internal fun AnnotatedString.Builder.appendMarkdownNode(marker: Marker, node: Node?) {
    marker.apply {
        when (node) {
            is HardLineBreak -> append("\n")
            is Paragraph -> appendParagraph { appendMarkdownContent(marker, node) }
            is Text -> appendText(node.literal ?: "")
            is Emphasis -> appendEmphasis { appendMarkdownContent(marker, node) }
            is StrongEmphasis -> appendStrongEmphasis { appendMarkdownContent(marker, node) }
            is Heading -> appendHeading(node.level) { appendMarkdownContent(marker, node) }
            is Strikethrough -> appendStrikethrough { appendMarkdownContent(marker, node) }
            is Link -> appendLink(node.destination ?: "") {
                pushStringAnnotation(URLTag, node.destination ?: "")
                appendMarkdownContent(marker, node)
                pop()
            }
            is BlockQuote -> appendBlockquote { appendMarkdownContent(marker, node) }
            is Image -> appendImage(node.destination ?: "", node.title ?: "")
            is Code -> appendCode(node.literal ?: "")
        }
    }
}