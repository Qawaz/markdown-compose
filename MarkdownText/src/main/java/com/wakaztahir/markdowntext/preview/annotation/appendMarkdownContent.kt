package com.wakaztahir.markdowntext.preview.annotation

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.*
import java.util.*

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
            is Code -> appendCode(marker, node)
        }
        node = node.next
    }
}


internal fun AnnotatedString.Builder.appendText(node: Text) {
    append(node.literal)
}

internal fun AnnotatedString.Builder.appendParagraph(marker: Marker, node: Paragraph) {
    appendMarkdownContent(marker, node)
    append("\n")
}

internal fun AnnotatedString.Builder.appendEmphasis(marker: Marker, node: Emphasis) {
    pushStyle(marker.emphasisStyle(node))
    appendMarkdownContent(marker, node)
    pop()
}

internal fun AnnotatedString.Builder.appendStrongEmphasis(marker: Marker, node: StrongEmphasis) {
    pushStyle(marker.strongEmphasisStyle(node))
    appendMarkdownContent(marker, node)
    pop()
}

internal fun AnnotatedString.Builder.appendHeading(marker: Marker, node: Heading) {
    pushStyle(marker.headingStyle(marker, node))
    appendMarkdownContent(marker, node)
    pop()
    append("\n")
}

internal fun AnnotatedString.Builder.appendStrikethrough(marker: Marker, node: Strikethrough) {
    pushStyle(marker.strikethroughStyle(node))
    appendMarkdownContent(marker, node)
    pop()
}

internal fun AnnotatedString.Builder.appendLink(marker: Marker, node: Link) {
    pushStyle(marker.linkStyle(node))
    pushStringAnnotation(URLTag, node.destination ?: "")
    appendMarkdownContent(marker, node)
    pop()
    pop()
}

internal fun AnnotatedString.Builder.appendBlockquote(marker: Marker, node: BlockQuote) {
    pushStyle(marker.blockQuoteStyle(node))
    appendMarkdownContent(marker, node)
    pop()
}

internal fun AnnotatedString.Builder.appendImage(marker: Marker, node: Image) {
    val id = UUID.randomUUID().toString()
    marker.blocks[id] = Image(node.destination ?: "", node.title ?: "Untitled Image")
    appendInlineContent(ImageTag, id)
}

internal fun AnnotatedString.Builder.appendCode(marker: Marker, node: Code) {
    pushStyle(
        SpanStyle(
            background = marker.colors.onBackground.copy(.4f),
            fontFamily = FontFamily.Monospace
        )
    )
    append(node.literal)
    pop()
}