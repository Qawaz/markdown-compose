package com.wakaztahir.markdowncompose.editor.model.blocks

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.model.TextFieldValueBlock
import com.wakaztahir.markdowncompose.editor.serialization.MutableStateSerializer
import com.wakaztahir.markdowncompose.editor.serialization.TFVAsTextSerializer
import com.wakaztahir.markdowncompose.editor.states.EditorState
import kotlinx.serialization.Serializable

@Serializable
class CodeBlock(
    var lang: String,
    @Serializable(with = MutableStateSerializer::class)
    val code: MutableState<@Serializable(with = TFVAsTextSerializer::class) TextFieldValue>,
) : EditorBlock(), TextFieldValueBlock {

    constructor(lang: String = "js", value: String = "") : this(
        lang = lang,
        code = mutableStateOf(TextFieldValue(text = value))
    )

    override var textFieldValue
        get() = code.value
        set(newValue) {
            code.value = newValue
        }

    val value: String get() = code.value.text

    override fun exportText(state: EditorState): String {
        return "Code ($lang) : $value\n"
    }

    override fun exportMarkdown(state: EditorState): String {
        return """${"\n"}```${lang}${"\n"}$value${"\n"}```${"\n"}"""
    }

    override fun exportMarkdownNew(state: EditorState): String {
        return """${"\n"}```${lang}${"\n"}$value${"\n"}```${"\n"}"""
    }

    override fun exportHTML(state: EditorState): String {
        return "<pre><code class='language-${lang.lowercase()}'>$value</code></pre>"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CodeBlock) return false
        if (lang != other.lang) return false
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int {
        var result = lang.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }


}