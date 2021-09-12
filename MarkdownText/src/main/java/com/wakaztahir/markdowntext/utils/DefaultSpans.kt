package com.wakaztahir.markdowntext.utils

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.Emphasis
import org.commonmark.node.Heading
import org.commonmark.node.StrongEmphasis

internal fun defaultEmphasisSpan(node: Emphasis): SpanStyle {
    return SpanStyle(fontStyle = FontStyle.Italic)
}

internal fun defaultStrongEmphasisSpan(node: StrongEmphasis): SpanStyle {
    return SpanStyle(fontWeight = FontWeight.Bold)
}

internal fun defaultStrikethroughSpan(node: Strikethrough): SpanStyle {
    return SpanStyle(textDecoration = TextDecoration.LineThrough)
}

@OptIn(ExperimentalUnitApi::class)
internal fun defaultHeadingSpan(node: Heading): SpanStyle {
    val size = when (node.level) {
        1 -> 40f
        2 -> 36f
        3 -> 32f
        4 -> 28f
        5 -> 24f
        6 -> 20f
        else -> 18f
    }
    return SpanStyle(fontSize = TextUnit(size, TextUnitType.Sp))
}