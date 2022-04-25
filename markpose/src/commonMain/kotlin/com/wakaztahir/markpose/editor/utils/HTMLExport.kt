package com.wakaztahir.markpose.editor.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import com.wakaztahir.markpose.editor.model.blocks.CodeBlock
import com.wakaztahir.markpose.editor.model.blocks.MathBlock
import com.wakaztahir.markpose.editor.states.EditorState
import com.wakaztahir.markpose.utils.fastForEach

fun EditorState.exportToHtml(): String {
    var html = blocks.joinToString("\n") { it.exportHTML(this@exportToHtml) }
    if (blocks.indexOfFirst { it is MathBlock } > -1) {
        html += "<script src='https://cdn.jsdelivr.net/npm/mathjax@3.0.0/es5/tex-svg.js'></script>"
    }
    if (blocks.indexOfFirst { it is CodeBlock } > -1) {
        html += """
                <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/highlight.js/11.2.0/styles/default.min.css">
                <script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/11.2.0/highlight.min.js"></script>
                <script>hljs.highlightAll();</script>
            """.trimIndent()
    }
    return html
}

/** converts annotated string to html **/
fun AnnotatedString.toHtml(): String {

    var html = ""

    val sortedStyles = toSortedStyleContainers()

    var index = text.length

    sortedStyles.fastForEach {
        val mark = when (it.style.item) {
            is SpanStyle -> {
                if (!it.isStarting) endHtml(it.style.item as SpanStyle) else startHtml(it.style.item as SpanStyle)
            }
            is LinkStyle -> {
                val link = it.style.item as LinkStyle
                if (it.isStarting) """<a href="${link.link}">""" else """</a>"""
            }
            is LineBreak -> {
                "<br />"
            }
            else -> {
                ""
            }
        }
        html = mark + text.substring(it.index, index) + html
        index = it.index
    }

    // Appending text before first style
    html = text.substring(0, index) + html

    return html
}

/** appends the starting html for given [style] **/
private fun startHtml(style: SpanStyle): String = buildString {
    with(style) {
        if (fontWeight != null && fontWeight!!.weight > 400) append("<b>")
        if (fontStyle == FontStyle.Italic) append("<i>")
        if (textDecoration == TextDecoration.LineThrough) append("<s>")
    }
}

/** appends the ending html for given [style] **/
private fun endHtml(style: SpanStyle): String = buildString {
    with(style) {
        if (fontWeight != null && fontWeight!!.weight > 400) append("</b>")
        if (fontStyle == FontStyle.Italic) append("</i>")
        if (textDecoration == TextDecoration.LineThrough) append("</s>")
    }
}