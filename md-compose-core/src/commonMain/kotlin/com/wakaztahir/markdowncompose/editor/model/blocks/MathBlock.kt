package com.wakaztahir.markdowncompose.editor.model.blocks

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.model.TextFieldValueBlock
import com.wakaztahir.markdowncompose.editor.serialization.MutableStateSerializer
import com.wakaztahir.markdowncompose.editor.serialization.TFVAsTextSerializer
import com.wakaztahir.markdowncompose.editor.states.EditorState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("math")
@Serializable
class MathBlock(
    @SerialName("latex")
    @Serializable(with = MutableStateSerializer::class)
    val latexState: MutableState<@Serializable(with = TFVAsTextSerializer::class) TextFieldValue>
) : EditorBlock(), TextFieldValueBlock {

    constructor(latex: String = "x = y + z") : this(
        latexState = mutableStateOf(TextFieldValue(text = latex))
    )

    val latex: String get() = latexState.value.text

    override var textFieldValue
        get() = latexState.value
        set(value) {
            latexState.value = value
        }

    override fun exportText(state: EditorState): String {
        return "Latex : $latex\n"
    }

    override fun exportMarkdown(state: EditorState): String {
        return """${"\n"}```latex${"\n"}$latex${"\n"}```${"\n"}"""
    }

    override fun toMarkdown(state: EditorState): String {
        return """${"\n"}```latex${"\n"}$latex${"\n"}```${"\n"}"""
    }

    override fun exportHTML(state: EditorState): String {
        return "<div id='math'>$latex</div>"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MathBlock) return false

        if (latex != other.latex) return false

        return true
    }

    override fun hashCode(): Int {
        return latex.hashCode()
    }

}