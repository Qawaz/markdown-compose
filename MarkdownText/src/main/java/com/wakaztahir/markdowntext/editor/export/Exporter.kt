package com.wakaztahir.markdowntext.editor.export

import com.wakaztahir.markdowntext.editor.ParsedMarkdown
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.renderer.text.TextContentRenderer

fun ParsedMarkdown.toMarkdown(): String {
    TODO("Implement Markdown Export")
}

fun ParsedMarkdown.toHtml(): String {
    val renderer = HtmlRenderer.builder().build()
    return renderer.render(this.parent)
}

fun ParsedMarkdown.toText(): String {
    val renderer = TextContentRenderer.builder().build()
    return renderer.render(this.parent)
}