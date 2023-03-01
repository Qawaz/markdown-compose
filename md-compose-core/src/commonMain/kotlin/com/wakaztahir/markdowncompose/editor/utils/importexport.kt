package com.wakaztahir.markdowncompose.editor.utils

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.utils.TAG_URL

sealed class EditorStyle(val index: Int) {

    class LinkStyle(
        index: Int,
        val text: String,
        val link: String,
        val isStarting: Boolean
    ) : EditorStyle(index = index)

    class SpanStyleContainer(
        index: Int,
        val spanStyle: SpanStyle,
        val isStarting: Boolean
    ) : EditorStyle(index = index)

    class LineBreak(index: Int) : EditorStyle(index = index)

}

internal fun AnnotatedString.toSortedStyleContainers(
    addLineBreaks: Boolean = true,
    descending : Boolean = true,
): MutableList<EditorStyle> {
    val styleContainers = mutableListOf<EditorStyle>()

    styleContainers.addAll(spanStyles.map {
        EditorStyle.SpanStyleContainer(spanStyle = it.item, isStarting = true, index = it.start)
    })

    styleContainers.addAll(spanStyles.map {
        EditorStyle.SpanStyleContainer(spanStyle = it.item, isStarting = false, index = it.end)
    })

    if (addLineBreaks) {
        var index = text.indexOf(char = '\n', startIndex = 0)
        var lineBreakIndex = maxOf(0, index) + 1
        while (index != -1) {
            styleContainers.add(EditorStyle.LineBreak(index))
            index = text.indexOf(char = '\n', startIndex = lineBreakIndex)
            if (index > -1) lineBreakIndex = index + 1
        }
    }

    val urlAnnotations = getStringAnnotations(
        tag = TAG_URL,
        start = 0,
        end = length
    )

    // Eliminate styles in between links
    for(it in urlAnnotations){
        val listIterator = styleContainers.listIterator()
        while(listIterator.hasNext()){
            val style = listIterator.next()
            if(style.index >= it.start && style.index <= it.end){
                listIterator.remove()
            }
        }
    }

    for(it in urlAnnotations){
        val linkStyle = EditorStyle.LinkStyle(
            text = text.substring(startIndex = it.start, endIndex = it.end),
            link = it.item,
            isStarting = true,
            index = it.start
        )
        styleContainers.add(linkStyle)
    }

    for(it in urlAnnotations){
        val linkStyle = EditorStyle.LinkStyle(
            text = text.substring(startIndex = it.start, endIndex = it.end),
            link = it.item,
            isStarting = false,
            index = it.end
        )
        styleContainers.add(linkStyle)
    }

    // Sort the styles on basis of start and end index
    if(descending) {
        styleContainers.sortByDescending { it.index }
    }else {
        styleContainers.sortBy { it.index }
    }

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