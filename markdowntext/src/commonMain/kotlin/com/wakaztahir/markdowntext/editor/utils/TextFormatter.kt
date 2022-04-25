package com.wakaztahir.markdowntext.editor.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import com.wakaztahir.markdowntext.utils.TAG_URL
import com.wakaztahir.markdowntext.utils.fastForEach

abstract class TextFormatter {

    var isBold by mutableStateOf(false)
    var isItalic by mutableStateOf(false)
    var isStrikeThrough by mutableStateOf(false)
    var isLink by mutableStateOf(false)

    abstract var value: TextFieldValue

    /** appends the linked [text] at [index] **/
    fun appendLink(
        text: String,
        link: String,
        index: Int = if (value.selection.reversed) value.selection.end else value.selection.start
    ) {
        value = value.copy(annotatedString = buildAnnotatedString {
            append(text = value.annotatedString.subSequence(startIndex = 0, endIndex = index))
            append(text = text)
            append(
                text = value.annotatedString.subSequence(
                    startIndex = index,
                    endIndex = value.annotatedString.length
                )
            )
            addStringAnnotation(tag = TAG_URL, annotation = link, start = index, end = index + text.length)
            addStyle(
                SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline),
                start = index,
                end = index + text.length
            )
        })
    }

    /** makes text at [start] till [end] a link **/
    fun appendLink(link: String, start: Int = value.selection.start, end: Int = value.selection.end) {
        value = value.copy(annotatedString = buildAnnotatedString {
            append(value.annotatedString)
            val startIndex = if (start < end) start else end
            val endIndex = if (end > start) end else start
            addStringAnnotation(tag = TAG_URL, annotation = link, start = startIndex, end = endIndex)
            addStyle(
                SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline),
                start = startIndex,
                end = endIndex
            )
        })
    }

    fun resetState() {
        isBold = false
        isItalic = false
        isStrikeThrough = false
        isLink = false
    }
}

/** checks whether the [AnnotatedString.Range] contains [TextRange] **/
internal fun <T> AnnotatedString.Range<T>.contains(range: TextRange): Boolean = range.min >= start && range.max <= end

/** checks whether the [TextRange] contains [AnnotatedString.Range] **/
internal fun <T> TextRange.contains(style: AnnotatedString.Range<T>): Boolean = min <= style.start && style.end <= max

/** updates formatter state from [style] **/
private fun TextFormatter.updateState(style: SpanStyle) {
    if ((style.fontWeight?.weight ?: 400) > 400) {
        isBold = true
    }
    if (style.fontStyle == FontStyle.Italic) {
        isItalic = true
    }
    if (style.textDecoration == TextDecoration.LineThrough) {
        isStrikeThrough = true
    }
}

/** updates formatter state from its value **/
internal fun TextFormatter.updateState() {

    resetState()

    value.annotatedString.spanStyles.fastForEach {
        if (it.contains(value.selection)) {
            updateState(it.item)
        }
    }
    value.annotatedString.getStringAnnotations(TAG_URL, value.selection.min, value.selection.max).fastForEach {
        if (it.contains(value.selection)) {
            isLink = true
        }
    }
}

/** searches for [char] from 0 till endIndex (excluding) in string in reverse order **/
private fun String.reverseIndexOf(char: Char, endIndex: Int): Int {
    if (endIndex - 1 < 0) return -1
    var index = -1
    for (i in (endIndex - 1) downTo 0) {
        if (this[i] == char) {
            index = i
            break
        }
    }
    return index
}

/** finds the range for the current word under the cursor (if selection is collapsed) (if not found , returns whole) **/
private fun TextFieldValue.getCurrentWordRange(): TextRange {

    val index = selection.start

    var start: Int
    var end: Int

    start = text.reverseIndexOf(
        char = ' ',
        endIndex = index
    )

    if (start == -1) {
        start = text.reverseIndexOf(
            char = '\n',
            endIndex = index
        )
    }

    end = text.indexOf(
        char = ' ',
        startIndex = index
    )

    if (end == -1) {
        end = text.indexOf(
            char = '\n',
            startIndex = index
        )
    }

    start = if (start < 0) 0 else minOf(start + 1, text.length)
    if (end < 0 || end > text.length) end = text.length

    return TextRange(start = start, end = end)
}

/** applies the [style] to selection , if there's no selection then current word or whole text **/
private fun TextFormatter.selectionStyle(style: SpanStyle) {

    val styleSelection: TextRange = if (value.selection.collapsed) {
        value.getCurrentWordRange()
    } else {
        value.selection
    }

    if (!styleSelection.collapsed && styleSelection.max <= value.text.length) {
        value = value.copy(annotatedString = buildAnnotatedString {
            append(value.annotatedString)
            addStyle(
                style = style,
                start = styleSelection.min,
                end = styleSelection.max
            )
        })

        updateState()
    }
}

/** replaces the style in selection or current word or whole text  **/
internal fun TextFormatter.replaceStyle(
    filterSpanStyle: (AnnotatedString.Range<SpanStyle>) -> Boolean = { true },
    filterParagraphStyle: (AnnotatedString.Range<ParagraphStyle>) -> Boolean = { true },
    removeNewEmptySpanStyles: Boolean = true,
    removeNewEmptyParagraphStyles: Boolean = true,
    replaceSpanStyle: ((AnnotatedString.Range<SpanStyle>) -> AnnotatedString.Range<SpanStyle>?)? = null,
    replaceParagraphStyle: ((AnnotatedString.Range<ParagraphStyle>) -> AnnotatedString.Range<ParagraphStyle>?)? = null
) {

    val styleSelection: TextRange = if (value.selection.collapsed) {
        value.getCurrentWordRange()
    } else {
        value.selection
    }


    if (!styleSelection.collapsed && styleSelection.max <= value.text.length) {

        val spanStyles: MutableList<AnnotatedString.Range<SpanStyle>> =
            value.annotatedString.spanStyles.toMutableList()
        val newParagraphStyles: MutableList<AnnotatedString.Range<ParagraphStyle>> =
            value.annotatedString.paragraphStyles.toMutableList()


        if (replaceSpanStyle != null) {
            val sItr = spanStyles.listIterator()
            while (sItr.hasNext()) {
                val spanStyle = sItr.next()
                if (
                    (styleSelection.collapsed && spanStyle.contains(styleSelection))
                    || styleSelection.contains(spanStyle)
                ) {
                    if (filterSpanStyle(spanStyle)) {
                        replaceSpanStyle(spanStyle).let { newSpanStyle ->
                            if (newSpanStyle == null || removeNewEmptySpanStyles && newSpanStyle.item.isEmpty()) {
                                sItr.remove()
                            } else {
                                sItr.set(newSpanStyle)
                            }
                        }
                    }
                }
            }
        }

        if (replaceParagraphStyle != null) {
            val pItr = newParagraphStyles.listIterator()
            while (pItr.hasNext()) {
                val paragraphStyle = pItr.next()
                if (
                    (styleSelection.collapsed && paragraphStyle.contains(styleSelection))
                    || styleSelection.contains(paragraphStyle)
                ) {
                    if (filterParagraphStyle(paragraphStyle)) {
                        replaceParagraphStyle(paragraphStyle).let { newParagraphStyle ->
                            if (newParagraphStyle == null || removeNewEmptyParagraphStyles && newParagraphStyle.item.isEmpty()) {
                                pItr.remove()
                            } else {
                                pItr.set(newParagraphStyle)
                            }
                        }
                    }
                }
            }
        }

        value = value.copy(
            annotatedString = AnnotatedString(
                text = value.annotatedString.text,
                spanStyles = spanStyles,
                paragraphStyles = newParagraphStyles
            )
        )

        updateState()
    }
}

/** checks if all span style's properties are null **/
internal fun SpanStyle.isEmpty(): Boolean = color == Color.Unspecified &&
        fontSize == TextUnit.Unspecified &&
        fontWeight == null || fontWeight == FontWeight.Normal &&
        fontStyle == null || fontStyle == FontStyle.Normal &&
        fontSynthesis == null &&
        fontFamily == null &&
        fontFeatureSettings == null &&
        letterSpacing == TextUnit.Unspecified &&
        baselineShift == null &&
        textGeometricTransform == null &&
        localeList == null &&
        background == Color.Unspecified &&
        textDecoration == null || textDecoration == TextDecoration.None &&
        shadow == null

/** checks if all span style's properties are null **/
internal fun ParagraphStyle.isEmpty(): Boolean = textAlign == null &&
        textDirection == null &&
        lineHeight == TextUnit.Unspecified &&
        textIndent == null

// Bold Functions

//fun TextFormatter.toggleBold()

fun TextFormatter.makeBold() {
    replaceStyle(
        filterSpanStyle = { it.item.fontWeight == FontWeight.Normal },
        removeNewEmptySpanStyles = false,
        replaceSpanStyle = { it.copy(item = it.item.copy(fontWeight = FontWeight.Bold)) }
    )
    selectionStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
}

fun TextFormatter.removeBold() = replaceStyle(
    filterSpanStyle = { it.item.fontWeight != FontWeight.Normal },
    replaceSpanStyle = { it.copy(item = it.item.copy(fontWeight = FontWeight.Normal)) }
)

// Italic Functions

//fun TextFormatter.toggleItalic()

fun TextFormatter.makeItalic() {
    replaceStyle(
        filterSpanStyle = { it.item.fontStyle == FontStyle.Normal },
        removeNewEmptySpanStyles = false,
        replaceSpanStyle = { it.copy(item = it.item.copy(fontStyle = FontStyle.Italic)) }
    )
    selectionStyle(style = SpanStyle(fontStyle = FontStyle.Italic))
}

fun TextFormatter.removeItalic() = replaceStyle(
    filterSpanStyle = { it.item.fontStyle == FontStyle.Italic },
    replaceSpanStyle = { it.copy(item = it.item.copy(fontStyle = FontStyle.Normal)) }
)

// Strike Through Functions

//fun TextFormatter.toggleStrikeThrough()

fun TextFormatter.makeStrikeThrough() {
    replaceStyle(
        filterSpanStyle = { it.item.textDecoration != TextDecoration.LineThrough },
        removeNewEmptySpanStyles = false,
        replaceSpanStyle = { it.copy(item = it.item.copy(textDecoration = TextDecoration.LineThrough)) }
    )
    selectionStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough))
}

fun TextFormatter.removeStrikeThrough() = replaceStyle(
    filterSpanStyle = { it.item.textDecoration == TextDecoration.LineThrough },
    replaceSpanStyle = { it.copy(item = it.item.copy(textDecoration = TextDecoration.None)) }
)