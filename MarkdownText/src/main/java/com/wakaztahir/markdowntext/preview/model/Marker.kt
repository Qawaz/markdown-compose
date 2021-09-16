package com.wakaztahir.markdowntext.preview.model

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.wakaztahir.markdowntext.preview.annotation.ImageTag
import com.wakaztahir.markdowntext.preview.annotation.createDefaultInlineTextContent
import org.commonmark.node.Image
import org.commonmark.node.Node
import java.util.*


/**
 * [Marker] is an open class that you can override to control the styling
 * of individual components
 */
open class Marker constructor(
    internal var colors: Colors = lightColors(),
    internal var typography: Typography = Typography(),

    // Inline Text Content
    var blocks: MutableMap<String, Node> = mutableMapOf(),
    var inlineContent: Map<String, InlineTextContent> = createDefaultInlineTextContent(blocks),
    // Rendering Variables
    var preventBulletMarker: Boolean = false // when task list marker (checkbox) is being rendered , next bullet marker is not supposed to render
) {

    open fun AnnotatedString.Builder.appendText(text: String) {
        append(text)
    }

    open fun AnnotatedString.Builder.appendParagraph(appendContent: AnnotatedString.Builder.() -> Unit) {
        appendContent()
        append("\n")
    }

    open fun AnnotatedString.Builder.appendEmphasis(appendContent: AnnotatedString.Builder.() -> Unit) {
        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
        appendContent()
        pop()
    }

    open fun AnnotatedString.Builder.appendStrongEmphasis(appendContent: AnnotatedString.Builder.() -> Unit) {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        appendContent()
        pop()
    }

    open fun AnnotatedString.Builder.appendHeading(
        level: Int,
        appendContent: AnnotatedString.Builder.() -> Unit
    ) {
        val textStyle = when (level) {
            1 -> typography.h1
            2 -> typography.h2
            3 -> typography.h3
            4 -> typography.h4
            5 -> typography.h5
            6 -> typography.h6
            else -> typography.body1
        }
        pushStyle(textStyle.toSpanStyle())
        appendContent()
        pop()
        append("\n")
    }

    open fun AnnotatedString.Builder.appendStrikethrough(appendContent: AnnotatedString.Builder.() -> Unit) {
        pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
        appendContent()
        pop()
    }

    open fun AnnotatedString.Builder.appendLink(
        destination: String,
        appendContent: AnnotatedString.Builder.() -> Unit
    ) {
        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = Color.Blue))
        appendContent()
        pop()
    }

    open fun AnnotatedString.Builder.appendBlockquote(appendContent: AnnotatedString.Builder.() -> Unit) {
        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
        appendContent()
        pop()
    }

    open fun AnnotatedString.Builder.appendImage(destination: String, title: String) {
        val id = UUID.randomUUID().toString()
        blocks[id] = Image(destination, title)
        appendInlineContent(ImageTag, id)
    }

    open fun AnnotatedString.Builder.appendCode(code: String) {
        pushStyle(
            SpanStyle(
                background = colors.onBackground.copy(.4f),
                fontFamily = FontFamily.Monospace
            )
        )
        append(code)
        pop()
    }
}