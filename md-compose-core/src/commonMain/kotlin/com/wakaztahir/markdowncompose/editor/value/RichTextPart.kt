package com.wakaztahir.markdowncompose.editor.value

import androidx.compose.ui.text.AnnotatedString

/**
 * A representation of a part of a rich text.
 * @param fromIndex the start index of the part (inclusive).
 * @param toIndex the end index of the part (exclusive).
 * @param styles the styles to apply to the part.
 * @see RichTextStyle
 */
internal data class RichTextPart(
    val fromIndex: Int,
    val toIndex: Int,
    val styles: Set<RichTextStyle>,
)