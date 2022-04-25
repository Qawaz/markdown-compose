package com.wakaztahir.markdowntext.editor.model.blocks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowntext.editor.model.EditorBlock
import com.wakaztahir.markdowntext.editor.states.EditorState

class MathBlock(latex: String = "x = y + z") : EditorBlock() {

    var textFieldValue by mutableStateOf(TextFieldValue(text = latex))

    val latex: String
        get() = textFieldValue.text

    override fun exportText(state: EditorState): String {
        return ""
    }

    override fun exportMarkdown(state: EditorState): String {
        return """${"\n"}```latex${"\n"}$latex${"\n"}```${"\n"}"""
    }

    override fun exportHTML(state: EditorState): String {
        return "<div id='math'>$latex</div>"
    }
}