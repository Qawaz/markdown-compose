package com.wakaztahir.markdowncompose.editor.model.blocks

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.model.TextFieldValueBlock
import com.wakaztahir.markdowncompose.editor.serialization.MutableStateSerializer
import com.wakaztahir.markdowncompose.editor.serialization.TFVAsTextSerializer
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.editor.utils.toHtml
import com.wakaztahir.markdowncompose.editor.utils.toMarkdown
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class ListItemBlock(
    @Transient
    internal var requestFocus: Boolean = false,
    @Serializable(with = MutableStateSerializer::class)
    private val value: MutableState<@Serializable(with = TFVAsTextSerializer::class) TextFieldValue>,
    @Serializable(with = MutableStateSerializer::class)
    @SerialName("is_checked")
    val isCheckedState: MutableState<Boolean>,
    @SerialName("indentation")
    @Serializable(with = MutableStateSerializer::class)
    val indentationState: MutableState<Int>,
) : EditorBlock(), TextFieldValueBlock {

    constructor(
        text: String,
        requestFocus: Boolean = false,
        isChecked: Boolean = false,
        isIndented: Boolean = false
    ) : this(
        requestFocus = requestFocus,
        value = mutableStateOf(TextFieldValue(text)),
        isCheckedState = mutableStateOf(isChecked),
        indentationState = mutableStateOf(if (isIndented) 1 else 0)
    )

    constructor(
        requestFocus: Boolean = false,
        textFieldValue: TextFieldValue = TextFieldValue(),
        isChecked: Boolean = false,
        isIndented: Boolean = false
    ) : this(
        requestFocus = requestFocus,
        value = mutableStateOf(textFieldValue),
        isCheckedState = mutableStateOf(isChecked),
        indentationState = mutableStateOf(if (isIndented) 1 else 0)
    )

    override var textFieldValue: TextFieldValue
        get() = value.value
        set(newValue) {
            value.value = newValue
        }

    var isIndented: Boolean
        get() = indentationState.value != 0
        set(value) {
            indentationState.value = if (value) 1 else 0
        }

    var indentation by indentationState
    var isChecked by isCheckedState

    var text: String
        get() = textFieldValue.text
        set(value) {
            textFieldValue = TextFieldValue(value)
        }

    @Transient
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListItemBlock) return false

        if (indentation != other.indentation) return false
        if (isChecked != other.isChecked) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = indentation
        result = 31 * result + isChecked.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }


}