package com.wakaztahir.markpose.editor.model.blocks

import androidx.compose.material.Colors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markpose.editor.model.EditorBlock
import com.wakaztahir.markpose.editor.states.EditorState
import com.wakaztahir.markpose.editor.utils.toHtml
import com.wakaztahir.markpose.editor.utils.toMarkdown
import com.wakaztahir.markpose.core.annotation.buildMarkdownAnnotatedString
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser


class TextBlock(value: TextFieldValue = TextFieldValue(),internal var requestFocus : Boolean = false) : EditorBlock() {

    var textFieldValue by mutableStateOf(value)
    val focusRequester = FocusRequester()

    constructor(text : String) : this(value = TextFieldValue(text = text))

    override fun exportText(state: EditorState): String {
        return textFieldValue.text
    }

    override fun exportMarkdown(state: EditorState): String {
        return textFieldValue.annotatedString.toMarkdown() + "\n"
    }

    override fun exportHTML(state: EditorState): String {
        return textFieldValue.annotatedString.toHtml()
    }

    fun importMarkdown(markdown: String, colors: Colors) {
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