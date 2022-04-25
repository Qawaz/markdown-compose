package com.wakaztahir.markdowntext.editor.utils

import androidx.compose.ui.text.AnnotatedString
import com.wakaztahir.markdowntext.editor.model.blocks.TextBlock
import com.wakaztahir.markdowntext.editor.states.EditorState
import com.wakaztahir.markdowntext.utils.TAG_URL

internal data class StyleContainer<T>(val isStarting: Boolean, val style: AnnotatedString.Range<T>) {
    val index: Int
        get() = if (isStarting) style.start else style.end
}

internal data class LinkStyle(val text: String, val link: String)

internal class LineBreak

internal fun AnnotatedString.toSortedStyleContainers(
    addLineBreaks: Boolean = true
): MutableList<StyleContainer<*>> {
    val styleContainers = mutableListOf<StyleContainer<*>>()

    fun AnnotatedString.Range<String>.toLinkRange(): AnnotatedString.Range<LinkStyle> = AnnotatedString.Range(
        item = LinkStyle(text = text.substring(startIndex = start, endIndex = end), link = item),
        start = start,
        end = end
    )

    styleContainers.addAll(spanStyles.map { StyleContainer(style = it, isStarting = true) })

    styleContainers.addAll(spanStyles.map { StyleContainer(style = it, isStarting = false) })

    if (addLineBreaks) {
        var index = text.indexOf(char = '\n', startIndex = 0)
        var lineBreakIndex = maxOf(0, index) + 1
        while (index != -1) {
            styleContainers.add(
                StyleContainer(
                    isStarting = false,
                    AnnotatedString.Range(item = LineBreak(), start = index, end = index)
                )
            )
            index = text.indexOf(char = '\n', startIndex = lineBreakIndex)
            if (index > -1) lineBreakIndex = index + 1
        }
    }

    styleContainers.addAll(
        getStringAnnotations(
            tag = TAG_URL,
            start = 0,
            end = length
        ).map { StyleContainer(style = it.toLinkRange(), isStarting = true) })
    styleContainers.addAll(
        getStringAnnotations(
            tag = TAG_URL,
            start = 0,
            end = length
        ).map { StyleContainer(style = it.toLinkRange(), isStarting = false) })

    // Sort the styles on basis of start and end index
    styleContainers.sortByDescending { it.index }

    return styleContainers
}

//-- Text Import Export Functions

fun EditorState.setText(text: String) {
    blocks.clear()
    importText(text = text)
}

fun EditorState.importText(text: String) = blocks.add(TextBlock(text = text))

fun EditorState.exportToText(): String {
    return blocks.joinToString("\n") { it.exportText(this@exportToText) }
}