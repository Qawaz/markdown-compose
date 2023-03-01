package com.wakaztahir.markdowncompose.editor.model.blocks

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.editor.utils.toHtml
import com.wakaztahir.markdowncompose.editor.utils.toMarkdown
import com.wakaztahir.markdowncompose.core.annotation.buildMarkdownAnnotatedString
import com.wakaztahir.markdowncompose.editor.model.TextFieldValueBlock
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser


class TextBlock(value: TextFieldValue = TextFieldValue(),internal var requestFocus : Boolean = false) : EditorBlock(),TextFieldValueBlock {

    override var textFieldValue by mutableStateOf(value)
    val focusRequester = FocusRequester()

    constructor(text : String) : this(value = TextFieldValue(text = text))

    override fun exportText(state: EditorState): String {
        return textFieldValue.text
    }

    override fun exportMarkdown(state: EditorState): String {
        return textFieldValue.annotatedString.toMarkdown()
    }

    override fun exportHTML(state: EditorState): String {
        return textFieldValue.annotatedString.toHtml()
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
}