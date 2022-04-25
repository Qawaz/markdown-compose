package com.wakaztahir.markdowntext.editor.model.blocks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowntext.editor.model.EditorBlock
import com.wakaztahir.markdowntext.editor.states.EditorState

class CodeBlock(
    var lang: String = "js",
    value: String = "",
) : EditorBlock() {

    var textFieldValue by mutableStateOf(TextFieldValue(text = value))

    val value: String
        get() = textFieldValue.text

    override fun exportText(state: EditorState): String {
        return ""
    }

    override fun exportMarkdown(state: EditorState): String {
        return """${"\n"}```${lang}${"\n"}$value${"\n"}```${"\n"}"""
    }

    override fun exportHTML(state: EditorState): String {
        return "<pre><code class='language-${lang.lowercase()}'>$value</code></pre>"
    }
}