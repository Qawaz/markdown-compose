package com.wakaztahir.markdowncompose.editor.model.blocks

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.editor.utils.toHtml
import com.wakaztahir.markdowncompose.editor.utils.toMarkdown
import com.wakaztahir.markdowncompose.core.annotation.buildMarkdownAnnotatedString
import com.wakaztahir.markdowncompose.editor.model.TextFieldValueBlock
import com.wakaztahir.markdowncompose.editor.serialization.MutableStateSerializer
import com.wakaztahir.markdowncompose.editor.serialization.TFVAsWrapperSerializer
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

typealias TextBlock = ParagraphBlock

@SerialName("paragraph")
@Serializable
class ParagraphBlock(
    @SerialName("text")
    @Serializable(with = MutableStateSerializer::class)
    val value: MutableState<@Serializable(with = TFVAsWrapperSerializer::class) TextFieldValue>,
    @Transient
    internal var requestFocus: Boolean = false
) : EditorBlock(), TextFieldValueBlock {

    constructor(
        value: TextFieldValue = TextFieldValue(),
        requestFocus: Boolean = false
    ) : this(
        value = mutableStateOf(value),
        requestFocus = requestFocus
    )

    override var textFieldValue
        get() = value.value
        set(newValue) {
            value.value = newValue
        }

    val textValue: String get() = value.value.text

    @Transient
    val focusRequester = FocusRequester()

    constructor(text: String) : this(value = TextFieldValue(text = text))

    override fun exportText(state: EditorState): String {
        return textFieldValue.text
    }

    override fun exportMarkdown(state: EditorState): String {
        return textFieldValue.annotatedString.toMarkdown()
    }

    override fun toMarkdown(state: EditorState): String {
        return textFieldValue.annotatedString.toMarkdown(true)
    }

    override fun exportHTML(state: EditorState): String {
        return textFieldValue.annotatedString.toHtml(nestedChildren = true)
    }

    fun importMarkdown(markdown: String, colors: ColorScheme) {
        textFieldValue = textFieldValue.copy(annotatedString = buildAnnotatedString {
            buildMarkdownAnnotatedString(
                content = markdown,
                node = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown),
                colors = colors
            )
        })
    }

    fun importText(text: String) {
        textFieldValue = textFieldValue.copy(text = text)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextBlock) return false
        if (textFieldValue != other.textFieldValue) return false

        return true
    }

    override fun hashCode(): Int {
        return textFieldValue.hashCode()
    }


}