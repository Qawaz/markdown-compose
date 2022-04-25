package com.wakaztahir.markdowntext.editor.model.blocks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowntext.editor.model.EditorBlock
import com.wakaztahir.markdowntext.editor.states.EditorState
import com.wakaztahir.markdowntext.editor.utils.toHtml
import com.wakaztahir.markdowntext.editor.utils.toMarkdown

class ListItemBlock : EditorBlock() {

    var textFieldValue by mutableStateOf(TextFieldValue())
    var text: String
        get() = textFieldValue.text
        set(value) {
            textFieldValue = TextFieldValue(value)
        }
    var isChecked by mutableStateOf(false)
    var isIndented by mutableStateOf(false)

    var requestFocus by mutableStateOf(false)

    override fun exportText(state: EditorState): String {
        return text
    }

    override fun exportMarkdown(state: EditorState): String {
        val index = state.blocks.indexOf(this)
        val notFirstListItem = if (index > 0) state.blocks[index - 1] is ListItemBlock else false
        return (if (isIndented && notFirstListItem) "\t" else "") + " - " + (if (isChecked) "[x]" else "[ ]") + " " + textFieldValue.annotatedString.toMarkdown() + "\n"
    }

    override fun exportHTML(state: EditorState): String {
        return "<li>${textFieldValue.annotatedString.toHtml()}</li>"
    }
}