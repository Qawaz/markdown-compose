package com.wakaztahir.markdowntext.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.wakaztahir.markdowntext.model.Marker
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.*

internal fun defaultEmphasisStyle(node: Emphasis): SpanStyle {
    return SpanStyle(fontStyle = FontStyle.Italic)
}

internal fun defaultStrongEmphasisStyle(node: StrongEmphasis): SpanStyle {
    return SpanStyle(fontWeight = FontWeight.Bold)
}

internal fun defaultStrikethroughStyle(node: Strikethrough): SpanStyle {
    return SpanStyle(textDecoration = TextDecoration.LineThrough)
}

@OptIn(ExperimentalUnitApi::class)
internal fun defaultHeadingStyle(marker: Marker, node: Heading): SpanStyle {
    val textStyle = when (node.level) {
        1 -> marker.typography.h1
        2 -> marker.typography.h2
        3 -> marker.typography.h3
        4 -> marker.typography.h4
        5 -> marker.typography.h5
        6 -> marker.typography.h6
        else -> marker.typography.body1
    }
    return textStyle.toSpanStyle()
}

internal fun defaultLinkStyle(node: Link): SpanStyle {
    return SpanStyle(textDecoration = TextDecoration.Underline, color = Color.Blue)
}

internal fun defaultBlockQuoteStyle(node: BlockQuote): SpanStyle {
    return SpanStyle(fontStyle = FontStyle.Italic)
}