package com.wakaztahir.markdowncompose.editor.model.blocks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.model.TextFieldValueBlock
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.editor.utils.toHtml
import com.wakaztahir.markdowncompose.editor.utils.toMarkdown

class ListItemBlock(
    internal var requestFocus: Boolean = false,
    textFieldValue: TextFieldValue = TextFieldValue(),
    isChecked: Boolean = false,
    isIndented: Boolean = false,
) : EditorBlock(), TextFieldValueBlock {

    override var textFieldValue by mutableStateOf(textFieldValue)
    var text: String
        get() = textFieldValue.text
        set(value) {
            textFieldValue = TextFieldValue(value)
        }
    var isChecked by mutableStateOf(isChecked)
    var isIndented by mutableStateOf(isIndented)
    val focusRequester = FocusRequester()

    override fun exportText(state: EditorState): String {
        return text
    }

    override fun exportMarkdown(state: EditorState): String {
        val index = state.blocks.indexOf(this)
        val notFirstListItem = if (index > 0) state.blocks[index - 1] is ListItemBlock else false
        return (if (isIndented && notFirstListItem) "\t" else "") + " - " + (if (isChecked) "[x]" else "[ ]") + " " + textFieldValue.annotatedString.toMarkdown()
    }

    override fun exportHTML(state: EditorState): String {
        return "<li>${textFieldValue.annotatedString.toHtml()}</li>"
    }
}