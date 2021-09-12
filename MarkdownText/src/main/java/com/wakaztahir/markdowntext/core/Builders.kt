package com.wakaztahir.markdowntext.core

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import com.wakaztahir.markdowntext.components.ImageBlockData
import com.wakaztahir.markdowntext.model.Marker
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.*
import java.util.*

internal fun AnnotatedString.Builder.appendText(node: Text) {
    append(node.literal)
}

internal fun AnnotatedString.Builder.appendParagraph(marker: Marker, node: Paragraph) {
    appendMarkdownContent(marker, node)
    append("\n")
}

internal fun AnnotatedString.Builder.appendEmphasis(marker: Marker, node: Emphasis) {
    pushStyle(marker.emphasisSpan(node))
    appendMarkdownContent(marker, node)
    pop()
}

internal fun AnnotatedString.Builder.appendStrongEmphasis(marker: Marker, node: StrongEmphasis) {
    pushStyle(marker.strongEmphasisSpan(node))
    appendMarkdownContent(marker, node)
    pop()
}

internal fun AnnotatedString.Builder.appendHeading(marker: Marker, node: Heading) {
    pushStyle(marker.headingSpan(node))
    appendMarkdownContent(marker, node)
    pop()
    append("\n")
}

internal fun AnnotatedString.Builder.appendStrikethrough(marker: Marker, node: Strikethrough) {
    pushStyle(marker.strikethroughSpan(node))
    appendMarkdownContent(marker, node)
    pop()
}

internal fun AnnotatedString.Builder.appendImage(marker: Marker,node : Image){
    val id = UUID.randomUUID().toString()
    marker.blocks[id] = ImageBlockData(node.title ?: "Untitled Image",node.destination)
    appendInlineContent(ImageTag,id)
}