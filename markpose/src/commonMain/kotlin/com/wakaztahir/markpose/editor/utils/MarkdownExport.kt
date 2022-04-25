package com.wakaztahir.markpose.editor.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import com.wakaztahir.markpose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markpose.editor.states.EditorState
import com.wakaztahir.markpose.utils.fastForEach
import com.wakaztahir.markpose.utils.fastForEachIndexed

fun EditorState.exportToMarkdown(): String {
    return buildString {
        blocks.fastForEachIndexed { index, block ->
            if (index > 0 && block !is ListItemBlock && blocks[index - 1] is ListItemBlock) {
                append("\n")
            }
            val mark = block.exportMarkdown(this@exportToMarkdown)
            append(mark)
            if (mark.lastOrNull() != '\n') {
                append("\n")
            }
        }
    }
}

/** converts annotated string to markdown **/
fun AnnotatedString.toMarkdown(): String {

    var markdown = ""

    val sortedStyles = toSortedStyleContainers(
        addLineBreaks = false
    )

    var index = text.length

    sortedStyles.fastForEach {
        val mark = when(it.style.item){
            is SpanStyle -> {
                if (!it.isStarting) endMarkdown(it.style.item as SpanStyle) else startMarkdown(it.style.item as SpanStyle)
            }
            is LinkStyle -> {
                val link = it.style.item as LinkStyle
                if (it.isStarting) "[" else "](${link.link})"
            }
            is LineBreak -> {
                "\n"
            }
            else -> ""
        }
        markdown = mark + text.substring(it.index, index) + markdown
        index = it.index
    }

    // Appending text before first style
    markdown = text.substring(0, index) + markdown

    return markdown
}

/** appends the starting markdown for given [style] **/
private fun startMarkdown(style: SpanStyle): String = buildString {
    with(style) {
        if (fontWeight != null && fontWeight!!.weight > 400) append("**")
        if (fontStyle == FontStyle.Italic) append("*")
        if (textDecoration == TextDecoration.LineThrough) append("~~")
    }
}

/** appends the ending markdown for given [style] **/
private fun endMarkdown(style: SpanStyle): String = startMarkdown(style)