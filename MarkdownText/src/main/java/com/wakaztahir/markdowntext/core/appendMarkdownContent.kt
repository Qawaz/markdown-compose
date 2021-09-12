package com.wakaztahir.markdowntext.core

import androidx.compose.ui.text.AnnotatedString
import com.wakaztahir.markdowntext.model.Marker
import com.wakaztahir.markdowntext.model.parse
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.ext.gfm.tables.*
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

            // Line Breaks
            is HardLineBreak -> append("\n")
            is SoftLineBreak -> {

            }
            is ThematicBreak -> {

            }

            // Text Styling
            is Paragraph -> appendParagraph(marker, node)
            is Text -> appendText(node)
            is Emphasis -> appendEmphasis(marker, node)
            is StrongEmphasis -> appendStrongEmphasis(marker, node)
            is Heading -> appendHeading(marker, node)
            is Strikethrough -> appendStrikethrough(marker, node)
            is Link -> appendLink(marker,node)
            is BlockQuote -> appendBlockquote(marker,node)
            is Image -> appendImage(marker, node)

            // Lists
            is BulletList -> {
            }
            is OrderedList -> {
//            AstOrderedList(
//                startNumber = node.startNumber,
//                delimiter = node.delimiter
//            )
            }
            is ListItem -> {
            }

            // Code Blocks
            is Code -> {
            }
            is IndentedCodeBlock -> {
//            AstIndentedCodeBlock(
//                literal = node.literal
//            )
            }
            is FencedCodeBlock -> {
//            AstFencedCodeBlock(
//                literal = node.literal,
//                fenceChar = node.fenceChar,
//                fenceIndent = node.fenceIndent,
//                fenceLength = node.fenceLength,
//                info = node.info
//            )
            }

            is HtmlInline -> {
//            AstHtmlInline(
//                literal = node.literal
//            )
            }
            is HtmlBlock -> {
//            AstHtmlBlock(
//                literal = node.literal
//            )
            }
            is LinkReferenceDefinition -> {
//            AstLinkReferenceDefinition(
//                title = node.title ?: "",
//                destination = node.destination,
//                label = node.label
//            )
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
//            AstTableCell(
//                header = node.isHeader,
//                alignment = when (node.alignment) {
//                    LEFT -> AstTableCellAlignment.LEFT
//                    CENTER -> AstTableCellAlignment.CENTER
//                    RIGHT -> AstTableCellAlignment.RIGHT
//                    null -> AstTableCellAlignment.LEFT
//                }
//            )
            }
            is CustomNode -> {
            }
            is CustomBlock -> {
            }
        }
        node = node.next
    }
}