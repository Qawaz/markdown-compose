package com.wakaztahir.example.testing

import androidx.compose.ui.text.AnnotatedString
import com.wakaztahir.example.builders.*
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.ext.gfm.tables.*
import org.commonmark.node.*

fun AnnotatedString.Builder.appendMarkdownContent(parent: Node) {
    var node = parent.firstChild
    while (node != null) {
        when (node) {
            is BulletList -> {
            }
            is Code -> {
            }
            is Paragraph -> this appendParagraph node
            is Text -> this appendText node
            is Emphasis -> this appendEmphasis node
            is StrongEmphasis -> this appendStrongEmphasis node
            is BlockQuote -> this appendBlockquote node
            is FencedCodeBlock -> {
//            AstFencedCodeBlock(
//                literal = node.literal,
//                fenceChar = node.fenceChar,
//                fenceIndent = node.fenceIndent,
//                fenceLength = node.fenceLength,
//                info = node.info
//            )
            }
            is HardLineBreak -> this appendHardLineBreak node
            is Heading -> this appendHeading node
            is ThematicBreak -> {
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
            is Image -> {
//            AstImage(
//                title = node.title,
//                destination = node.destination
//            )
            }
            is IndentedCodeBlock -> {
//            AstIndentedCodeBlock(
//                literal = node.literal
//            )
            }
            is Link -> {
//            AstLink(
//                title = node.title ?: "",
//                destination = node.destination
//            )
            }
            is ListItem -> {
            }
            is OrderedList -> {
//            AstOrderedList(
//                startNumber = node.startNumber,
//                delimiter = node.delimiter
//            )
            }
            is SoftLineBreak -> {
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
            is Strikethrough -> {
//            AstStrikethrough(
//                node.openingDelimiter
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